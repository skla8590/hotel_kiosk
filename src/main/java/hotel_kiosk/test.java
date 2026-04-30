package hotel_kiosk;

import java.util.ArrayList;
import java.util.List;

/**
 * 노드 트리 구조 구현
 * - 트리 생성, 삽입, 탐색(DFS/BFS), 출력 기능 포함
 */
public class test {

    // ============================
    // 노드 클래스
    // ============================
    static class TreeNode {
        int value;
        List<TreeNode> children;

        public TreeNode(int value) {
            this.value = value;
            this.children = new ArrayList<>();
        }

        // 자식 노드 추가
        public void addChild(TreeNode child) {
            this.children.add(child);
        }
    }

    // ============================
    // 트리 클래스
    // ============================
    static class Tree {
        TreeNode root;

        public Tree(int rootValue) {
            this.root = new TreeNode(rootValue);
        }

        /**
         * 특정 값을 가진 노드를 찾아 자식 추가 (DFS 기반)
         */
        public boolean addChild(int parentValue, int childValue) {
            TreeNode parent = findNode(root, parentValue);
            if (parent != null) {
                parent.addChild(new TreeNode(childValue));
                return true;
            }
            return false;
        }

        /**
         * DFS로 특정 값의 노드 탐색
         */
        public TreeNode findNode(TreeNode node, int value) {
            if (node == null) return null;
            if (node.value == value) return node;
            for (TreeNode child : node.children) {
                TreeNode found = findNode(child, value);
                if (found != null) return found;
            }
            return null;
        }

        /**
         * DFS 순회 (깊이 우선 탐색)
         */
        public void dfs(TreeNode node) {
            if (node == null) return;
            System.out.print(node.value + " ");
            for (TreeNode child : node.children) {
                dfs(child);
            }
        }

        /**
         * BFS 순회 (너비 우선 탐색)
         */
        public void bfs() {
            if (root == null) return;
            List<TreeNode> queue = new ArrayList<>();
            queue.add(root);
            while (!queue.isEmpty()) {
                TreeNode current = queue.remove(0);
                System.out.print(current.value + " ");
                queue.addAll(current.children);
            }
        }

        /**
         * 트리 구조를 시각적으로 출력
         */
        public void printTree(TreeNode node, String prefix, boolean isLast) {
            if (node == null) return;
            System.out.println(prefix + (isLast ? "└── " : "├── ") + node.value);
            for (int i = 0; i < node.children.size(); i++) {
                boolean last = (i == node.children.size() - 1);
                printTree(node.children.get(i), prefix + (isLast ? "    " : "│   "), last);
            }
        }

        /**
         * 트리 높이 계산
         */
        public int getHeight(TreeNode node) {
            if (node == null) return 0;
            if (node.children.isEmpty()) return 1;
            int maxHeight = 0;
            for (TreeNode child : node.children) {
                maxHeight = Math.max(maxHeight, getHeight(child));
            }
            return maxHeight + 1;
        }

        /**
         * 노드 개수 반환
         */
        public int getSize(TreeNode node) {
            if (node == null) return 0;
            int size = 1;
            for (TreeNode child : node.children) {
                size += getSize(child);
            }
            return size;
        }
    }

    // ============================
    // 메인
    // ============================
    public static void main(String[] args) {

        /*
         * 구성할 트리 구조:
         *
         *         1
         *       / | \
         *      2  3  4
         *     / \    |
         *    5   6   7
         *        |
         *        8
         */

        Tree tree = new Tree(1);

        tree.addChild(1, 2);
        tree.addChild(1, 3);
        tree.addChild(1, 4);
        tree.addChild(2, 5);
        tree.addChild(2, 6);
        tree.addChild(4, 7);
        tree.addChild(6, 8);

        // 트리 구조 시각적 출력
        System.out.println("=== 트리 구조 ===");
        tree.printTree(tree.root, "", true);

        // DFS 순회
        System.out.println("\n=== DFS 순회 (깊이 우선) ===");
        tree.dfs(tree.root);

        // BFS 순회
        System.out.println("\n\n=== BFS 순회 (너비 우선) ===");
        tree.bfs();

        // 트리 정보
        System.out.println("\n\n=== 트리 정보 ===");
        System.out.println("트리 높이: " + tree.getHeight(tree.root));
        System.out.println("노드 개수: " + tree.getSize(tree.root));

        // 특정 노드 탐색
        int searchValue = 6;
        TreeNode found = tree.findNode(tree.root, searchValue);
        System.out.println("노드 " + searchValue + " 탐색 결과: " + (found != null ? "찾음 (값: " + found.value + ")" : "없음"));
    }
}
