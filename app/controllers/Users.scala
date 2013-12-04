package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import models.UserData


object Users extends Controller{
  
  val userDataForm: Form[UserData] = Form(
	  mapping(
	    "email" -> text,
	    "password" -> tuple(
	        "main" -> text(minLength = 6), 
	        "confirm"->text
	        ).verifying(
	            "Passwords don't match.", passwords => passwords._1 == passwords._2
	        )
	  )
	  {
		  (email, passwords) => UserData(email, passwords._1) 
	  }	
	  {
		  user => Some(user.email, (user.password, ""))
	  }
	 
  )
  
  def newUserForm = Action {
    Ok(views.html.forms.addUserForm(userDataForm));
  }
  
  def submitNewUser = Action { implicit request =>
    userDataForm.bindFromRequest.fold(
      errors => BadRequest(views.html.forms.addUserForm(errors)),
      UserData => Ok(views.html.index("Agregado"))
    )
  }
}