public class ToTest<Alpha, Beta> {
	public ToTest(Alpha alpha, Beta beta) {
		System.out.println(alpha.toString() + beta.toString());
	}

	public Alpha getAlpha(Alpha alpha) {
		return alpha; // @Replace should replace wrong student code "return null" with expected code here
	}

	public Beta getBeta(Beta beta) {
		return beta; // @Replace should replace wrong student code "return null" with expected code here
	}

	public static <Gamma> Gamma getGamma(Gamma gamma) {
		return gamma; // @Replace should replace wrong student code "return null" with expected code here
	}
}
