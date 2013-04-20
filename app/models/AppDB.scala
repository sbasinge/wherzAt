package models

import org.squeryl.Schema

object AppDB extends Schema {
  val users = table[User]

//  val addressToCourses =
//    oneToManyRelation(address, course).
//      via((address, course) => address.id === course.address.id)

      
}