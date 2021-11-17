package de.wernz0r.nestedsets4jooq.dao;

import de.wernz0r.nestedsets4jooq.helper.TestHelper;
import de.wernz0r.nestedsets4jooq.model.CategoryNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AbstractNestedSetTest {

    public final NestedSetDao<CategoryNode> dao;

    @Autowired
    public AbstractNestedSetTest(NestedSetDao<CategoryNode> dao) {
        this.dao = dao;
    }

    @Test
    public void insertAsFirstChild() {
        var parent = TestHelper.getNode("rootNode");
        dao.insertAsRoot(parent);

        var child = TestHelper.getNode("firstChild");
        dao.insertAsFirstChild(parent, child);

        var result = dao.findAll();
        assertEquals(2, result.size());

        assertTrue(TestHelper.contains(result, "rootNode", 1, 4, 0));
        assertTrue(TestHelper.contains(result, "firstChild", 2, 3, 1));

        var child2 = TestHelper.getNode("secondChild");
        dao.insertAsFirstChild(parent, child2);

        result = dao.findAll();
        assertEquals(3, result.size());

        assertTrue(TestHelper.contains(result, "rootNode", 1, 6, 0));
        assertTrue(TestHelper.contains(result, "secondChild", 2, 3, 1));
        assertTrue(TestHelper.contains(result, "firstChild", 4, 5, 1));
    }

    @Test
    public void insertAsLastChild() {
        var parent = TestHelper.getNode("rootNode");
        dao.insertAsRoot(parent);

        var child = TestHelper.getNode("firstChild");
        dao.insertAsLastChild(parent, child);

        var result = dao.findAll();
        assertEquals(2, result.size());

        assertTrue(TestHelper.contains(result, "rootNode", 1, 4, 0));
        assertTrue(TestHelper.contains(result, "firstChild", 2, 3, 1));

        var child2 = TestHelper.getNode("secondChild");
        dao.insertAsLastChild(parent, child2);

        result = dao.findAll();
        assertEquals(3, result.size());

        assertTrue(TestHelper.contains(result, "rootNode", 1, 6, 0));
        assertTrue(TestHelper.contains(result, "firstChild", 2, 3, 1));
        assertTrue(TestHelper.contains(result, "secondChild", 4, 5, 1));
    }

    @Test
    public void hasChildren() {
        var parent = TestHelper.getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = TestHelper.getNode("firstChild");
        dao.insertAsLastChild(parent, child1);

        assertTrue(dao.hasChildren(parent));
    }

    @Test
    public void hasNoChildren() {
        var parent = TestHelper.getNode("rootNode");
        dao.insertAsRoot(parent);

        assertFalse(dao.hasChildren(parent));
    }

    @Test
    public void getChildrenOf() {
        var parent = TestHelper.getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = TestHelper.getNode("firstChild");
        dao.insertAsLastChild(parent, child1);

        var child2 = TestHelper.getNode("secondChild");
        dao.insertAsLastChild(parent, child2);

        var child3 = TestHelper.getNode("thirdChild");
        dao.insertAsLastChild(parent, child3);

        var grandchild1 = TestHelper.getNode("firstGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var grandchild2 = TestHelper.getNode("secondGrandchild");
        dao.insertAsLastChild(child1, grandchild2);

        var grandchild3 = TestHelper.getNode("thirdGrandchild");
        dao.insertAsLastChild(child3, grandchild3);

        var childrenOfParent = dao.getChildren(parent);
        assertEquals(3, childrenOfParent.size());

        assertTrue(TestHelper.contains(childrenOfParent, "firstChild", 2, 7, 1));
        assertTrue(TestHelper.contains(childrenOfParent, "secondChild", 8, 9, 1));
        assertTrue(TestHelper.contains(childrenOfParent, "thirdChild", 10, 13, 1));

        var childrenOfChild1 = dao.getChildren(child1);
        assertEquals(2, childrenOfChild1.size());

        assertTrue(TestHelper.contains(childrenOfChild1, "firstGrandchild", 3, 4, 2));
        assertTrue(TestHelper.contains(childrenOfChild1, "secondGrandchild", 5, 6, 2));
    }

    @Test
    public void moveAsFirstChild() {
        var parent = TestHelper.getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = TestHelper.getNode("firstChild");
        dao.insertAsLastChild(parent, child1);

        var child2 = TestHelper.getNode("secondChild");
        dao.insertAsLastChild(parent, child2);

        var child3 = TestHelper.getNode("thirdChild");
        dao.insertAsLastChild(parent, child3);

        var grandchild1 = TestHelper.getNode("firstGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var grandchild2 = TestHelper.getNode("secondGrandchild");
        dao.insertAsLastChild(child1, grandchild2);

        var grandchild3 = TestHelper.getNode("thirdGrandchild");
        dao.insertAsLastChild(child3, grandchild3);

        dao.moveAsFirstChild(grandchild2, child2);

        var result = dao.findAll();
        assertEquals(7, result.size());

        assertTrue(TestHelper.contains(result, "rootNode", 1, 14, 0));
        assertTrue(TestHelper.contains(result, "firstChild", 2, 5, 1));
        assertTrue(TestHelper.contains(result, "firstGrandchild", 3, 4, 2));
        assertTrue(TestHelper.contains(result, "secondChild", 6, 9, 1));
        assertTrue(TestHelper.contains(result, "secondGrandchild", 7, 8, 2));
        assertTrue(TestHelper.contains(result, "thirdChild", 10, 13, 1));
        assertTrue(TestHelper.contains(result, "thirdGrandchild", 11, 12, 2));

        var rootNode = TestHelper.getByName(result, "rootNode");
        var thirdChild = TestHelper.getByName(result, "thirdChild");

        dao.moveAsFirstChild(thirdChild, rootNode);

        result = dao.findAll();
        assertEquals(7, result.size());

        assertTrue(TestHelper.contains(result, "rootNode", 1, 14, 0));
        assertTrue(TestHelper.contains(result, "thirdChild", 2, 5, 1));
        assertTrue(TestHelper.contains(result, "thirdGrandchild", 3, 4, 2));
        assertTrue(TestHelper.contains(result, "firstChild", 6, 9, 1));
        assertTrue(TestHelper.contains(result, "firstGrandchild", 7, 8, 2));
        assertTrue(TestHelper.contains(result, "secondChild", 10, 13, 1));
        assertTrue(TestHelper.contains(result, "secondGrandchild", 11, 12, 2));

        var firstGrandchild = TestHelper.getByName(result, "firstGrandchild");
        var secondGrandchild = TestHelper.getByName(result, "secondGrandchild");

        dao.moveAsFirstChild(secondGrandchild, firstGrandchild);

        result = dao.findAll();
        assertEquals(7, result.size());

        assertTrue(TestHelper.contains(result, "rootNode", 1, 14, 0));
        assertTrue(TestHelper.contains(result, "thirdChild", 2, 5, 1));
        assertTrue(TestHelper.contains(result, "thirdGrandchild", 3, 4, 2));
        assertTrue(TestHelper.contains(result, "firstChild", 6, 11, 1));
        assertTrue(TestHelper.contains(result, "firstGrandchild", 7, 10, 2));
        assertTrue(TestHelper.contains(result, "secondGrandchild", 8, 9, 3));
        assertTrue(TestHelper.contains(result, "secondChild", 12, 13, 1));
    }

    @Test
    public void moveAsLastChild() {
        var parent = TestHelper.getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = TestHelper.getNode("firstChild");
        dao.insertAsLastChild(parent, child1);

        var child2 = TestHelper.getNode("secondChild");
        dao.insertAsLastChild(parent, child2);

        var child3 = TestHelper.getNode("thirdChild");
        dao.insertAsLastChild(parent, child3);

        var grandchild1 = TestHelper.getNode("firstGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var grandchild2 = TestHelper.getNode("secondGrandchild");
        dao.insertAsLastChild(child1, grandchild2);

        var grandchild3 = TestHelper.getNode("thirdGrandchild");
        dao.insertAsLastChild(child3, grandchild3);

        dao.moveAsLastChild(grandchild1, child3);

        var result = dao.findAll();
        assertEquals(7, result.size());

        assertTrue(TestHelper.contains(result, "rootNode", 1, 14, 0));
        assertTrue(TestHelper.contains(result, "firstChild", 2, 5, 1));
        assertTrue(TestHelper.contains(result, "secondGrandchild", 3, 4, 2));
        assertTrue(TestHelper.contains(result, "secondChild", 6, 7, 1));
        assertTrue(TestHelper.contains(result, "thirdChild", 8, 13, 1));
        assertTrue(TestHelper.contains(result, "thirdGrandchild", 9, 10, 2));
        assertTrue(TestHelper.contains(result, "firstGrandchild", 11, 12, 2));

        var rootNode = TestHelper.getByName(result, "rootNode");
        var secondGrandchild = TestHelper.getByName(result, "secondGrandchild");

        dao.moveAsLastChild(secondGrandchild, rootNode);

        result = dao.findAll();
        assertEquals(7, result.size());

        assertTrue(TestHelper.contains(result, "rootNode", 1, 14, 0));
        assertTrue(TestHelper.contains(result, "firstChild", 2, 3, 1));
        assertTrue(TestHelper.contains(result, "secondChild", 4, 5, 1));
        assertTrue(TestHelper.contains(result, "thirdChild", 6, 11, 1));
        assertTrue(TestHelper.contains(result, "thirdGrandchild", 7, 8, 2));
        assertTrue(TestHelper.contains(result, "firstGrandchild", 9, 10, 2));
        assertTrue(TestHelper.contains(result, "secondGrandchild", 12, 13, 1));
    }


    @Test
    public void insertAsPrevSiblingOf() {
        var parent = TestHelper.getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = TestHelper.getNode("firstChild");
        dao.insertAsLastChild(parent, child1);

        var child2 = TestHelper.getNode("secondChild");
        dao.insertAsLastChild(parent, child2);

        var sibling1 = TestHelper.getNode("firstSibling");
        dao.insertAsPrevSibling(child2, sibling1);

        var result = dao.findAll();
        assertEquals(4, result.size());

        assertTrue(TestHelper.contains(result, "rootNode", 1, 8, 0));
        assertTrue(TestHelper.contains(result, "firstChild", 2, 3, 1));
        assertTrue(TestHelper.contains(result, "firstSibling", 4, 5, 1));
        assertTrue(TestHelper.contains(result, "secondChild", 6, 7, 1));
    }

    @Test
    public void insertAsNextSiblingOf() {
        var parent = TestHelper.getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = TestHelper.getNode("firstChild");
        dao.insertAsLastChild(parent, child1);

        var child2 = TestHelper.getNode("secondChild");
        dao.insertAsLastChild(parent, child2);

        var sibling1 = TestHelper.getNode("firstSibling");
        dao.insertAsNextSibling(child1, sibling1);

        var result = dao.findAll();
        assertEquals(4, result.size());

        assertTrue(TestHelper.contains(result, "rootNode", 1, 8, 0));
        assertTrue(TestHelper.contains(result, "firstChild", 2, 3, 1));
        assertTrue(TestHelper.contains(result, "firstSibling", 4, 5, 1));
        assertTrue(TestHelper.contains(result, "secondChild", 6, 7, 1));
    }

    @Test
    public void moveAsNextSibling() {
        var root = TestHelper.getNode("rootNode");
        dao.insertAsRoot(root);

        var child1 = TestHelper.getNode("firstChild");
        dao.insertAsLastChild(root, child1);

        var grandchild1 = TestHelper.getNode("firstGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var child2 = TestHelper.getNode("secondChild");
        dao.insertAsLastChild(root, child2);

        var grandchild2 = TestHelper.getNode("secondGrandchild");
        dao.insertAsLastChild(child2, grandchild2);

        var child3 = TestHelper.getNode("thirdChild");
        dao.insertAsLastChild(root, child3);

        var grandchild3 = TestHelper.getNode("thirdGrandchild");
        dao.insertAsLastChild(child3, grandchild3);

        dao.moveAsNextSibling(child3, child1);

        var result = dao.findAll();
        assertEquals(7, result.size());

        assertTrue(TestHelper.contains(result, "rootNode", 1, 14, 0));
        assertTrue(TestHelper.contains(result, "firstChild", 2, 5, 1));
        assertTrue(TestHelper.contains(result, "firstGrandchild", 3, 4, 2));
        assertTrue(TestHelper.contains(result, "thirdChild", 6, 9, 1));
        assertTrue(TestHelper.contains(result, "thirdGrandchild", 7, 8, 2));
        assertTrue(TestHelper.contains(result, "secondChild", 10, 13, 1));
        assertTrue(TestHelper.contains(result, "secondGrandchild", 11, 12, 2));

        dao.moveAsNextSibling(grandchild3, grandchild1);

        result = dao.findAll();
        assertEquals(7, result.size());

        assertTrue(TestHelper.contains(result, "rootNode", 1, 14, 0));
        assertTrue(TestHelper.contains(result, "firstChild", 2, 7, 1));
        assertTrue(TestHelper.contains(result, "firstGrandchild", 3, 4, 2));
        assertTrue(TestHelper.contains(result, "thirdGrandchild", 5, 6, 2));
        assertTrue(TestHelper.contains(result, "thirdChild", 8, 9, 1));
        assertTrue(TestHelper.contains(result, "secondChild", 10, 13, 1));
        assertTrue(TestHelper.contains(result, "secondGrandchild", 11, 12, 2));
    }

    @Test
    public void moveAsPrevSibling() {
        var root = TestHelper.getNode("rootNode");
        dao.insertAsRoot(root);

        var child1 = TestHelper.getNode("firstChild");
        dao.insertAsLastChild(root, child1);

        var grandchild1 = TestHelper.getNode("firstGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var child2 = TestHelper.getNode("secondChild");
        dao.insertAsLastChild(root, child2);

        var grandchild2 = TestHelper.getNode("secondGrandchild");
        dao.insertAsLastChild(child2, grandchild2);

        var child3 = TestHelper.getNode("thirdChild");
        dao.insertAsLastChild(root, child3);

        var grandchild3 = TestHelper.getNode("thirdGrandchild");
        dao.insertAsLastChild(child3, grandchild3);

        dao.moveAsPrevSibling(child3, child1);

        var result = dao.findAll();
        assertEquals(7, result.size());

        assertTrue(TestHelper.contains(result, "rootNode", 1, 14, 0));
        assertTrue(TestHelper.contains(result, "thirdChild", 2, 5, 1));
        assertTrue(TestHelper.contains(result, "thirdGrandchild", 3, 4, 2));
        assertTrue(TestHelper.contains(result, "firstChild", 6, 9, 1));
        assertTrue(TestHelper.contains(result, "firstGrandchild", 7, 8, 2));
        assertTrue(TestHelper.contains(result, "secondChild", 10, 13, 1));
        assertTrue(TestHelper.contains(result, "secondGrandchild", 11, 12, 2));

        dao.moveAsPrevSibling(grandchild2, grandchild1);

        result = dao.findAll();
        assertEquals(7, result.size());

        assertTrue(TestHelper.contains(result, "rootNode", 1, 14, 0));
        assertTrue(TestHelper.contains(result, "thirdChild", 2, 5, 1));
        assertTrue(TestHelper.contains(result, "thirdGrandchild", 3, 4, 2));
        assertTrue(TestHelper.contains(result, "firstChild", 6, 11, 1));
        assertTrue(TestHelper.contains(result, "secondGrandchild", 7, 8, 2));
        assertTrue(TestHelper.contains(result, "firstGrandchild", 9, 10, 2));
        assertTrue(TestHelper.contains(result, "secondChild", 12, 13, 1));
    }


    @Test
    public void insertAsRoot() {
        var parent = TestHelper.getNode("rootNode");
        dao.insertAsRoot(parent);

        var result = dao.findAll();
        assertEquals(1, result.size());

        assertTrue(TestHelper.contains(result, "rootNode", 1, 2, 0));
    }

    @Test
    public void getDescendantsOf() {
        var parent = TestHelper.getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = TestHelper.getNode("firstChild");
        dao.insertAsLastChild(parent, child1);

        var child2 = TestHelper.getNode("secondChild");
        dao.insertAsLastChild(parent, child2);

        var grandchild1 = TestHelper.getNode("firstGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var result = dao.getDescendants(parent);
        assertEquals(3, result.size());

        assertTrue(TestHelper.contains(result, "firstChild", 2, 5, 1));
        assertTrue(TestHelper.contains(result, "firstGrandchild", 3, 4, 2));
        assertTrue(TestHelper.contains(result, "secondChild", 6, 7, 1));
    }

    @Test
    public void getAncestorsOf() {
        var parent = TestHelper.getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = TestHelper.getNode("firstChild");
        dao.insertAsLastChild(parent, child1);

        var child2 = TestHelper.getNode("secondChild");
        dao.insertAsLastChild(parent, child2);

        var grandchild1 = TestHelper.getNode("firstGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var result = dao.getAncestors(grandchild1);
        assertEquals(2, result.size());

        assertTrue(TestHelper.contains(result, "firstChild", 2, 5, 1));
        assertTrue(TestHelper.contains(result, "rootNode", 1, 8, 0));
    }

    @Test
    public void getParent() {
        var parent = TestHelper.getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = TestHelper.getNode("firstChild");
        dao.insertAsLastChild(parent, child1);

        var child2 = TestHelper.getNode("secondChild");
        dao.insertAsLastChild(parent, child2);

        var grandchild1 = TestHelper.getNode("firstGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var parentOfChild2 = dao.getParent(child2);
        assertTrue(TestHelper.matches(parentOfChild2, "rootNode", 1, 8, 0));

        var parentOfGrandchild1 = dao.getParent(grandchild1);
        assertTrue(TestHelper.matches(parentOfGrandchild1, "firstChild", 2, 5, 1));
    }

    @Test
    public void deleteIncludingDescendants() {
        var parent = TestHelper.getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = TestHelper.getNode("firstChild");
        dao.insertAsLastChild(parent, child1);

        var grandchild1 = TestHelper.getNode("firstGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var child2 = TestHelper.getNode("secondChild");
        dao.insertAsLastChild(parent, child2);

        dao.delete(child1);

        var result = dao.findAll();
        assertEquals(2, result.size());

        assertTrue(TestHelper.contains(result, "rootNode", 1, 4, 0));
        assertTrue(TestHelper.contains(result, "secondChild", 2, 3, 1));
    }

    @Test
    public void getDescendants() {
        var parent = TestHelper.getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = TestHelper.getNode("firstChild");
        dao.insertAsLastChild(parent, child1);

        var grandchild1 = TestHelper.getNode("firstGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var greatGrandchild = TestHelper.getNode("firstGreatGrandchild");
        dao.insertAsLastChild(grandchild1, greatGrandchild);

        var child2 = TestHelper.getNode("secondChild");
        dao.insertAsLastChild(parent, child2);

        var grandchild2 = TestHelper.getNode("secondGrandchild");
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
        var parent = TestHelper.getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = TestHelper.getNode("firstChild");
        dao.insertAsLastChild(parent, child1);

        var grandchild1 = TestHelper.getNode("firstGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var greadGrandchild = TestHelper.getNode("firstGreatGrandchild");
        dao.insertAsLastChild(grandchild1, greadGrandchild);

        var child2 = TestHelper.getNode("secondChild");
        dao.insertAsLastChild(parent, child2);

        var grandchild2 = TestHelper.getNode("secondGrandchild");
        dao.insertAsLastChild(child2, grandchild2);

        var result = dao.getAncestors(greadGrandchild);
        assertEquals(3, result.size());

        result = dao.getAncestors(greadGrandchild, 1);
        assertEquals(1, result.size());
        assertTrue(TestHelper.contains(result, "firstGrandchild", 3, 6, 2));

        result = dao.getAncestors(greadGrandchild, 2);
        assertEquals(2, result.size());
        assertTrue(TestHelper.contains(result, "firstGrandchild", 3, 6, 2));
        assertTrue(TestHelper.contains(result, "firstChild", 2, 7, 1));

        result = dao.getAncestors(greadGrandchild, 3);
        assertEquals(3, result.size());
        assertTrue(TestHelper.contains(result, "firstGrandchild", 3, 6, 2));
        assertTrue(TestHelper.contains(result, "firstChild", 2, 7, 1));
        assertTrue(TestHelper.contains(result, "rootNode", 1, 12, 0));
    }

    @Test
    public void getNodeAndAllDescendants() {
        var parent = TestHelper.getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = TestHelper.getNode("firstChild");
        dao.insertAsLastChild(parent, child1);

        var grandchild1 = TestHelper.getNode("firstGrandchild");
        dao.insertAsLastChild(child1, grandchild1);

        var greatGrandchild = TestHelper.getNode("firstGreatGrandchild");
        dao.insertAsLastChild(grandchild1, greatGrandchild);

        var child2 = TestHelper.getNode("secondChild");
        dao.insertAsLastChild(parent, child2);

        var grandchild2 = TestHelper.getNode("secondGrandchild");
        dao.insertAsLastChild(child2, grandchild2);

        var result = dao.getNodeAndAllDescendants(child1);
        assertEquals(3, result.size());

        assertTrue(TestHelper.contains(result, "firstChild", 2, 7, 1));
        assertTrue(TestHelper.contains(result, "firstGrandchild", 3, 6, 2));
        assertTrue(TestHelper.contains(result, "firstGreatGrandchild", 4, 5, 3));

        result = dao.getNodeAndAllDescendants(child2);
        assertEquals(2, result.size());

        assertTrue(TestHelper.contains(result, "secondChild", 8, 11, 1));
        assertTrue(TestHelper.contains(result, "secondGrandchild", 9, 10, 2));
    }

    @Test
    public void isRoot() {
        var parent = TestHelper.getNode("rootNode");
        dao.insertAsRoot(parent);

        var child1 = TestHelper.getNode("firstChild");
        dao.insertAsLastChild(parent, child1);

        assertTrue(dao.isRoot(parent));
        assertFalse(dao.isRoot(child1));
    }
}