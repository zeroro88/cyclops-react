package cyclops.monads.function;

import cyclops.function.Fn2;
import cyclops.function.Fn3;
import cyclops.monads.AnyM;
import cyclops.monads.WitnessType;

/**
 * Created by johnmcclean on 18/12/2016.
 */
@FunctionalInterface
public interface AnyMFn3<W extends WitnessType<W>,T1,T2,T3,R> extends Fn3<AnyM<W,T1>,AnyM<W,T2>,AnyM<W,T3>,AnyM<W,R>> {

}
