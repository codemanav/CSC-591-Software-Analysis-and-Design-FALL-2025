1. Breadth First Search
/**
 * Definition for a binary tree node.
 * struct TreeNode {
 *     int val;
 *     TreeNode *left;
 *     TreeNode *right;
 *     TreeNode() : val(0), left(nullptr), right(nullptr) {}
 *     TreeNode(int x) : val(x), left(nullptr), right(nullptr) {}
 *     TreeNode(int x, TreeNode *left, TreeNode *right) : val(x), left(left), right(right) {}
 * };
 */

class Solution {
public:
    TreeNode* invertTree(TreeNode* root) {
        if (!root) return nullptr;
        queue<TreeNode*> queue;
        queue.push(root);
        while (!queue.empty()) {
            TreeNode* node = queue.front();
            queue.pop();
            swap(node->left, node->right);
            if (node->left) queue.push(node->left);
            if (node->right) queue.push(node->right);
        }
        return root;
    }
};
Time & Space Complexity
Time complexity: 
O
(
n
)
O(n)
Space complexity: 
O
(
n
)
O(n)
2. Depth First Search
/**
 * Definition for a binary tree node.
 * struct TreeNode {
 *     int val;
 *     TreeNode *left;
 *     TreeNode *right;
 *     TreeNode() : val(0), left(nullptr), right(nullptr) {}
 *     TreeNode(int x) : val(x), left(nullptr), right(nullptr) {}
 *     TreeNode(int x, TreeNode *left, TreeNode *right) : val(x), left(left), right(right) {}
 * };
 */

class Solution {
public:
    TreeNode* invertTree(TreeNode* root) {
        if (!root) return nullptr;

        swap(root->left, root->right);
        invertTree(root->left);
        invertTree(root->right);

        return root;
    }
};
Time & Space Complexity
Time complexity: 
O
(
n
)
O(n)
Space complexity: 
O
(
n
)
O(n) for recursion stack.
3. Iterative DFS
/**
 * Definition for a binary tree node.
 * struct TreeNode {
 *     int val;
 *     TreeNode *left;
 *     TreeNode *right;
 *     TreeNode() : val(0), left(nullptr), right(nullptr) {}
 *     TreeNode(int x) : val(x), left(nullptr), right(nullptr) {}
 *     TreeNode(int x, TreeNode *left, TreeNode *right) : val(x), left(left), right(right) {}
 * };
 */

class Solution {
public:
    TreeNode* invertTree(TreeNode* root) {
        if (!root) return nullptr;
        stack<TreeNode*> stack;
        stack.push(root);
        while (!stack.empty()) {
            TreeNode* node = stack.top();
            stack.pop();
            swap(node->left, node->right);
            if (node->left) stack.push(node->left);
            if (node->right) stack.push(node->right);
        }
        return root;
    }
};
Time & Space Complexity
Time complexity: 
O
(
n
)
O(n)
Space complexity: 
O
(
n
)
O(n)