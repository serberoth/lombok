package lombok.javac.handlers;

import lombok.*;
import lombok.core.*;
import lombok.javac.*;
import org.mangosdk.spi.*;
import com.sun.tools.javac.code.*;
import com.sun.tools.javac.tree.JCTree.*;

@ProviderFor(JavacAnnotationHandler.class)
public class HandleGetterAndSetter implements JavacAnnotationHandler<GetterAndSetter> {

	@Override
	public boolean handle (AnnotationValues<GetterAndSetter> annotation, JCAnnotation ast, JavacNode annotationNode) {
		JavacNode fieldNode = annotationNode.up();
		AccessLevel level = annotation.getInstance().value();
		if (level == AccessLevel.NONE) return true;
		
		FieldNameMangler fieldNameMangler = null;
		try {
			Class<? extends FieldNameMangler> clazz = annotation.getInstance ().fieldNameMangler (); 
			fieldNameMangler = clazz.newInstance ();
		} catch (InstantiationException ie) {
			// Ignore this Exception (would log if there were a logger instance)
		} catch (IllegalAccessException iae) {
			// Ignore this Exception (would log if there were a logger instance)
		}
		boolean chainable = annotation.getInstance ().chainable();
		
		JCVariableDecl fieldDecl = (JCVariableDecl) fieldNode.get();
		long fieldFlags = fieldDecl.mods.flags;
		boolean isFinal = (fieldFlags & Flags.FINAL) != 0;
		new HandleGetter().generateGetterForField(fieldNode, level, annotationNode.get(), fieldNameMangler);
		if (!isFinal) new HandleSetter().generateSetterForField(fieldNode, level, annotationNode.get(), chainable, fieldNameMangler);
		
		return false;
	}
	
}
