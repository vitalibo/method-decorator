package com.github.vitalibo.decorator;

@FunctionalInterface
public interface MethodDecorator {

    Object apply(InvocationChain chain, Object... args) throws Throwable;

}
