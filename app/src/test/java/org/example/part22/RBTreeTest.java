package org.example.part22;


import org.example.part2.RBTree;
import org.example.part2.RBTreeNode;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class RBTreeTest {

    @Test
    void test_TC1_insertRoot() {
        RBTree<Integer> t = new RBTree<>();
        t.insert(10);
        assertEquals(RBTreeNode.Color.BLACK, t.getRootColor());
        assertEquals(List.of(10), t.toList());
    }

    @Test
    void test_TC2_uncleRed_recolor() throws Exception {
        RBTree<Integer> t = new RBTree<>();
        t.insert(10); t.insert(5); t.insert(15); t.insert(3);

        var root = getRoot(t);
        assertEquals(10, root.value);
        assertEquals(RBTreeNode.Color.BLACK, root.getColor());
        assertEquals(RBTreeNode.Color.BLACK, root.getLeft().getColor());
        assertEquals(RBTreeNode.Color.BLACK, root.getRight().getColor());
        assertEquals(RBTreeNode.Color.RED,   root.getLeft().getLeft().getColor());
    }

    @Test
    void test_TC3_line_rotateLeft() throws Exception {
        RBTree<Integer> t = new RBTree<>();
        t.insert(10); t.insert(20); t.insert(30);

        var root = getRoot(t);
        assertEquals(20, root.value);
        assertEquals(RBTreeNode.Color.BLACK, root.getColor());
        assertEquals(RBTreeNode.Color.RED, root.getLeft().getColor());
        assertEquals(RBTreeNode.Color.RED, root.getRight().getColor());
    }

    @Test
    void test_TC4_triangle_doubleRotate() {
        RBTree<Integer> t = new RBTree<>();
        t.insert(10); t.insert(5); t.insert(15);
        t.insert(3);  t.insert(7); t.insert(6);

        assertEquals(List.of(3, 5, 6, 7, 10, 15), t.toList());
        assertEquals(RBTreeNode.Color.BLACK, t.getRootColor());
    }

    @Test
    void test_TC5_deleteRedLeaf() {
        RBTree<Integer> t = new RBTree<>();
        t.insert(10); t.insert(5); t.insert(15);
        t.delete(5);

        assertFalse(t.find(5));
        assertEquals(List.of(10, 15), t.toList());
    }

    @Test
    void test_TC6_deleteTwoChildren() {
        RBTree<Integer> t = new RBTree<>();
        t.insert(10); t.insert(5); t.insert(15);
        t.insert(3);  t.insert(7);
        t.delete(5);

        assertFalse(t.find(5));
        assertTrue(t.find(7));
        assertEquals(List.of(3, 7, 10, 15), t.toList());
    }

    @Test
    void test_negative_operationsOnEmptyTree() {
        RBTree<Integer> t = new RBTree<>();

        assertFalse(t.find(10));
        assertDoesNotThrow(() -> t.delete(10));
        assertTrue(t.isEmpty());
        assertEquals(List.of(), t.toList());
    }

    @Test
    void test_negative_deleteMissingValue() {
        RBTree<Integer> t = new RBTree<>();
        t.insert(10);
        t.insert(5);
        t.insert(15);

        t.delete(7);

        assertFalse(t.find(7));
        assertEquals(List.of(5, 10, 15), t.toList());
        assertEquals(RBTreeNode.Color.BLACK, t.getRootColor());
    }

    private RBTreeNode<Integer> getRoot(RBTree<Integer> tree) throws Exception {
        var f = RBTree.class.getDeclaredField("root");
        f.setAccessible(true);
        return (RBTreeNode<Integer>) f.get(tree);
    }
}
