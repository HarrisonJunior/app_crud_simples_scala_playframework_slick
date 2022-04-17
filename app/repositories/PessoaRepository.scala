package repositories

import models.Pessoa
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import java.sql.Date
import java.util.Optional
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

case class PessoaRepository @Inject()(
                                       protected val dbConfigProvider : DatabaseConfigProvider
                                     )(
                                       implicit executionContext: ExecutionContext
                                     )  extends HasDatabaseConfigProvider[JdbcProfile]{

  import profile.api._

  private class MapaTabelaPessoas(tag: Tag) extends Table[Pessoa](tag, "pessoas"){
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def nome = column[String]("nome")
    def dt_nasc = column[Date]("dt_nasc")
    def * = (id, nome, dt_nasc) <> ((Pessoa.apply _).tupled, Pessoa.unapply)
  }

  private val tabelaPessoas = TableQuery[MapaTabelaPessoas]

  //método lista todas as pessoas
  def listar : Future[Seq[Pessoa]] = db.run(tabelaPessoas.result)

  def consultar(id: Long): Future[Pessoa] = {
    val consulta = tabelaPessoas.filter(_.id === id)
    val acao = consulta.result
    val dbRun = db.run(acao.head.asTry)
    dbRun.map {
      case Failure(exception) => throw exception
      case Success(value) => value
    }
  }

  //consulta pessoa por nome
  def consultarPorNome(nome: String) : Future[Pessoa] = {
    val consulta = for{
      pessoa <- tabelaPessoas if pessoa.nome === nome
    } yield pessoa

    val acao = consulta.result.head.asTry

    db.run(acao).map{
      case Failure(exception) => throw exception
      case Success(value) => value
    }
  }

  def inserir(p: Pessoa) : Future[Option[Long]] = db.run {
    //Como o tipo da coluna id é Option[Long] o retorno deste método é Option[Long]
    //Dessa forma abaixo são retornados os valores das colunas especificadas em map da linha afetada(inserida, neste caso).
    //Muitos sistemas de banco de dados permitem que apenas uma única coluna seja retornada, que deve ser a chave primária de incremento automático da tabela, como no comando abaixo.
    (tabelaPessoas returning tabelaPessoas.map(_.id)) += Pessoa(None, p.nome, p.dt_nasc)
  }

//  def inserir(p: Pessoa) : Future[Int] = db.run {
//    //Dessas formas abaixo é retornado o número de colunas afetadas, que geralmente é um.
////    tabelaPessoas += Pessoa(None, p.nome, p.dt_nasc)
////    ou
//    tabelaPessoas.map(c => (c.nome, c.dt_nasc)) += (p.nome, p.dt_nasc)
//  }


// método usado para inserir lista de pessoa e retorna lista de id das pessoas inseridas
  def inserirPessoas(pessoas: List[Pessoa])  = {

    val seq = (tabelaPessoas returning tabelaPessoas.map(_.id)) ++= pessoas

    val dbRun = db.run(DBIO.sequence(Seq(seq)).transactionally.asTry)

    dbRun.map{
      case Failure(exception) => throw exception
      case Success(value) => value
    }
  }

// método usado para inserir lista de pessoa e retorna lista de id das pessoas inseridas
//  def inserirPessoas(pessoas: List[Pessoa])  = db.run{
//    (tabelaPessoas returning tabelaPessoas.map(_.id)) ++= pessoas
//  }



//  def atualizar(id: Long, pessoa:Pessoa): Future[Int] ={
//    val q = for {
//      p <- tabelaPessoas if p.id === id
//    } yield (p.nome, p.dt_nasc)
//
//    val dbRun = db.run( q.update(pessoa.nome, pessoa.dt_nasc).asTry)
//
//    dbRun.map{
//      case Failure(exception) => throw exception
//      case Success(value) => value
//    }
//  }

  def atualizar(id: Long, pessoa:Pessoa): Future[Int] ={
    val consulta = tabelaPessoas.filter(_.id === id).map(pessoa => (pessoa.nome, pessoa.dt_nasc))
    val acao = consulta.update(pessoa.nome, pessoa.dt_nasc)
    val dbRun = db.run(acao.asTry)

    dbRun.map{
      case Failure(exception) => throw exception
      case Success(value) => value
    }
  }

//  def deletar(id: Long): Future[Int] ={
//    val consulta = for{
//     pessoa <- tabelaPessoas if pessoa.id === id
//    } yield pessoa
//
//    val acao = consulta.delete
//    db.run(acao)
//  }

  def deletar(id: Long): Future[Int] ={
    val consulta = tabelaPessoas.filter(_.id === id)
    val acao = consulta.delete //posso usar o asTry aqui
    db.run(acao)
  }



}



