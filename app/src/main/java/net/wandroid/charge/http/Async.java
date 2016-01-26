package net.wandroid.charge.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * the Async annotation indicates that a method can be called asyncronus.
 * In such method it is extra important to pay attention to if the activity is still valid
 * and other problems that can occure in the life cycle.
 */
@Target(ElementType.METHOD)
public @interface Async {
}
