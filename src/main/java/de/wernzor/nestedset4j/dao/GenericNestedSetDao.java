package de.wernzor.nestedset4j.dao;

import de.wernzor.nestedset4j.model.NestedSetNode;
import org.jooq.Configuration;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DAOImpl;

public abstract class GenericNestedSetDao<R extends UpdatableRecord<R>, N extends NestedSetNode<P>, P> extends DAOImpl<R, N, Long> {

    protected GenericNestedSetDao(Table<R> table, Class<N> type) {
        super(table, type);
    }

    protected GenericNestedSetDao(Table<R> table, Class<N> type, Configuration configuration) {
        super(table, type, configuration);
    }

    public abstract TableField<R, Long> getLeftField();

    public abstract TableField<R, Long> getRightField();

    public void insertAsRoot(N node) {
        node.setLeft(1L);
        node.setRight(2L);
        node.setLevel(0L);

        insert(node);
    }

    public void insertAsFirstChild(N parent, N child) {
        N parentRecord = fetchNode(parent);

        child.setLeft(parentRecord.getLeft() + 1);
        child.setRight(parentRecord.getLeft() + 2);
        child.setLevel(parentRecord.getLevel() + 1);

        shiftNodes(child.getLeft(), 2L);

        insert(child);
    }

    public void insertAsLastChildOf(N parent, N child) {
        N parentRecord = fetchNode(parent);

        child.setLeft(parentRecord.getRight());
        child.setRight(parentRecord.getRight() + 1);
        child.setLevel(parentRecord.getLevel() + 1);

        shiftNodes(child.getLeft(), 2L);

        insert(child);
    }

    private N fetchNode(N node) {
        N nodeRecord = findById(node.getId());
        if (nodeRecord == null) {
            throw new IllegalArgumentException("node not found");
        }
        return nodeRecord;
    }

    private void shiftNodes(Long first, Long delta) {
        shiftLeftNodes(first, delta);
        shiftRightNodes(first, delta);
    }

    private void shiftLeftNodes(Long first, Long delta) {
        ctx().update(this.getTable())
                .set(this.getLeftField(), this.getLeftField().add(delta))
                .where(this.getLeftField().greaterOrEqual(first))
                .execute();
    }

    private void shiftRightNodes(Long first, Long delta) {
        ctx().update(this.getTable())
                .set(this.getRightField(), this.getRightField().add(delta))
                .where(this.getRightField().greaterOrEqual(first))
                .execute();
    }
}
