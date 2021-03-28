package com.github.vitalibo.decorator;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DecoratorDispatcherTest {

    @Mock
    private ProceedingJoinPoint mockProceedingJoinPoint;
    @Mock
    private Signature mockSignature;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
    }

    @Test
    public void testDelegateExecution() throws Throwable {
        Mockito.when(mockProceedingJoinPoint.getSignature()).thenReturn(mockSignature);
        Mockito.when(mockSignature.getDeclaringType()).thenReturn(TestClass.class);
        Mockito.when(mockProceedingJoinPoint.getArgs()).thenReturn(new String[]{"foo", "bar"});
        Mockito.when(mockSignature.getName()).thenReturn("method");
        Mockito.doAnswer(a -> "baz").when(mockProceedingJoinPoint).proceed(Mockito.any());

        DecoratorDispatcher dispatcher = new DecoratorDispatcher();
        Object actual = dispatcher.delegateExecution(mockProceedingJoinPoint);

        Assert.assertNotNull(actual);
        Assert.assertEquals(actual, "<div>baz</div>");
        Mockito.verify(mockProceedingJoinPoint).proceed(new String[]{"foo", "bar"});
    }

    public static class TestClass {

        @Decorator(TestDecorator.class)
        public static String method(String a, String b) {
            throw new IllegalStateException();
        }

    }

    public static class TestDecorator implements MethodDecorator {

        @Override
        public Object apply(InvocationChain chain, Object... args) throws Throwable {
            return "<div>" + chain.invoke(args) + "</div>";
        }

    }

}
