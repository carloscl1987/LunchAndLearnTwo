package com.autozone.vertx.exampleTwo.verticles;

import java.util.UUID;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

public class InventoryReporter extends AbstractVerticle {
	
	private final String id = UUID.randomUUID().toString();
	
	@Override
	public void start() throws Exception {
		JsonObject properties = vertx.getOrCreateContext().config();
		
		String storeName = properties.getString("store_name","default store");
		
		EventBus bus = vertx.eventBus();
		
		vertx.setPeriodic(5000, handler -> {			
			int skuId = (int)Math.floor(Math.random() * 10000000);

			JsonObject message = new JsonObject().put("message", storeName + 
					" reports a inventory change of SKU" + skuId).put("id", id)
					.put("store_name", storeName);
			bus.publish("inventory.change", message);
		});
	}
}
