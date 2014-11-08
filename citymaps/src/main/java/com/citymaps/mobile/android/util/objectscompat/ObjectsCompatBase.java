package com.citymaps.mobile.android.util.objectscompat;

import java.util.Arrays;
import java.util.Comparator;

public class ObjectsCompatBase extends ObjectsCompat {

	static boolean deepEquals0(Object e1, Object e2) {
		assert e1 != null;
		boolean eq;
		if (e1 instanceof Object[] && e2 instanceof Object[])
			eq = Arrays.deepEquals((Object[]) e1, (Object[]) e2);
		else if (e1 instanceof byte[] && e2 instanceof byte[])
			eq = Arrays.equals((byte[]) e1, (byte[]) e2);
		else if (e1 instanceof short[] && e2 instanceof short[])
			eq = Arrays.equals((short[]) e1, (short[]) e2);
		else if (e1 instanceof int[] && e2 instanceof int[])
			eq = Arrays.equals((int[]) e1, (int[]) e2);
		else if (e1 instanceof long[] && e2 instanceof long[])
			eq = Arrays.equals((long[]) e1, (long[]) e2);
		else if (e1 instanceof char[] && e2 instanceof char[])
			eq = Arrays.equals((char[]) e1, (char[]) e2);
		else if (e1 instanceof float[] && e2 instanceof float[])
			eq = Arrays.equals((float[]) e1, (float[]) e2);
		else if (e1 instanceof double[] && e2 instanceof double[])
			eq = Arrays.equals((double[]) e1, (double[]) e2);
		else if (e1 instanceof boolean[] && e2 instanceof boolean[])
			eq = Arrays.equals((boolean[]) e1, (boolean[]) e2);
		else
			eq = e1.equals(e2);
		return eq;
	}

	/**
	 * Returns 0 if the arguments are identical and {@code
	 * c.compare(a, b)} otherwise.
	 * Consequently, if both arguments are {@code null} 0
	 * is returned.
	 * <p/>
	 * <p>Note that if one of the arguments is {@code null}, a {@code
	 * NullPointerException} may or may not be thrown depending on
	 * what ordering policy, if any, the {@link Comparator Comparator}
	 * chooses to have for {@code null} values.
	 *
	 * @param <T> the type of the objects being compared
	 * @param a   an object
	 * @param b   an object to be compared with {@code a}
	 * @param c   the {@code Comparator} to compare the first two arguments
	 * @return 0 if the arguments are identical and {@code
	 * c.compare(a, b)} otherwise.
	 * @see Comparable
	 * @see Comparator
	 */
	@Override
	public <T> int compareImpl(T a, T b, Comparator<? super T> c) {
		return (a == b) ? 0 : c.compare(a, b);
	}

	/**
	 * Returns {@code true} if the arguments are deeply equal to each other
	 * and {@code false} otherwise.
	 * <p/>
	 * Two {@code null} values are deeply equal.  If both arguments are
	 * arrays, the algorithm in {@link Arrays#deepEquals(Object[],
	 * Object[]) Arrays.deepEquals} is used to determine equality.
	 * Otherwise, equality is determined by using the {@link
	 * Object#equals equals} method of the first argument.
	 *
	 * @param a an object
	 * @param b an object to be compared with {@code a} for deep equality
	 * @return {@code true} if the arguments are deeply equal to each other
	 * and {@code false} otherwise
	 * @see Arrays#deepEquals(Object[], Object[])
	 * @see #equals(Object, Object)
	 */
	@Override
	public boolean deepEqualsImpl(Object a, Object b) {
		if (a == b)
			return true;
		else if (a == null || b == null)
			return false;
		else
			return deepEquals0(a, b);
	}

