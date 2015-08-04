package com.challenge.vngrs

import akka.actor.{ActorRef, Actor}
import com.mongodb.casbah.Imports._

import scala.xml.XML

class MongoService extends Actor {
  import MongoService._

  val db = MongoClient()("test")
  val coll = db("contacts")

  def receive = {
    case Load(sender:ActorRef, files: List[String], app: ActorRef) => load(sender, files, app, true)
    case AddOneFile(sender:ActorRef, fileName: String, app: ActorRef) => load(sender, fileName::Nil, app, false)
    case Find(sender:ActorRef, name: String, app: ActorRef) => sender ! Done(findByName(name),app)
    case Drop(sender:ActorRef, app: ActorRef) => sender ! Done(dropAndSendMessage, app)
  }

  def load(sender:ActorRef, files: List[String], app: ActorRef, isloading: Boolean) = {
    try {
      var resultMessage = "old collection is dropped. " + files.mkString(", ").concat(" loaded.")
      if(isloading) coll.drop()
      else resultMessage = files.mkString(", ").concat(" added.")
      files.foreach(insert)
      sender ! Done(resultMessage, app)
    } catch {
      case e:Throwable => sender ! Failed(Util.thereAreErrorsOrFilesAreNotExist, app)
    }
  }

  def insert(fileName: String):Unit =  {
    val url = getClass().getClassLoader.getResource(fileName)
    val xml = XML.load(url)
    val nodes = xml \ "contact"
    val contacts = nodes.map{ contact =>
      val id = ( contact \ "id")
      val name = (contact \ "name")
      val lastName = (contact \ "lastName")
      val phones = (contact \ "phones")
      Contact(id.text.trim,name.text.trim,lastName.text.trim, phones.text.trim::Nil)
    }.toList
    contacts foreach {
      contact => {
        val mongoDbObject = MongoDBObject(
          //"_id" -> contact.id,
          "name" -> contact.name,
          "lastName" -> contact.lastName
        )
        val newPhone = $addToSet("phones" -> contact.phones.head)
        coll.update(mongoDbObject, newPhone, upsert = true)
      }
    }
  }

  def findByName(name: String): String = {
    val iterator = coll.find(MongoDBObject("name" -> name))
    val result = iterator.toList.mkString("\n")
    /*def loop(acc: String):String ={
      if (iterator.hasNext) loop(acc.concat(iterator.next().toString + "\n"))
      else acc
    }
    val result = loop("")*/
    if (result.length > 0) result
    else "Contact is not found!"
  }

  def dropAndSendMessage = {
    coll.drop()
    "Contacts collection is dropped"
  }
}

object MongoService {
  case class Load(sender:ActorRef, files: List[String], app: ActorRef)
  case class AddOneFile(sender:ActorRef, fileName: String, app: ActorRef)
  case class Find(sender:ActorRef, name: String, app: ActorRef)
  case class Drop(sender:ActorRef, app: ActorRef)
  case class Done(result: String, app: ActorRef)
  case class Failed(result: String, app: ActorRef)
}
