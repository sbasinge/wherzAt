package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data.format.Formats
import play.api.data.format.Formatter
import play.api.data.validation.Constraints._
import play.api.i18n.Messages

import org.squeryl.PrimitiveTypeMode._

import views._
import models._


object CirclesController extends Controller with Secured {

  def index = IsAuthenticated { username =>
    implicit request =>
      User.findByEmail(username).map { user =>
        Ok(html.circles.index("Maintain Your Circles of Friends"))
//        NotImplemented
      }.getOrElse(Forbidden).flashing("error" -> "You must be signed in to view circles")
  }
}