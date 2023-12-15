package com.example.common;

public class NodeWithParentId {
    private Node node;
    private int parentId;

    public NodeWithParentId(Node node, int parentId) {
        this.node = node;
        this.parentId = parentId;
    }

    public NodeWithParentId() {}

    public Node getNode() {
        return node;
    }

    public int getParentId() {
        return parentId;
    }
}
