package de.wernzor.nestedset4jooq.model;

import de.wernzor.nestedset4jooq.test.tables.pojos.Category;

public class CategoryNode extends Category implements NestedSetNode<Category, Long> {
    @Override
    public Long getLeft() {
        return super.getLft();
    }

    @Override
    public Category setLeft(Long left) {
        return super.setLft(left);
    }

    @Override
    public Long getRight() {
        return super.getRgt();
    }

    @Override
    public Category setRight(Long right) {
        return super.setRgt(right);
    }
}
