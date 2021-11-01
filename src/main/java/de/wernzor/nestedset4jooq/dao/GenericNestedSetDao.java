package de.wernzor.nestedset4jooq.dao;

import de.wernzor.nestedset4jooq.model.NestedSetNode;
import org.jooq.*;
import org.jooq.impl.DAOImpl;
import org.jooq.impl.DSL;

import java.util.List;

public abstract class GenericNestedSetDao<R extends UpdatableRecord<R>, N extends NestedSetNode<P, T>, P, T> extends DAOImpl<R, N, T> {

    protected GenericNestedSetDao(Table<R> table, Class<N> type) {
        super(table, type);
    }

    protected GenericNestedSetDao(Table<R> table, Class<N> type, Configuration configuration) {
        super(table, type, configuration);
    }

    public abstract TableField<R, Long> getLeftField();

    public abstract TableField<R, Long> getRightField();

    public abstract TableField<R, Long> getLevelField();

    public void insertAsRoot(N node) {
        node.setLeft(1L);
        node.setRight(2L);
        node.setLevel(0L);

        insert(node);
    }

    public void insertAsFirstChild(N parent, N child) {
        final N parentRecord = fetchNode(parent);

        child.setLeft(parentRecord.getLeft() + 1);
        child.setRight(parentRecord.getLeft() + 2);
        child.setLevel(parentRecord.getLevel() + 1);

        shiftNodes(child.getLeft());

        insert(child);
    }

    public void insertAsLastChildOf(N parent, N child) {
        final N parentRecord = fetchNode(parent);

        child.setLeft(parentRecord.getRight());
        child.setRight(parentRecord.getRight() + 1);
        child.setLevel(parentRecord.getLevel() + 1);

        shiftNodes(child.getLeft());

        insert(child);
    }

    public void insertAsPrevSiblingOf(N existingSibiling, N newSibling) {
        final N existingSiblingRecord = fetchNode(existingSibiling);

        newSibling.setLeft(existingSiblingRecord.getLeft());
        newSibling.setRight(existingSiblingRecord.getLeft() + 1);
        newSibling.setLevel(existingSiblingRecord.getLevel());

        shiftNodes(newSibling.getLeft());

        insert(newSibling);
    }

    public void insertAsNextSiblingOf(N existingSibling, N newSibling) {
        final N existingSiblingRecord = fetchNode(existingSibling);

        newSibling.setLeft(existingSiblingRecord.getRight() + 1);
        newSibling.setRight(existingSiblingRecord.getRight() + 2);
        newSibling.setLevel(existingSiblingRecord.getLevel());

        shiftNodes(newSibling.getLeft());

        insert(newSibling);
    }

    public boolean hasChildren(N node) {
        final N nodeRecord = fetchNode(node);

        return nodeRecord.getRight() - node.getLeft() > 1;
    }

    public List<N> getChildrenOf(N node) {
        return getDescendantsOf(node, 1);
    }

    public List<N> getDescendantsOf(N node) {
        return getDescendantsOf(node, 0);
    }

    public List<N> getDescendantsOf(N node, int depth) {
        final N nodeRecord = fetchNode(node);

        final Condition depthIsZero = DSL.condition(depth == 0);

        return ctx().select()
                .from(getTable())
                .where(getLeftField().greaterThan(nodeRecord.getLeft()))
                .and(getRightField().lessThan(nodeRecord.getRight()))
                .and(depthIsZero.or(getLevelField().lessOrEqual(nodeRecord.getLevel() + depth)))
                .orderBy(getLeftField().asc())
                .fetchInto(getType());
    }

    public List<N> getAncestorsOf(N node) {
        final N nodeRecord = fetchNode(node);

        return ctx().select()
                .from(getTable())
                .where(getLeftField().lessThan(nodeRecord.getLeft()))
                .and(getRightField().greaterThan(nodeRecord.getRight()))
                .orderBy(getRightField().asc())
                .fetchInto(getType());
    }

    public N getParentOf(N node) {
        return getAncestorsOf(node).get(0);
    }

    private N fetchNode(N node) {
        final N nodeRecord = findById(node.getId());

        if (nodeRecord == null) {
            throw new IllegalArgumentException("node not found");
        }
        return nodeRecord;
    }

    private void shiftNodes(Long first) {
        shiftLeftNodes(first);
        shiftRightNodes(first);
    }

    private void shiftLeftNodes(Long first) {
        ctx().update(this.getTable())
                .set(this.getLeftField(), this.getLeftField().add(2))
                .where(this.getLeftField().greaterOrEqual(first))
                .execute();
    }

    private void shiftRightNodes(Long first) {
        ctx().update(this.getTable())
                .set(this.getRightField(), this.getRightField().add(2))
                .where(this.getRightField().greaterOrEqual(first))
                .execute();
    }
}
