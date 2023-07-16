package game;

import datastructures.Heap;
import datastructures.PQueue;
import diver.SewerDiver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InstructorSolution implements SewerDiver {

    @Override
    public void seek(SeekState state) {
        smartDFS(state, new HashSet<>());
    }

    /* Visit all nodes reachable from the current node n (say) until the ring
     * is reached.
     * If ring reached, return standing on node with ring.
     * If ring not reachable, return standing on n.
     * A node is visited if it is in set visited. Maintain that definition.
     * Precondition: n is not visited. */
    public void DFS(SeekState state, Set<Long> visited) {
        if (state.distanceToRing() == 0) {
            return;
        }

        long id = state.currentLocation();
        visited.add(id);

        for (NodeStatus n : state.neighbors()) {
            if (!visited.contains(n.getId())) {
                state.moveTo(n.getId());
                DFS(state, visited);
                if (state.distanceToRing() == 0) {
                    return;
                }
                state.moveTo(id);
            }
        }
    }

    /* Visit all nodes reachable from the current node n (say) until the ring
     * is reached.
     * If ring reached, return standing on node with ring.
     * If ring not reachable, return standing on n.
     * A node is visited if it is in set visited. Maintain that definition.
     * When processing neighbors of n, process in ascending order of
     * their distance to the ring (i.e. closest one first).
     * Precondition: n is not visited. */
    public void smartDFS(SeekState state, Set<Long> visited) {
        if (state.distanceToRing() == 0) {
            return;
        }

        long id = state.currentLocation();
        visited.add(id);

        List<NodeStatus> neighbors = new ArrayList<>(state.neighbors());
        Collections.sort(neighbors);
        for (NodeStatus n : neighbors) {
            if (!visited.contains(n.getId())) {
                state.moveTo(n.getId());
                smartDFS(state, visited);
                if (state.distanceToRing() == 0) {
                    return;
                }
                state.moveTo(id);
            }
        }
    }

    @Override
    public void scram(ScramState state) {
        getOutGreedy(state);
    }

    /**
     * Run Dijkstra's shortest path algorithm on the entire graph starting from {@code start}. This
     * will return a map from Node to GraphPath where each entry is another node in the graph and
     * the shortest path to that node. The nodes contained in the returned graph will be any node
     * that has gold on it as well as the specified exit nodes.
     * <p>
     * The returned map will have a deterministic iteration order. The entries will be ordered in
     * decreasing order of coin-value-on-destination / length-of-path.
     *
     * @param start the node to start this Dijkstra's algorithm from.
     * @param exit  the exit to the graph, which must always be included in the output even though
     *              it will not have gold.
     * @return A map of node to shortest path from {@code start} ordered by highest-to-lowest value
     * / steps.
     */
    private Map<Node, GraphPath> fullDijkstra(Node start, Node exit) {
        Map<Node, Integer> weights = new HashMap<>();
        Map<Node, Node> backPointers = new HashMap<>();
        PQueue<Node> heap = new Heap<>(true);

        // Run Dijkstra's algorithm on the graph.
        weights.put(start, 0);
        heap.add(start, 0);
        while (heap.size() > 0) {
            Node n = heap.extractMin();
            int weight = weights.get(n);

            for (Edge e : n.getExits()) {
                Node m = e.getOther(n);
                int weightThroughN = weight + e.length();
                Integer existingWeight = weights.get(m);
                if (existingWeight == null) {
                    weights.put(m, weightThroughN);
                    backPointers.put(m, n);
                    heap.add(m, weightThroughN);
                } else if (weightThroughN < existingWeight) {
                    weights.put(m, weightThroughN);
                    backPointers.put(m, n);
                    heap.changePriority(m, weightThroughN);
                }
            }
        }

        // Get all of the paths to nodes that have coins or are the exit.
        List<GraphPath> pathsList = new ArrayList<>();
        for (Node n : weights.keySet()) {
            if (n.getTile().coins() == 0 && !n.equals(exit) || n.equals(start)) {
                continue;
            }

            List<Node> path = new ArrayList<>();
            Node next = n;
            while (!next.equals(start)) {
                path.add(next);
                next = backPointers.get(next);
            }
            Collections.reverse(path);
            pathsList.add(new GraphPath(n, weights.get(n), path));
        }

        // Sort the paths in decreasing order of value and use a linked hash
        // map to store them. That way we can just iterate through in order
        // later for maximum efficiency.
        Collections.sort(pathsList,
                (p1, p2) -> -Double.compare((double) p1.dest.getTile().coins() / p1.length,
                        (double) p2.dest.getTile().coins() / p2.length));
        Map<Node, GraphPath> paths = new LinkedHashMap<>();
        for (GraphPath gp : pathsList) {
            paths.put(gp.dest, gp);
        }
        return paths;
    }

    /**
     * Get out while trying to collect as many coins as possible. <br> Will calculate the best
     * coin-value available and path to it as <br> long as it can still make it to the exit.
     */
    private void getOutGreedy(ScramState state) {
        Node start = state.currentNode();
        Node exit = state.exit();
        // Compute the shortest path between every node with coins and every
        // other node with coins.
        Map<Node, Map<Node, GraphPath>> allPaths = new HashMap<>();
        for (Node n : state.allNodes()) {
            if (!n.equals(exit) && (n.getTile().coins() > 0 || n.equals(start))) {
                allPaths.put(n, fullDijkstra(n, exit));
            }
        }

        List<Node> route = new ArrayList<>();
        Set<Node> nodesVisited = new HashSet<>();
        nodesVisited.add(start);
        int timeSpent = 0;
        Node current = start;
        while (!current.equals(exit)) {
            // Because the paths are stored in a linked hashmap in decreasing
            // order ofvalue-to-steps ratio, we can safely
            // iterate through in order and take the first usable next path!
            for (GraphPath gp : allPaths.get(current).values()) {
                if (gp.dest.equals(exit) ||
                        timeSpent + gp.length + allPaths.get(gp.dest).get(exit).length
                                <= state.stepsToGo() && !nodesVisited.contains(gp.dest)) {
                    // Add everything we walk over on the way to the node to
                    // our visited list.
                    nodesVisited.addAll(gp.path);
                    route.addAll(gp.path);

                    timeSpent += gp.length;
                    current = gp.dest;
                    break;
                }
            }
        }

        // Now that we've found the path, move along it and pick up coins (automatic).
        for (Node nextNode : route) {
//            if (state.currentNode().getTile().coins() > 0) {
//                state.grabCoins();
//            }
            state.moveTo(nextNode);
        }
    }

    private static class GraphPath {

        private final Node dest;
        private final int length;
        private final List<Node> path;

        public GraphPath(Node d, int l, List<Node> p) {
            dest = d;
            length = l;
            path = p;
        }
    }
}
