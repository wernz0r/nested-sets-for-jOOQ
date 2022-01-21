/*
 * Copyright 2022. Werner Elsler
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

package io.github.wernz0r.nestedsets4jooq.dao;

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
