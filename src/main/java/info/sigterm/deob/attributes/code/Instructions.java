package info.sigterm.deob.attributes.code;

import info.sigterm.deob.attributes.Code;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class Instructions
{
	private Code code;
	private ArrayList<Instruction> instructions = new ArrayList<Instruction>();

	public Instructions(Code code) throws IOException
	{
		this.code = code;
		DataInputStream is = code.getAttributes().getStream();

		int length = is.readInt();

		int pc;
		for (pc = 0; pc < length;)
		{
			byte opcode = is.readByte();

			InstructionType type = InstructionType.findInstructionFromCode(opcode);

			try
			{
				Constructor<? extends Instruction> con = type.getInstructionClass().getConstructor(Instructions.class, InstructionType.class, int.class);
				Instruction ins = con.newInstance(this, type, pc);

				instructions.add(ins);

				int len = ins.getLength();
				pc += len;
			}
			catch (java.lang.Exception ex)
			{
				throw new IOException(ex);
			}
		}

		assert pc == length;

		buildJumpGraph();
	}

	private void buildJumpGraph()
	{
		for (Instruction i : instructions)
			i.buildJumpGraph();
	}

	public void buildInstructionGraph()
	{
		for (Instruction i : instructions)
			i.buildInstructionGraph();
	}

	public Code getCode()
	{
		return code;
	}

	public Instruction getFirstInstruction()
	{
		return instructions.get(0);
	}

	public Instruction findInstruction(int pc)
	{
		for (Instruction i : instructions)
			if (i.getPc() == pc)
				return i;
		return null;
	}
}
