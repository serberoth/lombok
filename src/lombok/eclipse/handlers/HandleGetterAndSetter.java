package lombok.eclipse.handlers;

import lombok.*;
import lombok.core.*;
import lombok.eclipse.*;
import org.eclipse.jdt.internal.compiler.ast.*;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(EclipseAnnotationHandler.class)
public class HandleGetterAndSetter implements EclipseAnnotationHandler<GetterAndSetter> {
	
	@Override
	public boolean handle(AnnotationValues<GetterAndSetter> annotation, Annotation ast, EclipseNode annotationNode) {
		EclipseNode fieldNode = annotationNode.up();
		AccessLevel level = annotation.getInstance().value();
		if ( level == AccessLevel.NONE ) return true;
		
		FieldNameMangler fieldNameMangler = null;
		try {
			Class<? extends FieldNameMangler> clazz = annotation.getInstance ().fieldNameMangler (); 
			fieldNameMangler = clazz.newInstance ();
		} catch (InstantiationException ie) {
			// Ignore this Exception (would log if there were a logger instance)
		} catch (IllegalAccessException iae) {
			// Ignore this Exception (would log if there were a logger instance)
		}
		boolean chainable = annotation.getInstance().chainable();

		FieldDeclaration fieldDecl = (FieldDeclaration) fieldNode.get();
		boolean isFinal = (fieldDecl.modifiers & ClassFileConstants.AccFinal) != 0;
		
		new HandleGetter().generateGetterForField(fieldNode, level, annotationNode.get(), fieldNameMangler);
		if ( !isFinal ) new HandleSetter().generateSetterForField(fieldNode, level, annotationNode.get(), chainable, fieldNameMangler);
		
		return false;
	}
	
}
