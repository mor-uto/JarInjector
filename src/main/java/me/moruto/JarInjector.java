package me.moruto;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.zip.ZipEntry;

public class JarInjector implements Opcodes {
    public void inject(String injectionMainClass, JarLoader loader) {
        MethodNode mainMethodNode = getMainMethod(loader);
        if (mainMethodNode == null) {
            GUI.log("Main method not found in the specified class.");
            return;
        }

        InsnList insnList = new InsnList();
        insnList.add(new TypeInsnNode(NEW, injectionMainClass));
        insnList.add(new InsnNode(DUP));
        insnList.add(new MethodInsnNode(INVOKESPECIAL, injectionMainClass, "<init>", "()V", false));

        mainMethodNode.instructions.insertBefore(mainMethodNode.instructions.getFirst(), insnList);

        GUI.log("Injection successful!");
    }

    private MethodNode getMainMethod(JarLoader loader) {
        ZipEntry manifest = loader.getManifest();
        String mainClass = null;

        if (manifest != null) {
            mainClass = new String(manifest.getExtra()).split("Main-Class: ")[1].split("\n")[0].replace(".", "/");
        }

        for (ClassNode classNode : loader.getClasses()) {
            if (mainClass == null || classNode.name.equals(mainClass)) {
                for (MethodNode methodNode : classNode.methods) {
                    if (methodNode.name.equalsIgnoreCase("main") && methodNode.desc.equals("([Ljava/lang/String;)V")) {
                        return methodNode;
                    }
                }
            }
        }

        return null;
    }

}
