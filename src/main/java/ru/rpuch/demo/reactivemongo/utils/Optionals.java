package ru.rpuch.demo.reactivemongo.utils;

import java.util.Optional;
import java.util.function.BiFunction;

/**
 * @author rpuch
 */
public class Optionals {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T, U, V> Optional<V> merge(Optional<T> first, Optional<U> second, BiFunction<T, U, V> combiner) {
        return first.flatMap(firstValue
                -> second.map(secondValue -> combiner.apply(firstValue, secondValue))
        );
    }
}
