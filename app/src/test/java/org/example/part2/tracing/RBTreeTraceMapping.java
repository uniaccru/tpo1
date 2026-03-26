package org.example.part2.tracing;

import java.util.LinkedHashMap;
import java.util.Map;

public final class RBTreeTraceMapping {
    private RBTreeTraceMapping() {
    }

    public static Map<String, Map<Integer, RBTreeTracePoint>> mapping() {
        Map<String, Map<Integer, RBTreeTracePoint>> mapping = new LinkedHashMap<>();

        Map<Integer, RBTreeTracePoint> insert = new LinkedHashMap<>();
        insert.put(32, RBTreeTracePoint.INSERT_EMPTY_TREE);
        insert.put(45, RBTreeTracePoint.INSERT_GO_LEFT);
        insert.put(47, RBTreeTracePoint.INSERT_GO_RIGHT);
        insert.put(58, RBTreeTracePoint.INSERT_PLACED);
        mapping.put("insert", insert);

        Map<Integer, RBTreeTracePoint> fixAfterInsert = new LinkedHashMap<>();
        fixAfterInsert.put(72, RBTreeTracePoint.FIX_UNCLE_RED);
        fixAfterInsert.put(92, RBTreeTracePoint.FIX_UNCLE_RED);
        fixAfterInsert.put(79, RBTreeTracePoint.FIX_ROTATE_LEFT_CASE);
        fixAfterInsert.put(85, RBTreeTracePoint.FIX_ROTATE_RIGHT);
        fixAfterInsert.put(99, RBTreeTracePoint.FIX_ROTATE_RIGHT_CASE);
        fixAfterInsert.put(105, RBTreeTracePoint.FIX_ROTATE_LEFT);
        mapping.put("fixAfterInsert", fixAfterInsert);

        Map<Integer, RBTreeTracePoint> find = new LinkedHashMap<>();
        find.put(120, RBTreeTracePoint.FIND_MATCH);
        find.put(122, RBTreeTracePoint.FIND_GO_LEFT);
        find.put(124, RBTreeTracePoint.FIND_GO_RIGHT);
        find.put(127, RBTreeTracePoint.FIND_NOT_FOUND);
        mapping.put("find", find);

        Map<Integer, RBTreeTracePoint> delete = new LinkedHashMap<>();
        delete.put(136, RBTreeTracePoint.DELETE_NOT_FOUND);
        delete.put(138, RBTreeTracePoint.DELETE_FOUND);
        mapping.put("delete", delete);

        Map<Integer, RBTreeTracePoint> deleteNode = new LinkedHashMap<>();
        deleteNode.put(154, RBTreeTracePoint.DELETE_TWO_CHILDREN);
        deleteNode.put(171, RBTreeTracePoint.DELETE_FIX_NEEDED);
        mapping.put("deleteNode", deleteNode);

        Map<Integer, RBTreeTracePoint> fixAfterDelete = new LinkedHashMap<>();
        fixAfterDelete.put(185, RBTreeTracePoint.DELETE_FIX_SIBLING_RED);
        fixAfterDelete.put(216, RBTreeTracePoint.DELETE_FIX_SIBLING_RED);
        fixAfterDelete.put(194, RBTreeTracePoint.DELETE_FIX_SIBLING_BLACK_KIDS);
        fixAfterDelete.put(224, RBTreeTracePoint.DELETE_FIX_SIBLING_BLACK_KIDS);
        fixAfterDelete.put(205, RBTreeTracePoint.DELETE_FIX_SIBLING_RIGHT_RED);
        fixAfterDelete.put(233, RBTreeTracePoint.DELETE_FIX_SIBLING_RIGHT_RED);
        fixAfterDelete.put(241, RBTreeTracePoint.DELETE_FIX_DONE);
        mapping.put("fixAfterDelete", fixAfterDelete);

        return mapping;
    }
}
