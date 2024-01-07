package com.example.common;

import java.util.ArrayList;

public class Node {
  private int id;
  private ArrayList<Node> children;
  private Node parent;

  public Node(int id) {
    this.id = id;
    children = new ArrayList<>();
  }

  public Node() {}

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
    children.add(child);
  }
}
