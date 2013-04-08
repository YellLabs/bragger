package com.hibu.api.petstore.client.controllers;

import org.apache.axis2.AxisFault;
import org.apache.axis2.json.JSONMessageFormatter;
import org.apache.axis2.json.JSONOMBuilder;

import play.mvc.Controller;
import play.mvc.Result;

import com.hibu.api.petservice.PetserviceStub;
import com.hibu.api.petservice.models.AddPetRequestType;
import com.hibu.api.petservice.models.Category;
import com.hibu.api.petservice.models.GetPetByIdRequestType;
import com.hibu.api.petservice.models.Pet;
import com.hibu.api.petservice.models.Tag;

public class PetStoreClientController extends Controller {

	public static Result getPetById() throws Exception {
		
		try {
			PetserviceStub stub = new PetserviceStub();
			
			stub._getServiceClient().getAxisConfiguration().addMessageBuilder(
				"application/json", new JSONOMBuilder()
			);
			
			GetPetByIdRequestType request = new GetPetByIdRequestType();
			request.setPetId("1");
			
			Pet pet = stub.getPetById(request);
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
			
			PetserviceStub stub = new PetserviceStub("http://localhost:9000/pet.json");
			
			stub._getServiceClient().getAxisConfiguration().addMessageFormatter(
				"application/json", new JSONMessageFormatter()
			);
			stub._getServiceClient().getAxisConfiguration().addMessageBuilder(
				"application/json", new JSONOMBuilder()
			);
			
			AddPetRequestType request = new AddPetRequestType();
			Pet inputPet = new Pet();
			inputPet.setId(12345);
			inputPet.setName("myPet");
			inputPet.setStatus("status");

			//photoUrls
			inputPet.getPhotoUrls().add("http://my.photos.com/photo_1.jpg");
			inputPet.getPhotoUrls().add("http://my.photos.com/photo_2.jpg");
						
			// tags
			Tag tag1 = new Tag();
			tag1.setId(123);
			tag1.setName("sport");
			Tag tag2 = new Tag();
			tag2.setId(123);
			tag2.setName("cinema");
			inputPet.getTags().add(tag1);
			inputPet.getTags().add(tag2);
			
			// category
			Category cat = new Category();
			cat.setId(1212);
			cat.setName("bestof");
			inputPet.setCategory(cat);
			
			request.setBody(inputPet);
			
			com.hibu.api.petservice.models.Void response = stub.addPet(request);
			
			return ok("added Pet");
			
		} catch (AxisFault e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}
		
}