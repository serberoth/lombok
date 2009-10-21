package lombok;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface GetterAndSetter {
	/**
	 * If you want your getter/setter to be non-public, you can specify an alternate access level here.
	 */
	lombok.AccessLevel value() default lombok.AccessLevel.PUBLIC;
	
	/**
	 * Rename the field getter/setter to the specified field name
	 */
	Class<? extends FieldNameMangler> fieldNameMangler () default lombok.manglers.DefaultFieldNameMangler.class;
	
	boolean chainable () default true;
	
}
