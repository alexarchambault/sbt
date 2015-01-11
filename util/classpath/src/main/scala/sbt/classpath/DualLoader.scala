/* sbt -- Simple Build Tool
* Copyright 2008, 2009 Mark Harrah
*/
package sbt
package classpath

import java.util.Enumeration

/** Concatenates `a` and `b` into a single `Enumeration`.*/
final class DualEnumeration[T](a: Enumeration[T], b: Enumeration[T]) extends Enumeration[T] {
  // invariant: current.hasMoreElements or current eq b
  private[this] var current = if (a.hasMoreElements) a else b
  def hasMoreElements = current.hasMoreElements
  def nextElement =
    {
      val element = current.nextElement
      if (!current.hasMoreElements)
        current = b
      element
    }
}