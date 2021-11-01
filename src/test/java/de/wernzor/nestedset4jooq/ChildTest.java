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
        dao.insertAsLastChildOf(parent, child);

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
        dao.insertAsLastChildOf(parent, child1);

        var child2 = getNode("secondInsertedChild");
        dao.insertAsLastChildOf(parent, child2);

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
        dao.insertAsLastChildOf(parent, child1);

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
        dao.insertAsLastChildOf(parent, child1);

        var child2 = getNode("secondInsertedChild");
        dao.insertAsLastChildOf(parent, child2);

        var child3 = getNode("thirdInsertedChild");
        dao.insertAsLastChildOf(parent, child3);

        var grandchild1 = getNode("firstGrandchild");
        dao.insertAsLastChildOf(child1, grandchild1);

        var grandchild2 = getNode("secondGrandchild");
        dao.insertAsLastChildOf(child1, grandchild2);

        var grandchild3 = getNode("thirdGandchild");
        dao.insertAsLastChildOf(child3, grandchild3);

        var childrenOfParent = dao.getChildrenOf(parent);
        assertEquals(3, childrenOfParent.size());

        var firstInsertedChild = getByName(childrenOfParent, "firstInsertedChild");
        var secondInsertedChild = getByName(childrenOfParent, "secondInsertedChild");
        var thirdInsertedChild = getByName(childrenOfParent, "thirdInsertedChild");

        assertTrue(matches(firstInsertedChild, "firstInsertedChild", 2, 7, 1));
        assertTrue(matches(secondInsertedChild, "secondInsertedChild", 8, 9, 1));
        assertTrue(matches(thirdInsertedChild, "thirdInsertedChild", 10, 13, 1));

        var childrenOfChild1 = dao.getChildrenOf(child1);
        assertEquals(2, childrenOfChild1.size());

        var firstGrandchild = getByName(childrenOfChild1, "firstGrandchild");
        var secondGrandchild = getByName(childrenOfChild1, "secondGrandchild");

        assertTrue(matches(firstGrandchild, "firstGrandchild", 3, 4, 2));
        assertTrue(matches(secondGrandchild, "secondGrandchild", 5, 6, 2));
    }
}
