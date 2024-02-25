package test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class RegressionSuite {
    public static void main(String... args) {
        new RegressionSuite().run();
    }

    public void run() {
        long startSuiteMillis = System.currentTimeMillis();
        Set<Class> testClasses = new HashSet<>();
        testClasses.addAll(findAllClassesUsingClassLoader("aoc21"));
        testClasses.addAll(findAllClassesUsingClassLoader("aoc22"));
        testClasses.addAll(findAllClassesUsingClassLoader("aoc23"));

        final Set<String> excludedClasses = Set.of("aoc22.Day16Part2"); //takes too long to run

        testClasses.stream()
                .filter(c -> {
                    Method[] publicMethods = c.getMethods();
                    return Arrays.stream(publicMethods).anyMatch(m -> m.getName().equals("main"));
                })
                .filter(c -> !excludedClasses.contains(c.getName()))
                .sorted((Comparator.comparing(Class::getName)))
                .toList()
                .forEach(RegressionSuite::runDayAndPart);

        System.out.printf("Suite completed in %s millis..%n", System.currentTimeMillis() - startSuiteMillis);
    }

    public Set<Class> findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
    }

    private Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void runDayAndPart(Class c) {
        long start = System.currentTimeMillis();
        System.out.printf("About to run %s..%n", c.getName());
        try {
            Method mainMethod = c.getMethod("main", String[].class);
            mainMethod.invoke(null, new Object[]{new String[]{}});
            System.out.println(".........................................................");
            System.out.printf("%s completed successfully in %s millis..%n",
                    c.getName(), System.currentTimeMillis() - start);
        } catch (Exception e) {
            System.err.printf("Unable to run main method for class %s, exception=%s%n", c.getName(), e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        System.out.println("=========================================================");
    }
}
