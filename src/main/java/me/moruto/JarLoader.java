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
        try (ZipInputStream jarInputStream = new ZipInputStream(Files.newInputStream(Paths.get(file.toURI())))) {
            ZipEntry zipEntry;
            while ((zipEntry = jarInputStream.getNextEntry()) != null) {
                if (zipEntry.getName().endsWith(".class")) {
                    ClassReader reader = new ClassReader(jarInputStream);
                    ClassNode classNode = new ClassNode();
                    reader.accept(classNode, 0);
                    classes.add(classNode);
                    GUI.log("Loaded class: " + classNode.name);
                } else {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[0x1000];
                    int read;
                    while ((read = jarInputStream.read(buffer)) != -1) baos.write(buffer, 0, read);
                    resources.add(new ResourceWrapper(zipEntry, baos.toByteArray()));
                }

                jarInputStream.closeEntry();
            }
            return true;
        } catch (IOException e) {
            GUI.log(e.getMessage());
            return false;
        }
    }

    public ZipEntry getManifest() {
        for (ResourceWrapper resourceWrapper : resources) {
            if (resourceWrapper.getEntry().getName().endsWith("MANIFEST.MF")) {
                return resourceWrapper.getEntry();
            }
        }

        return null;
    }

    public void saveJar(String path) {
        try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(Paths.get(path)))) {
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

            for (ClassNode classNode : classes) {
                classNode.accept(classWriter);

                jos.putNextEntry(new ZipEntry(classNode.name.replace('.', '/') + ".class"));
                jos.write(classWriter.toByteArray());
                jos.closeEntry();

                classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            }

            for (ResourceWrapper resource : resources) {
                jos.putNextEntry(resource.getEntry());
                jos.write(resource.getBytes());
                jos.closeEntry();
            }

            GUI.log("Successfully saved the jar!");
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
