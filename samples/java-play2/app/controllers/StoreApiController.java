package controllers;

import java.io.IOException;

import javax.ws.rs.PathParam;

import models.Order;
import play.mvc.Result;
import api.StoreData;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiParamImplicit;
import com.wordnik.swagger.annotations.ApiParamsImplicit;

@Api(value="/store", listingPath="/docs/api-docs.{format}/store", description="Operations about store")
public class StoreApiController extends BaseApiController {


	
	@ApiOperation(
		value="Find purchase order by ID", notes="For valid response try integer IDs with value <= 5. Anything above 5 or nonintegers will generate API errors", 
		responseClass="models.Order", 
		httpMethod="GET"
	)
	@ApiErrors({ 
		@ApiError(code=400, reason="Invalid ID supplied"),
		@ApiError(code=404, reason="Order not found") 
	})
	public static Result getOrderById(
			@ApiParam(value="ID of order to fetch", required=true) @PathParam("orderId") String orderId) {
		Order order = StoreData.findOrderById(ru.getLong(0, 10000, 0, orderId));
		if (null != order) {
			return JsonResponse(order);
		} else {
			return JsonResponse(new models.ApiResponse(404, "Order not found"), 404);
		}
	}


	
	@ApiOperation(
		value="Place an order for a pet", 
		responseClass="void", 
		httpMethod="POST"
	)
	@ApiErrors(
		@ApiError(code=400, reason="Invalid order")
	)
	@ApiParamsImplicit(
		@ApiParamImplicit(value="order placed for purchasing the pet", required=true, dataType="models.Order", paramType="body")
	)
	public static Result placeOrder() {
		Object o = request().body().asJson();
		try {
			Order order = (Order) BaseApiController.mapper.readValue(o.toString(), Order.class);
			StoreData.placeOrder(order);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return JsonResponse("");
	}



	@ApiOperation(
		value="Delete purchase order by ID", notes="For valid response try integer IDs with value < 1000. Anything above 1000 or nonintegers will generate API errors", 
		responseClass="void", 
		httpMethod="DELETE"
	)
	@ApiErrors({ 
		@ApiError(code=400, reason="Invalid ID supplied"),
		@ApiError(code=404, reason="Order not found") 
	})
	public static Result deleteOrder(
			@ApiParam(value="ID of the order that needs to be deleted", required=true) @PathParam("orderId") String orderId) {
		StoreData.deleteOrder(ru.getLong(0, 10000, 0, orderId));
		return JsonResponse("");
	}

}