package de.wernzor.nestedset4jooq;

import de.wernzor.nestedset4jooq.dao.CategoryNestedSetDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TreeTest extends AbstractNestedSetTest {

    @Autowired
    TreeTest(CategoryNestedSetDao dao) {
        super(dao);
    }

    @Test
    public void insertAsRoot() {
        var parent = getNode("rootNode");
        dao.insertAsRoot(parent);

        var result = dao.findAll();
        assertEquals(1, result.size());

        var rootNode = result.get(0);

        assertTrue(matches(rootNode, "rootNode", 1, 2, 0));
    }

    @Test
    public void getDescendantsOf() {
        var parent = getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = getNode("firstInsertedChild");
        dao.insertAsLastChild(parent, child1);

        var child2 = getNode("secondInsertedChild");
        dao.insertAsLastChild(parent, child2);

        var grandchild1 = getNode("firstInsertedGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var result = dao.getDescendants(parent);
        assertEquals(3, result.size());

        var firstInsertedChild = getByName(result, "firstInsertedChild");
        var secondInsertedChild = getByName(result, "secondInsertedChild");
        var firstGrandchild = getByName(result, "firstInsertedGrandchild");

        assertTrue(matches(firstInsertedChild, "firstInsertedChild", 2, 5, 1));
        assertTrue(matches(firstGrandchild, "firstInsertedGrandchild", 3, 4, 2));
        assertTrue(matches(secondInsertedChild, "secondInsertedChild", 6, 7, 1));
    }

    @Test
    public void getAncestorsOf() {
        var parent = getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = getNode("firstInsertedChild");
        dao.insertAsLastChild(parent, child1);

        var child2 = getNode("secondInsertedChild");
        dao.insertAsLastChild(parent, child2);

        var grandchild1 = getNode("firstInsertedGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var result = dao.getAncestors(grandchild1);
        assertEquals(2, result.size());

        var firstInsertedChild = getByName(result, "firstInsertedChild");
        var rootNode = getByName(result, "rootNode");

        assertTrue(matches(firstInsertedChild, "firstInsertedChild", 2, 5, 1));
        assertTrue(matches(rootNode, "rootNode", 1, 8, 0));
    }

    @Test
    public void getParent() {
        var parent = getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = getNode("firstInsertedChild");
        dao.insertAsLastChild(parent, child1);

        var child2 = getNode("secondInsertedChild");
        dao.insertAsLastChild(parent, child2);

        var grandchild1 = getNode("firstInsertedGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var parentOfChild2 = dao.getParent(child2);
        assertTrue(matches(parentOfChild2, "rootNode", 1, 8, 0));

        var parentOfGrandchild1 = dao.getParent(grandchild1);
        assertTrue(matches(parentOfGrandchild1, "firstInsertedChild", 2, 5, 1));
    }

    @Test
    public void deleteIncludingDescendants() {
        var parent = getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = getNode("firstInsertedChild");
        dao.insertAsLastChild(parent, child1);

        var grandchild1 = getNode("firstInsertedGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var child2 = getNode("secondInsertedChild");
        dao.insertAsLastChild(parent, child2);

        dao.delete(child1);

        var result = dao.findAll();
        assertEquals(2, result.size());

        var rootNode = getByName(result, "rootNode");
        var secondInsertedChild = getByName(result, "secondInsertedChild");

        assertTrue(matches(rootNode, "rootNode", 1, 4, 0));
        assertTrue(matches(secondInsertedChild, "secondInsertedChild", 2, 3, 1));
    }
}
