package com.example.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Tree {
  private final Node root;

  public Tree(Node root) {
    this.root = root;
  }

  public Node getRoot() {
    return root;
  }

  public boolean deleteNode(int nodeIndex) {
    for (TreeIterator it = new TreeIterator(this); it.hasNext(); ) {
      Node node = it.next();
      if (node.getId() == nodeIndex) {
        ArrayList<Node> children = node.getParent().getChildren();
        for (int i = 0; ; i++) {
          if (children.get(i) == node) {
            Node lastChild = children.remove(children.size() - 1);
            if (i != children.size()) {
              children.set(i, lastChild);
            }
            node.getParent().getChildren().addAll(node.getChildren());
            for (Node child : node.getChildren()) {
              child.setParent(node.getParent());
            }
            return true;
          }
        }
      }
    }
    return false;
  }

  public boolean addChild(int childIndex, int parentIndex) {
    for (TreeIterator it = new TreeIterator(this); it.hasNext(); ) {
      Node node = it.next();
      if (node.getId() == parentIndex) {
        Node child = new Node(childIndex);
        child.setParent(node);
        node.addChild(child);
        return true;
      }
    }
    return false;
  }

  public String getInfo() {
    StringBuilder s = new StringBuilder("Корень: ");
    s.append(this.getRoot().getId());
    s.append(", его дети: [");
    boolean first = true;
    for (Node child : this.getRoot().getChildren()) {
      if (!first) {
        s.append(", ");
      }
      s.append(child.getId());
      first = false;
    }
    s.append("]\n");

    ArrayList<Node> nodesToProcess = new ArrayList<>(this.getRoot().getChildren());

    while (!nodesToProcess.isEmpty()) {
      Node node = nodesToProcess.remove(nodesToProcess.size() - 1);
      nodesToProcess.addAll(node.getChildren());

      s.append("Узел ");
      s.append(node.getId());
      s.append(", его родитель ");
      s.append(node.getParent().getId());

      s.append(", его дети: [");
      first = true;
      for (Node child : node.getChildren()) {
        if (!first) {
          s.append(", ");
        }
        s.append(child.getId());
        first = false;
      }
      s.append("]\n");
    }

    return s.toString();
  }

  public static ArrayList<Tree> fromNodesWithParentIds(
      ArrayList<NodeWithParentId> nodesWithParentIds) {
    nodesWithParentIds.sort(Comparator.comparingInt(pair -> pair.getNode().getId()));

    ArrayList<Tree> trees = new ArrayList<>();
    for (NodeWithParentId nodeWithParentId : nodesWithParentIds) {
      Node node = nodeWithParentId.getNode();
      int parentId = nodeWithParentId.getParentId();

      if (node.getId() == parentId) {
        trees.add(new Tree(node));
      } else {
        int parentIndex =
            Collections.binarySearch(
                nodesWithParentIds,
                new NodeWithParentId(new Node(parentId), 0),
                Comparator.comparingInt(pair -> pair.getNode().getId()));
        Node parent = nodesWithParentIds.get(parentIndex).getNode();
        node.setParent(parent);
        parent.addChild(node);
      }
    }

    return trees;
  }
}
