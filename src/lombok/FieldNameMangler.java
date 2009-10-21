package lombok;

public interface FieldNameMangler {
	
	public abstract CharSequence mangle (CharSequence fieldName);
	
}
