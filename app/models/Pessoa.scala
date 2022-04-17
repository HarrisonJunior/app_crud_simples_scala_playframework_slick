package models

import play.api.libs.json.{Json, Writes}

import java.sql.Date


case class Pessoa (
                    val id: Option[Long],
                    val nome: String,
                    val dt_nasc: Date
                  )

object Pessoa{
  implicit val pessoaWrites = new Writes[Pessoa] {
    def writes(pessoa: Pessoa) = Json.obj(
      "id"  -> pessoa.id,
      "nome" -> pessoa.nome,
      "dt_nasc" -> pessoa.dt_nasc
    )
  }
}