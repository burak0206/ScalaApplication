package com.console.app

import akka.actor.{ActorRef, Props, Actor}
import scala.io.StdIn._

class CLICommandExecute extends Actor {
  import CLICommandExecute._

  val mongoService = context.actorOf(Props[MongoService],"mongoService")

  def receive = {
    case Listen(app) => executeCommand(readLine(), app)
    case MongoService.Done(result, app) => app ! Done(result)
    case MongoService.Failed(result, app) => app ! Failed(result)
  }

  def executeCommand(command: String, app: ActorRef) = {
    command match {
      case Util.help() => app ! Done(Util.helpDescription)
      case Util.exit() => app ! Exit
      case Util.drop() => mongoService ! MongoService.Drop(self,app)
      case Util.load(fileNames) => mongoService ! MongoService.Load(self, fileNames.split("\\s+").map(_.trim).toList, app, true, false)
      case Util.add(fileNames) => mongoService ! MongoService.Load(self, fileNames.split("\\s+").map(_.trim).toList, app, false, false)
      case Util.find(name) => mongoService ! MongoService.Find(self, name, app)
      case Util.loadExtFile(fileNames) => mongoService ! MongoService.Load(self, fileNames.split("\\s+").map(_.trim).toList, app, true, true)
      case Util.addExtFile(fileNames) => mongoService ! MongoService.Load(self, fileNames.split("\\s+").map(_.trim).toList, app, false, true)
      case _ => sender ! Done(Util.unknownCommand)
    }
  }
}

object CLICommandExecute {
  case class Listen(app: ActorRef)
  case object Exit
  case class Done(result: String)
  case class Failed(result: String)
}