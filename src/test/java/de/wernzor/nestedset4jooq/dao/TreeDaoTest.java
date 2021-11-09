package de.wernzor.nestedset4jooq.dao;

import de.wernzor.nestedset4jooq.helper.TestHelper;
import de.wernzor.nestedset4jooq.model.CategoryNode;
import de.wernzor.nestedset4jooq.model.tree.TreeNode;
import de.wernzor.nestedset4jooq.test.tables.pojos.Category;
import de.wernzor.nestedset4jooq.test.tables.records.CategoryRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class TreeDaoTest extends AbstractNestedSetTest {

    private final TreeDao<CategoryRecord, CategoryNode, Category, Long> treeDao;

    @Autowired
    TreeDaoTest(CategoryNestedSetDao dao) {
        super(dao);
        treeDao = new TreeDao<>(dao);
    }

    @Test
    public void insertTree() {
        var rootNode = new TreeNode<>(TestHelper.getNode("root"));

        var firstChildNode = new TreeNode<>(TestHelper.getNode("firstChild"));
        firstChildNode.addChild(TestHelper.getNode("firstGrandchild"));
        firstChildNode.addChild(TestHelper.getNode("secondGrandchild"));
        firstChildNode.getChildren().get(1).addChild(TestHelper.getNode("firstGreatGrandchild"));

        var secondChildNode = new TreeNode<>(TestHelper.getNode("secondChild"));
        secondChildNode.addChild(TestHelper.getNode("thirdGrandchild"));

        rootNode.addChild(firstChildNode);
        rootNode.addChild(secondChildNode);

        treeDao.insertTree(rootNode);

        var result = dao.findAll();
        assertEquals(7, result.size());

        assertTrue(TestHelper.contains(result, "root", 1, 14, 0));
        assertTrue(TestHelper.contains(result, "firstChild", 2, 9, 1));
        assertTrue(TestHelper.contains(result, "firstGrandchild", 3, 4, 2));
        assertTrue(TestHelper.contains(result, "secondGrandchild", 5, 8, 2));
        assertTrue(TestHelper.contains(result, "firstGreatGrandchild", 6, 7, 3));
        assertTrue(TestHelper.contains(result, "secondChild", 10, 13, 1));
        assertTrue(TestHelper.contains(result, "thirdGrandchild", 11, 12, 2));

        var resultAsTree = treeDao.fetchTree();
        assertEquals(7, resultAsTree.size());
    }

    @Test
    public void fetchTree() {
        var parent = TestHelper.getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = TestHelper.getNode("firstChild");
        dao.insertAsLastChild(parent, child1);

        var grandchild1 = TestHelper.getNode("firstGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var child2 = TestHelper.getNode("secondChild");
        dao.insertAsLastChild(parent, child2);

        var grandchild2 = TestHelper.getNode("secondGrandchild");
        dao.insertAsLastChild(child2, grandchild2);

        var child3 = TestHelper.getNode("thirdChild");
        dao.insertAsLastChild(parent, child3);

        var grandchild3 = TestHelper.getNode("thirdGandchild");
        dao.insertAsLastChild(child3, grandchild3);

        final var result = treeDao.fetchTree();

        assertTrue(TestHelper.matches(result.getNode(), "rootNode", 1, 14, 0));
        assertTrue(result.isRoot());
        assertTrue(result.hasChildren());
        assertEquals(3, result.getChildren().size());

        final var firstTreeNode = result.getChildren().get(0);
        assertTrue(TestHelper.matches(firstTreeNode.getNode(), "firstChild", 2, 5, 1));
        assertFalse(firstTreeNode.isRoot());
        assertTrue(firstTreeNode.hasChildren());
        assertEquals(1, firstTreeNode.getChildren().size());
        assertTrue(TestHelper.matches(firstTreeNode.getChildren().get(0).getNode(), "firstGrandchild", 3, 4, 2));
        assertFalse(firstTreeNode.getChildren().get(0).hasChildren());

        final var secondTreeNode = result.getChildren().get(1);
        assertTrue(TestHelper.matches(secondTreeNode.getNode(), "secondChild", 6, 9, 1));
        assertFalse(secondTreeNode.isRoot());
        assertTrue(secondTreeNode.hasChildren());
        assertEquals(1, secondTreeNode.getChildren().size());
        assertTrue(TestHelper.matches(secondTreeNode.getChildren().get(0).getNode(), "secondGrandchild", 7, 8, 2));
        assertFalse(secondTreeNode.getChildren().get(0).hasChildren());

        final var thirdTreeNode = result.getChildren().get(2);
        assertTrue(TestHelper.matches(thirdTreeNode.getNode(), "thirdChild", 10, 13, 1));
        assertFalse(thirdTreeNode.isRoot());
        assertTrue(thirdTreeNode.hasChildren());
        assertEquals(1, thirdTreeNode.getChildren().size());
        assertTrue(TestHelper.matches(thirdTreeNode.getChildren().get(0).getNode(), "thirdGandchild", 11, 12, 2));
        assertFalse(thirdTreeNode.getChildren().get(0).hasChildren());
    }

    @Test
    public void fetchTreeWithNode() {
        var rootNode = new TreeNode<>(TestHelper.getNode("root"));

        var firstChildNode = new TreeNode<>(TestHelper.getNode("firstChild"));
        firstChildNode.addChild(TestHelper.getNode("firstGrandchild"));
        firstChildNode.addChild(TestHelper.getNode("secondGrandchild"));
        firstChildNode.getChildren().get(1).addChild(TestHelper.getNode("firstGreatGrandchild"));

        var secondChildNode = new TreeNode<>(TestHelper.getNode("secondChild"));
        secondChildNode.addChild(TestHelper.getNode("thirdGrandchild"));

        rootNode.addChild(firstChildNode);
        rootNode.addChild(secondChildNode);

        treeDao.insertTree(rootNode);

        var allNodes = dao.findAll();

        var result = treeDao.fetchTree(TestHelper.getByName(allNodes, "firstChild"));

        assertEquals(4, result.size());
        assertTrue(TestHelper.contains(result, "firstChild", 2, 9, 1));
        assertTrue(TestHelper.contains(result, "firstGrandchild", 3, 4, 2));
        assertTrue(TestHelper.contains(result, "secondGrandchild", 5, 8, 2));
        assertTrue(TestHelper.contains(result, "firstGreatGrandchild", 6, 7, 3));
    }
}