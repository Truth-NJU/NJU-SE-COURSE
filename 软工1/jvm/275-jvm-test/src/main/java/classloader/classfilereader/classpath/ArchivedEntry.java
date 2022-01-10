package classloader.classfilereader.classpath;

import com.njuse.jvmfinal.util.IOUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ArchivedEntry extends Entry {
    public ArchivedEntry(String classpath) {
        super(classpath);
    }

    @Override
    public byte[] readClass(String className) throws IOException {
        try {
            JarFile jarFile = new JarFile(classpath);
            InputStream is = jarFile.getInputStream(new JarEntry(className));
            return IOUtil.readFileByBytes(is);
        } catch (IOException e) {
            return null;
        }

    }
}
