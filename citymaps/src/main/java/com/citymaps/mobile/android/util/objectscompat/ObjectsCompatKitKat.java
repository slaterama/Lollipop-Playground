package com.citymaps.mobile.android.util.objectscompat;

import android.annotation.TargetApi;

import java.util.Comparator;
import java.util.Objects;

@TargetApi(19)
public class ObjectsCompatKitKat extends ObjectsCompat {

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
		return Objects.compare(a, b, c);
	}

	/**
	 * Returns {@code true} if the arguments are deeply equal to each other
	 * and {@code false} otherwise.
	 * <p/>
	 * Two {@code null} values are deeply equal.  If both arguments are
	 * arrays, the algorithm in {@link java.util.Arrays#deepEquals(Object[],
	 * Object[]) Arrays.deepEquals} is used to determine equality.
	 * Otherwise, equality is determined by using the {@link
	 * Object#equals equals} method of the first argument.
	 *
	 * @param a an object
	 * @param b an object to be compared with {@code a} for deep equality
	 * @return {@code true} if the arguments are deeply equal to each other
	 * and {@code false} otherwise
	 * @see java.util.Arrays#deepEquals(Object[], Object[])
	 * @see #equals(Object, Object)
	 */
	@Override
	public boolean deepEqualsImpl(Object a, Object b) {
		return Objects.deepEquals(a, b);
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
		return Objects.equals(a, b);
	}

	/**
	 * Generates a hash code for a sequence of input values. The hash
	 * code is generated as if all the input values were placed into an
	 * array, and that array were hashed by calling {@link
	 * java.util.Arrays#hashCode(Object[])}.
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
	 * @see java.util.Arrays#hashCode(Object[])
	 * @see java.util.List#hashCode
	 */
	@Override
	public int hashImpl(Object... values) {
		return Objects.hash(values);
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
		return Objects.hashCode(o);
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
		return Objects.requireNonNull(o, message);
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
		return Objects.requireNonNull(o);
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
		return Objects.toString(o, nullString);
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
		return Objects.toString(o);
	}
}
