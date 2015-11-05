public class Student {
	public static int sum = 0;

	public static int add(int val) {
		int ret = sum;
		sum += val;
		return ret;
	}
}
