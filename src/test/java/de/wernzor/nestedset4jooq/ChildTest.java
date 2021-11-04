package de.wernzor.nestedset4jooq;

import de.wernzor.nestedset4jooq.dao.CategoryNestedSetDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class ChildTest extends AbstractNestedSetTest {

    @Autowired
    ChildTest(CategoryNestedSetDao dao) {
        super(dao);
    }

    @Test
    public void insertAsFirstChildWhenNoChildExists() {
        var parent = getNode("rootNode");
        dao.insertAsRoot(parent);

        var child = getNode("firstInsertedChild");
        dao.insertAsFirstChild(parent, child);

        var result = dao.findAll();
        assertEquals(2, result.size());

        var root = getByName(result, "rootNode");
        var firstInsertedChild = getByName(result, "firstInsertedChild");

        assertTrue(matches(root, "rootNode", 1, 4, 0));
        assertTrue(matches(firstInsertedChild, "firstInsertedChild", 2, 3, 1));
    }

    @Test
    public void insertAsFirstChildWhenChildExists() {
        var parent = getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = getNode("firstInsertedChild");
        dao.insertAsFirstChild(parent, child1);

        var child2 = getNode("secondInsertedChild");
        dao.insertAsFirstChild(parent, child2);

        var result = dao.findAll();
        assertEquals(3, result.size());

        var root = getByName(result, "rootNode");
        var firstInsertedChild = getByName(result, "firstInsertedChild");
        var secondInsertedChild = getByName(result, "secondInsertedChild");

        assertTrue(matches(root, "rootNode", 1, 6, 0));
        assertTrue(matches(secondInsertedChild, "secondInsertedChild", 2, 3, 1));
        assertTrue(matches(firstInsertedChild, "firstInsertedChild", 4, 5, 1));
    }

    @Test
    public void insertAsLastChildWhenNoChildExists() {
        var parent = getNode("rootNode");
        dao.insertAsRoot(parent);

        var child = getNode("firstInsertedChild");
        dao.insertAsLastChild(parent, child);

        var result = dao.findAll();
        assertEquals(2, result.size());

        var root = getByName(result, "rootNode");
        var firstInsertedChild = getByName(result, "firstInsertedChild");

        assertTrue(matches(root, "rootNode", 1, 4, 0));
        assertTrue(matches(firstInsertedChild, "firstInsertedChild", 2, 3, 1));
    }

    @Test
    public void insertAsLastChildWhenChildExists() {
        var parent = getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = getNode("firstInsertedChild");
        dao.insertAsLastChild(parent, child1);

        var child2 = getNode("secondInsertedChild");
        dao.insertAsLastChild(parent, child2);

        var result = dao.findAll();
        assertEquals(3, result.size());

        var root = getByName(result, "rootNode");
        var firstInsertedChild = getByName(result, "firstInsertedChild");
        var secondInsertedChild = getByName(result, "secondInsertedChild");

        assertTrue(matches(root, "rootNode", 1, 6, 0));
        assertTrue(matches(firstInsertedChild, "firstInsertedChild", 2, 3, 1));
        assertTrue(matches(secondInsertedChild, "secondInsertedChild", 4, 5, 1));
    }

    @Test
    public void hasChildren() {
        var parent = getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = getNode("firstInsertedChild");
        dao.insertAsLastChild(parent, child1);

        assertTrue(dao.hasChildren(parent));
    }

    @Test
    public void hasNoChildren() {
        var parent = getNode("rootNode");
        dao.insertAsRoot(parent);

        assertFalse(dao.hasChildren(parent));
    }

    @Test
    public void getChildrenOf() {
        var parent = getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = getNode("firstInsertedChild");
        dao.insertAsLastChild(parent, child1);

        var child2 = getNode("secondInsertedChild");
        dao.insertAsLastChild(parent, child2);

        var child3 = getNode("thirdInsertedChild");
        dao.insertAsLastChild(parent, child3);

        var grandchild1 = getNode("firstGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var grandchild2 = getNode("secondGrandchild");
        dao.insertAsLastChild(child1, grandchild2);

        var grandchild3 = getNode("thirdGandchild");
        dao.insertAsLastChild(child3, grandchild3);

        var childrenOfParent = dao.getChildren(parent);
        assertEquals(3, childrenOfParent.size());

        var firstInsertedChild = getByName(childrenOfParent, "firstInsertedChild");
        var secondInsertedChild = getByName(childrenOfParent, "secondInsertedChild");
        var thirdInsertedChild = getByName(childrenOfParent, "thirdInsertedChild");

        assertTrue(matches(firstInsertedChild, "firstInsertedChild", 2, 7, 1));
        assertTrue(matches(secondInsertedChild, "secondInsertedChild", 8, 9, 1));
        assertTrue(matches(thirdInsertedChild, "thirdInsertedChild", 10, 13, 1));

        var childrenOfChild1 = dao.getChildren(child1);
        assertEquals(2, childrenOfChild1.size());

        var firstGrandchild = getByName(childrenOfChild1, "firstGrandchild");
        var secondGrandchild = getByName(childrenOfChild1, "secondGrandchild");

        assertTrue(matches(firstGrandchild, "firstGrandchild", 3, 4, 2));
        assertTrue(matches(secondGrandchild, "secondGrandchild", 5, 6, 2));
    }

    @Test
    public void moveAsFirstChild() {
        var parent = getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = getNode("firstInsertedChild");
        dao.insertAsLastChild(parent, child1);

        var child2 = getNode("secondInsertedChild");
        dao.insertAsLastChild(parent, child2);

        var child3 = getNode("thirdInsertedChild");
        dao.insertAsLastChild(parent, child3);

        var grandchild1 = getNode("firstGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var grandchild2 = getNode("secondGrandchild");
        dao.insertAsLastChild(child1, grandchild2);

        var grandchild3 = getNode("thirdGandchild");
        dao.insertAsLastChild(child3, grandchild3);

        dao.moveAsFirstChild(grandchild2, child2);

        var result = dao.findAll();
        assertEquals(7, result.size());


        var rootNode = getByName(result, "rootNode");
        var firstInsertedChild = getByName(result, "firstInsertedChild");
        var firstGrandchild = getByName(result, "firstGrandchild");
        var secondInsertedChild = getByName(result, "secondInsertedChild");
        var secondGrandchild = getByName(result, "secondGrandchild");
        var thirdInsertedChild = getByName(result, "thirdInsertedChild");
        var thirdGandchild = getByName(result, "thirdGandchild");

        assertTrue(matches(rootNode, "rootNode", 1, 14, 0));
        assertTrue(matches(firstInsertedChild, "firstInsertedChild", 2, 5, 1));
        assertTrue(matches(firstGrandchild, "firstGrandchild", 3, 4, 2));
        assertTrue(matches(secondInsertedChild, "secondInsertedChild", 6, 9, 1));
        assertTrue(matches(secondGrandchild, "secondGrandchild", 7, 8, 2));
        assertTrue(matches(thirdInsertedChild, "thirdInsertedChild", 10, 13, 1));
        assertTrue(matches(thirdGandchild, "thirdGandchild", 11, 12, 2));
    }
}
