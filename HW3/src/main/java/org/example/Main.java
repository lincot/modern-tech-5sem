package org.example;

import jakarta.persistence.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

class Node {
  private final int id;
  private final ArrayList<Node> children;
  private Node parent;

  public Node(int id) {
    this.id = id;
    children = new ArrayList<>();
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
    children.add(child);
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
}

class TreeIterator implements Iterator<Node> {
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

@Entity
@Table(name = "TREES")
class NodeEntity {
  @Id public int id;

  @Column(name = "parent_id")
  public int parentId;

  public NodeEntity(int id, int parentId) {
    this.id = id;
    this.parentId = parentId;
  }

  public NodeEntity() {}
}

class HibernateTreeDAO {
  public static ArrayList<Tree> read(Session session) {
    ArrayList<Pair<Node, Number>> nodesWithParentIds = new ArrayList<>();
    Query<NodeEntity> query = session.createQuery("from NodeEntity", NodeEntity.class);

    for (NodeEntity nodeEntity : query.list()) {
      nodesWithParentIds.add(new Pair<>(new Node(nodeEntity.id), nodeEntity.parentId));
    }

    nodesWithParentIds.sort(Comparator.comparingInt(pair -> pair.getLeft().getId()));

    ArrayList<Tree> trees = new ArrayList<>();
    for (Pair<Node, Number> nodeWithParentId : nodesWithParentIds) {
      Node node = nodeWithParentId.getLeft();
      int parentId = (int) nodeWithParentId.getRight();

      if (node.getId() == parentId) {
        trees.add(new Tree(node));
      } else {
        int parentIndex =
            Collections.binarySearch(
                nodesWithParentIds,
                new Pair<>(new Node(parentId), 0),
                Comparator.comparingInt(pair -> pair.getLeft().getId()));
        Node parent = nodesWithParentIds.get(parentIndex).getLeft();
        node.setParent(parent);
        parent.addChild(node);
      }
    }

    return trees;
  }

  public static void write(Session session, ArrayList<Tree> trees) {
    Transaction txTruncate = session.beginTransaction();
    session.createNativeQuery("TRUNCATE TABLE TREES", NodeEntity.class).executeUpdate();
    txTruncate.commit();
    session.clear();
    Transaction txFill = session.beginTransaction();
    for (Tree tree : trees) {
      for (TreeIterator it = new TreeIterator(tree); it.hasNext(); ) {
        Node node = it.next();
        int parentId = node.getId();
        if (node.getParent() != null) {
          parentId = node.getParent().getId();
        }
        session.persist(new NodeEntity(node.getId(), parentId));
      }
    }
    txFill.commit();
  }

