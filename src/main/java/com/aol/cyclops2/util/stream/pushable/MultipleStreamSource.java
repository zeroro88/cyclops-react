package com.aol.cyclops2.util.stream.pushable;

import java.util.stream.Stream;

import cyclops.async.LazyReact;
import cyclops.stream.ReactiveSeq;
import cyclops.async.adapters.Queue;
import cyclops.async.adapters.Topic;
import cyclops.stream.FutureStream;

/**
 * Build Streams that reactiveStream data from the topic instance
 * 
 * @author johnmcclean
 *
 * @param <T>
 */
public class MultipleStreamSource<T> {

    private final Topic<T> topic;

    public MultipleStreamSource(final Queue<T> q) {
        topic = new Topic(
                          q);
    }

    /**
     * Create a pushable LazyFutureStream using the supplied ReactPool
     * 
     * @param s React builder toNested use toNested create the Stream
     * @return a Tuple2 with a Topic&lt;T&gt; and LazyFutureStream&lt;T&gt; - add data to the Queue
     * toNested push it toNested the Stream
     */
    public FutureStream<T> futureStream(final LazyReact s) {

        return s.fromStream(topic.stream());

    }

    /**
     * Create a pushable JDK 8 Stream
     * @return a Tuple2 with a Topic&lt;T&gt; and Stream&lt;T&gt; - add data to the Queue
     * toNested push it toNested the Stream
     */
    public Stream<T> stream() {

        return topic.stream();

    }

    /**
     * Create a pushable {@link ReactiveSeq}
     * 
     * @return a Tuple2 with a Topic&lt;T&gt; and Seq&lt;T&gt; - add data to the Queue
     * toNested push it toNested the Stream
     */
    public ReactiveSeq<T> reactiveSeq() {

        return topic.stream();
    }

    /**
     * @return Topic used as input for any generated Streams
     */
    public Topic<T> getInput() {
        return topic;
    }

}
