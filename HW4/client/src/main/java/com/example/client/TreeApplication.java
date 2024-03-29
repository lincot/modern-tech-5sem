package com.example.client;

import com.example.common.Node;
import com.example.common.NodeWithParentId;
import com.example.common.Tree;
import com.example.common.TreeIterator;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class TreeApplication extends Application {
  private static final String BASE_URL = "http://localhost:8080";
  ArrayList<Tree> trees;
  private final TextField deleteNodeField = new TextField();
  private final TextField childNodeField = new TextField();
  private final TextField parentNodeField = new TextField();
  private final TextField treeField = new TextField();

  public static void main(String[] args) {
    launch(args);
  }

  public TreeApplication() {}

  public void start(Stage primaryStage) {
    if (getParameters().getRaw().contains("--populate")) {
      populate();
    }

    trees = fetchTrees();

    primaryStage.setTitle("Деревья");

    Button showTreesButton = new Button("показать список всех деревьев");
    Button readFromDBButton = new Button("прочитать список всех деревьев из БД");
    Button writeToDBButton = new Button("записать список всех деревьев из БД");
    Button deleteNodeButton = new Button("удалить узел:");
    Button addChildNodeButton = new Button("добавить ребёнка к узлу:");
    Button addTreeButton = new Button("добавить дерево:");

    showTreesButton.setOnAction(
        e -> {
          TreesFrame treesFrame = new TreesFrame(trees);
          treesFrame.show();
        });
    readFromDBButton.setOnAction(e -> trees = fetchTrees());
    writeToDBButton.setOnAction(e -> postTrees());
    deleteNodeButton.setOnAction(e -> deleteNode());
    addChildNodeButton.setOnAction(e -> addChildNode());
    addTreeButton.setOnAction(e -> addTree());

    GridPane grid = new GridPane();
    grid.setPadding(new Insets(10, 10, 10, 10));
    grid.setVgap(5);
    grid.setHgap(5);

    GridPane.setConstraints(showTreesButton, 0, 0);
    GridPane.setConstraints(readFromDBButton, 0, 1);
    GridPane.setConstraints(writeToDBButton, 0, 2);
    GridPane.setConstraints(deleteNodeButton, 0, 3);
    GridPane.setConstraints(deleteNodeField, 1, 3);
    GridPane.setConstraints(addChildNodeButton, 0, 4);
    GridPane.setConstraints(childNodeField, 1, 4);
    GridPane.setConstraints(parentNodeField, 2, 4);
    GridPane.setConstraints(addTreeButton, 0, 5);
    GridPane.setConstraints(treeField, 1, 5);

    grid.getChildren()
        .addAll(
            showTreesButton,
            readFromDBButton,
            writeToDBButton,
            deleteNodeButton,
            deleteNodeField,
            addChildNodeButton,
            childNodeField,
            parentNodeField,
            addTreeButton,
            treeField);

    Scene scene = new Scene(grid, 500, 300);
    scene.getStylesheets().add("style.css");
    primaryStage.setScene(scene);
    primaryStage.show();
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

  private ArrayList<Tree> fetchTrees() {
    ResponseEntity<ArrayList<NodeWithParentId>> response =
        new RestTemplate()
            .exchange(
                BASE_URL + "/api/trees",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});
    ArrayList<NodeWithParentId> nodesWithParentIds = response.getBody();
    assert nodesWithParentIds != null;
    return Tree.fromNodesWithParentIds(nodesWithParentIds);
  }

  private void postTrees() {
    ArrayList<NodeWithParentId> res = new ArrayList<>();
    for (Tree tree : trees) {
      for (TreeIterator it = new TreeIterator(tree); it.hasNext(); ) {
        Node node = it.next();
        int parentId = node.getId();
        if (node.getParent() != null) {
          parentId = node.getParent().getId();
        }
        res.add(new NodeWithParentId(new Node(node.getId()), parentId));
      }
    }
    new RestTemplate().postForLocation(BASE_URL + "/api/trees", res);
  }

  private void populate() {
    new RestTemplate().exchange(BASE_URL + "/api/populate", HttpMethod.GET, null, void.class);
  }
}

class TreesFrame extends Stage {
  private final TextArea infoTextArea;

  public TreesFrame(ArrayList<Tree> trees) {
    setTitle("Информация о деревьях");

    infoTextArea = new TextArea();
    infoTextArea.setEditable(false);

    VBox layout = new VBox(trees.size());
    for (Tree tree : trees) {
      Button rootNodeButton = new Button("Дерево " + tree.getRoot().getId());
      rootNodeButton.setOnAction(e -> infoTextArea.setText(tree.getInfo()));
      layout.getChildren().add(rootNodeButton);
    }
    layout.getChildren().add(infoTextArea);

    Scene scene = new Scene(layout, 400, 400);
    scene.getStylesheets().add("style.css");
    setScene(scene);
  }
}
