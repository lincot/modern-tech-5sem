package org.example;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.h2.tools.RunScript;

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
  public static ArrayList<Tree> readTreesFromSql(Connection connection, String tableName)
      throws SQLException {
    ArrayList<Pair<Node, Number>> nodes_with_parent_ids = new ArrayList<>();
    Statement statement = connection.createStatement();
    ResultSet ids = statement.executeQuery("SELECT * FROM " + tableName);

    while (ids.next()) {
      int id = ids.getInt("id");
      int parentId = ids.getInt("parent_id");

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
        int parentIndex =
            Collections.binarySearch(
                nodes_with_parent_ids,
                new Pair<>(new Node(parentId), 0),
                Comparator.comparingInt(pair -> pair.getLeft().getId()));
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

interface DatabaseConnector {
  Connection getConnection() throws SQLException;
}

class H2DatabaseConnector implements DatabaseConnector {
  private static final String URL = "jdbc:h2:~/treeDB";
  private static final String USER = "userTree";
  private static final String PASSWORD = "pass";

  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }
}

class PostgresDatabaseConnector implements DatabaseConnector {
  private static final String URL = "jdbc:postgresql://localhost/treeDB";
  private static final String USER = "userTree";
  private static final String PASSWORD = "pass";

  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }
}

public class Main {
  public static void main(String[] args) {
    try {
      boolean postgres = false;
      boolean populate = false;

      for (String arg : args) {
        if (arg.equals("--postgres")) {
          postgres = true;
        } else if (arg.equals("--populate")) {
          populate = true;
        }
      }

      if (postgres) {
        System.out.println("using PostgreSQL");
      } else {
        System.out.println("using H2");
      }

      Connection connection =
          postgres
              ? new PostgresDatabaseConnector().getConnection()
              : new H2DatabaseConnector().getConnection();

      if (populate) {
        System.out.println("populating the db");
        RunScript.execute(connection, new FileReader("init.sql"));
      }

      ArrayList<Tree> trees = TreeReader.readTreesFromSql(connection, "TREES");

      int numOfAllLeaves = trees.stream().map(Tree::countLeaves).reduce(0, Integer::sum);

      try (FileWriter writer = new FileWriter("output.csv")) {
        writer.write(numOfAllLeaves + "\n");
      }
    } catch (SQLException | IOException e) {
      e.printStackTrace();
    }
  }
}
