package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.ExecutionContext
import repositories.PostRepository

@Singleton
class AppController @Inject()(
  implicit ec: ExecutionContext,
  val controllerComponents: ControllerComponents,
  postsRepo: PostRepository
) extends BaseController {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok("App works!")
  }

  def listPosts = Action.async {
    postsRepo.list().map { 
      posts => Ok(Json.toJson(posts))}
  }
}
