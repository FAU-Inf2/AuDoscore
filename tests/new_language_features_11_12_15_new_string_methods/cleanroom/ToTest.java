public class ToTest {
	public static int toTest() {
		var singlewordstring = "string";
		var singlelinestring = "This is a single-line string";
		var multilinestring = """
				This is
				a "multi-line"
				string""";

		// ===== 11 =====
		var unStrippedBothString = "   " + singlewordstring + "   ";
		if (!unStrippedBothString.strip().equals(singlewordstring)) {
			throw new IllegalStateException("strip");
		}
		var unStrippedLeadingString = "   " + singlewordstring;
		if (!unStrippedLeadingString.stripLeading().equals(singlewordstring)) {
			throw new IllegalStateException("stripLeading");
		}
		var unStrippedTrailingString = singlewordstring + "   ";
		if (!unStrippedTrailingString.stripTrailing().equals(singlewordstring)) {
			throw new IllegalStateException("stripTrailing");
		}
		var blankString = "   ";
		if (!blankString.isBlank()) {
			throw new IllegalStateException("isBlank");
		}
		var lines = multilinestring.lines();
		if (lines.count() != 3) {
			throw new IllegalStateException("lines");
		}
		var repeated = singlewordstring.repeat(3);
		if (!repeated.equals(singlewordstring + singlewordstring + singlewordstring)) {
			throw new IllegalStateException("repeat");
		}

		// ===== 12 =====
		var indented = multilinestring.indent(4);
		if (!indented.equals("    This is\n    a \"multi-line\"\n    string\n")) {
			throw new IllegalStateException("indent");
		}
		var transformed = singlelinestring.transform((var s) -> new StringBuilder(s).reverse().append("!").reverse().toString());
		if (!transformed.equals("!" + singlelinestring)) {
			throw new IllegalStateException("transform");
		}

		// ===== 15 =====
		var notIndented = indented.substring(0, indented.length() - 1).stripIndent();
		if (!notIndented.equals(multilinestring)) {
			throw new IllegalStateException("stripIndent");
		}
		var translated = "\\t".translateEscapes();
		if (!translated.equals("\t")) {
			throw new IllegalStateException("translateEscapes");
		}
		var formatted = "|%1$-4d|%2$1.4f|%3$5s|".formatted(42, 0.815, "4711");
		if (!formatted.equals("|42  |0.8150| 4711|")) {
			throw new IllegalStateException("formatted");
		}

		return 0; // @Replace should replace wrong student code "return 42;" with expected code "return 0;"
	}
}
