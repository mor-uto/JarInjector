package me.moruto;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.List;

public class JarInjector implements Opcodes {
    public static void inject(String injectionMainClass, List<ClassNode> classes) {
        MethodNode mainMethodNode = getMainMethod(classes);
        if (mainMethodNode == null) {
            GUI.log("Main method not found in the specified class. ");
            return;
        }

        InsnList injectionInstructions = new InsnList();
        injectionInstructions.add(new TypeInsnNode(NEW, injectionMainClass));
        injectionInstructions.add(new InsnNode(DUP));
        injectionInstructions.add(new MethodInsnNode(INVOKESPECIAL, injectionMainClass, "<init>", "()V", false));

        mainMethodNode.instructions.insertBefore(mainMethodNode.instructions.getFirst(), injectionInstructions);

        GUI.log("Injection successful!");
    }

    private static MethodNode getMainMethod(List<ClassNode> classes) {
        for (ClassNode classNode : classes) {
            for (MethodNode methodNode : classNode.methods) {
                if (methodNode.name.equalsIgnoreCase("main") && methodNode.desc.equals("([Ljava/lang/String;)V")) {
                    return methodNode;
                }
            }
        }

        return null;
    }
}
