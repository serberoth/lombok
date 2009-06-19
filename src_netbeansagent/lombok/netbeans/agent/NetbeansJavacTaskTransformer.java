package lombok.netbeans.agent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class NetbeansJavacTaskTransformer {
	private static final String TARGET_STATIC_CLASS = "lombok/netbeans/TransformAST";
	private static final String TARGET_STATIC_METHOD_NAME = "transformCompilationUnitTree";
	private static final String TARGET_STATIC_METHOD_DESC = "(Ljava/lang/Object;Ljava/lang/Object;)V";
	
	private static final Map<String, Class<? extends MethodVisitor>> rewriters;
	
	static {
		Map<String, Class<? extends MethodVisitor>> map = new HashMap<String, Class<? extends MethodVisitor>>();
		map.put("parse()Ljava/lang/Iterable;", ParsePatcher.class);
		map.put(String.format(
				"reparseMethodBody(%1$sCompilationUnitTree;%1$sMethodTree;Ljava/lang/String;I)%2$s",
				"Lcom/sun/source/tree", "Lcom/sun/tools/javac/tree/JCTree$JCBlock;"), ReparseMethodPatcher.class);
		rewriters = Collections.unmodifiableMap(map);
	}
	
	public byte[] transform(byte[] classfileBuffer) {
		System.out.println("TRANSFORMING.......");
		ClassReader reader = new ClassReader(classfileBuffer);
		ClassWriter writer = new ClassWriter(reader, 0);
		
		ClassAdapter adapter = new JavacTaskPatcherAdapter(writer);
		reader.accept(adapter, 0);
		return writer.toByteArray();
	}
	
	public static RuntimeException sneakyThrow(Throwable t) {
		if ( t == null ) throw new NullPointerException("t");
		NetbeansJavacTaskTransformer.<RuntimeException>sneakyThrow0(t);
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Throwable> void sneakyThrow0(Throwable t) throws T {
		throw (T)t;
	}
	
	private static class JavacTaskPatcherAdapter extends ClassAdapter {
		public JavacTaskPatcherAdapter(ClassVisitor cv) {
			super(cv);
		}
		
		@Override public MethodVisitor visitMethod(int access, String name, String desc,
				String signature, String[] exceptions) {
			MethodVisitor writerVisitor = super.visitMethod(access, name, desc, signature, exceptions);
			Class<? extends MethodVisitor> targetVisitorClass = rewriters.get(name+desc);
			if ( targetVisitorClass == null ) return writerVisitor;
			
			try {
				Constructor<? extends MethodVisitor> c = targetVisitorClass.getDeclaredConstructor(MethodVisitor.class);
				c.setAccessible(true);
				return c.newInstance(writerVisitor);
			} catch ( InvocationTargetException e ) {
				throw sneakyThrow(e.getCause());
			} catch ( Exception e ) {
				//NoSuchMethodException: We know they exist.
				//IllegalAccessException: We called setAccessible.
				//InstantiationException: None of these classes are abstract.
				throw sneakyThrow(e);
			}
		}
	}
	
	static class ParsePatcher extends MethodAdapter {
		ParsePatcher(MethodVisitor mv) {
			super(mv);
		}
		
		@Override public void visitInsn(int opcode) {
			if ( opcode == Opcodes.ARETURN ) {
				//injects: TransformAST.transformCompilationUnitTree(thisJavacTask, theIterableAboutToBeReturned);
				super.visitInsn(Opcodes.DUP);
				super.visitVarInsn(Opcodes.ALOAD, 0);
				super.visitInsn(Opcodes.SWAP);
				super.visitMethodInsn(Opcodes.INVOKESTATIC, TARGET_STATIC_CLASS,
						TARGET_STATIC_METHOD_NAME, TARGET_STATIC_METHOD_DESC);
			}
			super.visitInsn(opcode);
		}
	}
	
	static class ReparseMethodPatcher extends MethodAdapter {
		ReparseMethodPatcher(MethodVisitor mv) {
			super(mv);
		}
		
		@Override public void visitInsn(int opcode) {
			//injects: TransformAST.transformCompilationUnitTree(thisJavacTask, theCompilationUnitContainingTheMethod);
			super.visitVarInsn(Opcodes.ALOAD, 0);
			super.visitVarInsn(Opcodes.ALOAD, 1);
			super.visitMethodInsn(Opcodes.INVOKESTATIC, TARGET_STATIC_CLASS,
					TARGET_STATIC_METHOD_NAME, TARGET_STATIC_METHOD_DESC);
			super.visitInsn(opcode);
		}
	}
}
