package org.example.part22;


import org.example.part2.RBTree;
import org.example.part2.RBTreeNode;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Характерные точки:
 * ТЧ1 — вставка в пустое дерево (корень становится BLACK)
 * 
 * ТЧ2 — fixAfterInsert: дядя RED → перекрашивание (случай 1)
 * ТЧ3 — fixAfterInsert: линия → одинарный поворот (случай 3)
 * ТЧ4 — fixAfterInsert: треугольник → двойной поворот (случай 2+3)
 * ТЧ5 — delete: удаление RED-листа (fix не нужен)
 * ТЧ6 — delete: удаление узла с двумя детьми (замена преемником)
 */ 
public class RBTreeTest {

    // ТЧ1: вставка [10] → корень BLACK
    @Test
    void test_TC1_insertRoot() {
        RBTree<Integer> t = new RBTree<>();
        t.insert(10);
        assertEquals(RBTreeNode.Color.BLACK, t.getRootColor());
        assertEquals(List.of(10), t.toList());
    }

    // ТЧ2: вставка [10, 5, 15, 3] → дядя RED → recolor
    // После fix: root=10(B), 5(B), 15(B), 3(R)
    @Test
    void test_TC2_uncleRed_recolor() throws Exception {
        RBTree<Integer> t = new RBTree<>();
        t.insert(10); t.insert(5); t.insert(15); t.insert(3);

        var root = getRoot(t);
        assertEquals(10, root.value);
        assertEquals(RBTreeNode.Color.BLACK, root.getColor());
        assertEquals(RBTreeNode.Color.BLACK, root.getLeft().getColor());  // 5 → BLACK после recolor
        assertEquals(RBTreeNode.Color.BLACK, root.getRight().getColor()); // 15 → BLACK после recolor
        assertEquals(RBTreeNode.Color.RED,   root.getLeft().getLeft().getColor()); // 3 → RED
    }

    // ТЧ3: вставка [10, 20, 30] → линия → rotateLeft
    // После fix: root=20(B), left=10(R), right=30(R)
    @Test
    void test_TC3_line_rotateLeft() throws Exception {
        RBTree<Integer> t = new RBTree<>();
        t.insert(10); t.insert(20); t.insert(30);

        var root = getRoot(t);
        assertEquals(20, root.value);
        assertEquals(RBTreeNode.Color.BLACK, root.getColor());
        assertEquals(RBTreeNode.Color.RED, root.getLeft().getColor());  // 10
        assertEquals(RBTreeNode.Color.RED, root.getRight().getColor()); // 30
    }

    // ТЧ4: вставка [10, 5, 15, 3, 7, 6] → треугольник → двойной поворот
    // Эталон: ТЧ1 → ТЧ2 → ТЧ4
    // После fix: 6 находится в дереве, inorder корректен
    @Test
    void test_TC4_triangle_doubleRotate() {
        RBTree<Integer> t = new RBTree<>();
        t.insert(10); t.insert(5); t.insert(15);
        t.insert(3);  t.insert(7); t.insert(6);

        assertEquals(List.of(3, 5, 6, 7, 10, 15), t.toList());
        assertEquals(RBTreeNode.Color.BLACK, t.getRootColor());
    }

    // ТЧ5: вставка [10, 5, 15], удаление 5 (RED-лист, fix не нужен)
    // Эталон: ТЧ5
    @Test
    void test_TC5_deleteRedLeaf() {
        RBTree<Integer> t = new RBTree<>();
        t.insert(10); t.insert(5); t.insert(15);
        t.delete(5);

        assertFalse(t.find(5));
        assertEquals(List.of(10, 15), t.toList());
    }

    // ТЧ6: вставка [10, 5, 15, 3, 7], удаление 5 (два ребёнка → преемник = 7)
    // Эталон: ТЧ6
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

    private RBTreeNode<Integer> getRoot(RBTree<Integer> tree) throws Exception {
        var f = RBTree.class.getDeclaredField("root");
        f.setAccessible(true);
        return (RBTreeNode<Integer>) f.get(tree);
    }
}
