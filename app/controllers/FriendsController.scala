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

object FriendsController extends Controller with Secured {

  val Home = Redirect(routes.FriendsController.list())

  private val userForm: Form[User] = Form(
    mapping(
      "firstName" -> optional(text),
      "lastName" -> optional(text),
      "email" -> nonEmptyText,
      "password" -> nonEmptyText)(User.apply)(User.unapply))

  //  def list = Action { implicit request =>
  //    println("Logged in user is "+request.session.get("email"))
  //    println("Secured user is "+Security.username)
  //    val allUsers = User.all
  //    Logger.info(allUsers.size + " Users found")
  //    Ok(html.users.list(allUsers))
  //  }

  def list = IsAuthenticated { username =>
    implicit request =>
      User.findByEmail(username).map { user =>
        val allUsers = User.all
        Logger.info(allUsers.size + " Users found")
        Ok(html.users.list(allUsers))
      }.getOrElse(Forbidden).flashing("error" -> "You must be signed in to view friends")
  }

  def save(id: Long) = Action { implicit request =>
    Logger.info("User about to be added")
    userForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.users.edit(formWithErrors, id)).flashing("error" -> "validation errors"),
      user =>
        {
          Logger.info("User form folded successfully")
          inTransaction {
            Logger.info(" About to update user %s".format(user))
            AppDB.users.update(n =>
              where(n.id === id)
                set (
                  n.firstName := user.firstName,
                  n.lastName := user.lastName,
                  n.email := user.email,
                  n.password := user.password))
            Home.flashing("success" -> "user %s has been updated".format(user.id))
          }
        })
  }

  def add() = Action { implicit request =>
    Logger.info("User about to be added")
    val newUserForm = this.userForm.bindFromRequest()

    newUserForm.fold(
      hasErrors = { form =>
        Redirect(routes.FriendsController.create()).
          flashing(Flash(form.data) + ("error" -> "validation errors"))
      },
      success = {
        user =>
          {
            User.add(user)
            Logger.info("User added: %s".format(user.id))
            Home.flashing("success" -> "User %s has been created".format(user.id))
          }
      })
  }

  def create = Action { implicit request =>
    val form = if (flash.get("error").isDefined)
      this.userForm.bind(flash.data)
    else
      this.userForm

    Ok(views.html.users.create(form))
  }

  def edit(id: Long) = Action { implicit request =>
    User.findById(id).map { user =>
      Ok(html.users.edit(userForm.fill(user), id))
    }.getOrElse(NotFound)
  }

  def delete(id: Long) = Action { implicit request =>
    User.delete(id)
    Home.flashing("success" -> "User has been deleted")
  }
}