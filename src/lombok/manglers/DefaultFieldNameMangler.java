package lombok.manglers;

import lombok.*;

public class DefaultFieldNameMangler implements FieldNameMangler {
	
	@Override
	public CharSequence mangle (CharSequence fieldName) {
		return fieldName;
	}
	
}
