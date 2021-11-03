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

        var child1 = getNode("firstInsertedChild");
        dao.insertAsLastChild(parent, child1);

        var child2 = getNode("secondInsertedChild");
        dao.insertAsLastChild(parent, child2);

        var sibling1 = getNode("firstInsertedSibling");
        dao.insertAsPrevSibling(child2, sibling1);

        var result = dao.findAll();
        assertEquals(4, result.size());

        var root = getByName(result, "rootNode");
        var firstInsertedChild = getByName(result, "firstInsertedChild");
        var secondInsertedChild = getByName(result, "secondInsertedChild");
        var firstSibling = getByName(result, "firstInsertedSibling");

        assertTrue(matches(root, "rootNode", 1, 8, 0));
        assertTrue(matches(firstInsertedChild, "firstInsertedChild", 2, 3, 1));
        assertTrue(matches(firstSibling, "firstInsertedSibling", 4, 5, 1));
        assertTrue(matches(secondInsertedChild, "secondInsertedChild", 6, 7, 1));
    }

    @Test
    public void insertAsNextSiblingOf() {
        var parent = getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = getNode("firstInsertedChild");
        dao.insertAsLastChild(parent, child1);

        var child2 = getNode("secondInsertedChild");
        dao.insertAsLastChild(parent, child2);

        var sibling1 = getNode("firstInsertedSibling");
        dao.insertAsNextSibling(child1, sibling1);

        var result = dao.findAll();
        assertEquals(4, result.size());

        var root = getByName(result, "rootNode");
        var firstInsertedChild = getByName(result, "firstInsertedChild");
        var secondInsertedChild = getByName(result, "secondInsertedChild");
        var firstSibling = getByName(result, "firstInsertedSibling");

        assertTrue(matches(root, "rootNode", 1, 8, 0));
        assertTrue(matches(firstInsertedChild, "firstInsertedChild", 2, 3, 1));
        assertTrue(matches(firstSibling, "firstInsertedSibling", 4, 5, 1));
        assertTrue(matches(secondInsertedChild, "secondInsertedChild", 6, 7, 1));
    }

}
