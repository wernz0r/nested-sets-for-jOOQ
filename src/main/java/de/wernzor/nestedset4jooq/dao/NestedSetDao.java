package de.wernzor.nestedset4jooq.dao;

import java.util.List;

public interface NestedSetDao<N> {

    N fetch(N node);

    void delete(N node);

    List<N> findAll();

    void insertAsRoot(N node);

    void insertAsFirstChild(N parent, N child);

    void insertAsLastChild(N parent, N child);

    void insertAsPrevSibling(N existingNode, N sibling);

    void insertAsNextSibling(N existingNode, N sibling);

    boolean hasChildren(N node);

    boolean isRoot(N node);

    List<N> getChildren(N node);

    List<N> getDescendants(N node);

    List<N> getDescendants(N node, int numberOfGenerations);

    List<N> getNodeAndAllDescendants(N node);

    List<N> getAncestors(N node, int numberOfAncestors);

    List<N> getAncestors(N node);

    N getParent(N node);

    void moveAsFirstChild(N source, N destination);

    void moveAsLastChild(N source, N destination);

    void moveAsNextSibling(N source, N destination);

    void moveAsPrevSibling(N source, N destination);
}
