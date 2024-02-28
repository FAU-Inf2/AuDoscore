public class ToTest {
	public static int toTest(String day) {
		boolean isWeekend = switch (day) {
			case "MONDAY" -> {
				System.out.println("Oh no! Monday is the worst day of the week...");
				yield false;
			}
			case "FRIDAY" -> {
				yield false;
			}
			case "SATURDAY", "SUNDAY" -> true;
			default -> false;
		};
		return isWeekend ? 0 : 1; // @Replace should replace wrong student code "? 0 : 1" with expected code "? 1 : 0"
	}
}
