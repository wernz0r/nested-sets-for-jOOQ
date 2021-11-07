package de.wernzor.nestedset4jooq.model.helper;

import de.wernzor.nestedset4jooq.model.NestedSetNode;

import java.util.List;
import java.util.stream.Collectors;

public class TreeNodeHelper<N extends NestedSetNode<P, T>, P, T> {

    private final List<N> list;

    public TreeNodeHelper(List<N> list) {
        this.list = list;
    }

    public List<N> getChildren(N node) {
        return list.stream()
                .filter(n -> isChild(node, n))
                .collect(Collectors.toList());
    }

    private boolean isChild(N parent, N child) {
        return child.getLeft() > parent.getLeft() &&
                child.getRight() < parent.getRight() &&
                child.getLevel() == parent.getLevel() + 1;
    }
}
