package org.example.part2;

import java.util.ArrayList;
import java.util.List;

public class RBTree<T extends Comparable<T>> {

    // =====================================================================
    // ПОЛЯ
    // =====================================================================
    private RBTreeNode<T> root;
    private final RBTreeNode<T> NIL; // sentinel-узел (пустой лист, всегда чёрный)

    public RBTree() {
        NIL = new RBTreeNode<>(null);
        NIL.color = RBTreeNode.Color.BLACK;
        NIL.left = NIL;
        NIL.right = NIL;
        NIL.parent = NIL;
        root = NIL;
    }

    // =====================================================================
    // INSERT
    // =====================================================================
    public void insert(T value) {
        RBTreeNode<T> node = new RBTreeNode<>(value);
        node.left = NIL;
        node.right = NIL;

        if (root == NIL) {
            root = node;
            root.color = RBTreeNode.Color.BLACK;
            root.parent = NIL;
            return;
        }

        // Обычная вставка как в BST
        RBTreeNode<T> current = root;
        RBTreeNode<T> parent = NIL;

        while (current != NIL) {
            parent = current;
            if (value.compareTo(current.value) < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        node.parent = parent;
        if (value.compareTo(parent.value) < 0) {
            parent.left = node;
        } else {
            parent.right = node;
        }

        fixAfterInsert(node);
    }

    // =====================================================================
    // БАЛАНСИРОВКА ПОСЛЕ ВСТАВКИ
    // =====================================================================
    private void fixAfterInsert(RBTreeNode<T> node) {
        while (node.parent.color == RBTreeNode.Color.RED) {
            if (node.parent == node.parent.parent.left) {
                // Родитель — левый ребёнок деда
                RBTreeNode<T> uncle = node.parent.parent.right;

                if (uncle.color == RBTreeNode.Color.RED) {
                    // Случай 1: дядя красный — перекрашиваем
                    node.parent.color = RBTreeNode.Color.BLACK;
                    uncle.color = RBTreeNode.Color.BLACK;
                    node.parent.parent.color = RBTreeNode.Color.RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.right) {
                        // Случай 2: узел — правый ребёнок (треугольник) → поворот влево
                        node = node.parent;
                        rotateLeft(node);
                    }
                    // Случай 3: поворот вправо
                    node.parent.color = RBTreeNode.Color.BLACK;
                    node.parent.parent.color = RBTreeNode.Color.RED;
                    rotateRight(node.parent.parent);
                }
            } else {
                // Зеркальный случай: родитель — правый ребёнок деда
                RBTreeNode<T> uncle = node.parent.parent.left;

                if (uncle.color == RBTreeNode.Color.RED) {
                    node.parent.color = RBTreeNode.Color.BLACK;
                    uncle.color = RBTreeNode.Color.BLACK;
                    node.parent.parent.color = RBTreeNode.Color.RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.left) {
                        // Случай 2 зеркальный: треугольник → поворот вправо
                        node = node.parent;
                        rotateRight(node);
                    }
                    // Случай 3 зеркальный: поворот влево
                    node.parent.color = RBTreeNode.Color.BLACK;
                    node.parent.parent.color = RBTreeNode.Color.RED;
                    rotateLeft(node.parent.parent);
                }
            }
        }
        root.color = RBTreeNode.Color.BLACK;
    }

