package io.github.fgrutsch.emergence.core.vcs

import cats.effect._
import io.github.fgrutsch.emergence.core.vcs.VcsFactory
import io.github.fgrutsch.emergence.core.vcs.bitbucketcloud.BitbucketCloudVcs
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import testutil._

class VcsFactorySpec extends BaseSpec {

  private given SttpBackend[IO, Any] = AsyncHttpClientCatsBackend.stub[IO]
  private val vcsFactory             = new VcsFactory[IO]

  test("getVcs returns a BitbucketCloudVcs instance") {
    val result = vcsFactory.getVcs(settings)
    result match {
      case _: BitbucketCloudVcs[IO] => succeed
      case _                        => fail("wrong VcsAlg")
    }

  }

}
