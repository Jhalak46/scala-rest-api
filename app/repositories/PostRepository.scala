package repositories

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.Future
import play.modules.reactivemongo.ReactiveMongoApi
import  reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection
import models.Post
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.api.{ReadPreference, Cursor}
import reactivemongo.api.commands.{ WriteResult }


class PostRepository @Inject()(
    implicit  ec: ExecutionContext,
    reactiveMongoApi: ReactiveMongoApi
) {
    private def collection: Future[JSONCollection] = 
        reactiveMongoApi.database.map(_.collection("posts"))

    def list(limit: Int = 100): Future[Seq[Post]] = 
        collection.flatMap(_
            .find(BSONDocument())
            .cursor[Post](ReadPreference.primary)
            .collect[Seq](limit, Cursor.FailOnError[Seq[Post]]())
        )

    def create(post: Post): Future[WriteResult] = 
        collection.flatMap(_.insert(post))

    def read(id: BSONObjectID): Future[Option[Post]] = 
        collection.flatMap(_.find(BSONDocument("_id" -> id)).one
        [Post])

    def update(id: BSONObjectID, post: Post): Future[Option
    [Post]] = 
        collection.flatMap(
            _.findAndUpdate(
                BSONDocument("_id" -> id),
                BSONDocument(
                    f"$$set" -> BSONDocument(
                        "title" -> post.title,
                        "description" -> post.description
                    )
                ),
                true
            ).map(_.result[Post])
        )

    def destroy(id: BSONObjectID): Future[Option[Post]] = 
        collection.flatMap(
            _.findAndRemove(BSONDocument("_id" -> id)).map(_.result
            [Post])
        )
}