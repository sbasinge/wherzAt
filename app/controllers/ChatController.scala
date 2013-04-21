package controllers

import play.api._
import play.api.mvc._

import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.libs.concurrent._
import play.api.Play.current

import models._

import akka.actor._
import akka.util.Timeout
import akka.actor._
import scala.concurrent.duration._

object ChatController extends Controller with Secured {

  /**
   * Display the chat room page.
   */
  def chatRoom = IsAuthenticated { username =>
    implicit request =>
    Ok(views.html.chatroom.chatroom("Chat with your Friends"))
  }

  /**
   * Handles the chat websocket.
   */
  def chat() = WebSocket.async[JsValue] { request  =>
  	val username = request.session.get("email").getOrElse("None")
    ChatRoom.join(username)
    
  }

}