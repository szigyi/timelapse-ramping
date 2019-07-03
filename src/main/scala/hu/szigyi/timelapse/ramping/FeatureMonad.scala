package hu.szigyi.timelapse.ramping

import cats.Monad
import scala.language.higherKinds

/**
  * When the feature is turned off - feature flag is not there - then the map and flatMap short curcuite the computation.
  * @tparam F
  */
class FeatureMonad[F[_]] extends Monad[F] {

  override def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B] = ???

  override def tailRecM[A, B](a: A)(f: A => F[Either[A, B]]): F[B] = ???

  override def pure[A](x: A): F[A] = ???
}
