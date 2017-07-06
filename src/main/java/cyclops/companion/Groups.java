package cyclops.companion;

import com.aol.cyclops2.data.collections.extensions.FluentCollectionX;
import com.aol.cyclops2.types.Zippable;
import com.aol.cyclops2.types.futurestream.SimpleReactStream;
import cyclops.async.Future;
import cyclops.async.SimpleReact;
import cyclops.collections.box.Mutable;
import cyclops.collections.immutable.*;
import cyclops.collections.mutable.*;
import cyclops.control.Ior;
import cyclops.control.Maybe;
import cyclops.control.Try;
import cyclops.control.Xor;
import cyclops.function.Group;
import cyclops.function.Monoid;
import cyclops.function.Semigroup;
import cyclops.stream.FutureStream;
import cyclops.stream.ReactiveSeq;
import cyclops.stream.Spouts;
import cyclops.typeclasses.NaturalTransformation;
import org.jooq.lambda.Seq;
import org.reactivestreams.Publisher;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;


public interface Groups {



    /**
     * @return A combiner for mutable lists
     */
    static <T> Group<List<T>> mutableListConcat() {
        return Group.<List<T>>of(l->ListX.fromIterable(l).reverse(),Monoids.mutableListConcat());
    }



    /**
     * @return A combiner for mutable SortedSets
     */
    static <T> Group<SortedSet<T>> mutableSortedSetConcat() {
        return Group.of(s->SortedSetX.fromIterable(s).reverse(),Monoids.mutableSortedSetConcat());
    }

    /**
     * @return A combiner for mutable Queues
     */
    static <T> Group<Queue<T>> mutableQueueConcat() {
        return Group.of(l->QueueX.fromIterable(l).reverse(),Monoids.mutableQueueConcat());
    }

    /**
     * @return A combiner for mutable Deques
     */
    static <T> Group<Deque<T>> mutableDequeConcat() {
        return Group.of(d->DequeX.fromIterable(d).reverse(),Monoids.mutableDequeConcat());
    }

    /**
     * @return A combiner for ListX (concatenates two ListX into a singleUnsafe ListX)
     */
    static <T> Group<ListX<T>> listXConcat() {
        return Group.of(ListX::reverse,Monoids.listXConcat());
    }


    /**
     * @return A combiner for SortedSetX (concatenates two SortedSetX into a singleUnsafe SortedSetX)
     */
    static <T> Monoid<SortedSetX<T>> sortedSetXConcat() {
        return Monoid.of(SortedSetX.empty(),Semigroups.collectionXConcat());
    }

    /**
     * @return A combiner for QueueX (concatenates two QueueX into a singleUnsafe QueueX)
     */
    static <T> Monoid<QueueX<T>> queueXConcat() {
        return Monoid.of(QueueX.empty(),Semigroups.collectionXConcat());
    }

    /**
     * @return A combiner for DequeX (concatenates two DequeX into a singleUnsafe DequeX)
     */
    static <T> Monoid<DequeX<T>> dequeXConcat() {
        return Monoid.of(DequeX.empty(),Semigroups.collectionXConcat());
    }

    /**
     * @return A combiner for LinkedListX (concatenates two LinkedListX into a singleUnsafe LinkedListX)
     */
    static <T> Monoid<LinkedListX<T>> linkedListXConcat() {
        return Monoid.of(LinkedListX.empty(),Semigroups.collectionXConcat());
    }

    /**
     * @return A combiner for VectorX (concatenates two VectorX into a singleUnsafe VectorX)
     */
    static <T> Monoid<VectorX<T>> vectorXConcat() {
        return Monoid.of(VectorX.empty(),Semigroups.collectionXConcat());
    }

    /**
     * @return A combiner for PersistentSetX (concatenates two PersistentSetX into a singleUnsafe PersistentSetX)
     */
    static <T> Monoid<PersistentSetX<T>> persistentSetXConcat() {
        return Monoid.of(PersistentSetX.empty(),Semigroups.collectionXConcat());
    }

