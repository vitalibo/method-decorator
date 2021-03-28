package com.github.vitalibo.decorator;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.function.BiFunction;

public class DecoratorDispatcherIntegrationTest {

    @DataProvider
    public Object[][] samples() {
        return new Object[][]{
            {fn(DecoratorDispatcherIntegrationTest::staticMethod1), 123, "foo", "123=foo"},
            {fn(DecoratorDispatcherIntegrationTest::staticMethod2), 123, "foo", "123=FOO"},
            {fn(DecoratorDispatcherIntegrationTest::staticMethod3), 123, "foo", "<div>123=FOO</div>"},
            {fn(DecoratorDispatcherIntegrationTest::staticMethod4), 123, "foo", "<div>123=FOO</div>"},
            {fn(DecoratorDispatcherIntegrationTest::staticMethod5), 123, "foo", "<div>123=FOO</div>"},
            {fn(DecoratorDispatcherIntegrationTest::staticMethod6), 123, "foo", "<DIV>123=FOO</DIV>"},
            {fn(this::method1), 123, "foo", "123=foo"},
            {fn(this::method2), 123, "foo", "<div>123=foo</div>"},
            {fn(this::method3), 123, "foo", "&lt;div&gt;123=FOO&lt;/div&gt;"},
            {fn(this::method4), 123, "foo", "<div>123=FOO</div>"},
            {fn(this::method5), 123, "foo", "&LT;DIV&GT;123=FOO&LT;/DIV&GT;"},
            {fn(this::method6), 123, "foo", "&lt;div&gt;123=FOO&lt;/div&gt;"},
            {fn(this::method4), 123, "<h1>foo</h1>", "<div>123=&lt;H1&gt;FOO&lt;/H1&gt;</div>"},
        };
    }

    @Test(dataProvider = "samples")
    public void testApply(BiFunction<Integer, String, String> function, Integer i, String s, String expected) {
        String actual = function.apply(i, s);

        Assert.assertEquals(actual, expected);
    }

    public String method1(Integer i, String s) {
        return staticMethod1(i, s);
    }

    @Decorator(Div.class)
    public String method2(Integer i, String s) {
        return staticMethod1(i, s);
    }

    @Decorator(UpperCaseArgs.class)
    @Decorator(Escape.class)
    @Decorator(Div.class)
    public String method3(Integer i, String s) {
        return staticMethod1(i, s);
    }

    @Decorator(UpperCaseArgs.class)
    @Decorator(Div.class)
    @Decorator(Escape.class)
    public String method4(Integer i, String s) {
        return staticMethod1(i, s);
    }

    @Decorator(UpperCase.class)
    @Decorator(Escape.class)
    @Decorator(Div.class)
    public String method5(Integer i, String s) {
        return staticMethod1(i, s);
    }

    @Decorator(Escape.class)
    @Decorator(Div.class)
    @Decorator(UpperCase.class)
    public String method6(Integer i, String s) {
        return staticMethod1(i, s);
    }

    public static String staticMethod1(Integer i, String s) {
        return String.format("%s=%s", i, s);
    }

    @Decorator(UpperCaseArgs.class)
    public static String staticMethod2(Integer i, String s) {
        return staticMethod1(i, s);
    }

    @Decorator(Div.class)
    @Decorator(UpperCaseArgs.class)
    public static String staticMethod3(Integer i, String s) {
        return staticMethod1(i, s);
    }

    @Decorator(UpperCaseArgs.class)
    @Decorator(Div.class)
    public static String staticMethod4(Integer i, String s) {
        return staticMethod1(i, s);
    }

    @Decorator(Div.class)
    @Decorator(UpperCase.class)
    public static String staticMethod5(Integer i, String s) {
        return staticMethod1(i, s);
    }

    @Decorator(UpperCase.class)
    @Decorator(Div.class)
    public static String staticMethod6(Integer i, String s) {
        return staticMethod1(i, s);
    }

    public static BiFunction<Integer, String, String> fn(BiFunction<Integer, String, String> function) {
        return function;
    }

    public static class Escape implements MethodDecorator {

        @Override
        public Object apply(InvocationChain chain, Object... args) throws Throwable {
            return String.valueOf(chain.invoke(args))
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
        }

    }

    public static class UpperCaseArgs implements MethodDecorator {

        @Override
        public Object apply(InvocationChain chain, Object... args) throws Throwable {
            return chain.invoke(Arrays.stream(args)
                .map(o -> o instanceof String ? ((String) o).toUpperCase() : o)
                .toArray());
        }

    }

    public static class UpperCase implements MethodDecorator {

        @Override
        public Object apply(InvocationChain chain, Object... args) throws Throwable {
            return String.valueOf(chain.invoke(args)).toUpperCase();
        }

    }

    public static class Div implements MethodDecorator {

        @Override
        public Object apply(InvocationChain chain, Object... args) throws Throwable {
            return "<div>" + chain.invoke(args) + "</div>";
        }

    }

}
