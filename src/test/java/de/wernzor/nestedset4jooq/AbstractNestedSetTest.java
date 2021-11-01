package de.wernzor.nestedset4jooq;

import de.wernzor.nestedset4jooq.dao.CategoryNestedSetDao;
import de.wernzor.nestedset4jooq.dto.CategoryNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
abstract class AbstractNestedSetTest {

    final CategoryNestedSetDao dao;

    @Autowired
    AbstractNestedSetTest(CategoryNestedSetDao dao) {
        this.dao = dao;
    }

    CategoryNode getNode(String name) {
        final CategoryNode node = new CategoryNode();
        node.setName(name);
        return node;
    }

    boolean matches(CategoryNode node, String name, long left, long right, long level) {
        return name.equals(node.getName()) &&
                node.getLeft() == left &&
                node.getRight() == right &&
                node.getLevel() == level;
    }

    CategoryNode getByName(List<CategoryNode> nodes, String name) {
        return nodes.stream()
                .filter(n -> name.equals(n.getName()))
                .findFirst()
                .orElse(null);
    }

}