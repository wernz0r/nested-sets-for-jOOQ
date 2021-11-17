/*
 * Copyright 2021. Werner Elsler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.wernz0r.nestedsets4jooq.model;

import de.wernz0r.nestedsets4jooq.test.tables.pojos.Category;

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
