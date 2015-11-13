package itmolabs.secondlab;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;


public interface IterativeParallelism {
    public <T> T minimum(int threads, List<T> list, Comparator<T> comparator);
    public <T> T maximum(int threads, List<T> list, Comparator<T> comparator);
    public <T> boolean all(int threads, List<T> list, Predicate<T> predicate);
    public <T> boolean any(int threads, List<T> list, Predicate<T> predicate);
    public <T> List<T> filter(int threads, List<T> list, Predicate<T> predicate);
    public <T,R> List<R> map(int threads, List<T> list, Function<T, R> function);
}

