package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import models.UserData
import models.UsersActions
import models.CandidatesActions
import models.UserData


object Users extends Controller{
  
  val userDataForm: Form[UserData] = Form(
	  mapping(
	    "basics" -> tuple(
	        "id" -> default(number, -1),
	        "email" -> nonEmptyText
	        ).verifying(
	        		"Email already exists", 
	        		valor => if(valor._2.equals(UsersActions.getEmailByID(valor._1))){
	        		  true
	        		}else{
	        		  !UsersActions.userExist(valor._2)
	        		}
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
		  (basics, passwords, level, status) => UserData(
				id = basics._1,
				email = basics._2, 
		      	password = passwords._1,
		      	level = if(level.equals("Mortal")) 1 else 2,
		      	status = status.equals("Activa")
		  ) 
	  }	
	  {
		  user => Some(
		      (user.id, user.email),
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