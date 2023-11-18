package org.example;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.util.Collections;
import java.util.Comparator;

class Node {
    private final int id;
    private final ArrayList<Node> children;
    private Node parent;

    public Node(int id) {
        this.id = id;
        this.children = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public boolean isRoot() {
        return parent == null;
    }
}

class Tree {
    private final Node root;

    public Tree(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }

    public ArrayList<Node> getNodes() {
        ArrayList<Node> allNodes = new ArrayList<>();
        ArrayList<Node> nodesToProcess = new ArrayList<>();
        nodesToProcess.add(root);

        while (!nodesToProcess.isEmpty()) {
            Node node = nodesToProcess.remove(nodesToProcess.size() - 1);
            allNodes.add(node);
            nodesToProcess.addAll(node.getChildren());
        }

        return allNodes;
    }

    public ArrayList<Node> getLeaves() {
        ArrayList<Node> leaves = new ArrayList<>();
        ArrayList<Node> nodesToProcess = new ArrayList<>();
        nodesToProcess.add(root);

        while (!nodesToProcess.isEmpty()) {
            Node node = nodesToProcess.remove(nodesToProcess.size() - 1);
            if (node.isLeaf()) {
                leaves.add(node);
            }
            nodesToProcess.addAll(node.getChildren());
        }

        return leaves;
    }

    public int countLeaves() {
        int numOfLeaves = 0;
        ArrayList<Node> nodesToProcess = new ArrayList<>();
        nodesToProcess.add(root);

        while (!nodesToProcess.isEmpty()) {
            Node node = nodesToProcess.remove(nodesToProcess.size() - 1);
            if (node.isLeaf()) {
                numOfLeaves++;
            }
            nodesToProcess.addAll(node.getChildren());
        }

        return numOfLeaves;
    }
}

class TreeReader {
    public static ArrayList<Tree> readTreesFromCsv(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        ArrayList<Pair<Node, Number>> nodes_with_parent_ids = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            String[] ids = line.split(",", 2);
            int id = Integer.parseInt(ids[0]);
            int parentId = Integer.parseInt(ids[1]);

            nodes_with_parent_ids.add(new Pair<>(new Node(id), parentId));
        }

        nodes_with_parent_ids.sort(Comparator.comparingInt(pair -> pair.getLeft().getId()));

        ArrayList<Tree> trees = new ArrayList<>();
        for (Pair<Node, Number> node_with_parent_id : nodes_with_parent_ids) {
            Node node = node_with_parent_id.getLeft();
            int parentId = (int) node_with_parent_id.getRight();

            if (node.getId() == parentId) {
                trees.add(new Tree(node));
            } else {
                int parentIndex = Collections.binarySearch(
                        nodes_with_parent_ids,
                        new Pair<>(new Node(parentId), 0),
                        Comparator.comparingInt(pair -> pair.getLeft().getId())
                );
                Node parent = nodes_with_parent_ids.get(parentIndex).getLeft();
                node.setParent(parent);
                parent.addChild(node);
            }
        }

        return trees;
    }
}

class Pair<L, R> {
    private final L left;
    private final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }
}


public class Main {
    public static void main(String[] args) {
        try {
            ArrayList<Tree> trees = TreeReader.readTreesFromCsv("input.csv");

            Tree maxTree = null;
            int maxTreeLeaves = 0;
            boolean moreThanOneMax = false;
            for (Tree tree : trees) {
                int treeLeaves = tree.countLeaves();
                if (treeLeaves > maxTreeLeaves) {
                    maxTree = tree;
                    maxTreeLeaves = treeLeaves;
                    moreThanOneMax = false;
                } else if (treeLeaves == maxTreeLeaves) {
                    moreThanOneMax = true;
                }
            }

            try (FileWriter writer = new FileWriter("output.csv")) {
                if (maxTree == null) {
                    System.out.print("no trees found");
                    System.exit(1);
                } else if (!moreThanOneMax) {
                    writer.write(maxTree.getRoot().getId() + "," + maxTreeLeaves);
                } else {
                    writer.write("0,0");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}