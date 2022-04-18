package controllers

import play.api.Configuration

import javax.inject.Inject
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
class TesteController @Inject()(controllerComponents: ControllerComponents)
                                (implicit executionContext: ExecutionContext, config: Configuration)
  extends AbstractController(controllerComponents) {

  //capturando variável de ambiente do application.conf
  def teste1: Action[AnyContent] = Action {
    Ok(config.get[String]("teste1"))
  }

  def teste2 = Action { request =>
    Ok("teste 2 " + request)
  }

  def teste3 = Action { implicit request =>
    Ok("teste 3 " + request)
  }

  def teste4 = Action{
    NotFound
  }

  def teste5 = Action{
    NotFound(<h1 style="color:red">Page not found</h1>)
  }

  def teste6 = Action{
    InternalServerError("teste 6")
  }

  def teste7 = Action{
    Status(488)("Teste 7,  Strange response type")
  }

  def teste8() = Action{
    Redirect("teste2")
  }

  def teste9() = TODO

  //Definindo o  content/type da resposta para json
  def teste11: Action[AnyContent] = Action {
    Ok("{\"nome\":\"Harrison\"}").as("application/json")
  }

  //Definindo o  content/type da resposta para html
  def teste12: Action[AnyContent] = Action {
    Ok(<button>ok</button>).as(HTML)
  }


}