package org.example.part2;

public class RBTreeNode<T extends Comparable<T>> {
    public enum Color { RED, BLACK }

    public T value;
    Color color;
    RBTreeNode<T> left;
    RBTreeNode<T> right;
    RBTreeNode<T> parent;

    public RBTreeNode(T value) {
        this.value = value;
        this.color = Color.RED; // новые узлы всегда красные
        this.left = null;
        this.right = null;
        this.parent = null;
    }

    public T getValue() { return value; }
    public Color getColor() { return color; }
    public RBTreeNode<T> getLeft() { return left; }
    public RBTreeNode<T> getRight() { return right; }
    public RBTreeNode<T> getParent() { return parent; }
}