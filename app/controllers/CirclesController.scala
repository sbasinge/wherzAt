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

  val Home = Redirect(routes.CirclesController.list())
  
  private val circleForm: Form[Circle] = Form(
    mapping(
      "name" -> nonEmptyText,
      "ownerId" -> of[Long])(Circle.apply)(Circle.unapply))

  def list = IsAuthenticated { username =>
    implicit request =>
      User.findByEmail(username).map { user =>
        val allCircles = Circle.all
        Logger.info(allCircles.size + " Circles found")
        Ok(html.circles.list(allCircles))
      }.getOrElse(Forbidden).flashing("error" -> "You must be signed in to view circles")
  }
    
    
  def save(id: Long) = Action { implicit request =>
    Logger.info("Circle about to be added")
    circleForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.circles.edit(formWithErrors, id)).flashing("error" -> "validation errors"),
      circle =>
        {
          Logger.info("Circle form folded successfully")
          inTransaction {
            Logger.info(" About to update circle %s".format(circle))
            AppDB.circles.update(n =>
              where(n.id === id)
                set (
                  n.name := circle.name,
                  n.ownerId := circle.ownerId))
            Home.flashing("success" -> "circle %s has been updated".format(circle.id))
          }
        })
  }

  def add() = Action { implicit request =>
    Logger.info("Circle about to be added")
    val newForm = this.circleForm.bindFromRequest()

    newForm.fold(
      hasErrors = { form =>
        Redirect(routes.CirclesController.create()).
          flashing(Flash(form.data) + ("error" -> "validation errors"))
      },
      success = {
        circle =>
          {
            Circle.add(circle)
            Logger.info("Circle added: %s".format(circle.id))
            Home.flashing("success" -> "Circle %s has been created".format(circle.id))
          }
      })
  }

  def create = Action { implicit request =>
    val form = if (flash.get("error").isDefined)
      this.circleForm.bind(flash.data)
    else
      this.circleForm

    Ok(html.circles.create(form))
  }

  def edit(id: Long) = Action { implicit request =>
    Circle.findById(id).map { circle =>
      Ok(html.circles.edit(circleForm.fill(circle), id))
    }.getOrElse(NotFound)
  }

  def delete(id: Long) = Action { implicit request =>
    Circle.delete(id)
    Home.flashing("success" -> "User has been deleted")
  }
    
}