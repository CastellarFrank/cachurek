package models

import play.api.Play.current
import play.api.db.DB
import anorm._

case class Candidate (name:String, group:String, imagePath:String, imageFlagPath:String)


object CandidatesActions{	
	
  def newCandidateInsert(newCandidate: Candidate): Boolean = {
    DB.withConnection { implicit connection =>
      SQL(
      		"""
    		  INSERT INTO candidates (name, `group`, image_path, image_flag_path) 
    		  VALUES ({name}, {group}, {image_path}, {image_flag_path})
    		"""
      ).on(
        "name" -> newCandidate.name,
        "group" -> newCandidate.group,
        "image_path" -> newCandidate.imagePath, 
        "image_flag_path" -> newCandidate.imageFlagPath
      ).executeUpdate() == 1
      
    } 
  }
  
  def getAllCandidates(): Seq[Candidate] = {
    DB.withConnection { implicit connection =>
		val selectCandidates = SQL("Select * from Candidates")
		 
		val candidates = selectCandidates().map(
				candidate => new Candidate(
				    candidate[String]("name"), 
				    candidate[String]("group"),
				    candidate[String]("image_path"),
				    candidate[String]("image_flag_path")
				    )
		).toList
		
		candidates
    }
  }
  
}