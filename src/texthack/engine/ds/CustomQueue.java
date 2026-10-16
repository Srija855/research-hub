package texthack.engine.ds;

/**
 * Hand-built generic circular array queue.
 * Zero java.util.* usage.
 *
 * @param <T> Element type
 */
public class CustomQueue<T> {
    private Object[] data;
    private int head;
    private int tail;
    private int count;

    public CustomQueue(int capacity) {
        if (capacity <= 0) capacity = 16;
        this.data = new Object[capacity];
        this.head = 0;
        this.tail = 0;
        this.count = 0;
    }

    public CustomQueue() {
        this(16);
    }

    public void enqueue(T item) {
        if (count == data.length) {
            resize();
        }
        data[tail] = item;
        tail = (tail + 1) % data.length;
        count++;
    }

    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (count == 0) return null;
        T item = (T) data[head];
        data[head] = null;
        head = (head + 1) % data.length;
        count--;
        return item;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (count == 0) return null;
        return (T) data[head];
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public int size() {
        return count;
    }

    private void resize() {
        Object[] next = new Object[data.length * 2];
        for (int i = 0; i < count; i++) {
            next[i] = data[(head + i) % data.length];
        }
        data = next;
        head = 0;
        tail = count;
    }
}
