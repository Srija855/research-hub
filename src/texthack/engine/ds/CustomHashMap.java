package texthack.engine.ds;

/**
 * Hand-built Hash Map using separate chaining with singly-linked buckets.
 * Zero java.util.* usage.
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class CustomHashMap<K, V> {
    public static class Entry<K, V> {
        public final K key;
        public V value;
        public Entry<K, V> next;

        public Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Entry<K, V>[] table;
    private int size;
    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    @SuppressWarnings("unchecked")
    public CustomHashMap(int capacity) {
        table = (Entry<K, V>[]) new Entry[capacity > 0 ? capacity : INITIAL_CAPACITY];
        size = 0;
    }

    public CustomHashMap() {
        this(INITIAL_CAPACITY);
    }

    private int hash(K key) {
        if (key == null) return 0;
        int h = key.hashCode();
        return (h ^ (h >>> 16)) & 0x7fffffff;
    }

    public V put(K key, V value) {
        if (size >= table.length * LOAD_FACTOR) {
            resize();
        }
        int index = hash(key) % table.length;
        Entry<K, V> curr = table[index];
        while (curr != null) {
            if (curr.key == key || (curr.key != null && curr.key.equals(key))) {
                V old = curr.value;
                curr.value = value;
                return old;
            }
            curr = curr.next;
        }
        table[index] = new Entry<>(key, value, table[index]);
        size++;
        return null;
    }

    public V get(K key) {
        int index = hash(key) % table.length;
        Entry<K, V> curr = table[index];
        while (curr != null) {
            if (curr.key == key || (curr.key != null && curr.key.equals(key))) {
                return curr.value;
            }
            curr = curr.next;
        }
        return null;
    }

    public boolean containsKey(K key) {
        int index = hash(key) % table.length;
        Entry<K, V> curr = table[index];
        while (curr != null) {
            if (curr.key == key || (curr.key != null && curr.key.equals(key))) {
                return true;
            }
            curr = curr.next;
        }
        return false;
    }

    public V remove(K key) {
        int index = hash(key) % table.length;
        Entry<K, V> curr = table[index];
        Entry<K, V> prev = null;
        while (curr != null) {
            if (curr.key == key || (curr.key != null && curr.key.equals(key))) {
                if (prev == null) {
                    table[index] = curr.next;
                } else {
                    prev.next = curr.next;
                }
                size--;
                return curr.value;
            }
            prev = curr;
            curr = curr.next;
        }
        return null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public CustomArrayList<K> keys() {
        CustomArrayList<K> keyList = new CustomArrayList<>(size);
        for (int i = 0; i < table.length; i++) {
            Entry<K, V> curr = table[i];
            while (curr != null) {
                keyList.add(curr.key);
                curr = curr.next;
            }
        }
        return keyList;
    }

    public CustomArrayList<Entry<K, V>> entries() {
        CustomArrayList<Entry<K, V>> list = new CustomArrayList<>(size);
        for (int i = 0; i < table.length; i++) {
            Entry<K, V> curr = table[i];
            while (curr != null) {
                list.add(curr);
                curr = curr.next;
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K, V>[] oldTable = table;
        int newCap = oldTable.length * 2;
        table = (Entry<K, V>[]) new Entry[newCap];
        size = 0;
        for (int i = 0; i < oldTable.length; i++) {
            Entry<K, V> curr = oldTable[i];
            while (curr != null) {
                put(curr.key, curr.value);
                curr = curr.next;
            }
        }
    }
}
