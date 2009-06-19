package lombok.netbeans;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TransformAST {
	private static boolean initialized;
	private static Method transform;
	
	static RuntimeException sneakyThrow(Throwable t) {
		if ( t == null ) throw new NullPointerException("t");
		TransformAST.<RuntimeException>sneakyThrow0(t);
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Throwable> void sneakyThrow0(Throwable t) throws T {
		throw (T)t;
	}
	
	
	public static void transformCompilationUnitTree(Object javacTaskImpl, Object compilationUnitOrIterable) throws Exception {
		if ( compilationUnitOrIterable instanceof Iterable<?> ) {
			for ( Object compilationUnit : (Iterable<?>)compilationUnitOrIterable ) transform(compilationUnit);
		} else transform(compilationUnitOrIterable);
	}
	
	private static void transform(Object compilationUnit) throws Exception {
		initialize(compilationUnit);
		try {
			transform.invoke(null, compilationUnit);
		} catch ( InvocationTargetException e ) {
			sneakyThrow(e.getCause());
		}
	}
	
	private static void initialize(Object unit) throws ClassNotFoundException {
		if ( initialized ) {
			if ( transform == null ) throw new ClassNotFoundException("lombok.netbeans.TransformNetbeansAST");
			return;
		}
		
		try {
			Class<?> c = Class.forName("lombok.netbeans.TransformNetbeansAST", true, unit.getClass().getClassLoader());
			for ( Method m : c.getDeclaredMethods() ) {
				m.setAccessible(true);
				if ( m.getName().equals("transform") ) {
					Class<?>[] types = m.getParameterTypes();
					if ( types.length == 1 && types[0].getName().equals("com.sun.source.tree.CompilationUnitTree") ) {
						transform = m;
						break;
					}
				}
			}
		} catch ( Exception ignore ) {
			ignore.printStackTrace();
		}
		initialized = true;
	}
}