	/**
	 * Returns {@code true} if the arguments are equal to each other
	 * and {@code false} otherwise.
	 * Consequently, if both arguments are {@code null}, {@code true}
	 * is returned and if exactly one argument is {@code null}, {@code
	 * false} is returned.  Otherwise, equality is determined by using
	 * the {@link Object#equals equals} method of the first
	 * argument.
	 *
	 * @param a an object
	 * @param b an object to be compared with {@code a} for equality
	 * @return {@code true} if the arguments are equal to each other
	 * and {@code false} otherwise
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equalsImpl(Object a, Object b) {
		return (a == b) || (a != null && a.equals(b));
	}

	/**
	 * Generates a hash code for a sequence of input values. The hash
	 * code is generated as if all the input values were placed into an
	 * array, and that array were hashed by calling {@link
	 * Arrays#hashCode(Object[])}.
	 * <p/>
	 * <p>This method is useful for implementing {@link
	 * Object#hashCode()} on objects containing multiple fields. For
	 * example, if an object that has three fields, {@code x}, {@code
	 * y}, and {@code z}, one could write:
	 * <p/>
	 * <blockquote><pre>
	 * &#064;Override public int hashCode() {
	 *     return Objects.hash(x, y, z);
	 * }
	 * </pre></blockquote>
	 * <p/>
	 * <b>Warning: When a single object reference is supplied, the returned
	 * value does not equal the hash code of that object reference.</b> This
	 * value can be computed by calling {@link #hashCode(Object)}.
	 *
	 * @param values the values to be hashed
	 * @return a hash value of the sequence of input values
	 * @see Arrays#hashCode(Object[])
	 * @see java.util.List#hashCode
	 */
	@Override
	public int hashImpl(Object... values) {
		return Arrays.hashCode(values);
	}

	/**
	 * Returns the hash code of a non-{@code null} argument and 0 for
	 * a {@code null} argument.
	 *
	 * @param o an object
	 * @return the hash code of a non-{@code null} argument and 0 for
	 * a {@code null} argument
	 * @see Object#hashCode
	 */
	@Override
	public int hashCodeImpl(Object o) {
		return o != null ? o.hashCode() : 0;
	}

	/**
	 * Checks that the specified object reference is not {@code null} and
	 * throws a customized {@link NullPointerException} if it is. This method
	 * is designed primarily for doing parameter validation in methods and
	 * constructors with multiple parameters, as demonstrated below:
	 * <blockquote><pre>
	 * public Foo(Bar bar, Baz baz) {
	 *     this.bar = Objects.requireNonNull(bar, "bar must not be null");
	 *     this.baz = Objects.requireNonNull(baz, "baz must not be null");
	 * }
	 * </pre></blockquote>
	 *
	 * @param o       the object reference to check for nullity
	 * @param message detail message to be used in the event that a {@code
	 *                NullPointerException} is thrown
	 * @param <T>     the type of the reference
	 * @return {@code obj} if not {@code null}
	 * @throws NullPointerException if {@code obj} is {@code null}
	 */
	@Override
	public <T> T requireNonNullImpl(T o, String message) {
		if (o == null)
			throw new NullPointerException(message);
		return o;
	}

	/**
	 * Checks that the specified object reference is not {@code null}. This
	 * method is designed primarily for doing parameter validation in methods
	 * and constructors, as demonstrated below:
	 * <blockquote><pre>
	 * public Foo(Bar bar) {
	 *     this.bar = Objects.requireNonNull(bar);
	 * }
	 * </pre></blockquote>
	 *
	 * @param o   the object reference to check for nullity
	 * @param <T> the type of the reference
	 * @return {@code obj} if not {@code null}
	 * @throws NullPointerException if {@code obj} is {@code null}
	 */
	@Override
	public <T> T requireNonNullImpl(T o) {
		if (o == null)
			throw new NullPointerException();
		return o;
	}

	/**
	 * Returns the result of calling {@code toString} on the first
	 * argument if the first argument is not {@code null} and returns
	 * the second argument otherwise.
	 *
	 * @param o          an object
	 * @param nullString string to return if the first argument is
	 *                   {@code null}
	 * @return the result of calling {@code toString} on the first
	 * argument if it is not {@code null} and the second argument
	 * otherwise.
	 * @see #toString(Object)
	 */
	@Override
	public String toStringImpl(Object o, String nullString) {
		return (o != null) ? o.toString() : nullString;
	}

	/**
	 * Returns the result of calling {@code toString} for a non-{@code
	 * null} argument and {@code "null"} for a {@code null} argument.
	 *
	 * @param o an object
	 * @return the result of calling {@code toString} for a non-{@code
	 * null} argument and {@code "null"} for a {@code null} argument
	 * @see Object#toString
	 * @see String#valueOf(Object)
	 */
	@Override
	public String toStringImpl(Object o) {
		return String.valueOf(o);
	}
}