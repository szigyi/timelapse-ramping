package hu.szigyi.timelapse.ramping.csv

import java.io.File

import shapeless.{::, Generic, HList, HNil, Lazy}

trait CsvEncoder[A] {
  def encode(value: A): String
}
object CsvEncoder {
  def apply[A](implicit enc: CsvEncoder[A]): CsvEncoder[A] = enc
}

object Encoders {
  implicit val bigDecimalEncoder: CsvEncoder[BigDecimal] = (v: BigDecimal) => v.toString
  implicit val intEncoder: CsvEncoder[Int] = (v: Int) => v.toString
  implicit val booleanEncoder: CsvEncoder[Boolean] = (v: Boolean) => if (v) "yes" else "no"
  implicit val fileEncoder: CsvEncoder[File] = (v: File) => v.getName
  implicit val hNilEncoder: CsvEncoder[HNil] = _ => ""
  implicit def optionEncoder[H, T <: HList](implicit
                                            headEncoder: Lazy[CsvEncoder[H]],
                                            tailEncoder: CsvEncoder[T]): CsvEncoder[Option[H] :: T] = v => v match {
    case head :: HNil => if (head.isDefined) headEncoder.value.encode(head.get) else ""
    case head :: tail => {
      if (head.isDefined) headEncoder.value.encode(head.get) ++ "," ++ tailEncoder.encode(tail)
      else ""
    }
  }
  implicit def hListEncoder[H, T <: HList](implicit
                                           headEncoder: Lazy[CsvEncoder[H]],
                                           tailEncoder: CsvEncoder[T]): CsvEncoder[H :: T] = v => v match {
    case head :: HNil => headEncoder.value.encode(head)
    case head :: tail => headEncoder.value.encode(head) ++ "," ++ tailEncoder.encode(tail)
  }
  implicit def genericEncoder[A, R](implicit
                                    gen: Generic.Aux[A, R],
                                    enc: Lazy[CsvEncoder[R]]): CsvEncoder[A] = (a: A) => {
    val repr = gen.to(a)
    enc.value.encode(repr)
  }
  implicit class CsvWriter[A](value: A) {
    def toCsv(implicit enc: CsvEncoder[A]): String = enc.encode(value)
  }
}
