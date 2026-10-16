package texthack.engine.ds;

/**
 * Hand-built Min-Heap / Priority Queue.
 * Zero java.util.* usage.
 *
 * @param <T> Element type
 */
public class CustomMinHeap<T> {
    private Object[] heap;
    private int size;
    private CustomComparator<T> comparator;
    private static final int DEFAULT_CAPACITY = 16;

    @SuppressWarnings("unchecked")
    public CustomMinHeap(int capacity, CustomComparator<T> comparator) {
        this.heap = new Object[capacity <= 0 ? DEFAULT_CAPACITY : capacity];
        this.size = 0;
        this.comparator = comparator;
    }

    public CustomMinHeap(CustomComparator<T> comparator) {
        this(DEFAULT_CAPACITY, comparator);
    }

    public void insert(T val) {
        if (size == heap.length) {
            resize();
        }
        heap[size] = val;
        siftUp(size);
        size++;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (size == 0) return null;
        return (T) heap[0];
    }

    @SuppressWarnings("unchecked")
    public T extractMin() {
        if (size == 0) return null;
        T root = (T) heap[0];
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;
        if (size > 0) {
            siftDown(0);
        }
        return root;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @SuppressWarnings("unchecked")
    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (compare((T) heap[index], (T) heap[parent]) < 0) {
                swap(index, parent);
                index = parent;
            } else {
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void siftDown(int index) {
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;

            if (left < size && compare((T) heap[left], (T) heap[smallest]) < 0) {
                smallest = left;
            }
            if (right < size && compare((T) heap[right], (T) heap[smallest]) < 0) {
                smallest = right;
            }
            if (smallest != index) {
                swap(index, smallest);
                index = smallest;
            } else {
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private int compare(T a, T b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        }
        return ((Comparable<T>) a).compareTo(b);
    }

    private void swap(int i, int j) {
        Object tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }

    private void resize() {
        Object[] next = new Object[heap.length * 2];
        System.arraycopy(heap, 0, next, 0, heap.length);
        heap = next;
    }
}
