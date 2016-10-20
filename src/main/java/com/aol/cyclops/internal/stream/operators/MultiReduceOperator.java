package com.aol.cyclops.internal.stream.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.Reducer;
import com.aol.cyclops.control.StreamUtils;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MultiReduceOperator<R> {

    private final Stream<R> stream;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<R> reduce(final Iterable<? extends Monoid<R>> reducers) {
        final Reducer<List<R>> m = new Reducer<List<R>>() {
            @Override
            public List<R> zero() {
                return StreamUtils.stream(reducers)
                                  .map(r -> r.zero())
                                  .collect(Collectors.toList());
            }

            @Override
            public BiFunction<List<R>, List<R>, List<R>> combiner() {
                return (c1, c2) -> {
                    final List l = new ArrayList<>();
                    int i = 0;
                    for (final Monoid next : reducers) {
                        l.add(next.combiner()
                                  .apply(c1.get(i), c2.get(0)));
                        i++;
                    }

                    return l;
                };
            }

            @Override
            public Stream mapToType(final Stream stream) {
                return stream.map(value -> Arrays.asList(value));
            }

            @Override
            public List<R> apply(final List<R> t, final List<R> u) {
                return combiner().apply(t, u);
            }
        };
        return m.mapReduce(stream);
    }
}