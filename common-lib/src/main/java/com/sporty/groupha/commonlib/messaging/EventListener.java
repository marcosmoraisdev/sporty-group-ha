package com.sporty.groupha.commonlib.messaging;

@FunctionalInterface
public interface EventListener<T> {

    void onMessage(T event);
}
