package models

import org.squeryl._
import org.squeryl.dsl._
import org.squeryl.PrimitiveTypeMode._

class CircleSubscription(val circleId: Long, val userId: Long) extends KeyedEntity[CompositeKey2[Long,Long]]  {
	def id = compositeKey(circleId, userId)
}

object AppDB extends Schema {
  val users = table[User]
  val circles = table[Circle] 

//  val circleMembers =
//    oneToManyRelation(circles, users).
//      via((circle, user) => circle.id === user.circle.id)

  val ownerToCircles = 
    oneToManyRelation(users, circles).via((user,circle) => user.id === circle.ownerId)
    
  val circleSubscriptions =
    manyToManyRelation(circles, users).
    via[CircleSubscription]((c,s,cs) => (cs.userId === s.id, c.id === cs.circleId))
}