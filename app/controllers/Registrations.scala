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
import play.api.db.DB

object Registrations extends Controller {
	
	def registerForm = Form(
			mapping(
					"email" -> (email).verifying("Email alredy exists!!",email => !Register.userExist(email)),
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
	
	def confirmRegistration(email: String,token: String)= Action { implicit request =>
		var result=0
		DB.withConnection { implicit connection =>
			result = SQL(
	      		"""
	    		  UPDATE Users SET status=true
	    		  WHERE email={email} and confirmationToken={confirmationToken}
	    		"""
	      ).on(
	        "email" -> email,
	        "confirmationToken" -> token
	      ).executeUpdate
	    }
		if(result>0) {
			Redirect(routes.Application.login).flashing("info"->"Your information was successfully completed")
		}
		else {
			Redirect(routes.Application.login).flashing("error"->"The data you provided is incorrect")
		}
				
	}
	
}