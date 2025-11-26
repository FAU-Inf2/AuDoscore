public class ToTest {
	public static int toTest() {
		return CompareInterface.compareInterface();
	}
}

class CompareInterface {
	public static int compareInterface() {
		return Exercises.exercises();
	}
}

class Exercises {
	public static int exercises() {
		return new Ex().ex();
	}
}

class Ex {
	public int ex() {
		return Forbidden.forbidden(42);
	}
}

class Forbidden {
	public static int forbidden(int x) {
		return new InitializeOnce().initializeOnce();
	}
}

class InitializeOnce {
	public int initializeOnce() {
		return NotForbidden.notForbidden();
	}
}

class NotForbidden {
	public static int notForbidden() {
		return new Points().points();
	}
}

class Points {
	public int points() {
		return new Replace().replace();
	}
}

class Replace {
	public int replace() {
		return SecretCase.secretCase();
	}
}

class SecretCase {
	public static int secretCase() {
		return 42;
	}
}
