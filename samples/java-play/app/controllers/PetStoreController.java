package controllers;

import java.util.List;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.sample.data.StoreData;
import com.wordnik.swagger.sample.model.Order;
import com.wordnik.swagger.sample.resource.JavaRestResourceUtil;

@Api(value = "/store", description = "Operations about store")
public class PetStoreController extends BaseApiController {

  private static StoreData storeData = new StoreData();
  private static JavaRestResourceUtil ru = new JavaRestResourceUtil();

  @ApiOperation(value = "Find purchase order by ID", notes = "For valid response try integer IDs with value <= 5. " + "Anything above 5 or nonintegers will generate API errors", responseClass = "com.wordnik.swagger.sample.model.Order")
  @ApiErrors({
    @ApiError(code = 400, reason = "Invalid ID supplied"),
    @ApiError(code = 404, reason = "Order not found")
  })
  public static void getOrderById(@ApiParam(value = "ID of pet that needs to be fetched", required = true) String orderId) {
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

  @ApiOperation(value = "Place an order for a pet", responseClass = "com.wordnik.swagger.sample.model.Order")
  @ApiErrors({ @ApiError(code = 400, reason = "Invalid Order")})
  public static void placeOrder(@ApiParam(value = "order placed for purchasing the pet", required = true) Order order) {
    storeData.placeOrder(order);
    renderText("");
  }

  @ApiOperation(value = "Delete purchase order by ID", notes = "For valid response try integer IDs with value < 1000. " + "Anything above 1000 or nonintegers will generate API errors")
  @ApiErrors({
    @ApiError(code = 400, reason = "Invalid ID supplied"), 
    @ApiError(code = 404, reason = "Order not found")
  })
  public static void deleteOrder(@ApiParam(value = "ID of the order that needs to be deleted", required = true) String orderId) {
    storeData.deleteOrder(ru.getLong(0, 10000, 0, orderId));
    renderText("");
  }

}
