package texthack.engine.ds;

/**
 * Hand-built string builder.
 * Zero java.util.* usage.
 */
public class CustomStringBuilder {
    private char[] buffer;
    private int length;

    public CustomStringBuilder(int capacity) {
        this.buffer = new char[capacity > 0 ? capacity : 32];
        this.length = 0;
    }

    public CustomStringBuilder() {
        this(32);
    }

    public CustomStringBuilder append(String str) {
        if (str == null) str = "null";
        int len = str.length();
        ensureCapacity(length + len);
        str.getChars(0, len, buffer, length);
        length += len;
        return this;
    }

    public CustomStringBuilder append(char c) {
        ensureCapacity(length + 1);
        buffer[length++] = c;
        return this;
    }

    public CustomStringBuilder append(int i) {
        return append(Integer.toString(i));
    }

    public CustomStringBuilder append(long l) {
        return append(Long.toString(l));
    }

    public CustomStringBuilder append(double d) {
        return append(Double.toString(d));
    }

    public CustomStringBuilder append(boolean b) {
        return append(b ? "true" : "false");
    }

    public CustomStringBuilder append(Object obj) {
        return append(obj == null ? "null" : obj.toString());
    }

    public int length() {
        return length;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > buffer.length) {
            int newCap = buffer.length * 2;
            if (newCap < minCapacity) newCap = minCapacity;
            char[] next = new char[newCap];
            System.arraycopy(buffer, 0, next, 0, length);
            buffer = next;
        }
    }

    @Override
    public String toString() {
        return new String(buffer, 0, length);
    }
}
