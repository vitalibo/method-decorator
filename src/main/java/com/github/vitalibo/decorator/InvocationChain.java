package com.github.vitalibo.decorator;

@FunctionalInterface
public interface InvocationChain {

    Object invoke(Object... args) throws Throwable;

}