    // =====================================================================
    // FIND
    // =====================================================================
    public boolean find(T value) {
        RBTreeNode<T> current = root;
        while (current != NIL) {
            int cmp = value.compareTo(current.value);
            if (cmp == 0) {
                return true;
            } else if (cmp < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return false;
    }

    // =====================================================================
    // DELETE
    // =====================================================================
    public void delete(T value) {
        RBTreeNode<T> node = findNode(value);
        if (node == NIL) {
            return;
        }
        deleteNode(node);
    }

    private void deleteNode(RBTreeNode<T> node) {
        RBTreeNode<T> toDelete = node;
        RBTreeNode<T> child;
        RBTreeNode.Color originalColor = toDelete.color;

        if (node.left == NIL) {
            child = node.right;
            transplant(node, node.right);
        } else if (node.right == NIL) {
            child = node.left;
            transplant(node, node.left);
        } else {
            // Два ребёнка — ищем минимум в правом поддереве (преемника)
            toDelete = minimum(node.right);
            originalColor = toDelete.color;
            child = toDelete.right;
            if (toDelete.parent == node) {
                child.parent = toDelete;
            } else {
                transplant(toDelete, toDelete.right);
                toDelete.right = node.right;
                toDelete.right.parent = toDelete;
            }
            transplant(node, toDelete);
            toDelete.left = node.left;
            toDelete.left.parent = toDelete;
            toDelete.color = node.color;
        }

        if (originalColor == RBTreeNode.Color.BLACK) {
            fixAfterDelete(child);
        }
    }

    // =====================================================================
    // БАЛАНСИРОВКА ПОСЛЕ УДАЛЕНИЯ
    // =====================================================================
    private void fixAfterDelete(RBTreeNode<T> node) {
        while (node != root && node.color == RBTreeNode.Color.BLACK) {
            if (node == node.parent.left) {
                RBTreeNode<T> sibling = node.parent.right;

                if (sibling.color == RBTreeNode.Color.RED) {
                    // Случай 1: брат красный
                    sibling.color = RBTreeNode.Color.BLACK;
                    node.parent.color = RBTreeNode.Color.RED;
                    rotateLeft(node.parent);
                    sibling = node.parent.right;
                }

                if (sibling.left.color == RBTreeNode.Color.BLACK &&
                        sibling.right.color == RBTreeNode.Color.BLACK) {
                    // Случай 2: оба ребёнка брата чёрные
                    sibling.color = RBTreeNode.Color.RED;
                    node = node.parent;
                } else {
                    if (sibling.right.color == RBTreeNode.Color.BLACK) {
                        // Случай 3: правый ребёнок брата чёрный
                        sibling.left.color = RBTreeNode.Color.BLACK;
                        sibling.color = RBTreeNode.Color.RED;
                        rotateRight(sibling);
                        sibling = node.parent.right;
                    }
                    // Случай 4: правый ребёнок брата красный
                    sibling.color = node.parent.color;
                    node.parent.color = RBTreeNode.Color.BLACK;
                    sibling.right.color = RBTreeNode.Color.BLACK;
                    rotateLeft(node.parent);
                    node = root;
                }
            } else {
                // Зеркальный случай
                RBTreeNode<T> sibling = node.parent.left;

                if (sibling.color == RBTreeNode.Color.RED) {
                    sibling.color = RBTreeNode.Color.BLACK;
                    node.parent.color = RBTreeNode.Color.RED;
                    rotateRight(node.parent);
                    sibling = node.parent.left;
                }

                if (sibling.right.color == RBTreeNode.Color.BLACK &&
                        sibling.left.color == RBTreeNode.Color.BLACK) {
                    sibling.color = RBTreeNode.Color.RED;
                    node = node.parent;
                } else {
                    if (sibling.left.color == RBTreeNode.Color.BLACK) {
                        sibling.right.color = RBTreeNode.Color.BLACK;
                        sibling.color = RBTreeNode.Color.RED;
                        rotateLeft(sibling);
                        sibling = node.parent.left;
                    }
                    sibling.color = node.parent.color;
                    node.parent.color = RBTreeNode.Color.BLACK;
                    sibling.left.color = RBTreeNode.Color.BLACK;
                    rotateRight(node.parent);
                    node = root;
                }
            }
        }
        node.color = RBTreeNode.Color.BLACK;
    }

    // =====================================================================
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // =====================================================================
    private void rotateLeft(RBTreeNode<T> x) {
        RBTreeNode<T> y = x.right;
        x.right = y.left;
        if (y.left != NIL) y.left.parent = x;
        y.parent = x.parent;
        if (x.parent == NIL) root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        y.left = x;
        x.parent = y;
    }

    private void rotateRight(RBTreeNode<T> x) {
        RBTreeNode<T> y = x.left;
        x.left = y.right;
        if (y.right != NIL) y.right.parent = x;
        y.parent = x.parent;
        if (x.parent == NIL) root = y;
        else if (x == x.parent.right) x.parent.right = y;
        else x.parent.left = y;
        y.right = x;
        x.parent = y;
    }

    private void transplant(RBTreeNode<T> u, RBTreeNode<T> v) {
        if (u.parent == NIL) root = v;
        else if (u == u.parent.left) u.parent.left = v;
        else u.parent.right = v;
        v.parent = u.parent;
    }

    private RBTreeNode<T> findNode(T value) {
        RBTreeNode<T> current = root;
        while (current != NIL) {
            int cmp = value.compareTo(current.value);
            if (cmp == 0) return current;
            current = cmp < 0 ? current.left : current.right;
        }
        return NIL;
    }

    private RBTreeNode<T> minimum(RBTreeNode<T> node) {
        while (node.left != NIL) node = node.left;
        return node;
    }

    // =====================================================================
    // УТИЛИТЫ
    // =====================================================================
    public List<T> toList() {
        List<T> result = new ArrayList<>();
        inorder(root, result);
        return result;
    }

    private void inorder(RBTreeNode<T> node, List<T> result) {
        if (node == NIL) return;
        inorder(node.left, result);
        result.add(node.value);
        inorder(node.right, result);
    }

    public boolean isEmpty() {
        return root == NIL;
    }

    // Получить цвет корня (для тестов)
    public RBTreeNode.Color getRootColor() {
        if (root == NIL) return null;
        return root.color;
    }
}