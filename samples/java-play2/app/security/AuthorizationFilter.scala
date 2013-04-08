package security

import com.wordnik.swagger.play.ApiAuthorizationFilter
import play.Logger
import java.io.File
import java.util.ArrayList
import java.net.URLDecoder

import play.api.mvc.RequestHeader 

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._

class AuthorizationFilter extends ApiAuthorizationFilter {
  val methodSecurityAnotations = Map(
    "/user.json" -> false,
    "/pet.json" -> false,
    "/store.json" -> true)

  val classSecurityAnotations = Map(
    "GET:/pet.json/{petId}" -> false,
    "POST:/pet.json" -> true,
    "PUT:/pet.json" -> true,
    "GET:/pet.json/findByStatus" -> false,
    "GET:/pet.json/findByTags" -> false,


    "GET:/store.json/order/{orderId}" -> true,
    "DELETE:/store.json/order/{orderId}" -> true,
    "POST:/store.json/order" -> true,

    "POST:/user.json" -> false,
    "POST:/user.json/createWithArray" -> false,
    "POST:/user.json/createWithList" -> false,
    "PUT:/user.json/{username}" -> true,
    "DELETE:/user.json/{username}" -> true,
    "GET:/user.json/{username}" -> false,
    "GET:/user.json/login" -> false,
    "GET:/user.json/logout" -> false)

  var securekeyId = "special-key"
  var unsecurekeyId = "default-key"
 
  def authorize(apiPath: String, httpOperation: String)(implicit requestHeader: RequestHeader): Boolean = {
    Logger.debug("authorizing path " + apiPath)
  	val isAuthorized = if(requestHeader != null) {
	    if (isPathSecure(requestHeader.method.toUpperCase + ":" + apiPath, false)) {
	      if (apiKey == securekeyId) 
          return true
	      else 
          return false
	    }
	    else 
        true
  	} else {
  		Logger.debug("no header to authroize path " + apiPath)
  		false
  	}

    isAuthorized
  }

  def authorizeResource(apiPath: String)(implicit requestHeader: RequestHeader): Boolean = {
    Logger.debug("authorizing resource " + apiPath)
    if (isPathSecure(apiPath, true)) {
      if (apiKey == securekeyId) return true
      else return false
    } else
      true
  }

  private def apiKey()(implicit requestHeader: RequestHeader): String = {
    if(requestHeader == null) null
  	else requestHeader.queryString.get("api_key") match {
  		case Some(keySeq) => keySeq.head
  		case None => null
  	}
  }

  private def isPathSecure(apiPath: String, isResource: Boolean): Boolean = {
    Logger.debug("checking security on path " + apiPath + ", " + isResource)
    isResource match {
      case true => methodSecurityAnotations.getOrElse(apiPath, false)
      case false => classSecurityAnotations.getOrElse(apiPath, false)
    }
  }
}
