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

        assertTrue(contains(result, "rootNode", 1, 4, 0));
        assertTrue(contains(result, "firstInsertedChild", 2, 3, 1));
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

        assertTrue(contains(result, "rootNode", 1, 6, 0));
        assertTrue(contains(result, "secondInsertedChild", 2, 3, 1));
        assertTrue(contains(result, "firstInsertedChild", 4, 5, 1));
    }

    @Test
    public void insertAsLastChildWhenNoChildExists() {
        var parent = getNode("rootNode");
        dao.insertAsRoot(parent);

        var child = getNode("firstInsertedChild");
        dao.insertAsLastChild(parent, child);

        var result = dao.findAll();
        assertEquals(2, result.size());

        assertTrue(contains(result, "rootNode", 1, 4, 0));
        assertTrue(contains(result, "firstInsertedChild", 2, 3, 1));
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

        assertTrue(contains(result, "rootNode", 1, 6, 0));
        assertTrue(contains(result, "firstInsertedChild", 2, 3, 1));
        assertTrue(contains(result, "secondInsertedChild", 4, 5, 1));
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

        assertTrue(contains(childrenOfParent, "firstInsertedChild", 2, 7, 1));
        assertTrue(contains(childrenOfParent, "secondInsertedChild", 8, 9, 1));
        assertTrue(contains(childrenOfParent, "thirdInsertedChild", 10, 13, 1));

        var childrenOfChild1 = dao.getChildren(child1);
        assertEquals(2, childrenOfChild1.size());

        assertTrue(contains(childrenOfChild1, "firstGrandchild", 3, 4, 2));
        assertTrue(contains(childrenOfChild1, "secondGrandchild", 5, 6, 2));
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
        var thirdInsertedChild = getByName(result, "thirdInsertedChild");

        assertTrue(contains(result, "rootNode", 1, 14, 0));
        assertTrue(contains(result, "firstInsertedChild", 2, 5, 1));
        assertTrue(contains(result, "firstGrandchild", 3, 4, 2));
        assertTrue(contains(result, "secondInsertedChild", 6, 9, 1));
        assertTrue(contains(result, "secondGrandchild", 7, 8, 2));
        assertTrue(contains(result, "thirdInsertedChild", 10, 13, 1));
        assertTrue(contains(result, "thirdGandchild", 11, 12, 2));

        dao.moveAsFirstChild(thirdInsertedChild, rootNode);

        result = dao.findAll();
        assertEquals(7, result.size());

        assertTrue(contains(result, "rootNode", 1, 14, 0));
        assertTrue(contains(result, "thirdInsertedChild", 2, 5, 1));
        assertTrue(contains(result, "thirdGandchild", 3, 4, 2));
        assertTrue(contains(result, "firstInsertedChild", 6, 9, 1));
        assertTrue(contains(result, "firstGrandchild", 7, 8, 2));
        assertTrue(contains(result, "secondInsertedChild", 10, 13, 1));
        assertTrue(contains(result, "secondGrandchild", 11, 12, 2));

        var firstGrandchild = getByName(result, "firstGrandchild");
        var secondGrandchild = getByName(result, "secondGrandchild");

        dao.moveAsFirstChild(secondGrandchild, firstGrandchild);

        result = dao.findAll();
        assertEquals(7, result.size());

        assertTrue(contains(result, "rootNode", 1, 14, 0));
        assertTrue(contains(result, "thirdInsertedChild", 2, 5, 1));
        assertTrue(contains(result, "thirdGandchild", 3, 4, 2));
        assertTrue(contains(result, "firstInsertedChild", 6, 11, 1));
        assertTrue(contains(result, "firstGrandchild", 7, 10, 2));
        assertTrue(contains(result, "secondGrandchild", 8, 9, 3));
        assertTrue(contains(result, "secondInsertedChild", 12, 13, 1));
    }
}
