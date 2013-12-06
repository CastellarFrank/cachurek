package controllers

import play.api.Play.current
import play.api._
import play.api.mvc._
import play.api.db._

object Application extends Controller {

  def index = Action {
  	var outString="Numer is"
  	val conn = DB.getConnection("default", true)
  	try {
  		val stmt= conn.createStatement()
  		val rs = stmt.executeQuery("SELECT * FROM Users")
  		while(rs.next()) {
  			outString += rs.getString("email")
  		}
  	} finally {
  		conn.close()
  	}	
  		
    Ok(views.html.index(outString))
  }
  
  

}