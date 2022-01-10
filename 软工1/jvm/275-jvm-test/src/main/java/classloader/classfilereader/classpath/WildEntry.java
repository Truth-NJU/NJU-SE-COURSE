package classloader.classfilereader.classpath;

import com.njuse.jvmfinal.util.IOUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class WildEntry extends Entry {

    public WildEntry(String classpath) {
        super(classpath);
    }

    @Override
    public byte[] readClass(String className) throws IOException, ClassNotFoundException {
        String path = this.classpath.substring(0, this.classpath.length() - 2);
        File file = new File(path);
        File[] files = file.listFiles();
        assert files != null;
        for (File f : files) {
            String name = String.valueOf(f);
            if (name.contains(".jar") || name.contains(".JAR") || name.contains(".zip") || name.contains(".ZIP")) {
                String jarName = f.getPath();
                JarFile jarFile = new JarFile(jarName);
                for (Enumeration<JarEntry> entrys = jarFile.entries(); entrys.hasMoreElements(); ) {
                    JarEntry jarEntry = entrys.nextElement();
                    String s = jarEntry.getName();
                    s = PathUtil.transform(s);
                    if (s.contains(className)) {
                        try {
                            return IOUtil.readFileByBytes(jarFile.getInputStream(jarEntry));
                        } catch (IOException e) {
                            return null;
                        }
                    }
                }

            }
        }
        return null;
    }
}

