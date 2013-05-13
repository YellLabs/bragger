package controllers;

import javax.ws.rs.PathParam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiParamImplicit;
import com.wordnik.swagger.annotations.ApiParamsImplicit;
import com.wordnik.swagger.sample.data.UserData;
import com.wordnik.swagger.sample.model.User;
import com.wordnik.swagger.sample.resource.JavaRestResourceUtil;

@Api(value = "/user", listingPath = "/api-docs.{format}/user", description = "Operations about user")
public class UserController extends BaseApiController {

	
	@ApiOperation(
		value = "Create user", 
		notes = "This can only be done by the logged in user."
	)
	@ApiParamsImplicit({
		@ApiParamImplicit(value= "Created user object", required = true, dataType = "com.wordnik.swagger.sample.model.User", paramType = "body")
	})
	public static void createUser(/*@ApiParam(value = "Created user object", required = true) User user*/ String body) {
		
		Gson gson = new GsonBuilder().create();
		User user = gson.fromJson(body, User.class);
		
		UserData userData = new UserData();
		JavaRestResourceUtil ru = new JavaRestResourceUtil();
		userData.addUser(user);
		renderJSON(user);
	}

	
	@ApiOperation(
		value = "Upsert a user", 
		notes = "This can only be done by the logged in user."
	)
	@ApiErrors({
		@ApiError(code = 400, reason = "Invalid user supplied"), 
		@ApiError(code = 404, reason = "User not found")
	})
	@ApiParamsImplicit({
		@ApiParamImplicit(name = "username", value = "name that need to be updated", required = true, dataType = "string", paramType = "path"),
		@ApiParamImplicit(value = "Updated user object", required = true, dataType = "com.wordnik.swagger.sample.model.User", paramType = "body")
	})
	public static void updateUser(String username, String body) 
	{
		Gson gson = new GsonBuilder().create();
		User user = gson.fromJson(body, User.class);
		
		UserData userData = new UserData();
		JavaRestResourceUtil ru = new JavaRestResourceUtil();
		userData.addUser(user);
		renderJSON(user);
	}

	
	@ApiOperation(
		value = "Delete user", 
		notes = "This can only be done by the logged in user."
	)
	@ApiErrors({
		@ApiError(code = 400, reason = "Invalid username supplied"), 
		@ApiError(code = 404, reason = "User not found")
	})
	public static void deleteUser(
		@ApiParam(value = "The name that needs to be deleted", required = true) @PathParam("username") String username) 
	{
		UserData userData = new UserData();
		JavaRestResourceUtil ru = new JavaRestResourceUtil();
		userData.removeUser(username);
		renderJSON("");
	}

	
	@ApiOperation(
		value = "Get user by user name", 
		responseClass = "com.wordnik.swagger.sample.model.User"
	)
	@ApiErrors({
		@ApiError(code = 400, reason = "Invalid username supplied"), 
		@ApiError(code = 404, reason = "User not found")
	})
	public static void getUserByName(
		@ApiParam(value = "The name that needs to be fetched. Use user1 for testing. ", required = true) @PathParam("username") String username) 
	{
		UserData userData = new UserData();
		JavaRestResourceUtil ru = new JavaRestResourceUtil();
		User user = userData.findUserByName(username);
		if (null != user) {
			if (returnXml()) renderXml(marshallToXml(user)); else renderJSON(user);
		} else {
			notFound();
		}
	}

	
	@ApiOperation(
		value = "Logs user into the system", 
		responseClass = "string"
	)
	@ApiErrors(
		@ApiError(code = 400, reason = "Invalid username and password combination")
	)
	public static void loginUser(
			@ApiParam(name = "username", value = "The user name for login", required = true) String username, 
			@ApiParam(name = "password", value = "The password for login in clear text", required = true) String password) 
	{
		String o = "logged in user session:" + System.currentTimeMillis();
		if (returnXml()) renderXml(marshallToXml(o)); else renderJSON(o);
	}

	
	@ApiOperation(
		value = "Logs out current logged in user session"
	)
	public static void logoutUser() {
		renderJSON("");
	}
	
}
