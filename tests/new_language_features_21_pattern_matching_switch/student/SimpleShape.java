public interface SimpleShape {
}

record SimpleRectangle(int length, long width) implements SimpleShape {
}

record SimpleCircle(int radius) implements SimpleShape {
}
