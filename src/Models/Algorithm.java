package Models;

import java.util.*;
import javax.swing.JOptionPane;

import Graphics.GraphPanel;
import Models.Edge;
import Models.Graph;
import Models.Node;

public class Algorithm {

    private boolean safe = false;
    private String message = null;
    private final Graph graph;
    private final Map<Node, Node> predecessors;
    private final Map<Node, Integer> distances;

    private PriorityQueue<Node> unvisited;
    private final HashSet<Node> visited;
    private int flag = 0;
    private Node NearestHospital = null;

    public class NodeComparator implements Comparator<Node> {

        @Override
        public int compare(Node node1, Node node2) {
            return distances.get(node1) - distances.get(node2);
        }
    };

    public Algorithm(Graph graph) {
        this.graph = graph;
        predecessors = new HashMap<>();
        distances = new HashMap<>();

        for (Node node : graph.getNodes()) {
            distances.put(node, Integer.MAX_VALUE);
        }
        visited = new HashSet<>();

        safe = evaluate();
    }

    private boolean evaluate() {
        if (graph.getSource() == null) {
            message = "Source must be present in the graph";
            return false;
        }

        if (graph.getDestination() == null) {
            message = "Destination must be present in the graph";
            return false;
        }

        for (Node node : graph.getNodes()) {
            if (!graph.isNodeReachable(node)) {
                message = "Graph contains unreachable nodes";
                return false;
            }
        }

        return true;
    }

    public void run() throws IllegalStateException {
        if (!safe) {
            throw new IllegalStateException(message);
        }
        int min = Integer.MAX_VALUE;
        unvisited = new PriorityQueue<>(graph.getNodes().size(), new NodeComparator());

        Node source = graph.getSource();
        distances.put(source, 0);
        visited.add(source);

        for (Edge neighbor : getNeighbors(source)) {
            Node adjacent = getAdjacent(neighbor, source);
            if (adjacent == null) {
                continue;
            }

            distances.put(adjacent, neighbor.getWeight());
            predecessors.put(adjacent, source);
            unvisited.add(adjacent);
        }
        while (!unvisited.isEmpty()) {

            Node current = unvisited.poll();
            if (Graph.redZoneNode.contains(current)) {
                continue;
            }
            updateDistance(current);
            unvisited.remove(current);
            visited.add(current);
            System.out.println(Graph.hospitalNode);
            if (distances.get(current) < min) {
                if (Graph.hospitalNode.contains(current)) {
                    min = distances.get(current);
                    NearestHospital = current;
                }
            }
        }
        for (Node node : graph.getNodes()) {

            node.setPath(getPath(node));
        }

        graph.setSolved(true);

    }

    private void updateDistance(Node node) {
        int distance = distances.get(node);

        for (Edge neighbor : getNeighbors(node)) {
            Node adjacent = getAdjacent(neighbor, node);
            if (visited.contains(adjacent)) {
                continue;
            }

            int current_dist = distances.get(adjacent);
            int new_dist = distance + neighbor.getWeight();

            if (new_dist < current_dist) {
                distances.put(adjacent, new_dist);
                predecessors.put(adjacent, node);
                unvisited.add(adjacent);
            }
        }
    }

    private Node getAdjacent(Edge edge, Node node) {
        if (edge.getNodeOne() != node && edge.getNodeTwo() != node) {
            return null;
        }

        return node == edge.getNodeTwo() ? edge.getNodeOne() : edge.getNodeTwo();
    }

    private List<Edge> getNeighbors(Node node) {
        List<Edge> neighbors = new ArrayList<>();

        for (Edge edge : graph.getEdges()) {
            if (edge.getNodeOne() == node || edge.getNodeTwo() == node) {
                neighbors.add(edge);
            }
        }

        return neighbors;
    }

    public Integer getDestinationDistance() {
        return distances.get(graph.getDestination());
    }

    public Integer getDistance(Node node) {
        return distances.get(node);
    }

    public List<Node> getDestinationPath() {
        if (flag == 0) {
            return getPath(graph.getDestination());
        } else {
            return null;
        }
    }

    public List<Node> getPath(Node node) {
        List<Node> path = new ArrayList<>();
        Node current = node;
        path.add(current);
        flag = 0;
        while (current != graph.getSource()) {
            if (current == graph.getSource()) {
                return path;
            }
            current = predecessors.get(current);
            if (current == null) {
                flag = 1;
                break;
            }
            path.add(current);
        }
        if (flag == 1 && path.contains(Graph.destination) == true) {
            JOptionPane.showMessageDialog(null,
                    "NO PATH AVAILABLE!",
                    "Warning Panel!",
                    JOptionPane.WARNING_MESSAGE);
            Graph.destination = NearestHospital;
            GraphPanel gp = new GraphPanel(graph);
            gp.setPath(getDestinationPath());
            flag = 0;
            return null;
        }
        Collections.reverse(path);
        return path;
    }
}
