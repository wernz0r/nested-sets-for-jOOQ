package de.wernzor.nestedset4jooq;

import de.wernzor.nestedset4jooq.dao.CategoryNestedSetDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RootTest extends AbstractNestedSetTest {

    @Autowired
    RootTest(CategoryNestedSetDao dao) {
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
}
