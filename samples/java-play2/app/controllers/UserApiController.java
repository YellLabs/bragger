package controllers;

import java.io.IOException;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import models.User;
import play.mvc.Result;
import api.UserData;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiParamImplicit;
import com.wordnik.swagger.annotations.ApiParamsImplicit;

@Api(value = "/user", listingPath = "/docs/api-docs.{format}/user", description = "Operations about user")
public class UserApiController extends BaseApiController {



	@ApiOperation(value = "Create user", notes = "This can only be done by the logged in user.")
	@ApiParamsImplicit(@ApiParamImplicit(name = "body", value = "Created user object", required = true, dataType = "models.User", paramType = "body"))
	public static Result createUser() {
		Object o = request().body().asJson();
		try {
			User user = (User) BaseApiController.mapper.readValue(o.toString(), User.class);
			UserData.addUser(user);
			return JsonResponse(user);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return JsonResponse(new models.ApiResponse(400, "Invalid input"));
	}



	@ApiOperation(value = "Creates list of users with given input array", responseClass = "void")
	@ApiParamsImplicit(
		@ApiParamImplicit(name = "body", value = "List of user object", required = true, dataType = "Array[models.User]", paramType = "body")
	)
	public static Result createUsersWithArrayInput() {
		Object o = request().body().asJson();
		try {
			User[] users = BaseApiController.mapper.readValue(o.toString(), User[].class);
			for (User user : users) {
				UserData.addUser(user);
			}
			return JsonResponse(users);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return JsonResponse(new models.ApiResponse(400, "Invalid input"));
	}



	@ApiOperation(value = "Creates list of users with given list input", responseClass = "void")
	@ApiParamsImplicit(
		@ApiParamImplicit(name = "body", value = "List of user object", required = true, dataType = "List[models.User]", paramType = "body")
	)
	public static Result createUsersWithListInput() {
		Object o = request().body().asJson();
		try {
			User[] users = BaseApiController.mapper.readValue(o.toString(), User[].class);
			for (User user : users) {
				UserData.addUser(user);
			}
			return JsonResponse(users);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return JsonResponse(new models.ApiResponse(400, "Invalid input"));
	}



	@ApiOperation(value = "Fetch a user", notes = "This can only be done by the logged in user.")
	@ApiErrors({ @ApiError(code = 400, reason = "Invalid username supplied"),
		@ApiError(code = 404, reason = "User not found") 
	})
	@ApiParamsImplicit({
		@ApiParamImplicit(name = "username", value = "name that need to be updated", required = true, dataType = "String", paramType = "path"),
		@ApiParamImplicit(name = "body", value = "Updated user object", required = true, dataType = "models.User", paramType = "body") 
	})
	public static Result updateUser(String username) {
		Object o = request().body().asJson();
		try {
			User user = (User) BaseApiController.mapper.readValue(o.toString(), User.class);
			UserData.addUser(user);
			return JsonResponse(user);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return JsonResponse(new models.ApiResponse(400, "Invalid input"));
	}



	@ApiOperation(value = "Delete user", notes = "This can only be done by the logged in user.")
	@ApiErrors({ 
		@ApiError(code = 400, reason = "Invalid username supplied"),
		@ApiError(code = 404, reason = "User not found") 
	})
	public static Result deleteUser(
			@ApiParam(value = "The name that needs to be deleted", required = true) String username) {
		UserData.removeUser(username);
		return ok();
	}



	@ApiOperation(value = "Get user by user name", responseClass = "models.User")
	@ApiErrors({ 
		@ApiError(code = 400, reason = "Invalid username supplied"),
		@ApiError(code = 404, reason = "User not found") 
	})
	public static Result getUserByName(
			@ApiParam(value = "The name that needs to be fetched. Use user1 for testing. ", required = true) @PathParam("username") String username) {
		User user = UserData.findUserByName(username);
		if (user != null)
			return JsonResponse(user);
		else
			return JsonResponse(new models.ApiResponse(400, "Invalid input"));
	}



		@ApiOperation(value = "Logs user into the system", responseClass = "String")
	@ApiErrors(
		@ApiError(code = 400, reason = "Invalid username and password combination")
	)
	public static Result loginUser(
			@ApiParam(value = "The user name for login", required = true) @QueryParam("username") String username,
			@ApiParam(value = "The password for login in clear text", required = true) @QueryParam("password") String password) {
		return JsonResponse("logged in user session:" + System.currentTimeMillis());
	}



	@ApiOperation(value = "Logs out current logged in user session")
	public static Result logoutUser() {
		return ok();
	}

}
