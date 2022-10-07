package com.fgrutsch.emergence.core.vcs.github

import io.circe.*
import cats.syntax.all.*

private[github] final case class CheckRunResponseItem(
    status: CheckRunResponseItem.Status,
    conclusion: Option[CheckRunResponseItem.Conclusion]
)

private[github] object CheckRunResponseItem {

  enum Status(val underlying: String) {
    case Completed            extends Status("completed")
    case Other(value: String) extends Status(value)
  }
  object Status {
    given Decoder[Status] = Decoder[String].emap {
      case Status.Completed.underlying => Status.Completed.asRight
      case other                       => Status.Other(other).asRight
    }
  }

  enum Conclusion(val underlying: String) {
    case Success              extends Conclusion("success")
    case Skipped              extends Conclusion("skipped")
    case Other(value: String) extends Conclusion(value)
  }
  object Conclusion {
    given Decoder[Conclusion] = Decoder[String].emap {
      case Conclusion.Success.underlying => Conclusion.Success.asRight
      case Conclusion.Skipped.underlying => Conclusion.Skipped.asRight
      case other                         => Conclusion.Other(other).asRight
    }
  }

  given Decoder[CheckRunResponseItem] = Decoder.instance { c =>
    for {
      status     <- c.downField("status").as[Status]
      conclusion <- c.downField("conclusion").as[Option[Conclusion]]
    } yield CheckRunResponseItem(status, conclusion)
  }

}
