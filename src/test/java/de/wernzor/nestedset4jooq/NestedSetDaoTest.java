package de.wernzor.nestedset4jooq;

import de.wernzor.nestedset4jooq.dao.CategoryNestedSetDao;
import de.wernzor.nestedset4jooq.dto.CategoryNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class NestedSetDaoTest {

    private final CategoryNestedSetDao dao;

    @Autowired
    NestedSetDaoTest(CategoryNestedSetDao dao) {
        this.dao = dao;
    }


    @Test
    public void createRoot() {
        CategoryNode node = getNode("a root");
        dao.insertAsRoot(node);

        List<CategoryNode> result = dao.findAll();
        assertEquals(1, result.size());
        CategoryNode firstNode = result.get(0);
        assertEquals("a root", firstNode.getName());
        assertEquals(1, firstNode.getLeft());
        assertEquals(2, firstNode.getRight());
        assertEquals(0, firstNode.getLevel());
    }

    private CategoryNode getNode(String name) {
        final CategoryNode node = new CategoryNode();
        node.setName(name);
        return node;
    }

}