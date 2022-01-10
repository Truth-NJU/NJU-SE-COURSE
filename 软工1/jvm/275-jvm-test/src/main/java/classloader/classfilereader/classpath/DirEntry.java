package classloader.classfilereader.classpath;

import com.njuse.jvmfinal.util.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DirEntry extends Entry {
    public DirEntry(String classpath) {
        super(classpath);
    }

    @Override
    public byte[] readClass(String className) throws IOException {
        try {
            File f=new File(classpath+ File.separator +className);
            InputStream is=new FileInputStream(f);
            byte[] ret=IOUtil.readFileByBytes(is);
            return ret;
        } catch (IOException e) {
            return null;
        }

    }
}

