package controllers

import play.api.Play.current
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.db._


import models._
import views._

object Application extends Controller {

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password) => UsersActions.authenticate(email, password).isDefined
    })
  )

  /**
   * Login page.
   */
  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => Redirect(routes.Chart.grafico).withSession("email" -> user._1).flashing("info"->"Welcome, the graphic represents the actual results from people's voting.")
    )
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Chart.grafico).withNewSession.flashing(
      "success" -> "You've been logged out succesfully."
    )
  }


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

  /**
   * Provide security features
   */
  trait Secured {

    /**
     * Retrieve the connected user email.
     */
    private def username(request: RequestHeader) = request.session.get("email")

    /**
     * Redirect to login if the user in not authorized.
     */
    private def onUnauthorized(request: RequestHeader) = Results.Forbidden//Redirect(routes.Application.login)

    // --

    /**
     * Action for authenticated users.
     */
    def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }
  }
  

}