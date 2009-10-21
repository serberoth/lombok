package lombok.manglers;

import lombok.*;

public class RemoveLeadingUnderscoreMangler implements FieldNameMangler {
	
	@Override
	public CharSequence mangle (CharSequence fieldName) {
		String value = fieldName.toString ();
		if (value.startsWith ("_")) {
			return value.substring (1);
		}
		return fieldName;
	}
	
}
