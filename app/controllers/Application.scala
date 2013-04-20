package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._

object Application extends Controller with Secured {

  def index = Action { implicit request =>
    def username = request.session.get("email")
    if (username == null)
      Ok(views.html.index("Your new application is ready.")).flashing("success" -> "You need to login or register")
    else
      Ok(views.html.index("Welcome " + username)).flashing("success" -> "Here we are")
  }

  private val registrationForm: Form[User] = Form(
    mapping(
      "firstName" -> optional(text),
      "lastName" -> optional(text),
      "email" -> nonEmptyText,
      "password" -> nonEmptyText)(User.apply)(User.unapply))

  def register = Action { implicit request =>
    val form = if (flash.get("error").isDefined)
      this.registrationForm.bind(flash.data)
    else
      this.registrationForm

    Ok(views.html.register(form))

  }

  // -- Authentication

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text) verifying ("Invalid email or password", result => result match {
        case (email, password) => User.authenticate(email, password).isDefined
      }))

  /**
   * Login page.
   */
  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => Redirect(request.session.get("access_uri").getOrElse(routes.Application.index.toString)).withSession("email" -> user._1))
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action { implicit request =>
    Redirect(routes.Application.index).withNewSession.flashing(
      "success" -> "You've been logged out")
  }

  // -- Javascript routing

  //  def javascriptRoutes = Action { implicit request =>
  //    import routes.javascript._
  //    Ok(
  //      Routes.javascriptRouter("jsRoutes")(
  //        Projects.add, Projects.delete, Projects.rename,
  //        Projects.addGroup, Projects.deleteGroup, Projects.renameGroup,
  //        Projects.addUser, Projects.removeUser, Tasks.addFolder, 
  //        Tasks.renameFolder, Tasks.deleteFolder, Tasks.index,
  //        Tasks.add, Tasks.update, Tasks.delete
  //      )
  //    ).as("text/javascript") 
  //  }

}

/**
 * Provide security features
 */
trait Secured {

  /**
   * Retrieve the connected user email.
   */
  private def username(request: RequestHeader) = request.session.get("email")

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login).withSession("access_uri" -> request.uri)

  // --

  /**
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }

  /**
   * Check if the connected user is a member of this project.
   */
  //  def IsMemberOf(project: Long)(f: => String => Request[AnyContent] => Result) = IsAuthenticated { user => request =>
  //    if(Project.isMember(project, user)) {
  //      f(user)(request)
  //    } else {
  //      Results.Forbidden
  //    }
  //  }

  /**
   * Check if the connected user is a owner of this task.
   */
  //  def IsOwnerOf(task: Long)(f: => String => Request[AnyContent] => Result) = IsAuthenticated { user => request =>
  //    if(Task.isOwner(task, user)) {
  //      f(user)(request)
  //    } else {
  //      Results.Forbidden
  //    }
  //  }

}

