package org.example.part2.tracing;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.ClassReader;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class RBTreeLineTraceAgent {
    private RBTreeLineTraceAgent() {
    }

    private static final String TARGET_CLASS = "org.example.part2.RBTree";
    private static volatile boolean installed = false;

    private static final Map<String, Set<Integer>> INTERESTING_LINES = new ConcurrentHashMap<>();

    public static synchronized void install(Map<String, Map<Integer, RBTreeTracePoint>> traceMapping) {
        if (installed) {
            return;
        }

        traceMapping.forEach((method, lines) -> {
            INTERESTING_LINES.put(method, lines.keySet());
            lines.forEach((line, point) -> RBTreeTraceRecorder.register(method, line, point));
        });

        Instrumentation instrumentation = ByteBuddyAgent.install();

        AgentBuilder.Transformer transformer = new AgentBuilder.Transformer() {
            @Override
            public DynamicType.Builder<?> transform(
                    DynamicType.Builder<?> builder,
                    TypeDescription typeDescription,
                    ClassLoader classLoader,
                    net.bytebuddy.utility.JavaModule module,
                    ProtectionDomain protectionDomain
            ) {
                return builder.visit(new AsmVisitorWrapper.AbstractBase() {
                    @Override
                    public int mergeReader(int flags) {
                        return (flags & ~ClassReader.SKIP_CODE) | ClassReader.EXPAND_FRAMES;
                    }

                    @Override
                    public int mergeWriter(int flags) {
                        return flags | ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES;
                    }

                    @Override
                    public ClassVisitor wrap(
                            TypeDescription instrumentedType,
                            ClassVisitor classVisitor,
                            net.bytebuddy.implementation.Implementation.Context implementationContext,
                            TypePool typePool,
                            FieldList<FieldDescription.InDefinedShape> fields,
                            MethodList<?> methods,
                            int writerFlags,
                            int readerFlags
                    ) {
                        return new ClassVisitor(Opcodes.ASM9, classVisitor) {
                            @Override
                            public MethodVisitor visitMethod(
                                    int access,
                                    String name,
                                    String descriptor,
                                    String signature,
                                    String[] exceptions
                            ) {
                                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                                if (!INTERESTING_LINES.containsKey(name)) {
                                    return mv;
                                }

                                Set<Integer> methodLines = INTERESTING_LINES.get(name);

                                return new MethodVisitor(Opcodes.ASM9, mv) {
                                    private Integer pendingLine;

                                    private void emitPendingHookIfNeeded() {
                                        if (pendingLine == null) {
                                            return;
                                        }
                                        super.visitLdcInsn(name);
                                        super.visitLdcInsn(pendingLine);
                                        super.visitMethodInsn(
                                                Opcodes.INVOKESTATIC,
                                                "org/example/part2/tracing/RBTreeTraceRecorder",
                                                "hitLine",
                                                "(Ljava/lang/String;I)V",
                                                false
                                        );
                                        pendingLine = null;
                                    }

                                    @Override
                                    public void visitLineNumber(int line, Label start) {
                                        super.visitLineNumber(line, start);
                                        if (methodLines.contains(line)) {
                                            pendingLine = line;
                                        }
                                    }

                                    @Override
                                    public void visitInsn(int opcode) {
                                        emitPendingHookIfNeeded();
                                        super.visitInsn(opcode);
                                    }

                                    @Override
                                    public void visitIntInsn(int opcode, int operand) {
                                        emitPendingHookIfNeeded();
                                        super.visitIntInsn(opcode, operand);
                                    }

                                    @Override
                                    public void visitVarInsn(int opcode, int varIndex) {
                                        emitPendingHookIfNeeded();
                                        super.visitVarInsn(opcode, varIndex);
                                    }

                                    @Override
                                    public void visitTypeInsn(int opcode, String type) {
                                        emitPendingHookIfNeeded();
                                        super.visitTypeInsn(opcode, type);
                                    }

                                    @Override
                                    public void visitFieldInsn(int opcode, String owner, String fieldName, String descriptor) {
                                        emitPendingHookIfNeeded();
                                        super.visitFieldInsn(opcode, owner, fieldName, descriptor);
                                    }

                                    @Override
                                    public void visitMethodInsn(int opcode, String owner, String methodName, String descriptor, boolean isInterface) {
                                        emitPendingHookIfNeeded();
                                        super.visitMethodInsn(opcode, owner, methodName, descriptor, isInterface);
                                    }

                                    @Override
                                    public void visitInvokeDynamicInsn(String methodName, String descriptor, net.bytebuddy.jar.asm.Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
                                        emitPendingHookIfNeeded();
                                        super.visitInvokeDynamicInsn(methodName, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
                                    }

                                    @Override
                                    public void visitJumpInsn(int opcode, Label label) {
                                        emitPendingHookIfNeeded();
                                        super.visitJumpInsn(opcode, label);
                                    }

                                    @Override
                                    public void visitLdcInsn(Object value) {
                                        emitPendingHookIfNeeded();
                                        super.visitLdcInsn(value);
                                    }

                                    @Override
                                    public void visitIincInsn(int varIndex, int increment) {
                                        emitPendingHookIfNeeded();
                                        super.visitIincInsn(varIndex, increment);
                                    }

                                    @Override
                                    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
                                        emitPendingHookIfNeeded();
                                        super.visitTableSwitchInsn(min, max, dflt, labels);
                                    }

                                    @Override
                                    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
                                        emitPendingHookIfNeeded();
                                        super.visitLookupSwitchInsn(dflt, keys, labels);
                                    }

                                    @Override
                                    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
                                        emitPendingHookIfNeeded();
                                        super.visitMultiANewArrayInsn(descriptor, numDimensions);
                                    }

                                    @Override
                                    public void visitEnd() {
                                        emitPendingHookIfNeeded();
                                        super.visitEnd();
                                    }
                                };
                            }
                        };
                    }
                });
            }
        };

        new AgentBuilder.Default()
            .ignore(ElementMatchers.none())
            .with(AgentBuilder.Listener.StreamWriting.toSystemError().withErrorsOnly())
                .type(ElementMatchers.named(TARGET_CLASS))
                .transform(transformer)
                .installOn(instrumentation);

        installed = true;
    }

    public static void startTrace() {
        RBTreeTraceRecorder.start();
    }

    public static List<RBTreeTracePoint> stopTrace() {
        return RBTreeTraceRecorder.stop();
    }

    public static void clearTrace() {
        RBTreeTraceRecorder.clear();
    }
}
