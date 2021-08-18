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

package com.fgrutsch.emergence.core.utils

object logging {

  val sectionSeperator: String = "=" * 200
  val newLine: String          = "\n"
  val indent: String           = " " * 17

  def highlight(s: String): String = s"${"*" * 20} $s ${"*" * 20}"

  def bulletPointed[A](seq: Seq[A]): String = {
    val list = seq
      .map(e => s"${indent}- $e")
      .mkString(newLine)

    s"${newLine}${list}"
  }

}
