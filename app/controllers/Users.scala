package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import models.UserData
import models.UsersActions
import models.CandidatesActions


object Users extends Controller{
  
  val userDataForm: Form[UserData] = Form(
	  mapping(
	    "email" -> text.verifying(
	        "Email already exists", email => !UsersActions.userExist(email)
	        ),
	    "password" -> tuple(
	        "main" -> text(minLength = 6), 
	        "confirm"->text
	        ).verifying(
	            "Passwords don't match.", passwords => passwords._1 == passwords._2
	        ),
	     "level" -> text,
	     "status" -> text
	  )
	  {
		  (email, passwords, level, status) => UserData(
				email, 
		      	passwords._1,
		      	if(level.equals("Mortal")) 1 else 2,
		      	status.equals("Activa")
		  ) 
	  }	
	  {
		  user => Some(
		      user.email,
		      (user.password, ""), 
		      if(user.level==1) "Mortal" else "Inmortal", 
		      if(user.status) "Activa" else "Inactiva"
		  )
	  }
  )
  
  def newUserForm = Action {
    Ok(views.html.forms.addUserForm(userDataForm));
  }
  
  def submitNewUser = Action { implicit request =>
    userDataForm.bindFromRequest.fold(
      errors => BadRequest(views.html.forms.addUserForm(errors)),
      UserData => {
    	  UsersActions.newUserInsert(UserData)
    	  Redirect(routes.Users.maintenance)
        }
    )
  }
  
  def maintenance = Action {implicit request =>
    Ok(views.html.maintenance.usersMaintenance(UsersActions.getAllUsers))
  }
  
  def vote = Action{implicit request =>
    Ok(views.html.vote(CandidatesActions.getAllCandidates))
  }
}