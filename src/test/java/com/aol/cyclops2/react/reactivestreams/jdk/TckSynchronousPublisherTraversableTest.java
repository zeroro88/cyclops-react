package com.aol.cyclops2.react.reactivestreams.jdk;


import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.Test;

import cyclops.stream.ReactiveSeq;
@Test
public class TckSynchronousPublisherTraversableTest extends PublisherVerification<Long>{

	public TckSynchronousPublisherTraversableTest(){
		  super(new TestEnvironment(300L));
	}
	

	@Override
	public Publisher<Long> createPublisher(long elements) {
		return ReactiveSeq.iterate(0l, i->i+1l).limit(Math.min(elements,1000)).toListX();
		
	}

	@Override
	public Publisher<Long> createFailedPublisher() {
		return null; //not possible toNested forEachAsync toNested failed Stream
		
	}
	

}
