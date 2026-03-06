package com.sporty.groupha.commonlib.messaging;

@FunctionalInterface
public interface EventPublisher<T> {

    void publish(T event);
}
