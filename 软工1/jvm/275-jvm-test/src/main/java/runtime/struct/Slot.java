package runtime.struct;

public class Slot {
    private JObject object;
    private Integer value;

    public Slot clone() {
        Slot toClone = new Slot();
        toClone.object = this.object;
        toClone.value = this.value;
        return toClone;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value=value;
    }

    public void setObject(JObject ref) {
        this.object=ref;
    }

    public JObject getObject() {
        return this.object;
    }
}