package com.challenge.vngrs

import scala.xml.Elem

object Util {
  val thereAreErrorsOrFilesAreNotExist: String = "There are errors in the files or the files are not exist!"
  val filesAreNotExist: String = "The files are not exist!"
  val thereAreErrorsInFiles = "There are errors in the files"
  val unknownCommand = "Wrong or Missing Command! You can enter help"
  val helpDescription = "Commands Example:".concat("\n1. load filename1 filename2 ...\n2. findByName name\n3. add filename1\n4. drop\n5. exit\n6. help\n")
  val welcomeMessage = helpDescription + "Please, You enter command:"
  val exitMessage = "Application is closing\n"
  val failedMessage = "Application is closing because there is an exception\n"
  val mongoTimeoutException = "Mongo is not running!"

  val help = """help""".r
  val exit = """exit""".r
  val drop = """drop""".r
  val load = """load\s((?:.+\s?)+)""".r
  val add = """add\s((?:.+\s?)+)""".r
  val find = """findByName\s((?:\w+\s?)+)""".r
  //val loadFiles = """load(\s+\w+)+?\s*""".r
}

object XMLHelper{
  def fromXMLToList(xml: Elem): List[Contact] = {
    val nodes = xml \ "contact"
    nodes.map{ contact =>
      val id = ( contact \ "id")
      val name = (contact \ "name")
      val lastName = (contact \ "lastName")
      val phones = (contact \ "phones")
      Contact(id.text.trim,name.text.trim,lastName.text.trim, phones.text.trim::Nil)
    }.toList
  }
}
