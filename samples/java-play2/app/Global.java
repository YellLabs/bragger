import java.lang.reflect.Method;

import org.codehaus.jackson.node.ObjectNode;

import play.GlobalSettings;
import play.Logger;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http.Request;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;

public class Global extends GlobalSettings {

	@Override
	public Action onRequest(Request request, Method actionMethod) {
		Logger.info("");
		Logger.info("");
		Logger.info("======= received a request: " + request.toString());
		Logger.info("======= request body: " + request.body());
		Logger.info("");
		Logger.info("");
	   return super.onRequest(request, actionMethod);
	}

	@Override
	public Result onHandlerNotFound(RequestHeader request) {
		// removing annoying message about the favicon not found
		if (!"/favicon.ico".equals(request.uri())) {
			Logger.info("");
			Logger.info("");
			Logger.info("======= route not found: " + request.toString());
		}
		return super.onHandlerNotFound(request);
	}

	@Override
	public Result onBadRequest(RequestHeader request, String arg1) {
		Logger.info("");
		Logger.info("");
		Logger.info("======= onBadRequest: " + request.toString());
		Logger.info("");
		Logger.info("");
		return super.onBadRequest(request, arg1);
	}

	@Override
	public Result onError(RequestHeader request, Throwable arg1) {
		
		Logger.info("");
		Logger.info("");
		Logger.info("======= onError: " + request.toString());
		Logger.info("");
		Logger.info("");
		
		ObjectNode result = Json.newObject();
		result.put("type", arg1.getCause().getClass().getName());
		result.put("message", arg1.getMessage());

		result.put("code", 500);
		return play.mvc.Results.internalServerError(result);

		//result.put("code", 404);
		//return play.mvc.Results.notFound(result);

		//result.put("code", 400);
		//return play.mvc.Results.badRequest(result);
		
		//return super.onError(request, arg1);
	}
	
	
	
}