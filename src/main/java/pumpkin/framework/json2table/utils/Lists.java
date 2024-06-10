package pumpkin.framework.json2table.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class Lists {
    private static final long INT_MAX_AS_LONG = 2147483647L;
    private static final long COMPUTE_ARRAY_LIST_CAPACITY_CONST_1 = 5L;
    private static final int COMPUTE_ARRAY_LIST_CAPACITY_CONST_2 = 10;

    public static <E> ArrayList<E> arrayList(final int estimatedSize) {
        return new ArrayList(computeArrayListCapacity(estimatedSize));
    }

    public static <T> Optional<T> first(final List<T> list) {
        if (isNullOrEmpty(list)) {
            return Optional.empty();
        }

        return Optional.ofNullable(list.get(0));
    }

    public static <T> boolean isNullOrEmpty(final Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T, R> List<R> mapReduce(final Collection<T> list, final Function<T, List<R>> mapper) {
        if (isNullOrEmpty(list)) {
            return Collections.emptyList();
        }

        return list.stream().map(mapper).reduce(new ArrayList<>(), Lists::concatForStream);
    }

    public static <T> T safeGet(final List<T> list, final int index) {
        if (index < 0 || index >= list.size()) {
            return null;
        }

        return list.get(index);
    }

    public static <T> List<T> toReversed(final List<T> list) {
        List<T> toBeReversed = new ArrayList<>(list);
        Collections.reverse(toBeReversed);

        return toBeReversed;
    }

    private static int computeArrayListCapacity(final int arraySize) {
        return saturatedCast(
                COMPUTE_ARRAY_LIST_CAPACITY_CONST_1
                        + (long) arraySize
                        + (long) (arraySize / COMPUTE_ARRAY_LIST_CAPACITY_CONST_2)
        );
    }

    private static <T> List<T> concatForStream(final List<T> list1, final List<T> list2) {
        if (list1 == null) {
            if (list2 == null) {
                return new ArrayList<>();
            } else {
                return new ArrayList<>(list2);
            }
        }

        if (!Lists.isNullOrEmpty(list2)) {
            list1.addAll(list2);
        }

        return list1;
    }

    private static int saturatedCast(final long value) {
        if (value > INT_MAX_AS_LONG) {
            return Integer.MAX_VALUE;
        } else {
            return (int) value;
        }
    }
}
