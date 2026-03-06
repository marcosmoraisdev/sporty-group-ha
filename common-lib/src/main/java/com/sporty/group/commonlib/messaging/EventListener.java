package com.sporty.group.commonlib.messaging;

@FunctionalInterface
public interface EventListener<T> {

    void onMessage(T event);
}
