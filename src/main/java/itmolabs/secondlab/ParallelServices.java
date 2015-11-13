package itmolabs.secondlab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class ParallelServices implements IterativeParallelism {

    @Override
    public <T> T minimum(int threads, final List<T> list, Comparator<T> comparator) {
        final List<T> minimums = new ArrayList<>();
        final List<Boolean> finishedThreads = new ArrayList<>();

        IntStream.range(0, threads).forEach(threadNumber -> {
            minimums.add(null);
            finishedThreads.add(false);

            int fromIndex = threadNumber * list.size() / threads;
            int toIndex = ((threadNumber + 1) * list.size() / threads);
            List<T> portion = list.subList(fromIndex, toIndex);

            new Thread(() -> {
                T min = Collections.min(portion, comparator);
                minimums.set(threadNumber, min);
                finishedThreads.set(threadNumber, true);
            }).start();
        });

        while (!finishedThreads.stream().allMatch(b -> b.equals(true))) {
            Thread.yield();
        }
        return Collections.min(minimums, comparator);
    }

    @Override
    public <T> T maximum(int threads, final List<T> list, Comparator<T> comparator) {
        return minimum(threads, list, comparator.reversed());
    }

    @Override
    public <T> boolean all(int threads, List<T> list, Predicate<T> predicate) {
        return check(threads, list, predicate.negate(), true, false);
    }

    @Override
    public <T> boolean any(int threads, List<T> list, Predicate<T> predicate) {
        return check(threads, list, predicate, false, true);
    }

    private <T> boolean check(int threads, List<T> list, Predicate<T> predicate, boolean initValue, boolean exitValue) {
        final List<Boolean> finishedThreads = new ArrayList<>();
        final List<Boolean> checkResults = new ArrayList<>();
        IntStream.range(0, threads).forEach(threadNumber -> {
            finishedThreads.add(false);
            checkResults.add(initValue);
            int fromIndex = threadNumber * list.size() / threads;
            int toIndex = ((threadNumber + 1) * list.size() / threads);
            List<T> portion = list.subList(fromIndex, toIndex);
            new Thread(() -> {
                for (T t : portion) {
                    if (predicate.test(t)) {
                        checkResults.set(threadNumber, exitValue);
                        break;
                    }
                }
                finishedThreads.set(threadNumber, true);
            }).start();
        });
        boolean check = checkResults.stream().anyMatch(b -> b.equals(exitValue));

        while (!finishedThreads.stream().allMatch(b -> b.equals(true)) && !check) {
            check = checkResults.stream().anyMatch(b -> b.equals(exitValue));
            Thread.yield();
        }
        return !check ? initValue : exitValue;
    }


    @Override
    public <T> List<T> filter(int threads, List<T> list, Predicate<T> predicate) {
        final List<List<T>> values = new ArrayList<>();
        final List<Boolean> finishedThreads = new ArrayList<>();

        IntStream.range(0, threads).forEach(threadNumber -> {
            int fromIndex = threadNumber * list.size() / threads;
            int toIndex = ((threadNumber + 1) * list.size() / threads);
            List<T> portion = list.subList(fromIndex, toIndex);
            List<T> portionResult = new ArrayList<>();

            values.add(portionResult);
            finishedThreads.add(false);
            final int finalThreadNumber = threadNumber;

            new Thread(() -> {
                portion.forEach(t -> {
                    if (predicate.test(t)) portionResult.add(t);
                });
                finishedThreads.set(finalThreadNumber, true);
            }).start();
        });
        while (!finishedThreads.stream().allMatch(b -> b.equals(true))) {
            Thread.yield();
        }
        List<T> filteredList = new ArrayList<>();
        values.forEach(filteredList::addAll);
        return filteredList;
    }

    @Override
    public <T, R> List<R> map(int threads, List<T> list, Function<T, R> function) {
        final List<List<R>> values = new ArrayList<>();
        final List<Boolean> finishedThreads = new ArrayList<>();

        IntStream.range(0, threads).forEach(threadNumber -> {
            int fromIndex = threadNumber * list.size() / threads;
            int toIndex = ((threadNumber + 1) * list.size() / threads);
            List<T> portion = list.subList(fromIndex, toIndex);
            List<R> portionResult = new ArrayList<>();
            values.add(portionResult);
            finishedThreads.add(false);
            final int finalThreadNumber = threadNumber;
            new Thread(() -> {
                portion.forEach(t -> portionResult.add(function.apply(t)));
                finishedThreads.set(finalThreadNumber, true);
            }).start();
        });
        while (!finishedThreads.stream().allMatch(b -> b.equals(true))) {
            Thread.yield();
        }
        List<R> mappedList = new ArrayList<>(list.size());
        values.forEach(mappedList::addAll);
        return mappedList;
    }


}
