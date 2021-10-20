package de.wernzor.nestedset4jooq.dao;

import de.wernzor.nestedset4jooq.dto.CategoryNode;
import de.wernzor.nestedset4jooq.test.tables.pojos.Category;
import de.wernzor.nestedset4jooq.test.tables.records.CategoryRecord;
import org.jooq.Configuration;
import org.jooq.TableField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static de.wernzor.nestedset4jooq.test.tables.Category.CATEGORY;

@Repository
public class CategoryNestedSetDao extends GenericNestedSetDao<CategoryRecord, CategoryNode, Category> {

    @Autowired
    public CategoryNestedSetDao(Configuration configuration) {
        super(CATEGORY, CategoryNode.class, configuration);
    }

    @Override
    public TableField<CategoryRecord, Long> getLeftField() {
        return CATEGORY.LFT;
    }

    @Override
    public TableField<CategoryRecord, Long> getRightField() {
        return CATEGORY.RGT;
    }

    @Override
    public Long getId(CategoryNode categoryNode) {
        return categoryNode.getId();
    }

    public void clearAll() {
        super.ctx().deleteFrom(CATEGORY)
                .execute();
    }
}
