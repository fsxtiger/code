package mysql.tree;

import java.util.Collections;
import java.util.List;

class TreeNode {
    int value;
    int left;
    int right;

    List<TreeNode> childs = Collections.emptyList();
}

public class TreeBuild {
    public static void main(String[] args) {
        TreeNode root = new TreeNode();
        root.right = buildTree(root, 1);
    }

    public static int buildTree(TreeNode node, int left) {
        node.left = left;
        int right = left + 1;
        for (TreeNode child : node.childs) {
            right = buildTree(child, right);
        }

        node.right = right;
        return node.right + 1;
    }
}
