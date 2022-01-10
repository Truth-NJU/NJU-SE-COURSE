package instructions.math;

import instructions.base.Instruction;
import runtime.StackFrame;
import runtime.Vars;

import java.nio.ByteBuffer;

public class IINC extends Instruction {
    public int index;
    public int con;

    public void execute(StackFrame frame) {
        Vars localVars = frame.getLocalVars();
        int val = localVars.getInt(index);
        val += con;
        localVars.setInt(index, val);
    }



    @Override
    public void fetchOperands(ByteBuffer reader) {
        index=reader.get();
        con=reader.get();
    }
}
