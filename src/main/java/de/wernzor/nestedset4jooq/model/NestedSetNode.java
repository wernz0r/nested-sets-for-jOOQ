package de.wernzor.nestedset4jooq.model;

public interface NestedSetNode<P> {

    Long getId();

    P setId(Long id);

    Long getLeft();

    P setLeft(Long left);

    Long getRight();

    P setRight(Long right);

    Long getLevel();

    P setLevel(Long level);
}
