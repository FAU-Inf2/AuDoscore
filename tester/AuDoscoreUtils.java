package tester;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public final class AuDoscoreUtils {

	@FunctionalInterface
	private static interface Matcher<T> {
		boolean match(T elem);
	}



	private AuDoscoreUtils() { }



	/**
	 * Returns an array of fields explicitly declared in the specified class.
	 *
	 * @param theClass the class
	 * @return array of declared fields
	 */
	public static Field[] getExplicitlyDeclaredFields(final Class<?> theClass) {
		return getMatching(theClass.getDeclaredFields(), elem -> !elem.isSynthetic());
	}



	/**
	 * Returns an array of methods explicitly declared in the specified class.
	 *
	 * @param theClass the class
	 * @return array of declared methods
	 */
	public static Method[] getExplicitlyDeclaredMethods(final Class<?> theClass) {
		return getMatching(theClass.getDeclaredMethods(),
				elem -> !elem.isBridge() && !elem.isSynthetic());
	}



	private static <T> T[] getMatching(final T[] array, final Matcher<T> matcher) {
		if (array == null) {
			return null;
		}

		final List<T> resultList = getMatching(Arrays.asList(array), matcher);
		if (resultList.isEmpty() && array.length == 0) {
			return array;
		}
		return resultList.toArray(
				(T[]) Array.newInstance(array[0].getClass(), resultList.size()));
	}



	private static <T> List<T> getMatching(final List<T> list, final Matcher<T> matcher) {
		if (list == null) {
			return null;
		}

		final List<T> result = new ArrayList<>(list.size());
		for (final T elem : list) {
			if (elem != null && matcher.match(elem)) {
				result.add(elem);
			}
		}
		return result;
	}
}

