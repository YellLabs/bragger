package controllers;

import java.io.IOException;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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

@Api(value = "/user", listingPath = "/api-docs.{format}/user", description = "Operations about user")
public class UserApiController extends BaseApiController {
	static UserData userData = new UserData();

	@POST
	@ApiOperation(value = "Create user", notes = "This can only be done by the logged in user.")
	@ApiParamsImplicit(@ApiParamImplicit(name = "body", value = "Created user object", required = true, dataType = "User", paramType = "body"))
	public static Result createUser() {
		Object o = request().body().asJson();
		try {
			User user = (User) BaseApiController.mapper.readValue(o.toString(),
					User.class);
			userData.addUser(user);
			return JsonResponse(user);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return JsonResponse(new models.ApiResponse(400, "Invalid input"));
	}

	@POST
	@Path("/createWithArray")
	@ApiOperation(value = "Creates list of users with given input array", responseClass = "void")
	@ApiParamsImplicit(@ApiParamImplicit(name = "body", value = "List of user object", required = true, dataType = "Array[User]", paramType = "body"))
	public static Result createUsersWithArrayInput() {
		Object o = request().body().asJson();
		try {
			User[] users = BaseApiController.mapper.readValue(o.toString(),
					User[].class);
			for (User user : users) {
				userData.addUser(user);
			}
			return JsonResponse(users);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return JsonResponse(new models.ApiResponse(400, "Invalid input"));
	}

	@POST
	@Path("/createWithList")
	@ApiOperation(value = "Creates list of users with given list input", responseClass = "void")
	@ApiParamsImplicit(@ApiParamImplicit(name = "body", value = "List of user object", required = true, dataType = "List[User]", paramType = "body"))
	public static Result createUsersWithListInput() {
		Object o = request().body().asJson();
		try {
			User[] users = BaseApiController.mapper.readValue(o.toString(),
					User[].class);
			for (User user : users) {
				userData.addUser(user);
			}
			return JsonResponse(users);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return JsonResponse(new models.ApiResponse(400, "Invalid input"));
	}

	@GET
	@Path("/{username}")
	@ApiOperation(value = "Fetch a user", notes = "This can only be done by the logged in user.")
	@ApiErrors({ @ApiError(code = 400, reason = "Invalid username supplied"),
			@ApiError(code = 404, reason = "User not found") })
	@ApiParamsImplicit({
			@ApiParamImplicit(name = "username", value = "name that need to be updated", required = true, dataType = "String", paramType = "path"),
			@ApiParamImplicit(name = "body", value = "Updated user object", required = true, dataType = "User", paramType = "body") })
	public static Result updateUser(String username) {
		Object o = request().body().asJson();
		try {
			User user = (User) BaseApiController.mapper.readValue(o.toString(),
					User.class);
			userData.addUser(user);
			return JsonResponse(user);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return JsonResponse(new models.ApiResponse(400, "Invalid input"));
	}

	@DELETE
	@Path("/{username}")
	@ApiOperation(value = "Delete user", notes = "This can only be done by the logged in user.")
	@ApiErrors({ @ApiError(code = 400, reason = "Invalid username supplied"),
			@ApiError(code = 404, reason = "User not found") })
	public static Result deleteUser(
			@ApiParam(value = "The name that needs to be deleted", required = true) String username) {
		userData.removeUser(username);
		return ok();
	}

	@GET
	@Path("/{username}")
	@ApiOperation(value = "Get user by user name", responseClass = "models.User")
	@ApiErrors({ @ApiError(code = 400, reason = "Invalid username supplied"),
			@ApiError(code = 404, reason = "User not found") })
	public static Result getUserByName(
			@ApiParam(value = "The name that needs to be fetched. Use user1 for testing. ", required = true) @PathParam("username") String username) {
		User user = userData.findUserByName(username);
		if (user != null)
			return JsonResponse(user);
		else
			return JsonResponse(new models.ApiResponse(400, "Invalid input"));
	}

	@GET
	@Path("/login")
	@ApiOperation(value = "Logs user into the system", responseClass = "String")
	@ApiErrors(@ApiError(code = 400, reason = "Invalid username and password combination"))
	public static Result loginUser(
			@ApiParam(value = "The user name for login", required = true) @QueryParam("username") String username,
			@ApiParam(value = "The password for login in clear text", required = true) @QueryParam("password") String password) {
		return JsonResponse("logged in user session:" + System.currentTimeMillis());
	}

	@GET
	@Path("/logout")
	@ApiOperation(value = "Logs out current logged in user session")
	public static Result logoutUser() {
		return ok();
	}
}