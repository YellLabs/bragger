package com.hibu.api.petstore.client.controllers;

import org.apache.axis2.AxisFault;

import play.mvc.Controller;
import play.mvc.Result;

import com.hibu.api.petservice.Petservice;
import com.hibu.api.petservice.PetserviceStub;
import com.hibu.api.petservice.models.AddPetRequestType;
import com.hibu.api.petservice.models.Category;
import com.hibu.api.petservice.models.GetPetByIdRequestType;
import com.hibu.api.petservice.models.Pet;
import com.hibu.api.petservice.models.Tag;
import com.hibu.api.petservice.models.UpdatePetRequestType;
import com.hibu.bragger.codegen.axis2.AxisClientFactory;

public class PetStoreClientController extends Controller {

	public static Result getPetById() throws Exception {
		
		try {

			// instantiate the api client auto generated from the wsdl.
			// the need for the second parameter is due to a bug in jettison 
			Petservice petService = new AxisClientFactory().getClient(Petservice.class, PetserviceStub.class, Pet.class);
			
			GetPetByIdRequestType request = new GetPetByIdRequestType();
			request.setPetId("1");
			
			Pet pet = petService.getPetById(request);
			System.out.println("returning Pet " + pet.getName());

			return ok("found Pet " + pet.getName());
			
		} catch (AxisFault e) {
			e.printStackTrace();
			throw e;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static Result addPet() throws Exception {
		
		try {
			
			// instantiate the api client auto generated from the wsdl.
			// the need for the second parameter is due to a bug in jettison
			Petservice petService = new AxisClientFactory().getClient(Petservice.class, PetserviceStub.class, Pet.class);
			
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
			
		} catch (AxisFault e) {
			e.printStackTrace();
			throw e;
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
			Petservice petService = new AxisClientFactory().getClient(Petservice.class, PetserviceStub.class, Pet.class);
			
			UpdatePetRequestType request = new UpdatePetRequestType();
			
			Pet inputPet = new Pet();
			inputPet.setId(1);
			inputPet.setName("Pet new name");
			inputPet.getPhotoUrls().add("http://my.photos.com/photo_1.jpg");
			request.setBody(inputPet);

			request.setBody(inputPet);
			petService.updatePet(request);
						
			return ok("updated Pet");
			
		} catch (AxisFault e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}
	
}
