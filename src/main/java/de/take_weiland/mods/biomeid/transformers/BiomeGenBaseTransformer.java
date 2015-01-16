package de.take_weiland.mods.biomeid.transformers;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.ListIterator;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.*;

/**
 * @author diesieben07
 */
public class BiomeGenBaseTransformer implements IClassTransformer {

	public static final String BIOME_GEN_BASE_BIN = "net.minecraft.world.biome.BiomeGenBase";
	public static final String BIOME_GEN_BASE_INT = BIOME_GEN_BASE_BIN.replace('.', '/');

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (bytes == null) return null;

		if (!transformedName.equals(BIOME_GEN_BASE_BIN)) {
			return bytes;
		}

		ClassReader cr = new ClassReader(bytes);
		ClassNode clazz = new ClassNode();
		cr.accept(clazz, 0);

		transformClass(clazz);

		ClassWriter cw = new ClassWriter(COMPUTE_FRAMES);
		clazz.accept(cw);
		return cw.toByteArray();
	}

	private void transformClass(ClassNode clazz) {
		String desc = Type.getMethodDescriptor(VOID_TYPE, INT_TYPE, BOOLEAN_TYPE);
		for (MethodNode method : clazz.methods) {
			if (method.name.equals("<init>") && method.desc.equals(desc)) {
				transformCstr(method);
				return;
			}
		}
		throw new RuntimeException("Could not find BiomeGenBase(int, boolean) constructor!");
	}

	private void transformCstr(MethodNode cstr) {
		InsnList toInject = new InsnList();
		toInject.add(new VarInsnNode(ALOAD, 0));
		toInject.add(new VarInsnNode(ILOAD, 1));
		toInject.add(new VarInsnNode(ILOAD, 2));

		String owner = "de/take_weiland/mods/biomeid/BiomeConflictManager";
		String name = "onBiomeConstruct";
		String desc = Type.getMethodDescriptor(VOID_TYPE, getObjectType(BIOME_GEN_BASE_INT), INT_TYPE, BOOLEAN_TYPE);
		toInject.add(new MethodInsnNode(INVOKESTATIC, owner, name, desc, false));

		AbstractInsnNode hook;
		Iterator<AbstractInsnNode> it = cstr.instructions.iterator();
		do {
			hook = it.next();
		} while (hook.getOpcode() != INVOKESPECIAL);

		cstr.instructions.insert(hook, toInject);
	}

	private AbstractInsnNode findLastReturn(MethodNode method) {
		int opcode = Type.getReturnType(method.desc).getOpcode(IRETURN);

		ListIterator<AbstractInsnNode> it = method.instructions.iterator(method.instructions.size());
		while (it.hasPrevious()) {
			AbstractInsnNode insn = it.previous();
			if (insn.getOpcode() == opcode) {
				return insn;
			}
		}
		throw new IllegalArgumentException("Missing return in method");
	}
}
