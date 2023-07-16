package datastructures;

import game.NodeStatus;

public class InsertionSort {

	/** Sort array b. */
	public static void sort(NodeStatus[] b) {

		// invariant: b[0..i-1 is sorted
		for (int i= 1; i < b.length; i++ ) {
			// Push b[i] down to its sorted place in b[0..i

			NodeStatus tmp= b[i];
			int j;
			// invariant is: x[0..i] is sorted except that b[j] may be out of place
			for (j= i; j > 0 && b[j - 1].getDistanceToRing() >= b[j].getDistanceToRing(); j= j - 1)
				b[j]= b[j - 1];
			b[j]= tmp;
		}
	}
}
