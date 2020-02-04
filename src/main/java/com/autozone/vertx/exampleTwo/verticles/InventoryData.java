package com.autozone.vertx.exampleTwo.verticles;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class InventoryData extends AbstractVerticle{
	
	private Map<String,Integer> inventoryData = new HashMap<String,Integer>();
	
	@Override
	public void start() throws Exception {
		EventBus bus = vertx.eventBus();
		
		bus.<JsonObject>consumer("inventory.change", this::updateData);
		bus.<JsonObject>consumer("inventory.quantity", this::getInventoryQuantity);
	}
	
	public void updateData (Message<JsonObject> message) {
		if (null == message.body()) {
			message.fail(500, "No message found");
		}
		
		String storeName = message.body().getString("store_name");
		
		int currentInventory = 0;
		
		if (inventoryData.containsKey(storeName)) {
			currentInventory = inventoryData.get(storeName);
		} 
		
		inventoryData.put(storeName, ++currentInventory);
		
		System.out.println("[inventory.change]: " + inventoryData);
	}
	
	public void getInventoryQuantity(Message<JsonObject> message) {
		Set<String> keys = inventoryData.keySet();
	
		JsonArray replyArray = new JsonArray();
		
		keys.forEach(key -> {
			JsonObject replyObject = new JsonObject();
			replyObject.put("store_name", key);
			replyObject.put("quantity", inventoryData.get(key));
			
			replyArray.add(replyObject);
		});
		
		System.out.println("[inventory.quantity]: " + inventoryData);
		
		message.reply(new JsonObject().put("result", replyArray));
	}
}
