package models

import play.api.db._
import play.api.Play.current
import org.squeryl._
import org.squeryl.dsl._
import org.squeryl.PrimitiveTypeMode._

case class Circle(name: String, ownerId: Long) extends BaseEntity
{
	lazy val owner: ManyToOne[User] = AppDB.ownerToCircles.right(this)
	lazy val members = AppDB.circleSubscriptions.left(this)

}

object Circle {
    val circles = AppDB.circles

  def all: List[Circle] = inTransaction(from(circles) { s => select(s) }.toList)

  def add(newCircle: Circle) {
    inTransaction {
      circles.insert(newCircle)
    }
  }

  def findById(id: Long): Option[Circle] = {
    inTransaction {
      return circles.lookup(id)
    }
  }

  def delete(id: Long) = {
    inTransaction {
      circles.delete(id)
    }
  }

}