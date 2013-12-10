package controllers

import play.api.Play.current
import play.api.mvc.Controller
import play.api.data._
import play.api.data.Forms._
import models.{registerUserData,Register}
import play.api.mvc.Action
import anorm._
import java.util.Calendar
import com.typesafe.plugin._

object Registrations extends Controller {
	
	def registerForm = Form(
			mapping(
					"email" -> email,
					"password" -> tuple(
							"main" -> nonEmptyText,
							"confirm" -> nonEmptyText
							).verifying("Passwords don't match", passwords => passwords._1==passwords._2)
					)
			((email, password) => registerUserData(email, password._1))
			(data => Some(data.email,(data.password,"")))
	)
	
	def add = Action { implicit request =>
		registerForm.bindFromRequest.fold(
		  error => BadRequest(views.html.Registrations.register(error)),
		  newRegister => {
		  			Register.insert(newRegister)
		  			Redirect(routes.Application.login).flashing("info"->"Now you are available to login.")
		  }
		)		
	}
	
	def register =  Action { implicit request =>
		Ok(views.html.Registrations.register(registerForm))
	}
	
	def confirmRegistration(email: String)= Action { implicit request =>
		Ok(views.html.Registrations.register(registerForm))
	}
	
}