package com.fgrutsch.emergence.core.vcs.github

import io.circe.*

private[github] final case class CheckSuiteResponseItem(
    id: CheckSuiteResponseItem.Id
)

private[github] object CheckSuiteResponseItem {

  opaque type Id = Int
  object Id {
    given Decoder[Id] = Decoder.decodeInt.map[Id](identity)
  }

  given Decoder[CheckSuiteResponseItem] = Decoder.instance { c =>
    c.downField("id")
      .as[Id]
      .map(CheckSuiteResponseItem(_))
  }
}
