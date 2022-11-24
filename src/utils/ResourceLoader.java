package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceLoader {
    public static List<String> readStrings(String resourceName) throws IOException {
        List<String> list = new ArrayList<>();

        try (InputStream is = ResourceLoader.class.getClassLoader().getResourceAsStream(resourceName);
             InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
        }

        return list;
    }

    public static List<Integer> readInts(String resourceName) throws IOException {
        return readStrings(resourceName)
                .stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
