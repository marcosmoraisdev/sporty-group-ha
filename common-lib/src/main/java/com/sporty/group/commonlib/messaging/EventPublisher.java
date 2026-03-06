package com.sporty.group.commonlib.messaging;

@FunctionalInterface
public interface EventPublisher<T> {

    void publish(T event);
}
