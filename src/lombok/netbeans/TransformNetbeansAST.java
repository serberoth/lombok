package lombok.netbeans;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;

public class TransformNetbeansAST {
	public static void transform(CompilationUnitTree compilationUnit) {
		System.out.println("Called to transform a CU! " + compilationUnit.getClass());
		System.out.println("  instanceof? " + (compilationUnit instanceof JCCompilationUnit));
	}
}
