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
    public void getDescendantsOf() {
        var parent = getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = getNode("firstInsertedChild");
        dao.insertAsLastChildOf(parent, child1);

        var child2 = getNode("secondInsertedChild");
        dao.insertAsLastChildOf(parent, child2);

        var grandchild1 = getNode("firstInsertedGrandchild");
        dao.insertAsLastChildOf(child1, grandchild1);

        var result = dao.getDescendantsOf(parent);
        assertEquals(3, result.size());

        var firstInsertedChild = getByName(result, "firstInsertedChild");
        var secondInsertedChild = getByName(result, "secondInsertedChild");
        var firstGrandchild = getByName(result, "firstInsertedGrandchild");

        assertTrue(matches(firstInsertedChild, "firstInsertedChild", 2, 5, 1));
        assertTrue(matches(firstGrandchild, "firstInsertedGrandchild", 3, 4, 2));
        assertTrue(matches(secondInsertedChild, "secondInsertedChild", 6, 7, 1));
    }
}
