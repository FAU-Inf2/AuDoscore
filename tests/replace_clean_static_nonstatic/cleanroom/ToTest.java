public class ToTest {
	public static int non_clean_public_static_var_1 = 42;
	public int non_clean_public_nonstatic_var_2 = Integer.valueOf(42).shortValue(); // intentionally more init-code
	public static int __clean_public_static_var_A = 42;
	public int __clean_public_nonstatic_var_B = Integer.valueOf(42).shortValue(); // intentionally more init-code
	protected static int __clean_protected_static_var_C = 42;
	protected int __clean_protected_nonstatic_var_D = 42;
	protected static int non_clean_protected_static_var_3 = 42;
	protected int non_clean_protected_nonstatic_var_4 = 42;

	static { // intentionally more init-code
		non_clean_public_static_var_1 = 666;
		System.out.println(non_clean_public_static_var_1);
		non_clean_protected_static_var_3 = 666;
		System.out.println(non_clean_protected_static_var_3);
	}

	{ // intentionally more init-code
		non_clean_public_nonstatic_var_2 = 666;
		System.out.println(non_clean_public_nonstatic_var_2);
		non_clean_protected_nonstatic_var_4 = 666;
		System.out.println(non_clean_protected_nonstatic_var_4);
	}

	public static int toTest_public_static_var_A() {
		return __clean_public_static_var_A;
	}

	public int toTest_public_nonstatic_var_B() {
		return __clean_public_nonstatic_var_B;
	}

	public static int toTest_protected_static_var_C() {
		return __clean_protected_static_var_C;
	}

	public int toTest_protected_static_var_D() {
		return __clean_protected_nonstatic_var_D;
	}
}
