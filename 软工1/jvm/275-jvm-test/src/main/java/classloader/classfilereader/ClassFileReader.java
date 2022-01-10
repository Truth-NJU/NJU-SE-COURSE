package classloader.classfilereader;

import classloader.classfilereader.classpath.*;
import com.njuse.jvmfinal.util.IOUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ClassFileReader {
    private static ClassFileReader reader = new ClassFileReader();
    private static final String FILE_SEPARATOR=File.separator;
    private static final String PATH_SEPARATOR=File.pathSeparator;
    private static Entry bootClasspath=null;
    private static Entry extClasspath=null;
    private static Entry userClasspath=null;


    private ClassFileReader() {
    }

    public static void setUserClasspath(String classpath) {
        userClasspath = chooseEntryType(classpath);
    }

    public static void setBootAndExtClasspath(String classpath) {
        bootClasspath = chooseEntryType(String.join(FILE_SEPARATOR, classpath, "lib", "*"));
        extClasspath = chooseEntryType(String.join(FILE_SEPARATOR, classpath, "lib", "ext", "*"));
    }

    public static ClassFileReader getInstance() {
        return reader;
    }

    public static Entry chooseEntryType(String classpath) {

        if (!classpath.contains(PATH_SEPARATOR) && classpath.contains("*")) {
            return new WildEntry(classpath);
        } else if (!classpath.contains(PATH_SEPARATOR) && !classpath.contains(".jar") && !classpath.contains(".JAR") && !classpath.contains(".zip") && !classpath.contains(".ZIP")) {
            return new DirEntry(classpath);
        } else if (!classpath.contains(PATH_SEPARATOR)) {
            return new ArchivedEntry(classpath);
        } else {
            return new CompositeEntry(classpath);
        }
    }

    /**
     *
     * @param classpath where to find target class
     * @param className format: /package/.../class
     * @return content of classfile
     */
    public Pair<byte[], Integer> readClassFile(String className) throws IOException, ClassNotFoundException {
        if (bootClasspath == null) {
        }
        if (userClasspath == null) {
            setUserClasspath(".");
        }
        if (extClasspath == null) {
            String path=String.join(FILE_SEPARATOR, ".", "jre");
            File f = new File(path);
            if (f.exists()) {
                setBootAndExtClasspath(path);
            } else {
                String JAVA_HOME = System.getenv("JAVA_HOME");
                if (JAVA_HOME != null) {
                    f = new File(JAVA_HOME);
                    if (!f.exists()) {
                        throw new FileNotFoundException();
                    }

                    setBootAndExtClasspath(String.join(FILE_SEPARATOR, JAVA_HOME, "jre"));
                }
            }
        }

        String realClassName = className+".class";
        realClassName = PathUtil.transform(realClassName);
        byte[] data;

        //双亲加载
        data = bootClasspath.readClass(realClassName);
        if (data != null) {
            return Pair.of(data,1);
        }

        data = extClasspath.readClass(realClassName);
        if (data != null) {
            return Pair.of(data,3);
        }

        data = userClasspath.readClass(realClassName);
        if (data != null) {
            return Pair.of(data,7);
        }
        return null;
    }
}


