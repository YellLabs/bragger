package controllers;

import java.util.List;

import javax.ws.rs.PathParam;

import play.Logger;
import play.mvc.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiParamImplicit;
import com.wordnik.swagger.annotations.ApiParamsImplicit;
import com.wordnik.swagger.sample.data.PetData;
import com.wordnik.swagger.sample.model.Pet;
import com.wordnik.swagger.sample.model.User;
import com.wordnik.swagger.sample.resource.JavaRestResourceUtil;

@Api(value = "/pet", listingPath = "/api-docs.{format}/pet", description = "Operations about pets")
public class PetController extends BaseApiController {
	
	// commented out, see https://github.com/YellLabs/bragger/issues/1
	//private static PetData petData = new PetData();
	
	@ApiOperation(
		value = "Find pet by ID", 
		responseClass = "com.wordnik.swagger.sample.model.Pet",
		notes = "Returns a pet when ID < 10. " + "ID > 10 or nonintegers will simulate API error conditions" 
	)
	@ApiErrors({
			@ApiError(code = 400, reason = "Invalid ID supplied"),
			@ApiError(code = 404, reason = "Pet not found")
	})
	public static void getPetById(@ApiParam(value = "ID of pet that needs to be fetched", required = true) @PathParam("petId") String petId) 
	{
		Logger.info("getPetById");
		PetData petData = new PetData();
		Pet pet = petData.getPetbyId(new JavaRestResourceUtil().getLong(0, 100000, 0, petId));
		if (null != pet) {
			if (returnXml()) {
				renderXml(marshallToXml(pet)); 
			}
			else {
				renderJSON(pet);
			}
		} else {
			 notFound("Pet not found for " + petId);
		}
	}

	@ApiOperation(
		value = "Add a new pet to the store"
	)
	@ApiParamsImplicit({
		@ApiParamImplicit(value= "Pet object that needs to be added to the store", required = true, dataType = "com.wordnik.swagger.sample.model.Pet", paramType = "body")
	})
	@ApiErrors(
		@ApiError(code = 405, reason = "Invalid input")
	)
	public static void addPet(String body) {
		
		Logger.info("addPet");
		
		Gson gson = new GsonBuilder().create();
		Pet pet = gson.fromJson(body, Pet.class);
		
		PetData petData = new PetData();
		petData.addPet(pet);
		renderJSON(pet);
	}

	@ApiOperation(
		value = "Update an existing pet"
	)
	@ApiParamsImplicit({
		@ApiParamImplicit(value= "Pet object that needs to be added to the store", required = true, dataType = "com.wordnik.swagger.sample.model.Pet", paramType = "body")
	})
	@ApiErrors({
		@ApiError(code = 400, reason = "Invalid ID supplied"), 
		@ApiError(code = 404, reason = "Pet not found"), 
		@ApiError(code = 405, reason = "Validation exception")
	})
	public static void updatePet(String body) {
		
		Logger.info("updatePet");

		Gson gson = new GsonBuilder().create();
		Pet pet = gson.fromJson(body, Pet.class);
		
		PetData petData = new PetData();
		petData.addPet(pet);
		renderJSON(pet);
	}

	@ApiOperation(
		value = "Finds Pets by status", 
		responseClass = "com.wordnik.swagger.sample.model.Pet", 
		multiValueResponse = true,
		notes = "Multiple status values can be provided with comma seperated strings"
	)
	@ApiErrors(
		@ApiError(code = 400, reason = "Invalid status value")
	)
	public static void findPetsByStatus(
		@ApiParam(name = "status", value = "Status values that need to be considered for filter", required = true, defaultValue = "available", allowableValues = "available,pending,sold", allowMultiple = true) String status) 
	{
		Logger.info("findPetsByStatus");
		PetData petData = new PetData();
		List<Pet> o = petData.findPetByStatus(status);
		if (returnXml()) {
			renderXml(marshallToXml(o)); 
		}
		else {
			renderJSON(o);
		}
	}

	@ApiOperation(
		value = "Finds Pets by tags", 
		responseClass = "com.wordnik.swagger.sample.model.Pet", 
		multiValueResponse = true,
		notes = "Multiple tags can be provided with comma separated strings. Use tag1, tag2, tag3 for testing."
	)
	@ApiErrors(
		@ApiError(code = 400, reason = "Invalid tag value")
	)
	@Deprecated
	public static void findPetsByTags(@ApiParam(name = "tags", value = "Tags to filter by", required = true, allowMultiple = true) String tags) {
		PetData petData = new PetData();
		List<Pet> o = petData.findPetByTags(tags);
		if (returnXml()) {
			marshallToXml(o); 
		}
		else {
			renderJSON(o);
		}
	}

}