    /**
     * @return A combiner for OrderedSetX (concatenates two OrderedSetX into a singleUnsafe OrderedSetX)
     */
    static <T> Monoid<OrderedSetX<T>> orderedSetXConcat() {
        return Monoid.of(OrderedSetX.empty(),Semigroups.collectionXConcat());
    }

    /**
     * @return A combiner for PersistentQueueX (concatenates two PersistentQueueX into a singleUnsafe PersistentQueueX)
     */
    static <T> Monoid<PersistentQueueX<T>> persistentQueueXConcat() {
        return Monoid.of(PersistentQueueX.empty(),Semigroups.collectionXConcat());
    }

    /**
     * @return A combiner for BagX (concatenates two BagX into a singleUnsafe BagX)
     */
    static <T> Monoid<BagX<T>> bagXConcat() {
        return Monoid.of(BagX.empty(),Semigroups.collectionXConcat());
    }

    /**
     * This Semigroup will attempt toNested combine JDK Collections. If the Supplied are instances of cyclops2-react extended Collections
     * or a pCollection persisent toX a new Collection type is created that contains the entries from both supplied collections.
     * If the supplied Collections are standard JDK mutable collections Colleciton b is appended toNested Collection a and a is returned.
     *
     *
     * To manage javac type inference takeOne assign the semigroup
     * <pre>
     * {@code
     *
     *    Monoid<List<Integer>> list = Monoids.collectionConcat();
     *    Monoid<Set<Integer>> set = Monoids.collectionConcat();
     *
     *
     *
     * }
     * </pre>
     * @return A Semigroup that attempts toNested combine the supplied Collections
     */
    static <T, C extends Collection<T>> Monoid<C> collectionConcat(C zero) {
        return Monoid.of(zero, Semigroups.collectionConcat());
    }
    /**
     * Example sum integer Maybes
     * <pre>
     * {@code
     *     Monoid<Maybe<Integer>> sumMaybes = Monoids.combineScalarFunctors(Maybe::just,Monoids.intSum);
     * }
     * </pre>
     *
     * @param zeroFn Function zeoFn lift the Identity value into a Scalar Functor
     * @param monoid Monoid toNested combine the values inside the Scalar Functors
     * @return Combination of two Scalar Functors
     */
    static <T,A extends Zippable<T>> Monoid<A> combineScalarFunctors(Function<T, A> zeroFn, Monoid<T> monoid) {

        return Monoid.of(zeroFn.apply(monoid.zero()),Semigroups.combineScalarFunctors(monoid));
    }
    /**
     * Example sum integer Lists
     * <pre>
     * {@code
     *      Monoid<ListX<Integer>> sumLists = Monoids.combineZippables(ListX::of,Monoids.intSum);
     * }
     * </pre>
     *
     * @param zeroFn Function toNested lift the Identity value into a Zippable
     * @param monoid Monoid toNested combine the values inside the Zippables
     * @return Combination of two Applicatives
     */
    static <T,A extends Zippable<T>> Monoid<A> combineZippables(Function<T, A> zeroFn, Monoid<T> monoid) {

        return Monoid.of(zeroFn.apply(monoid.zero()),Semigroups.combineZippables(monoid));
    }
    /**
     * @return Combination of two LazyFutureStreams Streams b is appended toNested a
     */
    static <T> Semigroup<FutureStream<T>> combineFutureStream() {
        return (a, b) -> a.appendS(b);
    }
    /**
     * @return Combination of two ReactiveSeq Streams b is appended toNested a
     */
    static <T> Group<ReactiveSeq<T>> combineReactiveSeq() {
        return Group.of(ReactiveSeq::reverse,Monoids.combineReactiveSeq());
    }
    static <T> Monoid<ReactiveSeq<T>> firstNonEmptyReactiveSeq() {
        return Monoid.of(ReactiveSeq.empty(), Semigroups.firstNonEmptyReactiveSeq());
    }

