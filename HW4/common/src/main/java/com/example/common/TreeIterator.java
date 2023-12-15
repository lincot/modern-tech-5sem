package com.example.common;

import java.util.ArrayList;
import java.util.Iterator;

public class TreeIterator implements Iterator<Node> {
    private final ArrayList<Node> nodesToProcess;

    public TreeIterator(Tree tree) {
        nodesToProcess = new ArrayList<>();
        nodesToProcess.add(tree.getRoot());
    }

    public boolean hasNext() {
        return !nodesToProcess.isEmpty();
    }

    public Node next() {
        Node node = nodesToProcess.remove(nodesToProcess.size() - 1);
        nodesToProcess.addAll(node.getChildren());
        return node;
    }
}
