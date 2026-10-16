package texthack.engine.ds;

/**
 * Hand-built high-performance primitive integer circular queue.
 * Zero java.util.* usage.
 */
public class IntQueue {
    private int[] data;
    private int head;
    private int tail;
    private int count;

    public IntQueue(int capacity) {
        if (capacity <= 0) capacity = 16;
        this.data = new int[capacity];
        this.head = 0;
        this.tail = 0;
        this.count = 0;
    }

    public IntQueue() {
        this(16);
    }

    public void enqueue(int item) {
        if (count == data.length) {
            resize();
        }
        data[tail] = item;
        tail = (tail + 1) % data.length;
        count++;
    }

    public int dequeue() {
        if (count == 0) throw new IllegalStateException("Queue is empty");
        int item = data[head];
        head = (head + 1) % data.length;
        count--;
        return item;
    }

    public int peek() {
        if (count == 0) throw new IllegalStateException("Queue is empty");
        return data[head];
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public int size() {
        return count;
    }

    public void clear() {
        head = 0;
        tail = 0;
        count = 0;
    }

    private void resize() {
        int[] next = new int[data.length * 2];
        for (int i = 0; i < count; i++) {
            next[i] = data[(head + i) % data.length];
        }
        data = next;
        head = 0;
        tail = count;
    }
}
