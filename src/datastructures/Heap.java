package datastructures;

import java.util.*;

/**
 * An implementation of a binary heap. An instance is a max-heap or a
 * min-heap of distinct values of type T with priorities of type double.
 */
public class Heap<T> implements PQueue<T> {
    /**
     * Class Invariant:<p>
     * 1. b[0..size-1] represents a complete binary
     * tree. b[0] is the root; For k > 0, (k-1)/2 (using int division)
     * is the index in b of the parent of b[k] For k >= 0, 2k+1 and 2k+2
     * are the indexes in b of left and right children of b[k].
     * <p>
     * 2. For k in 0..size-1, b[k] contains the value and its priority.
     * <p>
     * 3. The values in b[0..size-1] are all different.
     * <p>
     * 4. For k in 1..size-1,  .. if isMinHeap, (b[k]'s priority) >= (b[k]'s parent's
     * priority), .. if !isMinHeap, (b[k]'s priority) <= (b[k]'s parent's priority).
     * <p>
     * map and the tree are in sync, meaning:
     * <p>
     * 5. The keys of map are the values in b[0..size-1]. This implies that size = map.size().
     * <p>
     * 6. if value v is in b[k], then map.get(v) = k.
     */
    protected final boolean isMinHeap;
    protected Pair[] b;
    protected int size;
    protected HashMap<T, Integer> map;

    /**
     * Constructor: an empty heap with capacity 10.  It is a min-heap if
     * isMin is true and a max-heap if isMin is false.
     */
    @SuppressWarnings("unchecked")
    public Heap(boolean isMin) {
        isMinHeap = isMin;
        b = new Heap.Pair[10];
        map = new HashMap<>();
    }

    /**
     * If size = length of b, double the length of array b.  The
     * worst-case time is proportional to the length of b.
     */
    protected void ensureSpace() {
        // TODO #1. We ask you to write this method because it shows you
        // how class ArrayList can "increase" the size of its backing array.
        // When its capacity is reached (its backing array is filled), it creates
        // an array of twice the size, copies the old array into it, and
        // uses the new array from then on.

        // Any method that increases the size of the heap must call
        // this method FIRST, BEFORE increasing the size.

        // After this method is written, this testing procedure should work.
        // test01EnsureSpace1. Please read the hint in that method to learn how
        // we can test this method without calling method insert, which is below.

        // The body is most easily written using a method in Collections Framework
        // class Arrays. Look for methods that copy arrays and choose a suitable one.
    }

    /**
     * Insert v with priority p to the heap.  Throw an
     * IllegalArgumentException if v is already in the heap. The
     * expected time is logarithmic and the worst-case time is linear in
     * the size of the heap.
     */
    public void add(T v, double p) throws IllegalArgumentException {
        // TODO #2: Write this whole method. Note that bubbleUp is not implemented,
        // so calling it has no effect (yet). The first tests of insert, using
        // test10Insert, ensures that this method maintains fields c and map properly,
        // without worrying about bubbling up.

        // Testing procedures test10Insert, test14InsertAndEnsureSpace, and
        // test16InsertAndEnsureSpace should work. Look at their specifications.

        // Do NOT call bubbleUp until the class invariant is true
        // (except for the need to bubble up).
        // Calling bubbleUp is the last thing to be done.
    }

    /**
     * Return the size of this heap.  This operation takes constant time.
     */
    public int size() { // Do not change this method
        return size;
    }

    public boolean isEmpty() { return size == 0; }

    /**
     * Swap b[h] and b[k].
     * Requires: 0 <= h < heap-size, 0 <= k < heap-size.
     */
    void swap(int h, int k) {
        assert 0 <= h && h < size && 0 <= k && k < size;
        // TODO 3: When bubbling values up or down, two values,
        // say b[h] and b[k], will have to be swapped. At the same time,
        // the definition of map has to be maintained.  In order to
        // always get this right, use this method swap to do this.

        // When method swap is correct, testing procedure test20Swap
        // will find no errors.
        //
        // Read the Assignment A5 note about map.put(...).
    }

    /**
     * If a value with priority p1 belongs above a value with priority
     * p2 in the heap, return 1. If priority p1 and priority p2 are the
     * same, return 0.  If a value with priority p1 should be below a
     * value with priority p2 in the heap, return -1. This is based on
     * what kind of a heap this is; e.g., in a min-heap, the value with
     * the smallest priority is in the root. In a max-heap, the
     * value with the largest priority is in the root.
     */
    public int compareTo(double p1, double p2) {
        if (p1 == p2) return 0;
        return (isMinHeap == (p1 < p2)) ? 1 : -1;
    }

    /**
     * If b[h] should be above b[k] in the heap, return 1.  If b[h]'s
     * priority and b[k]'s priority are the same, return 0.  If b[h]
     * should be below b[k] in the heap, return -1.  This is based on
     * what kind of a heap this is, ... E.g. a min-heap, the value with
     * the smallest priority is in the root. ... E.g. a max-heap, the
     * value with the largest priority is in the root.
     */
    public int compareTo(int h, int k) {
        return compareTo(b[h].priority, b[k].priority);
    }

