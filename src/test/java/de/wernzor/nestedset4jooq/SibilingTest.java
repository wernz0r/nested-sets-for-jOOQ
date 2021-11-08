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

}
