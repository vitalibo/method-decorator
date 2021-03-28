package com.github.vitalibo.decorator;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ChainOf.class)
public @interface Decorator {

    Class<? extends MethodDecorator> value();

}
