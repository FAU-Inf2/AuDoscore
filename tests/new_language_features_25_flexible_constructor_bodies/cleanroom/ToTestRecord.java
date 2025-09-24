public record ToTestRecord<T>(long k, T v) {
	public ToTestRecord(T v) {
		// prologue:
		long k = v.hashCode();
		// canonical constructor invocation:
		this(k, v);
	}
}
