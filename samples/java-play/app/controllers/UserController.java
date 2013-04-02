package controllers;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.sample.data.UserData;
import com.wordnik.swagger.sample.model.User;
import com.wordnik.swagger.sample.resource.JavaRestResourceUtil;

@Api(value = "/user", description = "Operations about user")
public class UserController extends BaseApiController {
	
  private static UserData userData = new UserData();
  private static JavaRestResourceUtil ru = new JavaRestResourceUtil();

  @ApiOperation(value = "Create user", notes = "This can only be done by the logged in user.")
  public static void createUser(@ApiParam(value = "Created user object", required = true) User user) {
    userData.addUser(user);
    renderText("");
  }

  @ApiOperation(value = "Updated user", notes = "This can only be done by the logged in user.")
  @ApiErrors({
	  @ApiError(code = 400, reason = "Invalid user supplied"), 
	  @ApiError(code = 404, reason = "User not found")
  })
  public static void updateUser(@ApiParam(value = "name that need to be deleted", required = true) String username, @ApiParam(value = "Updated user object", required = true) User user) {
    userData.addUser(user);
    renderText("");
  }

  @ApiOperation(value = "Delete user", notes = "This can only be done by the logged in user.")
  @ApiErrors({
	  @ApiError(code = 400, reason = "Invalid username supplied"), 
	  @ApiError(code = 404, reason = "User not found")
  })
  public static void deleteUser(@ApiParam(value = "The name that needs to be deleted", required = true) String username) {
    userData.removeUser(username);
    renderText("");
  }

  @ApiOperation(value = "Get user by user name", responseClass = "com.wordnik.swagger.sample.model.User")
  @ApiErrors({
	  @ApiError(code = 400, reason = "Invalid username supplied"), 
	  @ApiError(code = 404, reason = "User not found")
  })
  public static void getUserByName(@ApiParam(value = "The name that needs to be fetched. Use user1 for testing. ", required = true) String username) {
    User user = userData.findUserByName(username);
    if (null != user) {
      if (returnXml()) renderXml(marshallToXml(user)); else renderJSON(user);
    } else {
      notFound();
    }
  }

  @ApiOperation(value = "Logs user into the system", responseClass = "String")
  @ApiErrors(@ApiError(code = 400, reason = "Invalid username/password supplied"))
  public static void loginUser(@ApiParam(value = "The user name for login", required = true) String username, @ApiParam(value = "The password for login in clear text", required = true) String password) {
    String o = "logged in user session:" + System.currentTimeMillis();
    if (returnXml()) renderXml(marshallToXml(o)); else renderJSON(o);
  }

  @ApiOperation(value = "Logs out current logged in user session")
  public static void logoutUser() {
    renderText("");
  }
}