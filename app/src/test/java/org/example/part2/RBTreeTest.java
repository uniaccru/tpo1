package org.example.part2;

import org.example.part2.tracing.RBTreeLineTraceAgent;
import org.example.part2.tracing.RBTreeTraceMapping;
import org.example.part2.tracing.RBTreeTracePoint;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.example.part2.tracing.RBTreeTracePoint.DELETE_FIX_NEEDED;
import static org.example.part2.tracing.RBTreeTracePoint.DELETE_FIX_SIBLING_BLACK_KIDS;
import static org.example.part2.tracing.RBTreeTracePoint.DELETE_FIX_SIBLING_RED;
import static org.example.part2.tracing.RBTreeTracePoint.DELETE_FIX_SIBLING_RIGHT_RED;
import static org.example.part2.tracing.RBTreeTracePoint.DELETE_FIX_DONE;
import static org.example.part2.tracing.RBTreeTracePoint.DELETE_FOUND;
import static org.example.part2.tracing.RBTreeTracePoint.DELETE_NOT_FOUND;
import static org.example.part2.tracing.RBTreeTracePoint.DELETE_TWO_CHILDREN;
import static org.example.part2.tracing.RBTreeTracePoint.FIND_GO_LEFT;
import static org.example.part2.tracing.RBTreeTracePoint.FIND_GO_RIGHT;
import static org.example.part2.tracing.RBTreeTracePoint.FIND_MATCH;
import static org.example.part2.tracing.RBTreeTracePoint.FIND_NOT_FOUND;
import static org.example.part2.tracing.RBTreeTracePoint.FIX_ROTATE_LEFT;
import static org.example.part2.tracing.RBTreeTracePoint.FIX_ROTATE_LEFT_CASE;
import static org.example.part2.tracing.RBTreeTracePoint.FIX_ROTATE_RIGHT;
import static org.example.part2.tracing.RBTreeTracePoint.FIX_ROTATE_RIGHT_CASE;
import static org.example.part2.tracing.RBTreeTracePoint.FIX_UNCLE_RED;
import static org.example.part2.tracing.RBTreeTracePoint.INSERT_EMPTY_TREE;
import static org.example.part2.tracing.RBTreeTracePoint.INSERT_GO_LEFT;
import static org.example.part2.tracing.RBTreeTracePoint.INSERT_GO_RIGHT;
import static org.example.part2.tracing.RBTreeTracePoint.INSERT_PLACED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RBTreeTest {

    private static Class<?> rbTreeClass;
    private static Method insertMethod;
    private static Method deleteMethod;
    private static Method findMethod;
    private static Method toListMethod;
    private static Method isEmptyMethod;
    private static Method getRootColorMethod;

    @BeforeAll
    static void installTracing() throws Exception {
        RBTreeLineTraceAgent.install(RBTreeTraceMapping.mapping());

        rbTreeClass = Class.forName("org.example.part2.RBTree");
        insertMethod = rbTreeClass.getMethod("insert", Comparable.class);
        deleteMethod = rbTreeClass.getMethod("delete", Comparable.class);
        findMethod = rbTreeClass.getMethod("find", Comparable.class);
        toListMethod = rbTreeClass.getMethod("toList");
        isEmptyMethod = rbTreeClass.getMethod("isEmpty");
        getRootColorMethod = rbTreeClass.getMethod("getRootColor");
    }

    @BeforeEach
    void clearTrace() {
        RBTreeLineTraceAgent.clearTrace();
    }

    private Object newTree() throws Exception {
        return rbTreeClass.getDeclaredConstructor().newInstance();
    }

    private void insert(Object tree, int value) throws Exception {
        insertMethod.invoke(tree, value);
    }

    private void delete(Object tree, int value) throws Exception {
        deleteMethod.invoke(tree, value);
    }

    private boolean find(Object tree, int value) throws Exception {
        return (Boolean) findMethod.invoke(tree, value);
    }

    @SuppressWarnings("unchecked")
    private List<Integer> toList(Object tree) throws Exception {
        return (List<Integer>) toListMethod.invoke(tree);
    }

    private boolean isEmpty(Object tree) throws Exception {
        return (Boolean) isEmptyMethod.invoke(tree);
    }

    private String rootColorName(Object tree) throws Exception {
        Object color = getRootColorMethod.invoke(tree);
        return color == null ? null : color.toString();
    }

    private List<RBTreeTracePoint> trace(ThrowingRunnable operation) throws Exception {
        RBTreeLineTraceAgent.startTrace();
        operation.run();
        return RBTreeLineTraceAgent.stopTrace();
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    @Test
    void insertEmptyTree() throws Exception {
        Object tree = newTree();
        List<RBTreeTracePoint> trace = trace(() -> insert(tree, 10));

        assertIterableEquals(Arrays.asList(10), toList(tree));
        assertEquals("BLACK", rootColorName(tree));
        assertIterableEquals(Arrays.asList(INSERT_EMPTY_TREE), trace);
    }

    @Test
    void insertGoRightNoFix() throws Exception {
        Object tree = newTree();
        insert(tree, 10);
        List<RBTreeTracePoint> trace = trace(() -> insert(tree, 20));

        assertIterableEquals(Arrays.asList(10, 20), toList(tree));
        assertIterableEquals(Arrays.asList(INSERT_GO_RIGHT, INSERT_PLACED), trace);
    }

    @Test
    void insertGoLeftNoFix() throws Exception {
        Object tree = newTree();
        insert(tree, 10);
        List<RBTreeTracePoint> trace = trace(() -> insert(tree, 5));

        assertIterableEquals(Arrays.asList(5, 10), toList(tree));
        assertIterableEquals(Arrays.asList(INSERT_GO_LEFT, INSERT_PLACED), trace);
    }

    @Test
    void insertFixUncleRed() throws Exception {
        Object tree = newTree();
        insert(tree, 10);
        insert(tree, 5);
        insert(tree, 20);
        List<RBTreeTracePoint> trace = trace(() -> insert(tree, 3));

        assertIterableEquals(Arrays.asList(3, 5, 10, 20), toList(tree));
        assertIterableEquals(Arrays.asList(INSERT_GO_LEFT, INSERT_GO_LEFT, INSERT_PLACED, FIX_UNCLE_RED), trace);
    }

    @Test
    void insertFixRotateRight() throws Exception {
        Object tree = newTree();
        insert(tree, 10);
        insert(tree, 5);
        List<RBTreeTracePoint> trace = trace(() -> insert(tree, 3));

        assertIterableEquals(Arrays.asList(3, 5, 10), toList(tree));
        assertIterableEquals(Arrays.asList(INSERT_GO_LEFT, INSERT_GO_LEFT, INSERT_PLACED, FIX_ROTATE_RIGHT), trace);
    }

    @Test
    void insertFixRotateLeftCase() throws Exception {
        Object tree = newTree();
        insert(tree, 10);
        insert(tree, 5);
        List<RBTreeTracePoint> trace = trace(() -> insert(tree, 7));

        assertIterableEquals(Arrays.asList(5, 7, 10), toList(tree));
        assertIterableEquals(Arrays.asList(INSERT_GO_LEFT, INSERT_GO_RIGHT, INSERT_PLACED, FIX_ROTATE_LEFT_CASE, FIX_ROTATE_RIGHT), trace);
    }

    @Test
    void insertFixRotateLeft() throws Exception {
        Object tree = newTree();
        insert(tree, 10);
        insert(tree, 20);
        List<RBTreeTracePoint> trace = trace(() -> insert(tree, 30));

        assertIterableEquals(Arrays.asList(10, 20, 30), toList(tree));
        assertIterableEquals(Arrays.asList(INSERT_GO_RIGHT, INSERT_GO_RIGHT, INSERT_PLACED, FIX_ROTATE_LEFT), trace);
    }

    @Test
    void insertFixRotateRightCase() throws Exception {
        Object tree = newTree();
        insert(tree, 10);
        insert(tree, 20);
        List<RBTreeTracePoint> trace = trace(() -> insert(tree, 15));

        assertIterableEquals(Arrays.asList(10, 15, 20), toList(tree));
        assertIterableEquals(Arrays.asList(INSERT_GO_RIGHT, INSERT_GO_LEFT, INSERT_PLACED, FIX_ROTATE_RIGHT_CASE, FIX_ROTATE_LEFT), trace);
    }

    @Test
    void findMatch() throws Exception {
        Object tree = newTree();
        insert(tree, 10);

        RBTreeLineTraceAgent.startTrace();
        boolean found = find(tree, 10);
        List<RBTreeTracePoint> trace = RBTreeLineTraceAgent.stopTrace();

        assertTrue(found);
        assertIterableEquals(Arrays.asList(FIND_MATCH), trace);
    }

    @Test
    void findGoLeftThenMatch() throws Exception {
        Object tree = newTree();
        insert(tree, 10);
        insert(tree, 5);

        RBTreeLineTraceAgent.startTrace();
        boolean found = find(tree, 5);
        List<RBTreeTracePoint> trace = RBTreeLineTraceAgent.stopTrace();

        assertTrue(found);
        assertIterableEquals(Arrays.asList(FIND_GO_LEFT, FIND_MATCH), trace);
    }

    @Test
    void findGoRightThenMatch() throws Exception {
        Object tree = newTree();
        insert(tree, 10);
        insert(tree, 20);

        RBTreeLineTraceAgent.startTrace();
        boolean found = find(tree, 20);
        List<RBTreeTracePoint> trace = RBTreeLineTraceAgent.stopTrace();

        assertTrue(found);
        assertIterableEquals(Arrays.asList(FIND_GO_RIGHT, FIND_MATCH), trace);
    }

    @Test
    void findNotFound() throws Exception {
        Object tree = newTree();
        insert(tree, 10);

        RBTreeLineTraceAgent.startTrace();
        boolean found = find(tree, 99);
        List<RBTreeTracePoint> trace = RBTreeLineTraceAgent.stopTrace();

        assertFalse(found);
        assertIterableEquals(Arrays.asList(FIND_GO_RIGHT, FIND_NOT_FOUND), trace);
    }

    @Test
    void deleteNotFound() throws Exception {
        Object tree = newTree();
        insert(tree, 10);
        List<RBTreeTracePoint> trace = trace(() -> delete(tree, 99));

        assertIterableEquals(Arrays.asList(10), toList(tree));
        assertIterableEquals(Arrays.asList(DELETE_NOT_FOUND), trace);
    }

    @Test
    void deleteSingleRoot() throws Exception {
        Object tree = newTree();
        insert(tree, 10);
        List<RBTreeTracePoint> trace = trace(() -> delete(tree, 10));

        assertTrue(isEmpty(tree));
        assertIterableEquals(Arrays.asList(DELETE_FOUND, DELETE_FIX_NEEDED, DELETE_FIX_DONE), trace);
    }

    @Test
    void deleteRedLeaf() throws Exception {
        Object tree = newTree();
        insert(tree, 10);
        insert(tree, 5);
        insert(tree, 20);
        List<RBTreeTracePoint> trace = trace(() -> delete(tree, 5));

        assertIterableEquals(Arrays.asList(10, 20), toList(tree));
        assertIterableEquals(Arrays.asList(DELETE_FOUND), trace);
    }

    @Test
    void deleteTwoChildren() throws Exception {
        Object tree = newTree();
        insert(tree, 10);
        insert(tree, 5);
        insert(tree, 20);
        List<RBTreeTracePoint> trace = trace(() -> delete(tree, 10));

        assertIterableEquals(Arrays.asList(5, 20), toList(tree));
        assertTrue(trace.contains(DELETE_TWO_CHILDREN));
    }

    @Test
    void deleteFixSiblingBlackKids() throws Exception {
        Object tree = newTree();
        for (int v : new int[]{10, 5, 20, 30}) insert(tree, v);
        delete(tree, 30);
        List<RBTreeTracePoint> trace = trace(() -> delete(tree, 5));

        assertIterableEquals(Arrays.asList(10, 20), toList(tree));
        assertTrue(trace.contains(DELETE_FIX_SIBLING_BLACK_KIDS));
    }

    @Test
    void deleteFixSiblingRightRed() throws Exception {
        Object tree = newTree();
        for (int v : new int[]{10, 5, 20, 30}) insert(tree, v);
        List<RBTreeTracePoint> trace = trace(() -> delete(tree, 5));

        assertIterableEquals(Arrays.asList(10, 20, 30), toList(tree));
        assertTrue(trace.contains(DELETE_FIX_SIBLING_RIGHT_RED));
    }

    @Test
    void deleteFixSiblingRed() throws Exception {
        Object tree = newTree();
        for (int v : new int[]{41, 38, 31, 12, 19, 8}) insert(tree, v);
        List<RBTreeTracePoint> trace = trace(() -> delete(tree, 41));

        assertIterableEquals(Arrays.asList(8, 12, 19, 31, 38), toList(tree));
        assertTrue(trace.contains(DELETE_FIX_SIBLING_RED), "Expected SIBLING_RED in trace: " + trace);
    }

    @Test
    void nodeGetters() {
        RBTreeNode<Integer> node = new RBTreeNode<>(42);
        assertEquals(Integer.valueOf(42), node.getValue());
        assertEquals(RBTreeNode.Color.RED, node.getColor());
        assertNull(node.getLeft());
        assertNull(node.getRight());
        assertNull(node.getParent());
    }

    @Test
    void isEmptyAndToList() throws Exception {
        Object tree = newTree();
        assertTrue(isEmpty(tree));
        assertTrue(toList(tree).isEmpty());
        assertNull(rootColorName(tree));

        insert(tree, 10);
        assertFalse(isEmpty(tree));
    }
}
