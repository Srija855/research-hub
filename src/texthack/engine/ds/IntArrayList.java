package texthack.engine.ds;

/**
 * Hand-built primitive int dynamic array.
 * Fast, unboxed, zero java.util.* usage.
 */
public class IntArrayList {
    private int[] data;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;

    public IntArrayList() {
        this(DEFAULT_CAPACITY);
    }

    public IntArrayList(int initialCapacity) {
        if (initialCapacity <= 0) initialCapacity = DEFAULT_CAPACITY;
        this.data = new int[initialCapacity];
        this.size = 0;
    }

    public void add(int val) {
        if (size == data.length) {
            int newCap = data.length * 2;
            int[] next = new int[newCap];
            System.arraycopy(data, 0, next, 0, size);
            data = next;
        }
        data[size++] = val;
    }

    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return data[index];
    }

    public void set(int index, int val) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        data[index] = val;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        size = 0;
    }

    public int[] toArray() {
        int[] res = new int[size];
        System.arraycopy(data, 0, res, 0, size);
        return res;
    }
}
