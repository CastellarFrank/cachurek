package controllers


import play.api.Play.current
import play.api._
import play.api.mvc._
import play.api.db._
import Application.Secured


import play.api.db.DB
import anorm._


object Chart extends Controller with Secured{

  def grafico = Action {implicit request =>
    DB.withConnection { implicit connection =>
      val selectContados = SQL("Select * from votoscontados")

      // Transform the resulting Stream[Row] to a List[(String,String)]
      val votos = selectContados().map(row =>
        row[String]("name") -> row[Long]("cantidadVotos")
      ).toList

      Ok(views.html.chart.votos(votos))
    }
  }

}