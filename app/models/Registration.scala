package models

import play.api.Play.current
import play.api.db.DB
import anorm._
import java.util.Date
import sun.security.jca.GetInstance
import scala.util.Random
import com.typesafe.plugin._

case class registerUserData(email: String,password: String) {}

object Register {
	
	def insert(newUser: registerUserData): Boolean = {
		var random= new Random(new Date().getTime().toInt)
    	var token = random.alphanumeric.take(45).mkString 
    	var result=1
    DB.withConnection { implicit connection =>
    	   	
      result = SQL(
      		"""
    		  INSERT INTO Users (email,password,created,level,status,confirmationToken)
    		  VALUES ({email}, {password}, {created}, {level}, {status}, {confirmationToken})
    		"""
      ).on(
        "email" -> newUser.email,
        "password" -> newUser.password,
        "created" -> new Date().getTime(), 
        "level" -> 1,
        "status" -> false,
        "confirmationToken" -> token
      ).executeUpdate()
    }
    				val mail = use[MailerPlugin].email
					mail.setSubject("mailer")
					mail.addRecipient("fernandez_alex_15@hotmail.com")
					mail.addBcc(List("Omar Lorenzo <olorenzo@outlook.com>",newUser.email):_*)
					mail.addFrom("Alex Fernandez <noreply@cachurek.com>")
					mail.send( newUser.email , "http://localhost:9000/confirm/"+token)
	(result == 1)
  }
}