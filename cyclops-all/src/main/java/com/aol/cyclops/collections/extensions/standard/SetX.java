package com.aol.cyclops.collections.extensions.standard;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.aol.cyclops.streams.StreamUtils;
import com.aol.cyclops.trampoline.Trampoline;

public interface SetX<T> extends Set<T>, MutableCollectionX<T> {
	static <T> Collector<T,?,Set<T>> defaultCollector(){
		return Collectors.toCollection(()-> new HashSet<>());
	}
	static <T> Collector<T,?,Set<T>> immutableCollector(){
		return Collectors.collectingAndThen(defaultCollector(), (Set<T> d)->Collections.unmodifiableSet(d));

	}
	static <T> Collector<T,?,SetX<T>> toSetX(){
		return Collectors.collectingAndThen(defaultCollector(), (Set<T> d)->new SetXImpl<>(d,defaultCollector()));
		
	}
	public static <T> SetX<T> empty(){
		return fromIterable((Deque<T>) defaultCollector().supplier().get());
	}
	public static <T> SetX<T> of(T...values){
		Set<T> res = (Set<T>) defaultCollector().supplier().get();
		for(T v: values)
			res.add(v);
		return  fromIterable(res);
	}
	public static <T> SetX<T> singleton(T value){
		return of(value);
	}
	public static <T> SetX<T> fromIterable(Iterable<T> it){
		return fromIterable(defaultCollector(),it);
	}
	public static <T> SetX<T> fromIterable(Collector<T,?,Set<T>>  collector,Iterable<T> it){
		if(it instanceof SetX)
			return (SetX)it;
		if(it instanceof Set)
			return new SetXImpl<T>( (Set)it, collector);
		return new SetXImpl<T>(StreamUtils.stream(it).collect(collector),collector);
	}
	
	
	
	default <T1> SetX<T1> from(Collection<T1> c){
		return SetX.<T1>fromIterable(getCollector(),c);
	}
	public <T>Collector<T,?,Set<T>> getCollector();
	
	
	
	default <X> SetX<X> fromStream(Stream<X> stream){
		return new SetXImpl<>(stream.collect(getCollector()),getCollector());
	}

	

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#reverse()
	 */
	@Override
	default SetX<T> reverse() {
		return (SetX)MutableCollectionX.super.reverse();
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#filter(java.util.function.Predicate)
	 */
	@Override
	default SetX<T> filter(Predicate<? super T> pred) {
		
		return (SetX)MutableCollectionX.super.filter(pred);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#map(java.util.function.Function)
	 */
	@Override
	default <R> SetX<R> map(Function<? super T, ? extends R> mapper) {
		
		return (SetX)MutableCollectionX.super.map(mapper);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#flatMap(java.util.function.Function)
	 */
	@Override
	default <R> SetX<R> flatMap(Function<? super T, ? extends Iterable<? extends R>> mapper) {
		
		return (SetX)MutableCollectionX.super.flatMap(mapper);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#limit(long)
	 */
	@Override
	default SetX<T> limit(long num) {
		return (SetX)MutableCollectionX.super.limit(num);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#skip(long)
	 */
	@Override
	default SetX<T> skip(long num) {
		
		return (SetX)MutableCollectionX.super.skip(num);
	}
	default  SetX<T> takeRight(int num){
		return (SetX)MutableCollectionX.super.takeRight(num);
	}
	default  SetX<T> dropRight(int num){
		return  (SetX)MutableCollectionX.super.dropRight(num);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#takeWhile(java.util.function.Predicate)
	 */
	@Override
	default SetX<T> takeWhile(Predicate<? super T> p) {
		
		return (SetX)MutableCollectionX.super.takeWhile(p);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#dropWhile(java.util.function.Predicate)
	 */
	@Override
	default SetX<T> dropWhile(Predicate<? super T> p) {
		
		return (SetX)MutableCollectionX.super.dropWhile(p);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#takeUntil(java.util.function.Predicate)
	 */
	@Override
	default SetX<T> takeUntil(Predicate<? super T> p) {
		
		return (SetX)MutableCollectionX.super.takeUntil(p);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#dropUntil(java.util.function.Predicate)
	 */
	@Override
	default SetX<T> dropUntil(Predicate<? super T> p) {
		
		return (SetX)MutableCollectionX.super.dropUntil(p);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#trampoline(java.util.function.Function)
	 */
	@Override
	default <R> SetX<R> trampoline(Function<? super T, ? extends Trampoline<? extends R>> mapper) {
		
		return (SetX)MutableCollectionX.super.trampoline(mapper);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#slice(long, long)
	 */
	@Override
	default SetX<T> slice(long from, long to) {
		
		return (SetX)MutableCollectionX.super.slice(from, to);
	}

	/* (non-Javadoc)
	 * @see com.aol.cyclops.collections.extensions.standard.MutableCollectionX#sorted(java.util.function.Function)
	 */
	@Override
	default <U extends Comparable<? super U>> SetX<T> sorted(Function<? super T, ? extends U> function) {
		
		return (SetX)MutableCollectionX.super.sorted(function);
	}

	default SetX<T> plus(T e){
		add(e);
		return this;
	}
	
	default SetX<T> plusAll(Collection<? extends T> list){
		addAll(list);
		return this;
	}
	
	default SetX<T> minus(Object e){
		remove(e);
		return this;
	}
	
	default SetX<T> minusAll(Collection<?> list){
		removeAll(list);
		return this;
	}
}
