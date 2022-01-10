package classloader.classfilereader.classpath;

import java.io.File;
import java.io.IOException;

public abstract class Entry {
    public final String PATH_SEPARATOR;
    public final String FILE_SEPARATOR;
    public String classpath;

    public Entry(String classpath){
        this.classpath = classpath;
        this.PATH_SEPARATOR = File.pathSeparator;
        this.FILE_SEPARATOR = File.separator;
    }

    public abstract byte[] readClass(String className) throws IOException, ClassNotFoundException;

    public String getPATH_SEPARATOR() {
        return this.PATH_SEPARATOR;
    }

    public String getFILE_SEPARATOR() {
        return this.FILE_SEPARATOR;
    }

    public String getClasspath() {
        return this.classpath;
    }

    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }
}

