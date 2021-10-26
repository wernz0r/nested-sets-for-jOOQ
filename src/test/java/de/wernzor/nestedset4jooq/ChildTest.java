package de.wernzor.nestedset4jooq;

import de.wernzor.nestedset4jooq.dao.CategoryNestedSetDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChildTest extends AbstractNestedSetTest {

    @Autowired
    ChildTest(CategoryNestedSetDao dao) {
        super(dao);
    }

    @Test
    public void insertAsFirstChildWhenNoChildrenExist() {
        var parent = getNode("rootNode");
        dao.insertAsRoot(parent);

        var child = getNode("firstInsertedChild");
        dao.insertAsFirstChild(parent, child);

        var result = dao.findAll();
        assertEquals(2, result.size());

        var root = getByName(result, "rootNode");
        var firstInsertedChild = getByName(result, "firstInsertedChild");

        assertTrue(equals(root, "rootNode", 1, 4, 0));
        assertTrue(equals(firstInsertedChild, "firstInsertedChild", 2, 3, 1));
    }

    @Test
    public void insertAsFirstChildWhenChildrenExist() {
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

        assertTrue(equals(root, "rootNode", 1, 6, 0));
        assertTrue(equals(secondInsertedChild, "secondInsertedChild", 2, 3, 1));
        assertTrue(equals(firstInsertedChild, "firstInsertedChild", 4, 5, 1));
    }
}
