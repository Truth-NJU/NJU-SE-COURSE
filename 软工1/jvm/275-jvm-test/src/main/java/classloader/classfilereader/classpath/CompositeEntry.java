package classloader.classfilereader.classpath;

import classloader.classfilereader.ClassFileReader;

import java.io.File;
import java.io.IOException;

public class CompositeEntry extends Entry {
    public CompositeEntry(String classpath) {
        super(classpath);
    }

    @Override
    public byte[] readClass(String className) throws IOException, ClassNotFoundException {
        try {
            String[] s = classpath.split(File.pathSeparator);
            for (String path : s) {
                Entry boot = ClassFileReader.chooseEntryType(path);
                byte[] ret = boot.readClass(className);
                if (ret != null) {
                    return ret;
                }
            }
            return null;
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;

    }
}

