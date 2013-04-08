import play.*;
import play.mvc.Action;
import play.mvc.Result;
import play.mvc.Http.Request;
import play.mvc.Http.RequestHeader;

import java.lang.reflect.Method;

public class Global extends GlobalSettings {

	@Override
	public Action onRequest(Request request, Method actionMethod) {
		Logger.info("");
		Logger.info("");
		Logger.info("============= received a request: " + request.toString());
	   return super.onRequest(request, actionMethod);
	}

	@Override
	public Result onHandlerNotFound(RequestHeader request) {
		// removing annoying message
		if (!"/favicon.ico".equals(request.uri())) {
			Logger.info("");
			Logger.info("");
			Logger.info("============= route not found: " + request.toString());
		}
		return super.onHandlerNotFound(request);
	}
}