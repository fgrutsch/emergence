/*
 * Copyright 2021 Emergence contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fgrutsch.emergence.core.condition

import cats.syntax.all.*

sealed abstract class ConditionOperator(val sign: String) {
  override def toString: String = sign
}

object ConditionOperator {
  case object Equal extends ConditionOperator("==")
  case object RegEx extends ConditionOperator("^$")

  def unapply(value: String): Option[ConditionOperator] = {
    value match {
      case Equal.sign => Equal.some
      case RegEx.sign => RegEx.some
      case _          => none

    }
  }

  def matches(condition: Condition & Condition.Matchable, input: String): MatchResult = {
    val operator = condition.operator
    val value    = condition.value

    operator match {
      case Equal if input == value.underlying         => ().validNel
      case RegEx if value.underlying.r.matches(input) => ().validNel
      case _ => s"Input '${input}' doesn't match condition '$value' with operator '$operator'".invalidNel
    }
  }

}
