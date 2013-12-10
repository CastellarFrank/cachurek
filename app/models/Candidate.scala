package models

import play.api.Play.current
import play.api.db.DB
import anorm._
import anorm.SqlParser._

case class Candidate (id:Int = -1, name:String, group:String,imagePath:String,imageFlagPath:String)


object CandidatesActions{	
	
  def Insert(newCandidate: Candidate): Long = {
    DB.withConnection { implicit connection =>
       val id: Long = SQL(
      		"""
    		  INSERT INTO Candidates (name, `group`, image_path, image_flag_path) 
    		  VALUES ({name}, {group}, {image_path}, {image_flag_path})
    		"""
      ).on(
        "name" -> newCandidate.name,
        "group" -> newCandidate.group,
        "image_path" -> newCandidate.imagePath, 
        "image_flag_path" -> newCandidate.imageFlagPath
      ).executeInsert().get
      
      id.longValue
    } 
  }
  
  def getAllCandidates(): Seq[Candidate] = {
    DB.withConnection { implicit connection =>
		val selectCandidates = SQL("Select * from Candidates")
		 
		val candidates = selectCandidates().map(
				candidate => Candidate(
				    id = candidate[Int]("id"),
				    name = candidate[String]("name"), 
				    group = candidate[String]("group"),
				    imagePath = candidate[String]("image_path"),
				    imageFlagPath = candidate[String]("image_flag_path")
				    )
		).toList
		
		candidates
    }
  }
  
  def newVoteForCandidate(cand:Int, user:Int): Boolean = {
    DB.withConnection { implicit connection =>
	    if(!userHasVoted(user)){
	    	SQL(
	    		"""
	    		  INSERT INTO candidate_votes (Users_id, Candidates_id)
	    		  VALUES ({user}, {cand})
	    		"""
	    	).on(
	    		"user" -> user,
	    		"cand" -> cand
	    	).executeUpdate() == 1
	    }else{
	      false
	    }
    }
  }
  
  def userHasVoted(user:Int): Boolean = {
    DB.withConnection { implicit connection =>
    	val count:Long = 
        SQL(
	        """
	    		SELECT count(*) FROM candidate_votes WHERE Users_id = {user}
	        """
	    ).on(
	    	"user" -> user
	    ).as(scalar[Long].single)
	    count!=0
    }
  }
}