    static <T> Monoid<ReactiveSeq<T>> mergeLatestReactiveSeq() {
        return Monoid.of(Spouts.empty(),Semigroups.mergeLatestReactiveSeq());
    }
    static <T> Monoid<Publisher<T>> mergeLatest() {
        return Monoid.of(Spouts.empty(),Semigroups.mergeLatest());
    }
    static <T> Monoid<Publisher<T>> amb() {
        return Monoid.of(Spouts.empty(), Semigroups.amb());
    }
    static <T> Monoid<ReactiveSeq<T>> ambReactiveSeq() {
        return Monoid.of(Spouts.empty(), Semigroups.ambReactiveSeq());
    }

    /**
     * @return Combination of two Seq's : b is appended toNested a
     */
    static <T> Monoid<Seq<T>> combineSeq() {
        return Monoid.of(Seq.empty(), Semigroups.combineSeq());
    }

    /**
     * @return Combination of two Stream's : b is appended toNested a
     */
    static <T> Monoid<Stream<T>> combineStream() {
        return Monoid.of(Stream.empty(), Semigroups.combineStream());
    }
    /**
     * @param zero Empty Collection of same type
     * @return Combination of two Collection, takeOne non-empty is returned
     */
    static <T,C extends Collection<T>> Monoid<C> firstNonEmpty(C zero) {
        return  Monoid.of(zero,Semigroups.firstNonEmpty());
    }
    /**
     * @param zero Empty Collection of same type
     * @return Combination of two Collection, last non-empty is returned
     */
    static <T,C extends Collection<T>> Monoid<C> lastNonEmpty(C zero) {
        return Monoid.of(zero,Semigroups.lastNonEmpty());
    }
    /**
     * @return Combination of two Objects of same type, takeOne non-null is returned
     */
    static <T> Monoid<T> firstNonNull() {
         return Monoid.of(null, Semigroups.firstNonNull());
    }
    /**
     * @return Combine two CompletableFuture's by taking the takeOne present
     */
    static <T> Monoid<CompletableFuture<T>> firstCompleteCompletableFuture() {
        return Monoid.of(new CompletableFuture<T>(), Semigroups.firstCompleteCompletableFuture());
    }
    /**
     * @return Combine two Future's by taking the takeOne result
     */
    static <T> Monoid<Future<T>> firstCompleteFuture() {
       return Monoid.of(Future.future(), Semigroups.firstCompleteFuture());
    }

