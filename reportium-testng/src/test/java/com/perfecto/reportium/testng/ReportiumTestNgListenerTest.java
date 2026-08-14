package com.perfecto.reportium.testng;

import org.testng.IClass;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.internal.ConstructorOrMethod;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlTest;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Unit tests for {@link ReportiumTestNgListener}, in particular {@code getTags()} and
 * {@code extractTagsFromAnnotations()}.
 * <p>
 * Note: {@code getTags()} extracts class-level tags via
 * {@code testResult.getTestClass().getClass()} rather than {@code getRealClass()}. This means it
 * actually reads annotations declared on the {@link IClass} *implementation* class, not on the
 * class under test. This looks like a pre-existing bug/quirk in production code, but since we
 * must not modify production code, these tests exercise the actual behavior: annotations placed
 * directly on the fake {@link IClass} implementation used in the test.
 */
public class ReportiumTestNgListenerTest {

    /**
     * Holds fixture methods carrying real {@code @Test} annotations, purely so this test can grab
     * a real {@link Method} with populated annotation elements via reflection. This class is never
     * itself registered as a TestNG suite class (its name doesn't match the surefire test-class
     * naming patterns), so TestNG never attempts to invoke these methods.
     */
    private static class MethodAnnotationFixtures {
        @org.testng.annotations.Test(suiteName = "MySuite", testName = "MyTestName", description = "MyDescription",
                groups = {"methodGroup1", "methodGroup2"})
        public void annotatedFixtureMethod() {
        }

        @org.testng.annotations.Test
        public void blankAnnotatedFixtureMethod() {
        }
    }

    /** Fixture class carrying its own class-level @Test annotation, used for the getTags() scenario. */
    @org.testng.annotations.Test(groups = {"classGroup"})
    @Deprecated
    private static class AnnotatedFakeIClass implements IClass {
        @Override
        public String getName() {
            return "fake";
        }

        @Override
        public XmlTest getXmlTest() {
            return null;
        }

        @Override
        public XmlClass getXmlClass() {
            return null;
        }

        @Override
        public String getTestName() {
            return null;
        }

        @Override
        public Class<?> getRealClass() {
            return AnnotatedFakeIClass.class;
        }

        @Override
        public Object[] getInstances(boolean create) {
            return new Object[0];
        }

        @Override
        public long[] getInstanceHashCodes() {
            return new long[0];
        }

        @Override
        public void addInstance(Object instance) {
            // no-op
        }
    }

    /** Same as above, but with no annotations at all on the implementation class. */
    private static class PlainFakeIClass implements IClass {
        @Override
        public String getName() {
            return "plain";
        }

        @Override
        public XmlTest getXmlTest() {
            return null;
        }

        @Override
        public XmlClass getXmlClass() {
            return null;
        }

        @Override
        public String getTestName() {
            return null;
        }

        @Override
        public Class<?> getRealClass() {
            return PlainFakeIClass.class;
        }

        @Override
        public Object[] getInstances(boolean create) {
            return new Object[0];
        }

        @Override
        public long[] getInstanceHashCodes() {
            return new long[0];
        }

        @Override
        public void addInstance(Object instance) {
            // no-op
        }
    }

    private Method getFixtureMethod(String name) throws NoSuchMethodException {
        return MethodAnnotationFixtures.class.getDeclaredMethod(name);
    }

