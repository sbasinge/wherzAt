import org.squeryl.adapters.{ H2Adapter, PostgreSqlAdapter, MSSQLServer }
import org.squeryl.internals.DatabaseAdapter
import org.squeryl.{ Session, SessionFactory }
import play.api.db.DB
import play.api.GlobalSettings
import play.api.Application
import org.squeryl.adapters.MySQLAdapter
//import models.AppDB
import org.squeryl.PrimitiveTypeMode.inTransaction

object Global extends GlobalSettings {
  override def onStart(app: Application) {

    SessionFactory.concreteFactory = app.configuration.getString("db.default.driver") match {
      case Some("org.h2.Driver") => Some(() => getSession(new H2Adapter, app))
      case Some("org.postgresql.Driver") => Some(() => getSession(new PostgreSqlAdapter, app))
      case Some("com.mysql.jdbc.Driver") => Some(() => getSession(new MySQLAdapter, app))
      case Some("com.microsoft.sqlserver.jdbc.SQLServerDriver") => Some(() => getSession(new MSSQLServer, app))
      case _ => sys.error("Database driver must be h2, postgres, mysql or sqlserver")
    }

  }

  def getSession(adapter: DatabaseAdapter, app: Application) = Session.create(DB.getConnection()(app), adapter)

  implicit def elvisOperator[T](alt: => T) = new {
    def ?:[A >: T](pred: A) = if (pred == null) alt else pred
  }
}

