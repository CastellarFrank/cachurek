package models

import play.api.Play.current
import play.api.db.DB
import anorm._
import java.util.Calendar
import anorm.SqlParser._
import scala.language.postfixOps
import java.util.Date

case class UserData(email:String, password:String = "", level:Int, 
    status:Boolean, id:Int = 0, modified: Option[Date] = null, created: Option[Date] = null)

object UsersActions{

  val parseo = {
        get[String]("USERS.email") ~
        get[String]("USERS.password") ~
        get[Int]("USERS.level") ~
        get[Boolean]("USERS.status") map {
        case email~password~level~status => UserData(email, password, level, status)
      }
    }

	  def newUserInsert(newUser: UserData): Boolean = {
	    DB.withConnection { implicit connection =>
	      SQL(
	      		"""
	    		  INSERT INTO Users (email, password, created, modified, level, status)
	    		  VALUES ({email}, {password}, {created}, {modified}, {level}, {status})
	    		"""
	      ).on(
	        "email" -> newUser.email,
	        "password" -> newUser.password,
	        "created" -> Calendar.getInstance().getTime(), 
	        "modified" -> Calendar.getInstance().getTime(),
	        "level" -> newUser.level,
	        "status" -> newUser.status
	      ).executeUpdate() == 1
	    }
	  }
	  
	  def getEmailByID(id:Int):String = {
	    if(id== -1){
	      ""
	    }else{
	    	DB.withConnection { implicit connection =>
		      SQL(
		        """
		    		SELECT email FROM USERS WHERE id = {id}
			    """
			   ).on(
			     "id" -> id
			   ).as(scalar[String].single)
		    }
	    }
	  }
	  
	  def getIdByEmail(email:String):Int = {
	    if(email==null || email.isEmpty()){
	      -1
	    }else{
	    	DB.withConnection { implicit connection =>
		       val num = SQL(
		        """
		    		SELECT id FROM USERS WHERE email = {email}
			    """
			   ).on(
			     "email" -> email
			   ).as(scalar[Int].singleOpt)
			   if(num==None){
			     -1
			   }else{
			     num.get
			   }
		    }
	    }
	  }
	  
	  def userExist(email:String):Boolean = {
	     DB.withConnection { implicit connection =>
	       val count:Long = SQL(
		        """
		    		SELECT count(*) FROM USERS WHERE email = {email}
		        """
		     ).on(
		        "email" -> email
		     ).as(scalar[Long].single)
		     
		     count !=0
		     
	     }
	  }



  /**
   * Authenticate a User.
   */
  def authenticate(email: String, password: String): Option[UserData] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
         select * from Users where
         email = {email} and password = {password}
        """
      ).on(
          'email -> email,
          'password -> password
        ).as(UsersActions.parseo.singleOpt)
    }
  }



	  
	  def getAllUsers():Seq[UserData] = {
	    DB.withConnection { implicit connection =>
	       val selectUsers = SQL("Select email, level, status, id, created, modified FROM Users")
			val users = selectUsers().map(
					user => UserData(
					    email = user[String]("email"), 
					    level = user[Int]("level"),
					    status = user[Boolean]("status"),
					    id = user[Int]("id"),
					    created = user[Option[java.util.Date]]("created"),
					    modified = user[Option[java.util.Date]]("modified")
					  )
			).toList
			users
	     }
	  }
}