    static <T> Monoid<SimpleReactStream<T>> firstOfSimpleReact() {
        return Monoid.of(new SimpleReact().of(),Semigroups.firstOfSimpleReact());
    }
    /**
     * @return Combine two Future's by taking the takeOne successful
     */
    static <T> Monoid<Future<T>> firstSuccessfulFuture() {
        return Monoid.of(Future.future(), Semigroups.firstSuccessfulFuture());
    }
    /**
     * @return Combine two Xor's by taking the takeOne primary
     */
    static <ST,PT> Monoid<Xor<ST,PT>> firstPrimaryXor(ST zero) {
        return Monoid.of(Xor.secondary(zero), Semigroups.firstPrimaryXor());
    }
    /**
     * @return Combine two Xor's by taking the takeOne secondary
     */
    static <ST,PT> Monoid<Xor<ST,PT>> firstSecondaryXor(PT zero) {
        return Monoid.of(Xor.primary(zero), Semigroups.firstSecondaryXor());
    }
    /**
     * @return Combine two Xor's by taking the last primary
     */
    static <ST,PT> Monoid<Xor<ST,PT>> lastPrimaryXor(ST zero) {
        return Monoid.of(Xor.secondary(zero), Semigroups.lastPrimaryXor());
    }
    /**
     * @return Combine two Xor's by taking the last secondary
     */
    static <ST,PT> Monoid<Xor<ST,PT>> lastSecondaryXor(PT zero) {
        return Monoid.of(Xor.primary(zero), Semigroups.lastSecondaryXor());
    }
    /**
     * @return Combine two Try's by taking the takeOne primary
     */
    static <T,X extends Throwable> Monoid<Try<T,X>> firstTrySuccess(X zero) {
        return Monoid.of(Try.failure(zero), Semigroups.firstTrySuccess());
    }
    /**
     * @return Combine two Try's by taking the takeOne secondary
     */
    static <T,X extends Throwable> Monoid<Try<T,X>> firstTryFailure(T zero) {
        return Monoid.of(Try.success(zero), Semigroups.firstTryFailure());
    }
    /**
     * @return Combine two Tryr's by taking the last primary
     */
    static<T,X extends Throwable> Monoid<Try<T,X>> lastTrySuccess(X zero) {
        return Monoid.of(Try.failure(zero), Semigroups.lastTrySuccess());
    }
    /**
     * @return Combine two Try's by taking the last secondary
     */
    static <T,X extends Throwable> Monoid<Try<T,X>>lastTryFailure(T zero) {
        return Monoid.of(Try.success(zero), Semigroups.lastTryFailure());
    }
    /**
     * @return Combine two Ior's by taking the takeOne primary
     */
    static <ST,PT> Monoid<Ior<ST,PT>> firstPrimaryIor(ST zero) {
        return Monoid.of(Ior.secondary(zero), Semigroups.firstPrimaryIor());
    }
    /**
     * @return Combine two Ior's by taking the takeOne secondary
     */
    static <ST,PT> Monoid<Ior<ST,PT>> firstSecondaryIor(PT zero) {
        return Monoid.of(Ior.primary(zero), Semigroups.firstSecondaryIor());
    }
    /**
     * @return Combine two Ior's by taking the last primary
     */
    static <ST,PT> Monoid<Ior<ST,PT>> lastPrimaryIor(ST zero) {
        return Monoid.of(Ior.secondary(zero), Semigroups.lastPrimaryIor());
    }
    /**
     * @return Combine two Ior's by taking the last secondary
     */
    static <ST,PT> Monoid<Ior<ST,PT>> lastSecondaryIor(PT zero) {
        return Monoid.of(Ior.primary(zero), Semigroups.lastSecondaryIor());
    }
    /**
     * @return Combine two Maybe's by taking the takeOne present
     */
    static <T> Monoid<Maybe<T>> firstPresentMaybe() {
        return Monoid.of(Maybe.none(), Semigroups.firstPresentMaybe());
    }

    /**
     * @return Combine two optionals by taking the takeOne present
     */
    static <T> Monoid<Optional<T>> firstPresentOptional() {
        return Monoid.of(Optional.empty(), Semigroups.firstPresentOptional());
    }

    /**
     * @return Combine two Maybes by taking the last present
     */
    static <T> Monoid<Maybe<T>> lastPresentMaybe() {
        return Monoid.of(Maybe.none(), Semigroups.lastPresentMaybe());
    }

    /**
     * @return Combine two optionals by taking the last present
     */
    static <T> Monoid<Optional<T>> lastPresentOptional() {
        return Monoid.of(Optional.empty(), Semigroups.lastPresentOptional());
    }

    /**
     * @param joiner Separator in joined String
     * @return Combine two strings separated by the supplied joiner
     */
    static Monoid<String> stringJoin(final String joiner) {
        return Group.of(s->new StringBuilder(s).reverse().toString(),Monoids.stringJoin(joiner));
    }

    /**
     * @param joiner Separator in joined String
     * @return Combine two StringBuilders separated by the supplied joiner
     */
    static Group<StringBuilder> stringBuilderJoin(final String joiner) {
        return Group.of(s->new StringBuilder(s).reverse(),Monoids.stringBuilderJoin(joiner));
    }

    /**
     * @param joiner Separator in joined String
     * @return Combine two StringBuffers separated by the supplied joiner
     */
    static Monoid<StringBuffer> stringBufferJoin(final String joiner) {
        return Monoid.of(new StringBuffer(""), Semigroups.stringBufferJoin(joiner));
    }

    /**
     * @return Combine two Comparables taking the lowest each time
     */
    static <T, T2 extends Comparable<T>> Monoid<T2> minComparable(T2 max) {
        return Monoid.of(max, Semigroups.minComparable());
    }

