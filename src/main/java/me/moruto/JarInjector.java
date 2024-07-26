package me.moruto;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.List;

public class JarInjector implements Opcodes {
    public static void inject(String mainClass, String mainMethod, List<ClassNode> classes) {
        MethodNode mainMethodNode = getMainMethod(mainClass, mainMethod, classes);
        if (mainMethodNode == null) {
            GUI.log("Main method not found in the specified class. " + mainClass);
            return;
        }

        classes.add(createInjectedClass());

        InsnList injectionInstructions = new InsnList();
        injectionInstructions.add(new TypeInsnNode(NEW, mainClass));
        injectionInstructions.add(new InsnNode(DUP));
        injectionInstructions.add(new MethodInsnNode(INVOKESPECIAL, mainClass, "<init>", "()V", false));

        mainMethodNode.instructions.insertBefore(mainMethodNode.instructions.getFirst(), injectionInstructions);

        GUI.log("Injection successful!");
    }

    private static MethodNode getMainMethod(String mainClass, String mainMethod, List<ClassNode> classes) {
        for (ClassNode classNode : classes) {
            if (classNode.name.equalsIgnoreCase(mainClass)) {
                for (MethodNode methodNode : classNode.methods) {
                    if (methodNode.name.equalsIgnoreCase(mainMethod)) {
                        return methodNode;
                    }
                }
            }
        }

        return null;
    }

    private static ClassNode createInjectedClass() {
        ClassNode classNode = new ClassNode();
        classNode.name = "me/moruto/Injected";
        classNode.superName = "java/lang/Object";
        classNode.version = V1_8;
        classNode.access = ACC_PUBLIC;
        classNode.methods.add(createConstructor());

        return classNode;
    }

    private static MethodNode createConstructor() {
        MethodNode constructor = new MethodNode(ACC_PUBLIC, "<init>", "()V", null, null);
        constructor.visitCode();
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        constructor.visitInsn(RETURN);
        constructor.visitEnd();
        return constructor;
    }
}
