public class ToTest {
	public static int toTest() {
		return Exercises.foo();
	}
}
class Exercises {
	public static int foo() {
		return new Ex().foo();
	}
}
class Ex {
	public int foo() {
		return new Bonus(42).bar();
	}
}
class Bonus {
	int bonus;
	Bonus(int bonus) {
		this.bonus = bonus;
	}
	public int bar() {
		return Malus.bar(-bonus);
	}
}
class Malus {
	public static int bar(int malus) {
		return Forbidden.forbid(-malus);
	}
}
class Forbidden {
	public static int forbid(int x) {
		return NotForbidden.minusOne() * (-x) + SecretCase.nothing();
	}
}
class NotForbidden {
	public static int minusOne() {
		return -1;
	}
}
class SecretCase {
	public static int nothing() {
		return 0;
	}
}

class Test {
}

class Rule {
}

class ClassRule {
}
