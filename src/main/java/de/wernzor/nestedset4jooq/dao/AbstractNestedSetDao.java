package de.wernzor.nestedset4jooq.dao;

import de.wernzor.nestedset4jooq.exception.NodeNotFoundException;
import de.wernzor.nestedset4jooq.model.NestedSetNode;
import org.jooq.*;
import org.jooq.impl.DAOImpl;
import org.jooq.impl.DSL;

import java.util.List;

public abstract class AbstractNestedSetDao<R extends UpdatableRecord<R>, N extends NestedSetNode<P, T>, P, T>
        extends DAOImpl<R, N, T>
        implements NestedSetDao<N> {

    protected AbstractNestedSetDao(Table<R> table, Class<N> type) {
        super(table, type);
    }

    protected AbstractNestedSetDao(Table<R> table, Class<N> type, Configuration configuration) {
        super(table, type, configuration);
    }

    public abstract TableField<R, Long> getLeftField();

    public abstract TableField<R, Long> getRightField();

    public abstract TableField<R, Long> getLevelField();

    /**
     * Adds a node as root. A root always has the left value 1, the right value 2 and the level 0.
     *
     * @param node node to be inserted as root
     */
    @Override
    public void insertAsRoot(N node) {
        node.setLeft(1L);
        node.setRight(2L);
        node.setLevel(0L);

        insert(node);
    }

    /**
     * Adds a child to an existing node. If the node already has children, the new child will be placed as first child.
     *
     * @param parent parent node
     * @param child  child node which will be added as first child
     */
    @Override
    public void insertAsFirstChild(N parent, N child) {
        final N parentRecord = fetch(parent);

        child.setLeft(parentRecord.getLeft() + 1);
        child.setRight(parentRecord.getLeft() + 2);
        child.setLevel(parentRecord.getLevel() + 1);

        shiftNodes(child.getLeft());

        insert(child);
    }

    /**
     * Adds a child to an existing node. If the node already has children, the new child will be placed as last child.
     *
     * @param parent parent node
     * @param child  child node which will be added as last child
     */
    @Override
    public void insertAsLastChild(N parent, N child) {
        final N parentRecord = fetch(parent);

        child.setLeft(parentRecord.getRight());
        child.setRight(parentRecord.getRight() + 1);
        child.setLevel(parentRecord.getLevel() + 1);

        shiftNodes(child.getLeft());

        insert(child);
    }

    /**
     * Adds a new sibling on the left side of an existing node.
     *
     * @param existingNode Node to which the sibling is to be added on the left side
     * @param sibling      Sibling which is to be added
     */
    @Override
    public void insertAsPrevSibling(N existingNode, N sibling) {
        final N nodeRecord = fetch(existingNode);

        sibling.setLeft(nodeRecord.getLeft());
        sibling.setRight(nodeRecord.getLeft() + 1);
        sibling.setLevel(nodeRecord.getLevel());

        shiftNodes(sibling.getLeft());

        insert(sibling);
    }

    /**
     * Adds a new sibling on the right side of an existing node.
     *
     * @param existingNode Node to which the sibling is to be added on the right side
     * @param sibling      Sibling which is to be added
     */
    @Override
    public void insertAsNextSibling(N existingNode, N sibling) {
        final N nodeRecord = fetch(existingNode);

        sibling.setLeft(nodeRecord.getRight() + 1);
        sibling.setRight(nodeRecord.getRight() + 2);
        sibling.setLevel(nodeRecord.getLevel());

        shiftNodes(sibling.getLeft());

        insert(sibling);
    }

    /**
     * Returns true, if a node has children. Otherwise the method will return false.
     *
     * @param node node
     * @return true, when there are children. Otherwise false.
     */
    @Override
    public boolean hasChildren(N node) {
        final N nodeRecord = fetch(node);

        return nodeRecord.getRight() - node.getLeft() > 1;
    }

    /**
     * Returns all childrens of a node as a sorted list.
     *
     * @param node Node whose children are to be determinded
     * @return Sorted list of all children of the node
     */
    @Override
    public List<N> getChildren(N node) {
        return getDescendants(node, 1);
    }

    /**
     * Returns all descendants of a node as a sorted list. The direct descendant (child) will be at the beginning of the
     * list.
     *
     * @param node Node whose descendants are to be determined
     * @return Sorted list of all descendants of the node
     */
    @Override
    public List<N> getDescendants(N node) {
        return getDescendants(node, 0);
    }

    /**
     * Returns the descendants of a node as a sorted list. The direct descendant (child) will be at the beginning of the
     * list. To only get the descendants up to a certain generation, the numberOfGenerations parameter must be set to a
     * value greater than zero. Otherwise all generations will be returned.
     *
     * @param node                Node whose descendants are to be determined
     * @param numberOfGenerations All generations when 0. Otherwise only the given amount of descendants will be
     *                            returned
     * @return Sorted list of descendants of the node.
     */
    @Override
    public List<N> getDescendants(N node, int numberOfGenerations) {
        final N nodeRecord = fetch(node);

        final Condition generationIsZero = DSL.condition(numberOfGenerations == 0);

        return ctx().select()
                .from(getTable())
                .where(getLeftField().greaterThan(nodeRecord.getLeft()))
                .and(getRightField().lessThan(nodeRecord.getRight()))
                .and(generationIsZero.or(getLevelField().lessOrEqual(nodeRecord.getLevel() + numberOfGenerations)))
                .orderBy(getLeftField().asc())
                .fetchInto(getType());
    }

    /**
     * Returns the node with all its descendants as a sorted list. The node itself  will be at the beginning of the
     * list.
     *
     * @param node Node whose descendants will be returned.
     * @return Sorted list of descendants of the node including the node.
     */
    @Override
    public List<N> getNodeAndAllDescendants(N node) {
        final N nodeRecord = fetch(node);

        return ctx().select()
                .from(getTable())
                .where(getLeftField().greaterOrEqual(nodeRecord.getLeft()))
                .and(getRightField().lessOrEqual(nodeRecord.getRight()))
                .orderBy(getLeftField().asc())
                .fetchInto(getType());
    }

    /**
     * Returns all entries as sorted list. The sorted list represents the order of the tree.
     *
     * @return Sorted list containing all entries.
     */
    @Override
    public List<N> findAll() {
        return ctx()
                .selectFrom(getTable())
                .orderBy(getLeftField().asc())
                .fetchInto(getType());
    }

    /**
     * Returns the ancestors of a node as a sorted list. The direct ancestor (parent) will be at the beginning of the
     * list. To only get the ancestors up to a certain generation, the numberOfAncestors parameter must be set to a
     * value greater than zero. Otherwise all generations will be returned.
     *
     * @param node              Node whose ancestors are to be determined
     * @param numberOfAncestors All generations when 0. Otherwise only the given amount of ancestors will be
     *                          returned
     * @return Sorted list of ancestors of the node
     */
    @Override
    public List<N> getAncestors(N node, int numberOfAncestors) {
        final N nodeRecord = fetch(node);

        final Condition generationIsZero = DSL.condition(numberOfAncestors == 0);

        return ctx().select()
                .from(getTable())
                .where(getLeftField().lessThan(nodeRecord.getLeft()))
                .and(getRightField().greaterThan(nodeRecord.getRight()))
                .and(generationIsZero.or(getLevelField().greaterOrEqual(nodeRecord.getLevel() - numberOfAncestors)))
                .orderBy(getRightField().asc())
                .fetchInto(getType());
    }

    /**
     * Returns all ancestors of a node as a sorted list. The direct ancestor (parent) will be at the beginning of the
     * list.
     *
     * @param node Node whose ancestors are to be determined
     * @return Sorted list of all ancestors of the node
     */
    @Override
    public List<N> getAncestors(N node) {
        return getAncestors(node, 0);
    }

    /**
     * Returns the parent of a given node.
     *
     * @param node Node whose parent should be returned
     * @return Parent node
     */
    @Override
    public N getParent(N node) {
        return getAncestors(node).get(0);
    }

    /**
     * Deletes a node and all its descendants from the database. The gaps in the tree created during deletion are
     * closed.
     *
     * @param node Node to be deleted
     */
    @Override
    public void delete(N node) {
        final N nodeRecord = fetch(node);

        ctx().deleteFrom(getTable())
                .where(getLeftField().greaterOrEqual(nodeRecord.getLeft()))
                .and(getRightField().lessOrEqual(nodeRecord.getRight()))
                .execute();

        // close the gap
        final Long sizeOfGap = nodeRecord.getLeft() - nodeRecord.getRight() - 1;

        shiftNodes(nodeRecord.getRight() + 1, sizeOfGap);
    }

    /**
     * Makes the source node the first child of the destination node.
     *
     * @param source      source node
     * @param destination destination node
     */
    @Override
    public void moveAsFirstChild(N source, N destination) {
        if (source == destination) {
            throw new IllegalArgumentException("Cannot make node the first child of itself.");
        }

        final N sourceRecord = fetch(source);
        final N destinationRecord = fetch(destination);

        final Long oldLevel = sourceRecord.getLevel();
        final Long newLevel = destinationRecord.getLevel() + 1;

        sourceRecord.setLevel(newLevel);
        update(sourceRecord);

        moveNode(sourceRecord, destinationRecord.getLeft() + 1, newLevel - oldLevel);
    }

    /**
     * Makes the source node the last child of the destination node.
     *
     * @param source      source node
     * @param destination destination node
     */
    @Override
    public void moveAsLastChild(N source, N destination) {
        if (source == destination) {
            throw new IllegalArgumentException("Cannot make node the last child of itself.");
        }

        final N sourceRecord = fetch(source);
        final N destinationRecord = fetch(destination);

        final Long oldLevel = sourceRecord.getLevel();
        final Long newLevel = destinationRecord.getLevel() + 1;

        sourceRecord.setLevel(newLevel);
        update(sourceRecord);

        moveNode(sourceRecord, destinationRecord.getRight(), newLevel - oldLevel);
    }

    /**
     * Makes the source node the next sibling of the destination node.
     *
     * @param source      source node
     * @param destination destination node
     */
    @Override
    public void moveAsNextSibling(N source, N destination) {
        if (source == destination) {
            throw new IllegalArgumentException("Cannot make node the next sibling of itself.");
        }

        final N sourceRecord = fetch(source);
        final N destinationRecord = fetch(destination);

        final Long oldLevel = sourceRecord.getLevel();
        final Long newLevel = destinationRecord.getLevel();

        sourceRecord.setLevel(newLevel);
        update(sourceRecord);

        moveNode(sourceRecord, destinationRecord.getRight() + 1, newLevel - oldLevel);
    }

    /**
     * Makes the source node the previous sibling of the destination node.
     *
     * @param source      source node
     * @param destination destination node
     */
    @Override
    public void moveAsPrevSibling(N source, N destination) {
        if (source == destination) {
            throw new IllegalArgumentException("Cannot make node the previous sibling of itself.");
        }

        final N sourceRecord = fetch(source);
        final N destinationRecord = fetch(destination);

        final Long oldLevel = sourceRecord.getLevel();
        final Long newLevel = destinationRecord.getLevel();

        sourceRecord.setLevel(newLevel);
        update(sourceRecord);

        moveNode(sourceRecord, destinationRecord.getLeft(), newLevel - oldLevel);
    }

    /**
     * Moves a node and all its children to new location inside the tree. The new location is defined by the
     * leftDest value.
     *
     * @param node            node which is to be moved
     * @param leftDest        new position of the node
     * @param levelDifference level difference which will result in moving the node
     */
    private void moveNode(N node, Long leftDest, Long levelDifference) {
        final long sizeOfTree = node.getRight() - node.getLeft() + 1;

        // create a gap at the new position in the size of the tree
        shiftNodes(leftDest, sizeOfTree);

        Long left = node.getLeft();
        Long right = node.getRight();

        // if the node was also shifted, the size of the tree must be added
        if (left >= leftDest) {
            left += sizeOfTree;
            right += sizeOfTree;
        }

        // set the new level for all descendants of the node
        ctx().update(getTable())
                .set(getLevelField(), getLevelField().add(levelDifference))
                .where(getLeftField().greaterThan(left))
                .and(getRightField().lessThan(right))
                .execute();

        // increment all nodes between left and right in the size of the tree
        shiftNodes(left, right, leftDest - left);

        // close gap
        shiftNodes(right + 1, -sizeOfTree);
    }

    /**
     * Reads a passed node from the database using its ID and returns the associated record.
     * If no node is found for the ID, the function throws a NodeNotFoundException.
     *
     * @param node Node to be read
     * @return Record
     */
    private N fetch(N node) {
        final N nodeRecord = findById(node.getId());

        if (nodeRecord == null) {
            throw new NodeNotFoundException("Node with id " + node.getId() + " not found.");
        }
        return nodeRecord;
    }

    /**
     * Creates a gap on the right and left side of a tree so that a new node can be inserted there.
     * The gap always has a size of 2.
     *
     * @param startValue start value for the creation of the gap
     */
    private void shiftNodes(Long startValue) {
        shiftLeftNodes(startValue, 2L);
        shiftRightNodes(startValue, 2L);
    }

    /**
     * Creates a gap on the right and left side of a tree so that a new node can be inserted there.
     * The size of the gap is defined by the sizeOfGap.
     *
     * @param from      start value for the gap
     * @param sizeOfGap size of gap
     */
    private void shiftNodes(Long from, Long sizeOfGap) {
        shiftLeftNodes(from, sizeOfGap);
        shiftRightNodes(from, sizeOfGap);
    }

    /**
     * Adds an increment to the left and right side of all nodes, who live between the from and to range.
     *
     * @param from      start value
     * @param to        end value
     * @param increment incrementation
     */
    private void shiftNodes(Long from, Long to, Long increment) {
        shiftLeftNodes(from, to, increment);
        shiftRightNodes(from, to, increment);
    }

    /**
     * Adds an increment to all nodes whose left value is greater than or equal to the from value.
     *
     * @param from      start value of the left side
     * @param increment increment, which will be added to the left side of all nodes, whose left value is greater or
     *                  equal to the start value.
     */
    private void shiftLeftNodes(Long from, Long increment) {
        ctx().update(this.getTable())
                .set(this.getLeftField(), this.getLeftField().add(increment))
                .where(this.getLeftField().greaterOrEqual(from))
                .execute();
    }

    /**
     * Adds an increment to all nodes whose left value is between from and to value.
     *
     * @param from      start value
     * @param to        end value
     * @param increment increment, which will be added to the left side of all nodes
     */
    private void shiftLeftNodes(Long from, Long to, Long increment) {
        ctx().update(this.getTable())
                .set(this.getLeftField(), this.getLeftField().add(increment))
                .where(this.getLeftField().between(from, to))
                .execute();
    }

    /**
     * Adds an increment to all nodes whose right value is greater than or equal to the from value.
     *
     * @param from      start value of the right side
     * @param increment increment, which will be added to the right side of all nodes, whose right value is greater or
     *                  equal to the start value.
     */
    private void shiftRightNodes(Long from, Long increment) {
        ctx().update(this.getTable())
                .set(this.getRightField(), this.getRightField().add(increment))
                .where(this.getRightField().greaterOrEqual(from))
                .execute();
    }

    /**
     * Adds an increment to all nodes whose right value is between from and to value.
     *
     * @param from      start value
     * @param to        end value
     * @param increment increment, which will be added to the left side of all nodes
     */
    private void shiftRightNodes(Long from, Long to, Long increment) {
        ctx().update(this.getTable())
                .set(this.getRightField(), this.getRightField().add(increment))
                .where(this.getRightField().between(from, to))
                .execute();
    }
}
