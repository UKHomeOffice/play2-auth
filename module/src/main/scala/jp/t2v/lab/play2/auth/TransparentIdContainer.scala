package jp.t2v.lab.play2.auth

import play.api.mvc.RequestHeader

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.Exception._

class TransparentIdContainer[Id: ToString: FromString] extends AsyncIdContainer[Id] {

  override def startNewSession(userId: Id, timeoutInSeconds: Int)(implicit request: RequestHeader, context: ExecutionContext): Future[AuthenticityToken] = Future.successful(implicitly[ToString[Id]].apply(userId))

  override def remove(token: AuthenticityToken)(implicit context: ExecutionContext): Future[Unit] = Future.unit

  override def get(token: AuthenticityToken)(implicit context: ExecutionContext): Future[Option[Id]] = Future.successful(implicitly[FromString[Id]].apply(token))

  override def prolongTimeout(token: AuthenticityToken, timeoutInSeconds: Int)(implicit request: RequestHeader, context: ExecutionContext): Future[Unit] = Future.unit
}

trait ToString[A] {
  def apply(id: A): String
}
object ToString {
  def apply[A](f: A => String) = new ToString[A] {
    def apply(id: A) = f(id)
  }
  implicit val string = ToString[String](identity)
  implicit val int = ToString[Int](_.toString)
  implicit val long = ToString[Long](_.toString)
}
trait FromString[A] {
  def apply(id: String): Option[A]
}
object FromString {
  def apply[A](f: String => A) = new FromString[A] {
    def apply(id: String) = allCatch opt f(id)
  }
  implicit val string = FromString[String](identity)
  implicit val int = FromString[Int](_.toInt)
  implicit val long = FromString[Long](_.toLong)
}
