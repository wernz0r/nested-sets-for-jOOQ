package de.wernzor.nestedset4jooq;

import de.wernzor.nestedset4jooq.dao.CategoryNestedSetDao;
import de.wernzor.nestedset4jooq.dto.CategoryNode;
import de.wernzor.nestedset4jooq.model.TreeNode;
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

    /**
     * Creates a CategoryNodewith a given name.
     *
     * @param name Name of the Cateory
     * @return CategoryNode
     */
    CategoryNode getNode(String name) {
        final CategoryNode node = new CategoryNode();
        node.setName(name);
        return node;
    }

    /**
     * Checks the name, left, right and level values of a given node.
     *
     * @param node  node to check
     * @param name  expected name of the node
     * @param left  expected left value
     * @param right expected right value
     * @param level expected level
     * @return true, when the node values matches. otherwise false
     */
    boolean matches(CategoryNode node, String name, long left, long right, long level) {
        return name.equals(node.getName()) &&
                node.getLeft() == left &&
                node.getRight() == right &&
                node.getLevel() == level;
    }

    /**
     * Checks if a list of category nodes contains a node with the give name, left, right and level value.
     *
     * @param list  list to be searched
     * @param name  name of the node
     * @param left  left value of the node
     * @param right right value of the node
     * @param level level of the node
     * @return true, when the list contains a matching node. otherwise false
     */
    boolean contains(List<CategoryNode> list, String name, long left, long right, long level) {
        return list.stream().anyMatch(n -> matches(n, name, left, right, level));
    }

    /**
     * Checks if a TreeNode contains a node with the give name, left, right and level value.
     *
     * @param node  TreeNode to be searched
     * @param name  name of the node
     * @param left  left value of the node
     * @param right right value of the node
     * @param level level of the node
     * @return true, when the TreeNode contains a matching node. otherwise false
     */
    boolean contains(TreeNode<CategoryNode> node, String name, long left, long right, long level) {
        if (matches(node.getNode(), name, left, right, level)) {
            return true;
        }

        for (TreeNode<CategoryNode> child : node.getChildren()) {
            if (contains(child, name, left, right, level)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Searches a list of CateoryNodes for a node with a given name. Returns the node with the name, if it can be
     * found inside the list.
     *
     * @param nodes List of nodes
     * @param name  name of node which will be searched
     * @return node if it can be found. null otherwise.
     */
    CategoryNode getByName(List<CategoryNode> nodes, String name) {
        return nodes.stream()
                .filter(n -> name.equals(n.getName()))
                .findFirst()
                .orElse(null);
    }

}