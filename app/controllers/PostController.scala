package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import models.Post
import repositories.PostRepository
import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.bson.{BSONDocument, BSONObjectID}


class PostController @Inject()(
  implicit ec: ExecutionContext,
  val controllerComponents: ControllerComponents,
  postsRepo: PostRepository
) extends BaseController {

  def listPosts = Action.async {
    postsRepo.list().map { 
      posts => Ok(Json.toJson(posts))}
  }

  def createPost = Action.async(parse.json) {
    _.body
        .validate[Post]
        .map { post =>
            postsRepo.create(post).map { _ =>
            Created
        }
    }.getOrElse(Future.successful(BadRequest("Invalid format")))
  }

  def readPost(id: BSONObjectID) = Action.async {
    postsRepo.read(id).map { maybePost =>
        maybePost.map { post =>
            Ok(Json.toJson(post))
        }.getOrElse(NotFound)
    }
  }

    def updatePost(id: BSONObjectID) = Action.async(parse.json) {
    _.body
        .validate[Post]
        .map { post =>
            postsRepo.update(id, post).map {
                case Some(post) => Ok(Json.toJson(post))
                case _ => NotFound
        }
    }.getOrElse(Future.successful(BadRequest("Invalid format")))
  }

  def deletePost(id: BSONObjectID) = Action.async {
    postsRepo.destroy(id).map {
        case Some(post) => Ok(Json.toJson(post))
        case _ => NotFound
        }
    }

}