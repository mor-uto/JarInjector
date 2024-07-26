package me.moruto.utils;

import java.util.zip.ZipEntry;

public class ResourceWrapper {
    private final ZipEntry entry;
    private final byte[] bytes;

    public ResourceWrapper(ZipEntry entry, byte[] bytes) {
        this.entry = entry;
        this.bytes = bytes;
    }

    public ZipEntry getEntry() {
        return entry;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
