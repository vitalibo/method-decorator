package com.github.vitalibo.decorator;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

@Aspect
public class DecoratorDispatcher {

    @Pointcut("execution(* *(..))")
    public void executionMethod() {
    }

    @Pointcut("@annotation(com.github.vitalibo.decorator.ChainOf) || @annotation(com.github.vitalibo.decorator.Decorator)")
    public void decoratedMethod() {
    }

    @Around("executionMethod() && decoratedMethod()")
    public Object delegateExecution(ProceedingJoinPoint point) throws Throwable {
        Decorator[] decorators = invokedMethodDecorators(point);
        InvocationChain chain = buildInvocationChain(point, decorators);
        return chain.invoke(point.getArgs());
    }

    private static Decorator[] invokedMethodDecorators(ProceedingJoinPoint point) throws NoSuchMethodException {
        final Signature signature = point.getSignature();
        Class<?> cls = signature.getDeclaringType();
        Class<?>[] args = Stream.of(point.getArgs()).map(Object::getClass).toArray(Class[]::new);
        Method method = cls.getDeclaredMethod(signature.getName(), args);
        return Optional.ofNullable(method.getAnnotation(ChainOf.class)).map(ChainOf::value)
            .orElse(new Decorator[]{method.getAnnotation(Decorator.class)});
    }

    private static InvocationChain buildInvocationChain(ProceedingJoinPoint point, Decorator[] decorators) throws Throwable {
        InvocationChain chain = point::proceed;
        for (int i = decorators.length - 1; i > -1; i--) {
            Class<? extends MethodDecorator> decorator = decorators[i].value();
            MethodDecorator instance = decorator.getDeclaredConstructor().newInstance();
            InvocationChain prevChain = chain;
            chain = (args) -> instance.apply(prevChain, args);
        }

        return chain;
    }

}