  public static void populate(Session session) {
    Transaction tx = session.beginTransaction();
    int[] init = {
      1, 3,
      3, 5,
      2, 3,
      5, 12,
      4, 5,
      12, 12,
      6, 12,
      11, 12,
      9, 11,
      7, 9,
      10, 11,
      8, 9,
      104, 103,
      103, 102,
      105, 103,
      102, 101,
      106, 102,
      101, 101,
      107, 101,
      108, 101,
      109, 108,
      110, 109,
      112, 108,
      111, 109,
      777, 111,
      778, 111
    };
    for (int i = 0; i < init.length; i += 2) {
      session.persist(new NodeEntity(init[i], init[i + 1]));
    }
    tx.commit();
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

class TreeGUI extends JFrame {
  ArrayList<Tree> trees;
  private final JTextField deleteNodeField;
  private final JTextField childNodeField;
  private final JTextField parentNodeField;
  private final JTextField treeField;

  public TreeGUI(Session session) {
    setSize(500, 350);
    setTitle("Деревья");
    trees = HibernateTreeDAO.read(session);

    try {
      UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
      getContentPane().setBackground(new Color(40, 40, 40));
    } catch (UnsupportedLookAndFeelException
        | ClassNotFoundException
        | InstantiationException
        | IllegalAccessException e) {
      e.printStackTrace();
    }

    JButton showTreesButton = new JButton("показать список всех деревьев");
    JButton readFromDBButton = new JButton("прочитать список всех деревьев из БД");
    JButton writeToDBButton = new JButton("записать список всех деревьев из БД");
    JButton deleteNodeButton = new JButton("удалить узел:");
    deleteNodeField = new JTextField(5);
    JButton addChildNodeButton = new JButton("добавить ребёнка к узлу:");
    childNodeField = new JTextField(5);
    parentNodeField = new JTextField(5);
    JButton addTreeButton = new JButton("добавить дерево:");
    treeField = new JTextField(5);

    showTreesButton.addActionListener(
        e -> {
          TreesFrame treesFrame = new TreesFrame(trees);
          treesFrame.setVisible(true);
        });
    readFromDBButton.addActionListener(e -> trees = HibernateTreeDAO.read(session));
    writeToDBButton.addActionListener(e -> HibernateTreeDAO.write(session, trees));
    deleteNodeButton.addActionListener(e -> deleteNode());
    addChildNodeButton.addActionListener(e -> addChildNode());
    addTreeButton.addActionListener(e -> addTree());

    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets(5, 5, 5, 5);
    add(showTreesButton, gbc);
    gbc.gridy++;
    add(readFromDBButton, gbc);
    gbc.gridy++;
    add(writeToDBButton, gbc);
    gbc.gridy++;
    add(deleteNodeButton, gbc);
    gbc.gridx++;
    add(deleteNodeField, gbc);
    gbc.gridx--;
    gbc.gridy++;
    add(addChildNodeButton, gbc);
    gbc.gridx++;
    add(childNodeField, gbc);
    gbc.gridx++;
    add(parentNodeField, gbc);
    gbc.gridx -= 2;
    gbc.gridy++;
    add(addTreeButton, gbc);
    gbc.gridx++;
    add(treeField, gbc);
  }

  private void deleteNode() {
    int nodeIndex = Integer.parseInt(deleteNodeField.getText());
    for (int i = 0; i < trees.size(); i++) {
      if (trees.get(i).getRoot().getId() == nodeIndex) {
        Tree last = trees.remove(trees.size() - 1);
        if (i != trees.size()) {
          trees.set(i, last);
        }
        break;
      }
      if (trees.get(i).deleteNode(nodeIndex)) {
        break;
      }
    }
  }

  private void addChildNode() {
    int childIndex = Integer.parseInt(childNodeField.getText());
    int parentIndex = Integer.parseInt(parentNodeField.getText());
    for (Tree tree : trees) {
      if (tree.addChild(childIndex, parentIndex)) {
        break;
      }
    }
  }

  private void addTree() {
    int root = Integer.parseInt(treeField.getText());
    trees.add(new Tree(new Node(root)));
  }
}

class TreesFrame extends JFrame {
  private final JTextArea infoTextArea;

  public TreesFrame(ArrayList<Tree> trees) {
    setSize(1000, 1000);
    setTitle("Информация о деревьях");
    setLayout(new GridLayout(trees.size(), 1));

    infoTextArea = new JTextArea();
    infoTextArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(infoTextArea);

    JPanel buttonsPanel = new JPanel(new FlowLayout());

    for (Tree tree : trees) {
      JButton rootNodeButton = new JButton("Дерево " + tree.getRoot().getId());
      rootNodeButton.addActionListener(e -> infoTextArea.setText(tree.getInfo()));
      buttonsPanel.add(rootNodeButton);
    }

    add(buttonsPanel, BorderLayout.NORTH);
    add(scrollPane, BorderLayout.CENTER);
  }
}

public class Main {
  public static void main(String[] args) {
    try (SessionFactory factory = new Configuration().configure().buildSessionFactory()) {
      Session session = factory.openSession();

      for (String arg : args) {
        if (arg.equals("--populate")) {
          HibernateTreeDAO.populate(session);
          break;
        }
      }

      TreeGUI treeGUI = new TreeGUI(session);
      treeGUI.setVisible(true);

      while (treeGUI.isVisible()) {
        TimeUnit.SECONDS.sleep(1);
      }
    } catch (HibernateException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
