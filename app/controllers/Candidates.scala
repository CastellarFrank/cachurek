package controllers

import play.api.Play.current
import play.api.mvc.Controller
import models.{Candidate,CandidatesActions}
import play.api.data._
import play.api.data.Forms._
import play.api.mvc.Action
import play.api.mvc.HttpConnection

object Candidates extends Controller{
	
	def addCandidateForm = Form(
			mapping(
					"name" -> nonEmptyText,
					"group" -> nonEmptyText
					)
					((name, group) => Candidate(name, group,"",""))
					(data => Some(data.name,data.group)
					)
			)
	
	def index = TODO

	def addForm = Action {implicit request =>
		Ok(views.html.Candidates.add(addCandidateForm))
	}
	
	def add = Action { implicit request =>
		  addCandidateForm.bindFromRequest.fold(
		  error => BadRequest(views.html.Candidates.add(error)),
		  newCandidate => {
					var body = request.body.asMultipartFormData
			        var imageFile = body.get.file("image")
					var flagFile = body.get.file("flag")
			        if (imageFile.isDefined && flagFile.isDefined) {
			        	
			        	val id: Long = CandidatesActions.Insert(newCandidate)
			            imageFile.map { picture =>
						    import java.io.File
						    val contentType = picture.contentType.toString()
						    picture.ref.moveTo(new File(s"./public/images/$id"))
						}
						flagFile.map { picture =>
							import java.io.File
						    val contentType = picture.contentType.toString()
						    picture.ref.moveTo(new File(s"./public/images/$id"+"_flag"))
						}
						Redirect(routes.Candidates.index)
			        }
			        else
			        {
			        	BadRequest("File upload error, Please make sure you selected a correct file");
			        }
		  }
		)
	}
	
	def editForm(id: Int) = 
	{
		Ok(views.html.Candidates.add(addCandidateForm))
	}
	def edit = TODO
	def delete(id: Int) = TODO
	
	
}