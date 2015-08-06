package com.console.app

import akka.actor.{Props, Actor}

class Application extends Actor{

  val cliCommandExecute = context.actorOf(Props[CLICommandExecute],"CLICommandExecute")
  println(Util.welcomeMessage)
  cliCommandExecute ! CLICommandExecute.Listen(self)

  def receive = {
    case CLICommandExecute.Done(result: String) => {
      println(result)
      sender ! CLICommandExecute.Listen(self)
    }
    case CLICommandExecute.Failed(result: String) => {
      println(result)
      println(Util.failedMessage)
      context.stop(self)
    }
    case CLICommandExecute.Exit => {
      println(Util.exitMessage)
      context.stop(self)
    }
  }
}
