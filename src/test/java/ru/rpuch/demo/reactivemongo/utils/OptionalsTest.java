package ru.rpuch.demo.reactivemongo.utils;

import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author rpuch
 */
public class OptionalsTest {
    @Test
    public void givenBothAreEmpty_whenMerge_thenResultIsEmpty() {
        Optional<String> result = Optionals.merge(Optional.<String>empty(), Optional.empty(), (s1, s2) -> s1 + s2);
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void givenFirstIsNonEmptyAndSecondIsEmpty_whenMerge_thenResultIsEmpty() {
        Optional<String> result = Optionals.merge(Optional.of("test"), Optional.empty(), (s1, s2) -> s1 + s2);
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void givenFirstIsEmptyAndSecondIsNotEmpty_whenMerge_thenResultIsEmpty() {
        Optional<String> result = Optionals.merge(Optional.empty(), Optional.of("test"), (s1, s2) -> s1 + s2);
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void givenBothAreNotEmpty_whenMerge_thenResultIsNotEmpty() {
        Optional<String> result = Optionals.merge(Optional.of("one"), Optional.of("two"), (s1, s2) -> s1 + s2);
        assertThat(result.isPresent(), is(true));
        assertThat(result.orElse("no idea"), is("onetwo"));
    }
}