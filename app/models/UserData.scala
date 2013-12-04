package models

import play.api.Play.current
import play.api.db._

case class UserData(email:String, password:String){
    
  def emailExist(email:String):Boolean = { 
  	val conn = DB.getConnection("default", true)
  	try {
  		val stmt= conn.createStatement() 
  		val rs = stmt.executeQuery("SELECT * FROM Users")
  		while(rs.next()) {
  			if(rs.getString("email").equals(email))
  			  return true
  		} 
  	} finally {
  		conn.close()
  	}
  	return false
  	
  }
  
}

