package diver;


import game.NodeStatus;
import game.SeekState;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

/** This class contains only an optimized version of a dfsWalk. <br>
 * It is optimized in two ways.<br>
 * (1) As usual, neighbors are sorted acc. to shortest distance from the <br>
 * ring before processing them. This greedy choice doesn't always work.<br>
 * (2) It is an iterative dfs walk, with a stack. */
public class McDiverIterativeSolutionFile {
    // used to simplify getting an array from a Collection.
    private NodeStatus[] nsa= new NodeStatus[0];

    SeekState mcNode; // the state used to describe McDiver

    private HashSet<Long> visited= new HashSet<>(); // set of ids of visited nodes

    // The stack of Nodes as in a recursive dfs, exactly.
    // Along with a node is
    // (1) an array of its neighbors and
    // (2) The number of neighbors that have been processed
    private Stack<SEntry> stack= new Stack<>();

    // For each entry in stack, the key is its id, the value
    // its index in stack
    private HashMap<Long, Integer> stackEntries= new HashMap<>();

    private int numMoves; // number of moves in the dfs walk
    private int numRemoved; // number of stack items removed

    /** McDiver is standing on the node given by McDiversNode.<br>
     * McDiversNode's id is not in visited. <br>
     * Visit all nodes that are reachable along a path of unvisited nodes until<br>
     * the ring is found. In that case, return with McDiversNode standing on the ring's node.<br>
     * If the ring is not found, return with McDiversNode standing on node McDiversNode.<br>
     * Add to visited the id of all nodes moved to, including this one.<br>
     * Return the number of moves. */
    public int dfsWalk(SeekState McDiversNode) {
        if (McDiversNode.distanceToRing() == 0) {
            System.out.println(numRemoved + " stack steps saved");
            return numMoves;
        }

        mcNode= McDiversNode;
        long id= McDiversNode.currentLocation();
        stack.add(new SEntry(id, McDiversNode.neighbors()));
        stackEntries.put(id, 0);
        visited.add(id);

        // inv: stack contains the nodes from start node to
        while (stack.size() >= 0) {
            SEntry top= stack.peek();
            // int s= stack.size()-1;
            getNextNaybor(top);
            if (top.n == top.naybors.length) {
                // all neighbors have been processed. Remove top entry
                // from stack and walk to the new top entry's node.
                int cn= closestNeighbor();
                if (cn >= 0) {
                    // System.out.println("neighbor on stack: " + cn);
                    // printStack(cn);
                    int b= remove(cn);
                    numRemoved= numRemoved + b;
                    // System.out.println("stack after remove: " + cn);
                    // printStack(cn);
                }
                SEntry t= stack.pop();
                stackEntries.remove(t.id);
                t= stack.peek();
                McDiversNode.moveTo(t.id);
                numMoves= numMoves + 1;
                // System.out.println("pop leaves this on stack: " + t+ " visited is " + visited);
            } else {
                // naybors[top.n] is next neighbor to process.
                // Move to it, return if it is the ring, and
                // if not the ring, visit it and push it onto the stack.
                long neighborId= top.naybors[top.n].getId();
                getNextNaybor(top);
                McDiversNode.moveTo(neighborId);
                numMoves= numMoves + 1;
                if (McDiversNode.distanceToRing() == 0) {
                    System.out.println(numRemoved + " stack steps saved");
                    return numMoves;
                }
                visited.add(neighborId);
                stackEntries.put(neighborId, stack.size());
                SEntry entry= new SEntry(neighborId, McDiversNode.neighbors());
                Arrays.sort(entry.naybors);
                stack.add(entry);
                // System.out.println("pushed: " + entry + " visited is " + visited);
            }
        }
        return 0;

    }

    /** Print stack[k..] */
    public void printStack(int k) {
        System.out.println("start of stack");
        for (k= k; k < stack.size(); k= k + 1) {
            System.out.println(k + ": " + stack.get(k));
        }
        System.out.println("end of stack");
    }

    /** If the back track can be shortened, remove some entries<br>
     * from the stack. */
    private void backTrack() {
        SEntry entry= stack.peek();

    }

    /** If all neighbors of entries stack[cn+1..stack.size()-2] are visited,<br>
     * delete them from the stack. Return the number removed (>= 0) */
    private int remove(int cn) {
        int s= stack.size() - 1;
        for (int k= cn + 1; k <= s - 1; k= k + 1) {
            SEntry entry= stack.get(k);
            if (!allDoneWith(entry)) {
                // System.out.println("Can't remove because of " + entry);
                return 0;
            }
        }
        // entries can be removed.
        System.out.println("Can remove elements " + (cn + 1) + ".." + (s - 1));
        // if (cn == cn) return false;
        int rem= s - (cn + 1);
        System.out.println("removing " + (cn + 1) + ".." + (s - 1));
        for (int k= cn + 1; k <= s - 1; k= k + 1) {
            SEntry se= stack.get(k);
            stackEntries.remove(se.id);
        }
        stack.set(cn + 1, stack.get(s));
        stack.setSize(cn + 2);
        return rem;
    }

    /** Return "All unprocessed neighbors of se's id are already visited". */
    public boolean allDoneWith(SEntry se) {
        for (int k= se.n; k < se.naybors.length; k= k + 1) {
            if (!visited.contains(se.naybors[k].getId())) return false;
        }
        return true;
    }

    /** Return the closest neighbor of the top stack node that is<br>
     * also on the stack but not the one just below it --return its<br>
     * index --return -1 if none. */
    private int closestNeighbor() {
        SEntry entry= stack.peek();

        // Set naybor to the highest index of a neighbor that is in the
        // top stack node that is not the penultimate stack node.
        // (to -1 if not possible).
        int naybr= -1;
        for (NodeStatus ns : entry.naybors) {
            Integer n= stackEntries.get(ns.getId());
            if (n != null && naybr < n && n < stack.size() - 2)
                naybr= n;
        }
        // if (naybr != -1) System.out.println("Best neighbor is " + naybr);
        return naybr;
    }

    /** Parameter t is the top stack entry, i.e. stack[stack.size()-1]<br>
     * Change t.n to the next unvisited neighbor to process.<br>
     * Method uses the fields. */
    private void getNextNaybor(SEntry t) {
        // inv: all neighbors in naybors[0..top.n] are visited.
        while (t.n < t.naybors.length && visited.contains(t.naybors[t.n].getId())) {
            t.n= t.n + 1;
        }
    }

    /** An SEntry contains a node, an array of its neighbors, and an integer<br>
     * indicating how many of its neighbors have been processed. */
    class SEntry {
        public long id; // The node's status
        public NodeStatus[] naybors; // the neighbors of node
        public int n; // neighbors naybors[0..n-1] have been processed

        /** Constructor: an instance for node node, neighbors ni,<br>
         * with no neighbors processed. */
        public SEntry(long nodeId, Collection<NodeStatus> ni) {
            id= nodeId;
            naybors= ni.toArray(nsa); // field nsa helps to make assignment easy.
        }

        /** A representation of this entry */
        @Override
        public String toString() {
            return id + ", " + n + ", " + allNaybors();
        }

        /** Return all neighbors of this node */
        public String allNaybors() {
            String res= "[";
            for (int k= 0; k < naybors.length; k= k + 1) {
                if (k > 0) res= res + ", ";
                res= res + naybors[k].getId();
            }
            return res + "]";
        }

    }
}

