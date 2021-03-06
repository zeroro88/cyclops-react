package cyclops.typeclasses;

import com.aol.cyclops2.hkt.Higher;
import com.aol.cyclops2.hkt.Higher3;
import com.aol.cyclops2.types.functor.Transformable;
import cyclops.control.Xor;
import cyclops.function.Fn1;
import cyclops.function.Fn3;
import cyclops.function.Fn4;

import cyclops.monads.WitnessType;
import cyclops.typeclasses.functor.Functor;
import cyclops.typeclasses.monad.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.function.BiFunction;
import java.util.function.Function;


import cyclops.monads.Witness.kleisli;


/**
 * Compose functions that return monads
 *
 * @param <W> Monad kind
 * @param <T> Function input type
 * @param <R> Function return type
 *              (inside monad e.g. Kleisli[stream,String,Integer] represents a function that takes a String and returns a Stream of Integers)
 */

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Kleisli<W,T,R> implements Fn1<T,Higher<W,R>>,
                                        Transformable<R>,
                                        Higher3<kleisli,W,T,R> {
    
    Monad<W> monad;
    
    Function<? super T, ? extends Higher<W,? extends R>> fn;
    
    public static <W,T,R> Kleisli<W,T,R> of(Monad<W> monad, Function<? super T, ? extends Higher<W,? extends R>> fn){
        return new Kleisli<W,T,R>(monad,fn);
    }
    
    public Kleisli<W,T,R> local(Function<? super R, ? extends R> local){
        return kleisliK(monad, t->monad.map(r->local.apply(r),apply(t)));
    }
    public <R1> Kleisli<W,T,R1> map(Function<? super R, ? extends R1> mapper){
        return kleisliK(monad,andThen(am->monad.map(mapper,am)));
    }
    public <R1> Kleisli<W,T,R1> flatMap(Function<? super R, ? extends Higher<W,? extends R1>> mapper){
        return kleisliK(monad,andThen(am->monad.flatMap((Function)mapper,am)));
    }
    public  <R2> Kleisli<W, T, Tuple2<R,R2>> zip(Kleisli<W, T, R2> o){
        return zip(o, Tuple::tuple);
    }
    public  <R2,B> Kleisli<W, T, B> zip(Kleisli<W, T, R2> o, BiFunction<? super R,? super R2,? extends B> fn){
        return flatMapK(a -> o.map(b -> fn.apply(a,b)));
    }
    
    public <R1> Kleisli<W,T,R1> flatMapK(Function<? super R, ? extends Kleisli<W,T, R1>> mapper){
        return kleisliK(monad, t->monad.flatMap(r ->  mapper.apply(r).apply(t),apply(t)));
    }

    public <A> Kleisli<W,A,R> compose(Kleisli<W,A,T> kleisli) {
        return of(monad,a -> monad.flatMap(this,kleisli.apply(a)));
    }
    public <R2> Kleisli<W,T,R2> then(Kleisli<W,R,R2> kleisli) {
        return of(monad,t-> monad.flatMap(kleisli,apply(t)));

    }

    public <__> Kleisli<W,Xor<T, __>, Xor<R, __>> leftK(W type) {
        return kleisliK(monad, xr -> xr.visit(l -> monad.map(Xor::secondary,apply(l)), r -> monad.map(Xor::primary,monad.unit(r))));
    }
    public <__> Kleisli<W,Xor<__,T>, Xor<__,R>> rightK(W type) {
        return kleisliK(monad, xr -> xr.visit(l -> monad.map(Xor::secondary,monad.unit(l)), r -> monad.map(Xor::primary,apply(r))));
    }
    public <__> Kleisli<W,Tuple2<T, __>, Tuple2<R, __>> firstK() {
        return kleisliK(monad, xr -> xr.map((v1, v2) -> monad.map(r1-> Tuple.tuple(r1,v2),apply(v1))));
    }
    public <__> Kleisli<W,Tuple2<__,T>, Tuple2<__,R>> secondK() {
        return kleisliK(monad, xr -> xr.map((v1, v2) -> monad.map(r2-> Tuple.tuple(v1,r2),apply(v2))));
    }


    public <T2,R2> Kleisli<W,Xor<T, T2>, Xor<R, R2>> merge(Kleisli<W,T2,R2> merge, W type) {
        Kleisli<W,T, Xor<R, R2>> first = then(lift(monad,Xor::secondary, type));
        Kleisli<W,T2, Xor<R, R2>> second = merge.then(lift(monad,Xor::primary, type));
        return first.fanIn(second);

    }

    public <T2> Kleisli<W,Xor<T, T2>, R> fanIn(Kleisli<W,T2,R> fanIn) {
        return of(monad,e -> e.visit(this, fanIn));
    }



    public <R1, R2, R3, R4> Kleisli<W,T,R4> forEach4(Function<? super R, Function<? super T,? extends Higher<W,? extends R1>>> value2,
                                                     BiFunction<? super R, ? super R1, Function<? super T,? extends Higher<W,? extends R2>>> value3,
                                                     Fn3<? super R, ? super R1, ? super R2, Function<? super T,? extends Higher<W,? extends R3>>> value4,
                                                     Fn4<? super R, ? super R1, ? super R2, ? super R3, ? extends R4> yieldingFunction) {




        return this.flatMapK(in -> {

            Kleisli<W,T,R1> a = kleisliK(monad,value2.apply(in));
            return a.flatMapK(ina -> {
                Kleisli<W,T,R2> b = kleisliK(monad,value3.apply(in,ina));
                return b.flatMapK(inb -> {

                    Kleisli<W,T,R3> c = kleisliK(monad,value4.apply(in,ina,inb));
                    return c.map(inc->yieldingFunction.apply(in, ina, inb, inc));

                });


            });


        });

    }




    public <R1, R2, R4> Kleisli<W,T,R4> forEach3(Function<? super R, Function<? super T,? extends Higher<W,? extends R1>>> value2,
                                                 BiFunction<? super R, ? super R1, Function<? super T,? extends Higher<W,? extends R2>>> value3,
                                                 Fn3<? super R, ? super R1, ? super R2, ? extends R4> yieldingFunction) {

        return this.flatMapK(in -> {

            Kleisli<W,T,R1> a = kleisliK(monad,value2.apply(in));
            return a.flatMapK(ina -> {
                Kleisli<W,T,R2> b = kleisliK(monad,value3.apply(in,ina));
                return b.map(in2 -> {
                    return yieldingFunction.apply(in, ina, in2);

                });



            });

        });

    }

    public <R1, R4> Kleisli<W,T,R4> forEach2(Function<? super R, Function<? super T,? extends Higher<W,? extends R1>>> value2,
                                             BiFunction<? super R, ? super R1, ? extends R4> yieldingFunction) {

        return this.flatMapK(in -> {

            Kleisli<W,T,R1> a = kleisliK(monad,value2.apply(in));
            return a.map(in2 -> {
                return yieldingFunction.apply(in, in2);

            });




        });


    }


    public static <T,R,W> Kleisli<W,T,R> kleisliK(Monad<W> monad, Function<? super T,? extends Higher<W,? extends R>> fn){
        return of(monad,fn);
    }
    public static <T,R,W> Kleisli<W,T,R> lift(Monad<W> monad, Function<? super T,? extends R> fn, W type){
        return  kleisliK(monad,fn.andThen(r->monad.unit(r)));
    }

    static <T, W, R> Fn1<T,Higher<W,R>> narrow(Function<? super T, ? extends Higher<W, ? extends R>> fn) {
        if(fn instanceof Fn1){
            return (Fn1)fn;
        }
        return in -> (Higher<W,R>)fn.apply(in);
    }

    static <T, W, R> Kleisli<W,T,R> narrowK(Higher<Higher<Higher<kleisli, W>, T>, R> k) {

        return (Kleisli)k;
    }

    static <T, W, R> Kleisli<W,T,R> narrowK3(Higher3<kleisli,W,T,R> kleisliHigher3) {

        return (Kleisli<W,T,R>)kleisliHigher3;
    }

    @Override
    public Higher<W, R> apply(T a) {
        return (Higher<W,R>)fn.apply(a);
    }

    public static class Instances{

        public static <W extends  WitnessType<W>,IN> Functor<Higher<Higher<kleisli,W>,IN>> functor(){
            return new Functor<Higher<Higher<kleisli,W>,IN>> (){
                @Override
                public <T, R> Higher<Higher<Higher<kleisli, W>, IN>, R> map(Function<? super T, ? extends R> fn, Higher<Higher<Higher<kleisli, W>, IN>, T> ds) {
                    Kleisli<W, IN, T> fn1 = narrowK(ds);
                    Kleisli<W, IN, R> res = fn1.map(fn);
                    Higher3<kleisli,W,IN,R> hk = res;
                    return res;
                }

            };
        }


    }
}
