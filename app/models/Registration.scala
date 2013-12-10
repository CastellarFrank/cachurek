package models

import play.api.Play.current
import play.api.db.DB
import anorm._
import anorm.SqlParser._
import sun.security.jca.GetInstance
import scala.util.Random
import com.typesafe.plugin._
import java.util.Date
import java.text.SimpleDateFormat

case class registerUserData(email: String,password: String) {}

object Register {
	
	def insert(newUser: registerUserData): Boolean = {
		var random= new Random(new Date().getTime().toInt)
    	var token = random.alphanumeric.take(45).mkString 
    	var dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		var date = new Date();
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
        "created" -> dateFormat.format(date), 
        "level" -> 1,
        "status" -> false,
        "confirmationToken" -> token
      ).executeUpdate()
    }
    				val mail = use[MailerPlugin].email
					mail.setSubject("Please Confirm Your Registration")
					mail.addRecipient("fernandez_alex_15@hotmail.com")
					mail.addBcc(List("Omar Lorenzo <olorenzo@outlook.com>",newUser.email):_*)
					mail.addFrom("Alex Fernandez <noreply@cachurek.com>")
					mail.send( newUser.email , "http://cachurek.herokuapp.com/confirm/"+newUser.email+"/"+token)
	(result == 1)
  }
	
	def userExist(email:String):Boolean = {
	     DB.withConnection { implicit connection =>
	       val count:Long = SQL(
		        """
		    		SELECT count(*) FROM Users WHERE email = {email}
		        """
		     ).on(
		        "email" -> email
		     ).as(scalar[Long].single)
		     
		     count !=0
		     
	     }
	  }
}