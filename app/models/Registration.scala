package models

import play.api.Play.current
import play.api.db.DB
import anorm._
import java.util.Calendar
import sun.security.jca.GetInstance
import scala.util.Random

case class registerUserData(email: String,password: String) {}

object Register {
	
	def insert(newUser: registerUserData): Boolean = {
    DB.withConnection { implicit connection =>
    	var random= new Random(Calendar.getInstance().getTime().getSeconds())
    	var token = random.alphanumeric.take(45).mkString    	
      SQL(
      		"""
    		  INSERT INTO Users (email,password,created,level,status,confirmationToken)
    		  VALUES ({email}, {password}, {created}, {level}, {status}, {confirmationToken})
    		"""
      ).on(
        "email" -> newUser.email,
        "password" -> newUser.password,
        "created" -> Calendar.getInstance().getTime(), 
        "level" -> 1,
        "status" -> false,
        "confirmationToken" -> token
      ).executeUpdate() == 1
    }
  }
}