public record GenericRecord<T, V>(T t, V v) {
}

sealed interface A {
}

record B(int theInteger) implements A {
}

record C(long theLong) implements A {
}