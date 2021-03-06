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

package io.github.wernz0r.nestedsets4jooq.helper;

import io.github.wernz0r.nestedsets4jooq.model.CategoryNode;

import java.util.List;

public class TestHelper {

    /**
     * Creates a CategoryNodewith a given name.
     *
     * @param name Name of the Cateory
     * @return CategoryNode
     */
    public static CategoryNode getNode(String name) {
        final CategoryNode node = new CategoryNode();
        node.setName(name);
        return node;
    }

    /**
     * Checks the name, left, right and level values of a given node.
     *
     * @param node  node to check
     * @param name  expected name of the node
     * @param left  expected left value
     * @param right expected right value
     * @param level expected level
     * @return true, when the node values matches. otherwise false
     */
    public static boolean matches(CategoryNode node, String name, long left, long right, long level) {
        return name.equals(node.getName()) &&
                node.getLeft() == left &&
                node.getRight() == right &&
                node.getLevel() == level;
    }

    /**
     * Checks if a list of category nodes contains a node with the give name, left, right and level value.
     *
     * @param list  list to be searched
     * @param name  name of the node
     * @param left  left value of the node
     * @param right right value of the node
     * @param level level of the node
     * @return true, when the list contains a matching node. otherwise false
     */
    public static boolean contains(List<CategoryNode> list, String name, long left, long right, long level) {
        return list.stream().anyMatch(n -> matches(n, name, left, right, level));
    }

    /**
     * Searches a list of CateoryNodes for a node with a given name. Returns the node with the name, if it can be
     * found inside the list.
     *
     * @param nodes List of nodes
     * @param name  name of node which will be searched
     * @return node if it can be found. null otherwise.
     */
    public static CategoryNode getByName(List<CategoryNode> nodes, String name) {
        return nodes.stream()
                .filter(n -> name.equals(n.getName()))
                .findFirst()
                .orElse(null);
    }
}