    /**
     * @return Combine two Comparables taking the highest each time
     */
    static <T, T2 extends Comparable<T>> Monoid<T2> maxComparable(T2 min) {
        return Monoid.of(min, Semigroups.maxComparable());
    }

    /**
     * Combine two Integers by summing them
     */
    static Monoid<Integer> intSum =  Monoid.of(0, Semigroups.intSum);
    /**
     * Combine two Longs by summing them
     */
    static Monoid<Long> longSum =  Monoid.of(0l, Semigroups.longSum);
    /**
     * Combine two Doubles by summing them
     */
    static Monoid<Double> doubleSum =  Monoid.of(0d, Semigroups.doubleSum);
    /**
     * Combine two BigIngegers by summing them
     */
    static Monoid<BigInteger> bigIntSum =  Monoid.of(BigInteger.ZERO, Semigroups.bigIntSum);
    /**
     * Combine two Integers by multiplying them
     */
    static Monoid<Integer> intMult =  Monoid.of(1, Semigroups.intMult);
    /**
     * Combine two Longs by multiplying them
     */
    static Monoid<Long> longMult =  Monoid.of(0l, Semigroups.longMult);
    /**
     * Combine two Doubles by multiplying them
     */
    static Monoid<Double> doubleMult = Monoid.of(0d, Semigroups.doubleMult);
    /**
     * Combine two BigIntegers by multiplying them
     */
    static Monoid<BigInteger> bigIntMult = Monoid.of(BigInteger.ZERO, Semigroups.bigIntMult);
    /**
     * Combine two Integers by selecting the max
     */
    static Monoid<Integer> intMax = Monoid.of(Integer.MIN_VALUE, Semigroups.intMax);
    /**
     * Combine two Longs by selecting the max
     */
    static Monoid<Long> longMax = Monoid.of(Long.MIN_VALUE, Semigroups.longMax);
    /**
     * Combine two Doubles by selecting the max
     */
    static Monoid<Double> doubleMax = Monoid.of(Double.MIN_VALUE, Semigroups.doubleMax);
    /**
     * Combine two BigIntegers by selecting the max
     */
    static Monoid<BigInteger> bigIntMax = Monoid.of(BigInteger.valueOf(Long.MIN_VALUE), Semigroups.bigIntMax);
    /**
     * Combine two Integers by selecting the min
     */
    static Monoid<Integer> intMin = Monoid.of(Integer.MAX_VALUE, Semigroups.intMin);
    /**
     * Combine two Longs by selecting the min
     */
    static Monoid<Long> longMin = Monoid.of(Long.MAX_VALUE, Semigroups.longMin);
    /**
     * Combine two Doubles by selecting the min
     */
    static Monoid<Double> doubleMin = Monoid.of(Double.MAX_VALUE, Semigroups.doubleMin);
    /**
     * Combine two BigIntegers by selecting the min
     */
    static Monoid<BigInteger> bigIntMin = Monoid.of(BigInteger.valueOf(Long.MAX_VALUE), Semigroups.bigIntMin);
    /**
     * String concatenation
     */
    static Monoid<String> stringConcat = Monoid.of("", Semigroups.stringConcat);
    /**
     * StringBuffer concatenation
     */
    static Monoid<StringBuffer> stringBufferConcat = Monoid.of(new StringBuffer(""), Semigroups.stringBufferConcat);
    /**
     * StringBuilder concatenation
     */
    static Monoid<StringBuilder> stringBuilderConcat = Monoid.of(new StringBuilder(""), Semigroups.stringBuilderConcat);
    /**
     * Combine two booleans by OR'ing them (disjunction)
     */
    static Group<Boolean> booleanDisjunction = Group.of(a->!a,Monoids.booleanDisjunction);
    /**
     * Combine two booleans by XOR'ing them (exclusive disjunction)
     */
    static Group<Boolean> booleanXDisjunction = Group.of(a->!a,Monoids.booleanXDisjunction);
    /**
     * Combine two booleans by AND'ing them (conjunction)
     */
    static Group<Boolean> booleanConjunction = Group.of(a->!a,Monoids.booleanConjunction);



}