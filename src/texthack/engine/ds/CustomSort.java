package texthack.engine.ds;

/**
 * Hand-built Sorting algorithms (MergeSort and QuickSort).
 * Zero java.util.* usage.
 */
public class CustomSort {

    public static <T> void mergeSort(T[] a, CustomComparator<T> comp) {
        if (a == null || a.length <= 1) return;
        @SuppressWarnings("unchecked")
        T[] aux = (T[]) new Object[a.length];
        mergeSortInternal(a, aux, 0, a.length - 1, comp);
    }

    private static <T> void mergeSortInternal(T[] a, T[] aux, int low, int high, CustomComparator<T> comp) {
        if (low >= high) return;
        int mid = low + (high - low) / 2;
        mergeSortInternal(a, aux, low, mid, comp);
        mergeSortInternal(a, aux, mid + 1, high, comp);
        merge(a, aux, low, mid, high, comp);
    }

    private static <T> void merge(T[] a, T[] aux, int low, int mid, int high, CustomComparator<T> comp) {
        for (int k = low; k <= high; k++) {
            aux[k] = a[k];
        }
        int i = low;
        int j = mid + 1;
        for (int k = low; k <= high; k++) {
            if (i > mid) {
                a[k] = aux[j++];
            } else if (j > high) {
                a[k] = aux[i++];
            } else if (comp.compare(aux[j], aux[i]) < 0) {
                a[k] = aux[j++];
            } else {
                a[k] = aux[i++];
            }
        }
    }

    public static void sortInts(int[] a) {
        if (a == null || a.length <= 1) return;
        quickSortInts(a, 0, a.length - 1);
    }

    private static void quickSortInts(int[] a, int low, int high) {
        if (low < high) {
            int p = partition(a, low, high);
            quickSortInts(a, low, p - 1);
            quickSortInts(a, p + 1, high);
        }
    }

    private static int partition(int[] a, int low, int high) {
        int pivot = a[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (a[j] <= pivot) {
                i++;
                int temp = a[i];
                a[i] = a[j];
                a[j] = temp;
            }
        }
        int temp = a[i + 1];
        a[i + 1] = a[high];
        a[high] = temp;
        return i + 1;
    }
}
