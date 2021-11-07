package de.wernzor.nestedset4jooq;

import de.wernzor.nestedset4jooq.dao.CategoryNestedSetDao;
import de.wernzor.nestedset4jooq.model.TreeNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

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

        assertTrue(contains(result, "rootNode", 1, 2, 0));
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

        assertTrue(contains(result, "firstInsertedChild", 2, 5, 1));
        assertTrue(contains(result, "firstInsertedGrandchild", 3, 4, 2));
        assertTrue(contains(result, "secondInsertedChild", 6, 7, 1));
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

        assertTrue(contains(result, "firstInsertedChild", 2, 5, 1));
        assertTrue(contains(result, "rootNode", 1, 8, 0));
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

        assertTrue(contains(result, "rootNode", 1, 4, 0));
        assertTrue(contains(result, "secondInsertedChild", 2, 3, 1));
    }

    @Test
    public void getDescendants() {
        var parent = getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = getNode("firstInsertedChild");
        dao.insertAsLastChild(parent, child1);

        var grandchild1 = getNode("firstInsertedGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var grandGrandchild = getNode("firstInsertedGrandGrandchild");
        dao.insertAsLastChild(grandchild1, grandGrandchild);

        var child2 = getNode("secondInsertedChild");
        dao.insertAsLastChild(parent, child2);

        var grandchild2 = getNode("secondInsertedGrandchild");
        dao.insertAsLastChild(child2, grandchild2);

        var result = dao.getDescendants(parent);
        assertEquals(5, result.size());

        result = dao.getDescendants(parent, 1);
        assertEquals(2, result.size());

        result = dao.getDescendants(parent, 2);
        assertEquals(4, result.size());

        result = dao.getDescendants(parent, 3);
        assertEquals(5, result.size());

        result = dao.getDescendants(parent, 10);
        assertEquals(5, result.size());

    }

    @Test
    public void getAncestors() {
        var parent = getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = getNode("firstInsertedChild");
        dao.insertAsLastChild(parent, child1);

        var grandchild1 = getNode("firstInsertedGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var grandGrandchild = getNode("firstInsertedGrandGrandchild");
        dao.insertAsLastChild(grandchild1, grandGrandchild);

        var child2 = getNode("secondInsertedChild");
        dao.insertAsLastChild(parent, child2);

        var grandchild2 = getNode("secondInsertedGrandchild");
        dao.insertAsLastChild(child2, grandchild2);

        var result = dao.getAncestors(grandGrandchild);
        assertEquals(3, result.size());

        result = dao.getAncestors(grandGrandchild, 1);
        assertEquals(1, result.size());
        assertTrue(contains(result, "firstInsertedGrandchild", 3, 6, 2));

        result = dao.getAncestors(grandGrandchild, 2);
        assertEquals(2, result.size());
        assertTrue(contains(result, "firstInsertedGrandchild", 3, 6, 2));
        assertTrue(contains(result, "firstInsertedChild", 2, 7, 1));

        result = dao.getAncestors(grandGrandchild, 3);
        assertEquals(3, result.size());
        assertTrue(contains(result, "firstInsertedGrandchild", 3, 6, 2));
        assertTrue(contains(result, "firstInsertedChild", 2, 7, 1));
        assertTrue(contains(result, "rootNode", 1, 12, 0));
    }

    @Test
    public void fetchTree() {
        var parent = getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = getNode("firstInsertedChild");
        dao.insertAsLastChild(parent, child1);

        var grandchild1 = getNode("firstGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var child2 = getNode("secondInsertedChild");
        dao.insertAsLastChild(parent, child2);

        var grandchild2 = getNode("secondGrandchild");
        dao.insertAsLastChild(child2, grandchild2);

        var child3 = getNode("thirdInsertedChild");
        dao.insertAsLastChild(parent, child3);

        var grandchild3 = getNode("thirdGandchild");
        dao.insertAsLastChild(child3, grandchild3);

        final var result = dao.fetchTree();

        assertTrue(matches(result.getNode(), "rootNode", 1, 14, 0));
        assertTrue(result.isRoot());
        assertTrue(result.hasChildren());
        assertEquals(3, result.getChildren().size());

        final var firstTreeNode = result.getChildren().get(0);
        assertTrue(matches(firstTreeNode.getNode(), "firstInsertedChild", 2, 5, 1));
        assertFalse(firstTreeNode.isRoot());
        assertTrue(firstTreeNode.hasChildren());
        assertEquals(1, firstTreeNode.getChildren().size());
        assertTrue(matches(firstTreeNode.getChildren().get(0).getNode(), "firstGrandchild", 3, 4, 2));
        assertFalse(firstTreeNode.getChildren().get(0).hasChildren());

        final var secondTreeNode = result.getChildren().get(1);
        assertTrue(matches(secondTreeNode.getNode(), "secondInsertedChild", 6, 9, 1));
        assertFalse(secondTreeNode.isRoot());
        assertTrue(secondTreeNode.hasChildren());
        assertEquals(1, secondTreeNode.getChildren().size());
        assertTrue(matches(secondTreeNode.getChildren().get(0).getNode(), "secondGrandchild", 7, 8, 2));
        assertFalse(secondTreeNode.getChildren().get(0).hasChildren());

        final var thirdTreeNode = result.getChildren().get(2);
        assertTrue(matches(thirdTreeNode.getNode(), "thirdInsertedChild", 10, 13, 1));
        assertFalse(thirdTreeNode.isRoot());
        assertTrue(thirdTreeNode.hasChildren());
        assertEquals(1, thirdTreeNode.getChildren().size());
        assertTrue(matches(thirdTreeNode.getChildren().get(0).getNode(), "thirdGandchild", 11, 12, 2));
        assertFalse(thirdTreeNode.getChildren().get(0).hasChildren());
    }

    @Test
    public void insertTree() {
        var rootNode = new TreeNode<>(getNode("root"));

        var firstChildNode = new TreeNode<>(getNode("firstChild"));
        firstChildNode.addChild(getNode("firstGrandchild"));
        firstChildNode.addChild(getNode("secondGrandchild"));
        firstChildNode.getChildren().get(1).addChild(getNode("firstGreatGrandchild"));

        var secondChildNode = new TreeNode<>(getNode("secondChild"));
        secondChildNode.addChild(getNode("thirdGrandchild"));

        rootNode.addChild(firstChildNode);
        rootNode.addChild(secondChildNode);

        dao.insertTree(rootNode);

        var result = dao.findAll();
        assertEquals(7, result.size());

        assertTrue(contains(result, "root", 1, 14, 0));
        assertTrue(contains(result, "firstChild", 2, 9, 1));
        assertTrue(contains(result, "firstGrandchild", 3, 4, 2));
        assertTrue(contains(result, "secondGrandchild", 5, 8, 2));
        assertTrue(contains(result, "firstGreatGrandchild", 6, 7, 3));
        assertTrue(contains(result, "secondChild", 10, 13, 1));
        assertTrue(contains(result, "thirdGrandchild", 11, 12, 2));

        var resultAsTree = dao.fetchTree();
        assertEquals(7, resultAsTree.size());
    }

    @Test
    public void fetchTreeWithNode() {
        var rootNode = new TreeNode<>(getNode("root"));

        var firstChildNode = new TreeNode<>(getNode("firstChild"));
        firstChildNode.addChild(getNode("firstGrandchild"));
        firstChildNode.addChild(getNode("secondGrandchild"));
        firstChildNode.getChildren().get(1).addChild(getNode("firstGreatGrandchild"));

        var secondChildNode = new TreeNode<>(getNode("secondChild"));
        secondChildNode.addChild(getNode("thirdGrandchild"));

        rootNode.addChild(firstChildNode);
        rootNode.addChild(secondChildNode);

        dao.insertTree(rootNode);

        var allNodes = dao.findAll();

        var result = dao.fetchTree(getByName(allNodes, "firstChild"));

        assertEquals(4, result.size());
        assertTrue(contains(result, "firstChild", 2, 9, 1));
        assertTrue(contains(result, "firstGrandchild", 3, 4, 2));
        assertTrue(contains(result, "secondGrandchild", 5, 8, 2));
        assertTrue(contains(result, "firstGreatGrandchild", 6, 7, 3));
    }
}
