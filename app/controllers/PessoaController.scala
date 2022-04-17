package controllers

import models.Pessoa
import play.api.Configuration
import play.api.data.Form
import play.api.i18n.Lang.jsonTagWrites
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.JsObject.writes
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites

import javax.inject.Inject
import play.api.libs.json.{Json, Writes}
import play.api.mvc.Results.Ok
import play.api.mvc._
import repositories.PessoaRepository
import views.html.helper.form

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
class PessoaController @Inject()(controllerComponents: ControllerComponents, pessoaRepository: PessoaRepository)
                                (implicit executionContext: ExecutionContext)
  extends AbstractController(controllerComponents) {

  object PessoaController {
    implicit val pessoaForm: Form[Pessoa] = {
      import play.api.data.Forms._
      Form(
        mapping(
          "id" -> optional(longNumber),
          "nome" -> text,
          "dt_nasc" -> sqlDate
        )(Pessoa.apply)(Pessoa.unapply)
      )
    }

    implicit val pessoasForm: Form[List[Pessoa]] = {
      import play.api.data.Forms._
      Form(
        list(
          mapping(
          "id" -> optional(longNumber),
          "nome" -> text,
          "dt_nasc" -> sqlDate
          )(Pessoa.apply)(Pessoa.unapply)
        )
      )
    }
  }


  def listar: Action[AnyContent] = Action.async {
    pessoaRepository.listar.map { pessoa => Ok(Json.toJson(pessoa)) }
  }

  def consultar(id: Long): Action[AnyContent] = Action.async {
    pessoaRepository.consultar(id).map(pessoa => Ok(Json.toJson(pessoa)))
  }

  def consultarPorNome(nome: String): Action[AnyContent] = Action.async {
    pessoaRepository.consultarPorNome(nome).map(pessoa => Ok(Json.toJson(pessoa)))
  }


  def cadastrar: Action[AnyContent] = Action.async { implicit request =>
    PessoaController.pessoaForm.bindFromRequest.fold(
      formWithErrors => Future(BadRequest("erro")),
      pessoaForm => pessoaRepository.inserir(Pessoa(pessoaForm.id, pessoaForm.nome, pessoaForm.dt_nasc)).map(pId => Ok(Json.toJson(pId)))
    )
  }

  def cadastrarPessoas: Action[AnyContent] = Action.async { implicit request =>
    PessoaController.pessoasForm.bindFromRequest.fold(
      formWithErrors => Future(BadRequest("erro")),
      pessoasForm =>  pessoaRepository.inserirPessoas(pessoasForm.map(p=>Pessoa(p.id,p.nome,p.dt_nasc))).map(result => Ok(Json.toJson(result)))
    )
  }

  def atualizar(id: Long): Action[AnyContent] = Action.async { implicit request =>
    PessoaController.pessoaForm.bindFromRequest.fold(
      formWithErrors => Future(BadRequest("Erro ao atualizar")),
      pessoaForm => pessoaRepository.atualizar(id, Pessoa(None, pessoaForm.nome, pessoaForm.dt_nasc)).map(result => Ok(Json.toJson(result)))
    )
  }

  def deletar(id: Long): Action[AnyContent] = Action.async {
      pessoaRepository.deletar(id).map(result => Ok(Json.toJson(result)))
  }


}