    /**
     * Builds a dynamic proxy implementing {@code org.testng.annotations.Test} that returns
     * {@code null} for suiteName/testName/description and the given groups array. Real annotation
     * instances can never return null for these elements (their defaults are empty strings), so a
     * proxy is required to exercise the null-check branch in {@code addNonEmptyTag()}.
     */
    private org.testng.annotations.Test nullValuedTestAnnotationProxy(final String[] groups) {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                switch (method.getName()) {
                    case "annotationType":
                        return org.testng.annotations.Test.class;
                    case "suiteName":
                    case "testName":
                    case "description":
                        return null;
                    case "groups":
                        return groups;
                    default:
                        throw new UnsupportedOperationException("Unexpected call to " + method.getName());
                }
            }
        };
        return (org.testng.annotations.Test) Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class<?>[]{org.testng.annotations.Test.class}, handler);
    }

    // ---------------------------------------------------------------------------------------
    // extractTagsFromAnnotations()
    // ---------------------------------------------------------------------------------------

    @org.testng.annotations.Test
    public void testExtractTagsFromAnnotations_fullyPopulatedAnnotation() throws Exception {
        ReportiumTestNgListener listener = new ReportiumTestNgListener();
        Method fixture = getFixtureMethod("annotatedFixtureMethod");

        List<String> tags = listener.extractTagsFromAnnotations(fixture.getAnnotations());

        assertEquals(tags, Arrays.asList("MySuite", "MyTestName", "MyDescription", "methodGroup1", "methodGroup2"));
    }

    @org.testng.annotations.Test
    public void testExtractTagsFromAnnotations_blankAnnotation_yieldsNoTags() throws Exception {
        ReportiumTestNgListener listener = new ReportiumTestNgListener();
        Method fixture = getFixtureMethod("blankAnnotatedFixtureMethod");

        List<String> tags = listener.extractTagsFromAnnotations(fixture.getAnnotations());

        assertTrue(tags.isEmpty());
    }

    @org.testng.annotations.Test
    public void testExtractTagsFromAnnotations_emptyAnnotationsArray_yieldsNoTags() {
        ReportiumTestNgListener listener = new ReportiumTestNgListener();

        List<String> tags = listener.extractTagsFromAnnotations(new Annotation[0]);

        assertTrue(tags.isEmpty());
    }

    @org.testng.annotations.Test
    public void testExtractTagsFromAnnotations_nonTestAnnotation_isIgnored() {
        ReportiumTestNgListener listener = new ReportiumTestNgListener();
        // @Deprecated (RUNTIME retention) is present on this fixture class, but it is not @Test,
        // so it should be filtered out by the annotationType().equals(Test.class) check.
        List<String> tags = listener.extractTagsFromAnnotations(AnnotatedFakeIClass.class.getAnnotations());

        // AnnotatedFakeIClass carries @Test(groups={"classGroup"}) and @Deprecated: only the
        // @Test one should contribute a tag.
        assertEquals(tags, Collections.singletonList("classGroup"));
    }

    @org.testng.annotations.Test
    public void testExtractTagsFromAnnotations_nullValuesAndEmptyGroupElements_areSkipped() {
        ReportiumTestNgListener listener = new ReportiumTestNgListener();

        org.testng.annotations.Test proxyAnnotation =
                nullValuedTestAnnotationProxy(new String[]{null, "", "g3"});

        List<String> tags = listener.extractTagsFromAnnotations(new Annotation[]{proxyAnnotation});

        assertEquals(tags, Collections.singletonList("g3"));
    }

    // ---------------------------------------------------------------------------------------
    // getTags(ITestResult)
    // ---------------------------------------------------------------------------------------

    @org.testng.annotations.Test
    public void testGetTags_classAndMethodAnnotations_combined() throws Exception {
        ReportiumTestNgListener listener = new ReportiumTestNgListener();

        IClass fakeIClass = new AnnotatedFakeIClass();

        Method fixtureMethod = getFixtureMethod("annotatedFixtureMethod");
        ConstructorOrMethod constructorOrMethod = new ConstructorOrMethod(fixtureMethod);

        ITestNGMethod testngMethod = createMock(ITestNGMethod.class);
        expect(testngMethod.getConstructorOrMethod()).andReturn(constructorOrMethod);
        replay(testngMethod);

        ITestResult testResult = createMock(ITestResult.class);
        expect(testResult.getTestClass()).andReturn(fakeIClass);
        expect(testResult.getMethod()).andReturn(testngMethod);
        replay(testResult);

        String[] tags = listener.getTags(testResult);

        assertEquals(tags,
                new String[]{"classGroup", "MySuite", "MyTestName", "MyDescription", "methodGroup1", "methodGroup2"});

        verify(testResult, testngMethod);
    }

    @org.testng.annotations.Test
    public void testGetTags_noClassAnnotationsAndNoMethod_yieldsEmptyArray() throws Exception {
        ReportiumTestNgListener listener = new ReportiumTestNgListener();

        IClass fakeIClass = new PlainFakeIClass();

        // A ConstructorOrMethod built from a Constructor makes getMethod() return null,
        // exercising the "method != null" false branch in getTags().
        ConstructorOrMethod constructorOrMethod =
                new ConstructorOrMethod(PlainFakeIClass.class.getDeclaredConstructor());

        ITestNGMethod testngMethod = createMock(ITestNGMethod.class);
        expect(testngMethod.getConstructorOrMethod()).andReturn(constructorOrMethod);
        replay(testngMethod);

        ITestResult testResult = createMock(ITestResult.class);
        expect(testResult.getTestClass()).andReturn(fakeIClass);
        expect(testResult.getMethod()).andReturn(testngMethod);
        replay(testResult);

        String[] tags = listener.getTags(testResult);

        assertEquals(tags, new String[0]);

        verify(testResult, testngMethod);
    }
}
