package de.wernzor.nestedset4jooq.model;

import java.util.LinkedList;
import java.util.List;

public class TreeNode<N> {

    private final N node;
    private N parent;
    private final List<TreeNode<N>> children;

    public boolean isRoot() {
        return this.parent == null;
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    public TreeNode(N node) {
        this.node = node;
        this.children = new LinkedList<>();
    }

    public void addChild(N child) {
        TreeNode<N> childNode = new TreeNode<>(child);
        childNode.parent = this.node;
        this.children.add(childNode);
    }

    public void addChild(TreeNode<N> child) {
        child.parent = this.node;
        this.children.add(child);
    }

    public List<TreeNode<N>> getChildren() {
        return children;
    }

    public TreeNode<N> findTreeNode(Comparable<N> cmp) {
        if (cmp.compareTo(this.node) == 0) {
            return this;
        }

        for (TreeNode<N> element : this.children) {
            if (cmp.compareTo(element.node) == 0)
                return element;
        }

        return null;
    }

    public N getNode() {
        return node;
    }

    public N getParent() {
        return parent;
    }

    public void setParent(N parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "data=" + this.node +
                ", parent=" + this.parent +
                ", children=" + this.children +
                '}';
    }

}
