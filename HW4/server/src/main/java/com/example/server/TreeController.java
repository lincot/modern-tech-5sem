package com.example.server;

import com.example.common.Node;
import com.example.common.NodeWithParentId;
import jakarta.persistence.*;
import java.util.ArrayList;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.springframework.web.bind.annotation.*;

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

  // Hibernate requires this for some reason
  public NodeEntity() {}
}

class HibernateTreeDAO {
  public static ArrayList<NodeWithParentId> read(Session session) {
    ArrayList<NodeWithParentId> nodesWithParentIds = new ArrayList<>();
    Query<NodeEntity> query = session.createQuery("from NodeEntity", NodeEntity.class);

    for (NodeEntity nodeEntity : query.list()) {
      nodesWithParentIds.add(new NodeWithParentId(new Node(nodeEntity.id), nodeEntity.parentId));
    }

    return nodesWithParentIds;
  }

  public static void write(Session session, ArrayList<NodeWithParentId> nodeWithParentIds) {
    Transaction txTruncate = session.beginTransaction();
    session.createNativeQuery("TRUNCATE TABLE TREES", NodeEntity.class).executeUpdate();
    txTruncate.commit();
    session.clear();
    Transaction txFill = session.beginTransaction();
    for (NodeWithParentId nodeWithParentId : nodeWithParentIds) {
      session.persist(
          new NodeEntity(nodeWithParentId.getNode().getId(), nodeWithParentId.getParentId()));
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

@RestController
@RequestMapping("/api/trees")
public class TreeController {
  private final SessionFactory sessionFactory;

  TreeController() {
    sessionFactory = new Configuration().configure().buildSessionFactory();
  }

  @PostMapping
  public void postTrees(@RequestBody ArrayList<NodeWithParentId> nodeWithParentIds) {
    HibernateTreeDAO.write(sessionFactory.openSession(), nodeWithParentIds);
  }

  @GetMapping
  public ArrayList<NodeWithParentId> getTrees() {
    return HibernateTreeDAO.read(sessionFactory.openSession());
  }
}
