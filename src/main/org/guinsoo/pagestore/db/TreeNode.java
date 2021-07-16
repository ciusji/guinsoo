/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.pagestore.db;

import org.guinsoo.result.Row;

/**
 * Represents a index node of a tree index.
 */
class TreeNode {

    /**
     * The balance. For more information, see the AVL tree documentation.
     */
    int balance;

    /**
     * The left child node or null.
     */
    TreeNode left;

    /**
     * The right child node or null.
     */
    TreeNode right;

    /**
     * The parent node or null if this is the root node.
     */
    TreeNode parent;

    /**
     * The row.
     */
    final Row row;

    TreeNode(Row row) {
        this.row = row;
    }

    /**
     * Check if this node is the left child of its parent. This method returns
     * true if this is the root node.
     *
     * @return true if this node is the root or a left child
     */
    boolean isFromLeft() {
        return parent == null || parent.left == this;
    }

}
