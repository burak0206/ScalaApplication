package com.console.app

/**
 * Created by burakdagli on 1.08.15.
 */
case class Contact(id: String, name: String, lastName:String, phones: List[String]) {

  override def toString(): String = "{\n\tid: " + id + ",\n\tname: \"" + name +"\",\n\tlastName: \"" + lastName + "\",\n\tphones: [\"" + phones.mkString("\", \"") +"\"]\n}"

}
