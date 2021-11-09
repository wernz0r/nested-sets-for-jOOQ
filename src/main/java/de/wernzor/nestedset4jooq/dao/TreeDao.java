package de.wernzor.nestedset4jooq.dao;

import de.wernzor.nestedset4jooq.model.NestedSetNode;
import de.wernzor.nestedset4jooq.model.TreeNode;
import de.wernzor.nestedset4jooq.model.helper.TreeNodeHelper;
import org.jooq.UpdatableRecord;

import java.util.List;

public class TreeDao<R extends UpdatableRecord<R>, N extends NestedSetNode<P, T>, P, T> {

    private final AbstractNestedSetDao<R, N, P, T> dao;

    public TreeDao(AbstractNestedSetDao<R, N, P, T> dao) {
        this.dao = dao;
    }

    /**
     * Reads the complete tree and returns it as a tree structure.
     *
     * @return Tree structure of the complete tree.
     */
    public TreeNode<N> fetchTree() {
        final List<N> list = dao.findAll();

        if (list.isEmpty()) {
            return null;
        }

        final N root = list.iterator().next();

        return getTreeNode(list, root);
    }

    /**
     * Returns the tree to a node. the tree contains the node and all its children and descendants.
     *
     * @return Tree structure of the complete tree starting with the node.
     */
    public TreeNode<N> fetchTree(N node) {
        final List<N> list = dao.getNodeAndAllDescendants(node);

        if (list.isEmpty()) {
            return null;
        }

        final N root = list.iterator().next();

        return getTreeNode(list, root);
    }

    /**
     * Inserts a node and all its children / descendants as nested set tree.
     *
     * @param treeNode treeNode
     */
    public void insertTree(TreeNode<N> treeNode) {
        if (treeNode.isRoot()) {
            dao.insertAsRoot(treeNode.getNode());
        } else {
            dao.insertAsLastChild(treeNode.getParent(), treeNode.getNode());
        }

        for (TreeNode<N> child : treeNode.getChildren()) {
            insertTree(child);
        }
    }

    /**
     * Creates a tree structure for a node based on the sorted list of all nodes of the tree.
     *
     * @param sortedTreeNodes Sorted list of all nodes of the tree
     * @param node            node which will be transformed in a TreeNode
     * @return TreeNode of node including all descendants
     */
    private TreeNode<N> getTreeNode(List<N> sortedTreeNodes, N node) {
        final TreeNode<N> result = new TreeNode<>(node);

        final TreeNodeHelper<N, P, T> helper = new TreeNodeHelper<>(sortedTreeNodes);

        final List<N> children = helper.getChildren(node);

        for (N child : children) {
            result.addChild(getTreeNode(sortedTreeNodes, child));
        }

        return result;
    }

}
