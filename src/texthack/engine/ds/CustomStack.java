package texthack.engine.ds;

/**
 * Hand-built LIFO Stack.
 * Zero java.util.* usage.
 *
 * @param <T> Element type
 */
public class CustomStack<T> {
    private Object[] data;
    private int top;

    public CustomStack(int capacity) {
        if (capacity <= 0) capacity = 16;
        this.data = new Object[capacity];
        this.top = -1;
    }

    public CustomStack() {
        this(16);
    }

    public void push(T item) {
        if (top + 1 == data.length) {
            resize();
        }
        data[++top] = item;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (top == -1) return null;
        T item = (T) data[top];
        data[top--] = null;
        return item;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (top == -1) return null;
        return (T) data[top];
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public int size() {
        return top + 1;
    }

    private void resize() {
        Object[] next = new Object[data.length * 2];
        System.arraycopy(data, 0, next, 0, data.length);
        data = next;
    }
}
