package texthack.engine.ds;

/**
 * Hand-built Comparator interface.
 * Zero java.util.* usage.
 */
public interface CustomComparator<T> {
    int compare(T a, T b);
}
