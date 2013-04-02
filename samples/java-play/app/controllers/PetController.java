package controllers;

import java.util.List;

import play.Logger;
import play.mvc.Controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.sample.data.PetData;
import com.wordnik.swagger.sample.model.Pet;
import com.wordnik.swagger.sample.resource.JavaRestResourceUtil;

@Api(value = "/pet", description = "Operations about pets")
public class PetController extends BaseApiController {
	
	private static PetData petData = new PetData();
	
	@ApiOperation(value = "Find pet by ID", notes = "Returns a pet when ID < 10. " + "ID > 10 or nonintegers will simulate API error conditions", responseClass = "com.wordnik.swagger.sample.model.Pet")
	@ApiErrors({
			@ApiError(code = 400, reason = "Invalid ID supplied"),
			@ApiError(code = 404, reason = "Pet not found")
	})
	public static void getPetById(@ApiParam(value = "ID of pet that needs to be fetched", required = true) String petId) 
	{
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

	@ApiOperation(value = "Add a new pet to the store")
	@ApiErrors(@ApiError(code = 405, reason = "Invalid input"))
	public static void addPet(@ApiParam(value = "Pet object that needs to be added to the store", required = true) Pet pet) {
		PetData petData = new PetData();
		petData.addPet(pet);
		renderText("");
	}

	@ApiOperation(value = "Update an existing pet")
	@ApiErrors({
			@ApiError(code = 400, reason = "Invalid ID supplied"), 
			@ApiError(code = 404, reason = "Pet not found"), 
			@ApiError(code = 405, reason = "Validation exception")
	})
	public static void updatePet(@ApiParam(value = "Pet object that needs to be added to the store", required = true) Pet pet) {
		PetData petData1 = new PetData();
		Logger.info("updatePet");
		petData.addPet(pet);
		renderText("");
	}

	@ApiOperation(value = "Finds Pets by status", notes = "Multiple status values can be provided with comma seperated strings", responseClass = "com.wordnik.swagger.sample.model.Pet", multiValueResponse = true)
	@ApiErrors(@ApiError(code = 400, reason = "Invalid status value"))
	public static void	findPetsByStatus(@ApiParam(value = "Status values that need to be considered for filter", required = true, defaultValue = "available", allowableValues = "available,pending,sold", allowMultiple = true) String status) 
	{
		Logger.info("findPetsByStatus");
		List<Pet> o = petData.findPetByStatus(status);
		if (returnXml()) {
			renderXml(marshallToXml(o)); 
		}
		else {
			renderJSON(o);
		}
	}

	@ApiOperation(value = "Finds Pets by tags", notes = "Muliple tags can be provided with comma seperated strings. Use tag1, tag2, tag3 for testing.", responseClass = "com.wordnik.swagger.sample.model.Pet", multiValueResponse = true)
	@ApiErrors(@ApiError(code = 400, reason = "Invalid tag value"))
	@Deprecated
	public static void findPetsByTags(@ApiParam(value = "Tags to filter by", required = true, allowMultiple = true) String tags) {
		//PetData petData = new PetData();
		List<Pet> o = petData.findPetByTags(tags);
		if (returnXml()) {
			marshallToXml(o); 
		}
		else {
			renderJSON(o);
		}
	}

}
