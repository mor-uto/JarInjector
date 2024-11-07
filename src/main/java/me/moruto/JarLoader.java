package me.moruto;

import me.moruto.utils.ResourceWrapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarLoader {
    private final List<ClassNode> classes = new ArrayList<>();
    private final List<ResourceWrapper> resources = new ArrayList<>();

    public boolean loadJar(File file) {
        try (ZipInputStream jarInputStream = new ZipInputStream(Files.newInputStream(file.toPath()))) {
            ZipEntry zipEntry;
            byte[] buffer = new byte[4096];
            while ((zipEntry = jarInputStream.getNextEntry()) != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int read;
                while ((read = jarInputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, read);
                }

                byte[] data = baos.toByteArray();
                if (zipEntry.getName().endsWith(".class")) {
                    ClassNode classNode = new ClassNode();
                    new ClassReader(data).accept(classNode, 0);
                    classes.add(classNode);
                    GUI.log("Loaded class: " + classNode.name);
                } else {
                    resources.add(new ResourceWrapper(zipEntry, data));
                    GUI.log("Loaded resource: " + zipEntry.getName());
                }
                jarInputStream.closeEntry();
            }
            return true;
        } catch (IOException e) {
            GUI.log("Error loading jar: " + e.getMessage());
            return false;
        }
    }

    public void saveJar(String path) {
        try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(Paths.get(path)))) {
            for (ClassNode classNode : classes) {
                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                classNode.accept(classWriter);
                jos.putNextEntry(new ZipEntry(classNode.name.replace('.', '/') + ".class"));
                jos.write(classWriter.toByteArray());
                jos.closeEntry();
            }

            for (ResourceWrapper resource : resources) {
                jos.putNextEntry(resource.getEntry());
                jos.write(resource.getBytes());
                jos.closeEntry();
            }

            GUI.log("Successfully saved the jar to: " + path);
        } catch (IOException e) {
            GUI.log("Failed to save the jar: " + e.getMessage());
        }
    }

    public List<ClassNode> getClasses() {
        return classes;
    }

    public List<ResourceWrapper> getResources() {
        return resources;
    }
}
