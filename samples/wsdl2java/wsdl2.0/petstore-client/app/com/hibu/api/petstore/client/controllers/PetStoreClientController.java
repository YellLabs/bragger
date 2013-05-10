package com.hibu.api.petstore.client.controllers;

import java.net.URL;
import java.rmi.RemoteException;

import play.mvc.Controller;
import play.mvc.Result;

import com.hibu.apis.petstore.clients.pet.Petservice;
import com.hibu.apis.petstore.clients.pet.PetserviceStub;
import com.hibu.apis.petstore.clients.user.Userservice;
import com.hibu.apis.petstore.clients.user.UserserviceError;
import com.hibu.apis.petstore.clients.user.UserserviceStub;
import com.hibu.apis.petstore.models.AddPetRequestType;
import com.hibu.apis.petstore.models.Category;
import com.hibu.apis.petstore.models.ErrorResponse;
import com.hibu.apis.petstore.models.FindPetsByStatusRequestType;
import com.hibu.apis.petstore.models.GetPetByIdRequestType;
import com.hibu.apis.petstore.models.Pet;
import com.hibu.apis.petstore.models.PetList;
import com.hibu.apis.petstore.models.Tag;
import com.hibu.apis.petstore.models.UpdatePetRequestType;
import com.hibu.apis.petstore.models.UpdateUserRequestType;
import com.hibu.apis.petstore.models.User;
import com.hibu.bragger.codegen.axis2.AxisClientFactory;
import com.hibu.bragger.utils.Wsdl20Validator;

public class PetStoreClientController extends Controller {
	
	public static Result getPetById() throws Exception {
		
		try {

			// instantiate the api client auto generated from the wsdl.
			// the need for the second parameter is due to a bug in jettison 
			Petservice petService = new AxisClientFactory().getClient(Petservice.class, PetserviceStub.class, Pet.class, "petstore");
			
			GetPetByIdRequestType request = new GetPetByIdRequestType();
			request.setPetId("1");
			
			Pet pet = petService.getPetById(request);
			System.out.println("returning Pet " + pet.getName());

			return ok("found Pet " + pet.getName()); //return null; 

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static Result addPet() throws Exception {
		
		try {
			
			// instantiate the api client auto generated from the wsdl.
			// the need for the second parameter is due to a bug in jettison
			Petservice petService = new AxisClientFactory().getClient(Petservice.class, PetserviceStub.class, Pet.class, "petstore");
			
			AddPetRequestType request = new AddPetRequestType();
			
			Pet inputPet = new Pet();
			inputPet.setId(12345);
			inputPet.setName("myPet");
			inputPet.setStatus("status");

			//photoUrls
			inputPet.getPhotoUrls().add("http://my.photos.com/photo_1.jpg"); //inputPet.getPhotoUrls().add("http://my.photos.com/photo_2.jpg");
						
			// tags
			Tag tag1 = new Tag();
			tag1.setId(123);
			tag1.setName("sport");
			Tag tag2 = new Tag();
			tag2.setId(123);
			tag2.setName("cinema");
			inputPet.getTags().add(tag1); //inputPet.getTags().add(tag2);
			
			// category
			Category cat = new Category();
			cat.setId(1212);
			cat.setName("bestof");
			inputPet.setCategory(cat);
			
			request.setBody(inputPet);

			petService.addPet(request);
			
			return ok("added Pet");
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * renaming and adding a photoUrl to the Pet with id=1
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Result updatePet() throws Exception {
		
		try {
			
			// instantiate the api client auto generated from the wsdl.
			// the need for the second parameter is due to a bug in jettison
			Petservice petService = new AxisClientFactory().getClient(Petservice.class, PetserviceStub.class, Pet.class, "petstore");
			
			UpdatePetRequestType request = new UpdatePetRequestType();
			
			Pet inputPet = new Pet();
			inputPet.setId(1);
			inputPet.setName("Pet new name");
			inputPet.getPhotoUrls().add("http://my.photos.com/photo_1.jpg");
			request.setBody(inputPet);

			request.setBody(inputPet);
			petService.updatePet(request);
						
			return ok("updated Pet");

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static Result findPetsByStatus() throws Exception {
		
		String statusParam = "available";
		
		// instantiate the stub
		Petservice petservice = new AxisClientFactory().getClient(Petservice.class, PetserviceStub.class, Pet.class, "petstore");
		
		// prepare request
		FindPetsByStatusRequestType request = new FindPetsByStatusRequestType();
		request.setStatus(statusParam);
		
		// execute call and get response
		PetList response = petservice.findPetsByStatus(request);
		
		return ok("found " + response.getPet().size() + " pets by status = [" + statusParam + "]");
	}
	
	// ========================================================================
	
	/**
	 * @throws RemoteException 
	 * 
	 */
	public static Result updateUser() throws RemoteException {
		
		try {

			// instantiate the api client auto generated from the wsdl.
			// the need for the second parameter is due to a bug in jettison
			Userservice petService = new AxisClientFactory().getClient(Userservice.class, UserserviceStub.class, User.class, "petstore");
			
			UpdateUserRequestType request = new UpdateUserRequestType();
			User inputUser = new User();
			inputUser.setUsername("paolo");
			inputUser.setEmail("paolo.gentili@hibu.com");
			inputUser.setFirstName("paolo");
			inputUser.setId(12345678);
			inputUser.setLastName("gentili");
			inputUser.setPassword("mypassword");
			inputUser.setPhone("12345");
			inputUser.setUserStatus(2);

			// TODO try with an empty body (null user) !!!!
			request.setBody(inputUser);

			request.setUsername("paolo");

			petService.updateUser(request);
			
		} catch (UserserviceError e) {
			ErrorResponse error = e.getFaultMessage();
			return internalServerError(
				"thrown exception of type: " + e.getClass().getName() + "<br/>" +
				"CODE="+error.getCode() +"<br/>TYPE="+error.getType()+"<br/>MESSAGE="+error.getMessage()).as("text/html");
			
		} catch (InstantiationException e) {
			return internalServerError("thrown exception: " + e.getClass().getName() + ": "+ e.getMessage());
		}

		return ok("updated User");
	}
	
	// ========================================================================
	
	public static Result validateWsdl(String resourceName) {
		try {			
			Wsdl20Validator validator = new Wsdl20Validator(new URL("http://localhost:9000/docs/api-docs.wsdl/" + resourceName));
			return ok("validation result:" + validator.isValid());
		} catch (Exception e) {
			return internalServerError(e.getMessage());
		}
	}

}