    /**
     * If h >= size, return. Otherwise, bubble b[h] up the heap to its
     * right place.  Precondition: 0 <= h and, if h < size, the class
     * invariant is true, except perhaps that b[h] belongs above its
     * parent (if h > 0) in the heap.
     */
    void bubbleUp(int h) {
        // TODO #4 This method should be called within insert in order
        // to bubble a value up to its proper place, based on its priority.

        // Do not use recursion. Use iteration.

        // Use the compareTo methods to test whether value h is in its right place.
        // That way, YOU don't have to worry about whether it is a min- or max-heap!

        // If this method is correct, these testing procedures should not find errors:
        // test30InsertMinHeap_BubbleUp,
        // test32InsertMaxHeap_BubbleUp,
        // test34InsertMin_BubbleUpDuplicatePriorities,
        // test36InsertMax_BubbleUpDuplicatePriorities
    }

    /**
     * If this is a min-heap, return the heap value with lowest
     * priority. If this is a max-heap, return the heap value with
     * highest priority. Do not change the heap. This operation takes
     * constant time.  Throw a NoSuchElementException if the heap is
     * empty.
     */
    public T peek() {
        // TODO 5: Do peek. This is an easy one.

        // If this method is correct, these testing procs will show no errors:
        // test40MinPeek and test42MaxPeek
    }

    /**
     * If this is a min-heap, return the lowest priority.  If this
     * is a max-heap, return the highest priority. Do not change
     * the heap.  This operation takes constant time.  Throw a
     * NoSuchElementException if the heap is empty.
     */
    public double peekAtPriority() {
        if (size <= 0) {
            throw new NoSuchElementException("heap is empty");
        }
        return b[0].priority;
    }

    /**
     * If h < 0 or size <= h, return. Otherwise, Bubble b[h] down in
     * heap until the class invariant is true.  If there is a choice to
     * bubble down to both the left and right children (because their
     * priorities are equal), choose the left child.
     *
     * Requires: If 0 <= h < size, the class invariant is true except
     * that perhaps b[h] belongs below one or both of its children.
     */
    void bubbleDown(int h) {
        // TODO 6: DO NOT USE RECURSION. Use iteration.
        // When this method is correct, these testing procedures will find no errors:
        // test50MinBubbleDown thru test54MinBubble_DownRight
        // test56MaxBubbleDown thru test57MaxBubbleDownRight
    }

    /**
     * Return an array of all values in the heap
     */
    public T[] values() {
        T[] vals = (T[]) new Object[size];
        // System.out.println("New vals array: " + vals.length);
        for (int k = 0; k < size; k = k + 1) {
            vals[k] = b[k].value;
        }
        return vals;
    }

    /**
     * If this is a min-heap, remove and return heap value with
     * lowest priority.  If this is a max-heap, remove and
     * return heap value with highest priority.
     * Throw a NoSuchElementException if the heap is empty.
     * Expected time: logarithmic.
     * Worst-case time: linear in the size of the
     * heap.
     */
    public T extractMin() {
        // TODO 7: When this method correct, these testing procedure will find no errors:
        // test60Poll_BubbleDown_NoDups thru
        // test66testDuplicatePriorities
        return null;
    }

    /**
     * Change the priority of value v to p.
     * Throw an IllegalArgumentException if v is not in the heap.
     * Expected time: logarithmic.
     * Worst-case time: linear in the size of the heap.
     */
    public void changePriority(T v, double p) {
        // TODO 8: When this method is correct, all testing procedures
        // will find no errors.
    }

    /**
     * Return the heap values (only, not the priorities) in a syntax like
     * [5, 3, 2].
     */
    public String toStringValues() {
        StringBuilder resb = new StringBuilder("[");
        for (int h = 0; h < size; h = h + 1) {
            if (h > 0) {
                resb.append(", ");
            }
            resb.append(b[h].value);
        }
        return resb.append(']').toString();
    }

    /**
     * Return the heap priorities in the syntax [5.0, 3.0, 2.0].
     */
    public String toStringPriorities() {
        StringBuilder resb = new StringBuilder("[");
        for (int h = 0; h < size; h = h + 1) {
            if (h > 0) {
                resb.append(", ");
            }
            resb.append(b[h].priority);
        }
        return resb.append(']').toString();
    }

    /**
     * An object of class Pair houses a value and a priority.
     */
    class Pair {

        protected T value;             // The value
        protected double priority;   // The priority

        /**
         * An instance with value v and priority p.
         */
        protected Pair(T v, double p) {
            value = v;
            priority = p;
        }

        /**
         * Return a representation of this object.
         */
        @Override
        public String toString() {
            return "(" + value + ", " + priority + ")";
        }

        /**
         * = "this and ob are of the same class and have equal val and priority fields."
         */
        @Override
        public boolean equals(Object ob) {
            if (ob == null || getClass() != ob.getClass()) {
                return false;
            }
            Pair obe = (Pair) ob;
            return value == obe.value && priority == obe.priority;
        }
    }
}
