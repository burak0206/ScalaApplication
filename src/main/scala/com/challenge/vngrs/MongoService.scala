package com.challenge.vngrs

import akka.actor.{ActorRef, Actor}
import com.mongodb.casbah.Imports._

import scala.xml.XML

class MongoService extends Actor {
  import MongoService._

  val db = MongoClient()("test")
  val coll = db("contacts")

  def receive = {
    case Load(sender:ActorRef, files: List[String], app: ActorRef, dropFlag: Boolean, isExternal:Boolean) => load(sender, files, app, dropFlag, isExternal)
    case Find(sender:ActorRef, name: String, app: ActorRef) => findByName(sender, name, app)
    case Drop(sender:ActorRef, app: ActorRef) => dropAndSendMessage(sender, app)
  }

  def load(sender:ActorRef, files: List[String], app: ActorRef, dropFlag: Boolean, isExternal: Boolean) = {
    try {
      var resultMessage = "old collection is dropped. " + files.mkString(", ").concat(" loaded.")
      if(dropFlag) coll.drop()
      else resultMessage = "old collection is not dropped. " + files.mkString(", ").concat(" added.")
      files.foreach(insert(isExternal))
      sender ! Done(resultMessage, app)
    } catch {
      case m: com.mongodb.MongoTimeoutException =>  sender ! Failed(m.toString + "\n" + Util.mongoTimeoutException,app)
      case s: org.xml.sax.SAXParseException =>  sender ! Failed(s.toString + "\n" + Util.thereAreErrorsInFiles,app)
      case n: java.lang.NullPointerException => sender ! Failed(n.toString + "\n" + Util.filesAreNotExist, app)
      case f: java.io.FileNotFoundException => sender ! Failed(f.toString + "\n" + Util.filesAreNotExist, app)
    }
  }

  def insert(isExternal:Boolean)(fileName: String):Unit =  {
    var xml = <contact></contact>
    if (isExternal) {
      xml = XML.loadFile(fileName)
    }
    else {
      val url = getClass().getClassLoader.getResource(fileName)
      xml = XML.load(url)
    }
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

  def findByName(sender: ActorRef, name: String, app: ActorRef) = {
    try {
      val iterator = coll.find(MongoDBObject("name" -> name))
      val result = iterator.toList.mkString("\n")
      /*def loop(acc: String):String ={
        if (iterator.hasNext) loop(acc.concat(iterator.next().toString + "\n"))
        else acc
      }
      val result = loop("")*/
      if (result.length > 0) sender ! Done(result, app)
      else sender ! Done("Contact is not found!", app)
    } catch {
      case m: com.mongodb.MongoTimeoutException =>  sender ! Failed(m.toString + "\n" + Util.mongoTimeoutException,app)
    }
  }

  def dropAndSendMessage(sender:ActorRef, app: ActorRef) = {
    try {
      coll.drop()
      sender ! Done("Contacts collection is dropped",app)
    } catch {
      case m: com.mongodb.MongoTimeoutException =>  sender ! Failed(m.toString + "\n" + Util.mongoTimeoutException,app)
    }
  }
}

object MongoService {
  case class Load(sender:ActorRef, files: List[String], app: ActorRef, dropFlag: Boolean, isExternal: Boolean)
  case class Find(sender:ActorRef, name: String, app: ActorRef)
  case class Drop(sender:ActorRef, app: ActorRef)
  case class Done(result: String, app: ActorRef)
  case class Failed(result: String, app: ActorRef)
}
