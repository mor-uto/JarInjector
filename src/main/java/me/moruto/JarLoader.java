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
    public List<ClassNode> classes = new ArrayList<>();
    public List<ResourceWrapper> resources = new ArrayList<>();

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
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[0x1000];
                    int read;
                    while ((read = jarInputStream.read(buffer)) != -1) bos.write(buffer, 0, read);
                    resources.add(new ResourceWrapper(zipEntry, bos.toByteArray()));
                }

                jarInputStream.closeEntry();
            }
            return true;
        } catch (IOException e) {
            GUI.log(e.getMessage());
            return false;
        }
    }

    public boolean saveJar(String path) {
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

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
