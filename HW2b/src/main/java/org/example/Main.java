package org.example;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import org.h2.tools.RunScript;

class Node {
  private final int id;
  private final ArrayList<Node> children;
  private final ArrayList<Node> parents;

  public Node(int id) {
    this.id = id;
    this.children = new ArrayList<>();
    this.parents = new ArrayList<>();
  }

  public int getId() {
    return id;
  }

  public ArrayList<Node> getChildren() {
    return children;
  }

  public ArrayList<Node> getParents() {
    return parents;
  }

  public void addChild(Node child) {
    this.children.add(child);
  }

  public void addParent(Node parent) {
    this.parents.add(parent);
  }

  public boolean isInitialNode() {
    return parents.isEmpty() && !children.isEmpty();
  }

  public boolean isInACycle() {
    HashSet<Node> visited = new HashSet<>();
    ArrayList<Node> nodesToProcess = new ArrayList<>(this.getChildren());
    while (!nodesToProcess.isEmpty()) {
      Node node = nodesToProcess.remove(nodesToProcess.size() - 1);
      if (node == this) {
        return true;
      }
      for (Node child : node.getChildren()) {
        if (!visited.contains(child)) {
          nodesToProcess.add(child);
        }
      }
      visited.add(node);
    }
    return false;
  }
}

class DirectedGraph {
  private final ArrayList<Node> nodes;

  public DirectedGraph(ArrayList<Node> nodes) {
    this.nodes = nodes;
  }

  public ArrayList<Node> getNodes() {
    return nodes;
  }

  public boolean containsCycleReachableFromInitialNode() {
    HashSet<Node> visited = new HashSet<>();
    Node initialNode = null;
    for (Node node : nodes) {
      if (node.isInitialNode()) {
        initialNode = node;
      }
    }
    if (initialNode == null) {
      return false;
    }
    ArrayList<Node> nodesToProcess = new ArrayList<>();
    nodesToProcess.add(initialNode);
    while (!nodesToProcess.isEmpty()) {
      Node node = nodesToProcess.remove(nodesToProcess.size() - 1);
      if (node.isInACycle()) {
        return true;
      }
      for (Node child : node.getChildren()) {
        if (!visited.contains(child)) {
          nodesToProcess.add(child);
        }
      }
      visited.add(node);
    }

    return false;
  }
}

class DirectedGraphReader {
  public static DirectedGraph readGraphFromSql(Connection connection, String tableName)
      throws SQLException {
    Statement statement = connection.createStatement();
    ResultSet ids = statement.executeQuery("SELECT * FROM " + tableName);

    ArrayList<Pair<Number, Number>> data = new ArrayList<>(ids.getFetchSize());

    while (ids.next()) {
      int id = ids.getInt("id");
      int childId = ids.getInt("child_id");

      data.add(new Pair<>(id, childId));
    }

    ArrayList<Number> nodes = new ArrayList<>();

    for (Pair<Number, Number> pair : data) {
      nodes.add(pair.getLeft());
      nodes.add(pair.getRight());
    }

    nodes.sort(Comparator.comparingInt(x -> (int) x));

    ArrayList<Node> uniqueNodes =
        nodes.stream()
            .distinct()
            .map(x -> new Node((int) x))
            .collect(Collectors.toCollection(ArrayList::new));

    for (Pair<Number, Number> pair : data) {
      int parentIndex =
          Collections.binarySearch(
              uniqueNodes, new Node((int) pair.getLeft()), Comparator.comparingInt(Node::getId));
      int childIndex =
          Collections.binarySearch(
              uniqueNodes, new Node((int) pair.getRight()), Comparator.comparingInt(Node::getId));
      uniqueNodes.get(parentIndex).addChild(uniqueNodes.get(childIndex));
      uniqueNodes.get(childIndex).addParent(uniqueNodes.get(parentIndex));
    }

    return new DirectedGraph(uniqueNodes);
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

      DirectedGraph graph = DirectedGraphReader.readGraphFromSql(connection, "TREES");
      boolean result = graph.containsCycleReachableFromInitialNode();

      try (FileWriter writer = new FileWriter("output.csv")) {
        writer.write(result + "\n");
      }
    } catch (SQLException | IOException e) {
      e.printStackTrace();
    }
  }
}
