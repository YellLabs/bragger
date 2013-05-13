package controllers;

import javax.ws.rs.PathParam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiParamImplicit;
import com.wordnik.swagger.annotations.ApiParamsImplicit;
import com.wordnik.swagger.sample.data.StoreData;
import com.wordnik.swagger.sample.model.Order;
import com.wordnik.swagger.sample.model.Pet;
import com.wordnik.swagger.sample.resource.JavaRestResourceUtil;

@Api(value = "/store", listingPath = "/api-docs.{format}/store", description = "Operations about store")
public class PetStoreController extends BaseApiController {

	@ApiOperation(
		value = "Find purchase order by ID", 
		responseClass = "com.wordnik.swagger.sample.model.Order",
		notes = "For valid response try integer IDs with value <= 5. " + "Anything above 5 or nonintegers will generate API errors" 
	)
	@ApiErrors({
		@ApiError(code = 400, reason = "Invalid ID supplied"),
		@ApiError(code = 404, reason = "Order not found")
	})
	public static void getOrderById(@ApiParam(value = "ID of pet that needs to be fetched", required = true) @PathParam("petId") String orderId) {
		StoreData storeData = new StoreData();
		JavaRestResourceUtil ru = new JavaRestResourceUtil();
		Order order = storeData.findOrderById(ru.getLong(0, 10000, 0, orderId));
		if (null != order) {
						if (returnXml()) {
				renderXml(marshallToXml(order)); 
			}
			else {
				renderJSON(order);
			}
		} else {
			notFound();
		}
	}

	@ApiOperation(
		value = "Place an order for a pet", 
		responseClass = "com.wordnik.swagger.sample.model.Order"
	)
	@ApiParamsImplicit({
		@ApiParamImplicit(value= "order placed for purchasing the pet", required = true, dataType = "com.wordnik.swagger.sample.model.Order", paramType = "body")
	})
	@ApiErrors({
		@ApiError(code = 400, reason = "Invalid Order")
	})
	public static void placeOrder(String body) {
		
		Gson gson = new GsonBuilder().create();
		Order order = gson.fromJson(body, Order.class);
		
		StoreData storeData = new StoreData();
		JavaRestResourceUtil ru = new JavaRestResourceUtil();
		storeData.placeOrder(order);
		renderJSON("");
	}

	@ApiOperation(
		value = "Delete purchase order by ID", 
		notes = "For valid response try integer IDs with value < 1000. " + "Anything above 1000 or nonintegers will generate API errors"
	)
	@ApiErrors({
		@ApiError(code = 400, reason = "Invalid ID supplied"), 
		@ApiError(code = 404, reason = "Order not found")
	})
	public static void deleteOrder(@ApiParam(value = "ID of the order that needs to be deleted", required = true) @PathParam("petId") String orderId) {
		StoreData storeData = new StoreData();
		JavaRestResourceUtil ru = new JavaRestResourceUtil();
		storeData.deleteOrder(ru.getLong(0, 10000, 0, orderId));
		renderJSON("");
	}

}
