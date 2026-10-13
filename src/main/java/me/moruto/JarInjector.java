package me.moruto;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class JarInjector {
    public void inject(JarLoader loader) {
        MethodNode mainMethodNode = getMainMethod(loader);
        if (mainMethodNode == null) {
            GUI.log("Main method not found in the specified class.");
            return;
        }

        InsnList insnList = new InsnList();
        insnList.add(new TypeInsnNode(Opcodes.NEW, mainMethodNode.name));
        insnList.add(new InsnNode(Opcodes.DUP));
        insnList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, mainMethodNode.name, "<init>", "()V", false));

        if (mainMethodNode.name.equals("<init>")) {
            mainMethodNode.instructions.insertBefore(mainMethodNode.instructions.getFirst(), insnList);
            return;
        }

        mainMethodNode.instructions.add(insnList);
    }

    private MethodNode getMainMethod(JarLoader loader) {
        String manifest = loader.getManifest().toString();
        String mainClass = null;

        System.out.println(manifest);

        if (!manifest.isEmpty()) {
            try {
                if (manifest.contains("Main-Class: ")) {
                    mainClass = manifest.split("Main-Class: ")[1].split("\\r?\\n")[0].trim().replace(".", "/");
                }
            } catch (Exception e) {
                GUI.log("Error parsing manifest: " + e.getMessage());
            }
        }

        if (mainClass != null) {
            for (ClassNode classNode : loader.getClasses()) {
                if (classNode.name.equals(mainClass)) {
                    for (MethodNode methodNode : classNode.methods) {
                        if (methodNode.name.equalsIgnoreCase("main") && methodNode.desc.equals("([Ljava/lang/String;)V")) {
                            return methodNode;
                        }

                        if (methodNode.name.equals("<init>") & methodNode.desc.equals("()V")) {
                            return methodNode;
                        }
                    }
                }
            }
        }

        GUI.log("Main method not found.");
        return null;
    }
}
