package models

import play.api.Play.current
import play.api.db.DB
import anorm._
import java.util.Calendar

case class UserData(email:String, password:String, level:Int, status:Boolean)

object UsersActions{
	  def newUserInsert(newUser: UserData): Boolean = {
	    DB.withConnection { implicit connection =>
	      SQL(
	      		"""
	    		  INSERT INTO Users (email, password, created, level, status)
	    		  VALUES ({email}, {password}, {created}, {level}, {status})
	    		"""
	      ).on(
	        "email" -> newUser.email,
	        "password" -> newUser.password,
	        "created" -> Calendar.getInstance().getTime(), 
	        "level" -> newUser.level,
	        "status" -> newUser.status
	      ).executeUpdate() == 1
	  }
  }
  
}
