package de.wernzor.nestedset4jooq;

import de.wernzor.nestedset4jooq.dao.CategoryNestedSetDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SibilingTest extends AbstractNestedSetTest {

    @Autowired
    SibilingTest(CategoryNestedSetDao dao) {
        super(dao);
    }

    @Test
    public void insertAsPrevSiblingOf() {
        var parent = getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = getNode("firstChild");
        dao.insertAsLastChild(parent, child1);

        var child2 = getNode("secondChild");
        dao.insertAsLastChild(parent, child2);

        var sibling1 = getNode("firstSibling");
        dao.insertAsPrevSibling(child2, sibling1);

        var result = dao.findAll();
        assertEquals(4, result.size());

        assertTrue(contains(result, "rootNode", 1, 8, 0));
        assertTrue(contains(result, "firstChild", 2, 3, 1));
        assertTrue(contains(result, "firstSibling", 4, 5, 1));
        assertTrue(contains(result, "secondChild", 6, 7, 1));
    }

    @Test
    public void insertAsNextSiblingOf() {
        var parent = getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = getNode("firstChild");
        dao.insertAsLastChild(parent, child1);

        var child2 = getNode("secondChild");
        dao.insertAsLastChild(parent, child2);

        var sibling1 = getNode("firstSibling");
        dao.insertAsNextSibling(child1, sibling1);

        var result = dao.findAll();
        assertEquals(4, result.size());

        assertTrue(contains(result, "rootNode", 1, 8, 0));
        assertTrue(contains(result, "firstChild", 2, 3, 1));
        assertTrue(contains(result, "firstSibling", 4, 5, 1));
        assertTrue(contains(result, "secondChild", 6, 7, 1));
    }

    @Test
    public void moveAsNextSibling() {
        var root = getNode("rootNode");
        dao.insertAsRoot(root);

        var child1 = getNode("firstChild");
        dao.insertAsLastChild(root, child1);

        var grandchild1 = getNode("firstGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var child2 = getNode("secondChild");
        dao.insertAsLastChild(root, child2);

        var grandchild2 = getNode("secondGrandchild");
        dao.insertAsLastChild(child2, grandchild2);

        var child3 = getNode("thirdChild");
        dao.insertAsLastChild(root, child3);

        var grandchild3 = getNode("thirdGrandchild");
        dao.insertAsLastChild(child3, grandchild3);

        dao.moveAsNextSibling(child3, child1);

        var result = dao.findAll();
        assertEquals(7, result.size());

        assertTrue(contains(result, "rootNode", 1, 14, 0));
        assertTrue(contains(result, "firstChild", 2, 5, 1));
        assertTrue(contains(result, "firstGrandchild", 3, 4, 2));
        assertTrue(contains(result, "thirdChild", 6, 9, 1));
        assertTrue(contains(result, "thirdGrandchild", 7, 8, 2));
        assertTrue(contains(result, "secondChild", 10, 13, 1));
        assertTrue(contains(result, "secondGrandchild", 11, 12, 2));

        dao.moveAsNextSibling(grandchild3, grandchild1);

        result = dao.findAll();
        assertEquals(7, result.size());

        assertTrue(contains(result, "rootNode", 1, 14, 0));
        assertTrue(contains(result, "firstChild", 2, 7, 1));
        assertTrue(contains(result, "firstGrandchild", 3, 4, 2));
        assertTrue(contains(result, "thirdGrandchild", 5, 6, 2));
        assertTrue(contains(result, "thirdChild", 8, 9, 1));
        assertTrue(contains(result, "secondChild", 10, 13, 1));
        assertTrue(contains(result, "secondGrandchild", 11, 12, 2));
    }

    @Test
    public void moveAsPrevSibling() {
        var root = getNode("rootNode");
        dao.insertAsRoot(root);

        var child1 = getNode("firstChild");
        dao.insertAsLastChild(root, child1);

        var grandchild1 = getNode("firstGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var child2 = getNode("secondChild");
        dao.insertAsLastChild(root, child2);

        var grandchild2 = getNode("secondGrandchild");
        dao.insertAsLastChild(child2, grandchild2);

        var child3 = getNode("thirdChild");
        dao.insertAsLastChild(root, child3);

        var grandchild3 = getNode("thirdGrandchild");
        dao.insertAsLastChild(child3, grandchild3);

        dao.moveAsPrevSibling(child3, child1);

        var result = dao.findAll();
        assertEquals(7, result.size());

        assertTrue(contains(result, "rootNode", 1, 14, 0));
        assertTrue(contains(result, "thirdChild", 2, 5, 1));
        assertTrue(contains(result, "thirdGrandchild", 3, 4, 2));
        assertTrue(contains(result, "firstChild", 6, 9, 1));
        assertTrue(contains(result, "firstGrandchild", 7, 8, 2));
        assertTrue(contains(result, "secondChild", 10, 13, 1));
        assertTrue(contains(result, "secondGrandchild", 11, 12, 2));

        dao.moveAsPrevSibling(grandchild2, grandchild1);

        result = dao.findAll();
        assertEquals(7, result.size());

        assertTrue(contains(result, "rootNode", 1, 14, 0));
        assertTrue(contains(result, "thirdChild", 2, 5, 1));
        assertTrue(contains(result, "thirdGrandchild", 3, 4, 2));
        assertTrue(contains(result, "firstChild", 6, 11, 1));
        assertTrue(contains(result, "secondGrandchild", 7, 8, 2));
        assertTrue(contains(result, "firstGrandchild", 9, 10, 2));
        assertTrue(contains(result, "secondChild", 12, 13, 1));
    }

}
