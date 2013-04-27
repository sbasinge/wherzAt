package models

import play.api.db._
import play.api.Play.current
import org.squeryl._
import org.squeryl.PrimitiveTypeMode._

case class User(firstName: Option[String], lastName: Option[String], email: String, password: String, lat: Option[Float], lon: Option[Float], address: Option[String]) extends BaseEntity {

}

object User {
  val users = AppDB.users

  def all: List[User] = inTransaction(from(users) { s => select(s) }.toList)

  def add(newUser: User) {
    inTransaction {
      users.insert(newUser)
    }
  }

  def findById(id: Long): Option[User] = {
    inTransaction {
      return users.lookup(id)
    }
  }

  def delete(id: Long) = {
    inTransaction {
      users.delete(id)
    }
  }

  /**
   * Authenticate a User.
   */
  def authenticate(email: String, password: String): Option[User] = {
    transaction {
      from(users)(t => {
        where((t.email === email) and (t.password === password)).select(t)
      }).headOption
    }

  }

  def findByEmail(email: String): Option[User] = {
    transaction {
      from(users)(t => {
        where(t.email === email).select(t)
      }).headOption
    }

  }

}