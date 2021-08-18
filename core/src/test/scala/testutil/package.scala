import cats.data.Kleisli
import cats.effect.{IO, Ref}

package object testutil {

  type Eff[A] = Kleisli[IO, Ref[IO, TestState], A]

  implicit class EffOps[A](private val fa: Eff[A]) extends AnyVal {
    def runA(state: TestState): IO[A] =
      Ref[IO].of(state).flatMap(fa.run)

    def runS(state: TestState): IO[TestState] =
      Ref[IO].of(state).flatMap(ref => fa.run(ref) >> ref.get)
  }

}
