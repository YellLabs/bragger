package controllers;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import models.Pet;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.mvc.Result;
import api.PetData;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiParamImplicit;
import com.wordnik.swagger.annotations.ApiParamsImplicit;

@Api(value = "/pet", listingPath = "/docs/api-docs.{format}/pet", description = "Operations about pets")
public class PetApiController extends BaseApiController {



	@ApiOperation(value = "Find pet by ID", responseClass = "models.Pet", notes = "Returns a pet when ID < 10. ID > 10 or nonintegers will simulate API error conditions")
	@ApiErrors({
		@ApiError(code=400, reason="Invalid ID supplied"),
		@ApiError(code=404, reason="Pet not found")
	})
	public static Result getPetById(@ApiParam(value="ID of pet that needs to be fetched", allowableValues="range[1,5]", required=true) @PathParam("petId") String petId) {
		Logger.info(PetApiController.class.getSimpleName()+ ": getPetById: "+ petId+ " - start");
		return JsonResponse(PetData.getPetbyId(Long.parseLong(petId)));
	}


	
	@ApiOperation(value="Add a new pet to the store")
	@ApiErrors({
		@ApiError(code=405, reason="Invalid input")
	})
	@ApiParamsImplicit({
		@ApiParamImplicit(value="Pet object that needs to be added to the store", required=true, dataType="models.Pet", paramType="body")
	})
	public static Result addPet() {
		Logger.info(PetApiController.class.getSimpleName()+ ": addPet - start");
		JsonNode json = request().body().asJson();
		try {
			Pet pet = (Pet) BaseApiController.mapper.readValue(json.toString(), Pet.class);
			PetData.addPet(pet);
			Logger.info(PetApiController.class.getSimpleName()+ " added Pet with id = " + pet.getId());
			return JsonResponse(pet);
			
		} catch (IOException e) {
			e.printStackTrace();
			return internalServerError(e.getMessage());
		}
	}


	
	@ApiOperation(value="Update an existing pet")
	@ApiErrors({ 
		@ApiError(code=400, reason="Invalid ID supplied"),
		@ApiError(code=404, reason="Pet not found"),
		@ApiError(code=405, reason="Validation exception") 
	})
	@ApiParamsImplicit({ 
		@ApiParamImplicit(value="Pet object that needs to be updated in the store", required=true, dataType="models.Pet", paramType="body") 
	})
	public static Result updatePet() {
		Logger.info(PetApiController.class.getSimpleName()+ ": updatePet - start");
		Object o = request().body().asJson();
		try {
			Pet pet = (Pet) BaseApiController.mapper.readValue(o.toString(), Pet.class);
			PetData.addPet(pet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return JsonResponse("SUCCESS");
	}


	
	@ApiOperation(value="Finds Pets by status", responseClass="models.Pet", multiValueResponse=true, notes="Multiple status values can be provided with comma separated strings")
	@ApiErrors({@ApiError(code=400, reason="Invalid status value")})
	public static Result findPetsByStatus(@ApiParam(value="Status values that need to be considered for filter", required=true, defaultValue="available", allowableValues="available,pending,sold", allowMultiple=true) @QueryParam("status") String status) {
		Logger.info(PetApiController.class.getSimpleName() + ": findPetByStatus - start");
		List<Pet> pets = PetData.findPetByStatus(status);
		Logger.info(PetApiController.class.getSimpleName() + ": found " + pets.size() + " pets");
		return JsonResponse(pets);
	}


	
	@ApiOperation(value = "Finds Pets by tags", responseClass = "models.Pet", multiValueResponse = true, notes = "Multiple tags can be provided with comma separated strings. Use tag1, tag2, tag3 for testing."
	)
	@ApiErrors({
		@ApiError(code=400, reason="Invalid tag value")
	})
	public static Result findPetsByTags(@ApiParam(value = "Tags to filter by", required = true, allowMultiple = true) @QueryParam("tags") String tags) {
		Logger.info(PetApiController.class.getSimpleName()+ ": findPetByTag - start");
		return JsonResponse(PetData.findPetByTags(tags));
	}

}
