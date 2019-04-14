package ru.rpuch.demo.reactivemongo.web;

import org.apache.commons.compress.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author rpuch
 */
class TestResources {
    static String resourceAsString(String resourcePath, Class<?> baseClass) {
        try (InputStream stream = baseClass.getResourceAsStream(resourcePath)) {
            Objects.requireNonNull(stream, String.format("The resource is not found: '%s'", resourcePath));
            byte[] bytes = IOUtils.toByteArray(stream);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read a resource", e);
        }
    }
}
