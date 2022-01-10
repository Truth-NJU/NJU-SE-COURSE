package runtime.struct;

import com.njuse.jvmfinal.memory.jclass.JClass;
import org.apache.commons.lang3.tuple.Pair;
import runtime.Vars;

import java.util.ArrayList;
import java.util.Arrays;

public class NonArrayObject extends JObject {

    private Vars fields;
    private ArrayList<Pair<String, Integer>> fieldInfoList;

    public Vars getFields() {
        return fields;
    }

    public NonArrayObject(JClass clazz) {
        assert clazz != null;
        this.clazz = clazz;
        numInHeap++;
        fields = new Vars(clazz.getInstanceSlotCount());
        fieldInfoList = new ArrayList<>();
        initDefaultValue(clazz);
        generateInstanceFieldInfoList(clazz);
    }

    private void generateInstanceFieldInfoList(JClass clazz) {
        do {
            Arrays.stream(clazz.getFields())
                    .filter(f -> !f.isStatic())
                    .forEach(f -> {
                        String type = parseDescriptor(f.getDescriptor());
                        String name = f.getName();
                        int slotID = f.getSlotID();
                        fieldInfoList.add(Pair.of(type + " " + name, slotID));
                    });
            clazz = clazz.getSuperClass();
        } while (clazz != null);

    }

    public static String parseDescriptor(String descriptor) {
        switch(descriptor.charAt(0)) {
            case 'B':
                return "byte";
            case 'C':
                return "char";
            case 'D':
                return "double";
            case 'E':
            case 'G':
            case 'H':
            case 'K':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            default:
                throw new RuntimeException("Invalid field descriptor.");
            case 'F':
                return "float";
            case 'I':
                return "int";
            case 'J':
                return "long";
            case 'L':
                String name = descriptor.substring(1, descriptor.length() - 1);
                String[] tmp = name.split("/");
                name = tmp[tmp.length - 1];
                return name;
            case 'S':
                return "short";
            case 'Z':
                return "boolean";
            case '[':
                return parseArrayName(descriptor);
        }
    }

    private void initDefaultValue(JClass clazz) {
        do {
            Arrays.stream(clazz.getFields())
                    .filter(f -> !f.isStatic())
                    .forEach(f -> {
                        String descriptor = f.getDescriptor();
                        switch (descriptor.charAt(0)) {
                            case 'Z':
                            case 'B':
                            case 'C':
                            case 'S':
                            case 'I':
                                this.fields.setInt(f.getSlotID(), 0);
                                break;
                            case 'F':
                                this.fields.setFloat(f.getSlotID(), 0);
                                break;
                            case 'J':
                                this.fields.setLong(f.getSlotID(), 0);
                                break;
                            case 'D':
                                this.fields.setDouble(f.getSlotID(), 0);
                                break;
                            default:
                                this.fields.setObjectRef(f.getSlotID(), new NullObject());
                                break;
                        }
                    });
            clazz = clazz.getSuperClass();
        } while (clazz != null);
    }

    private static String parseArrayName(String descriptor) {

        return null;
    }
}
