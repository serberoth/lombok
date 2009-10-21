package lombok.manglers;

import lombok.*;

public class UpperCaseMangler implements FieldNameMangler {
	
	@Override
	public CharSequence mangle (CharSequence fieldName) {
		return fieldName.toString ().toUpperCase ();
	}
	
}
