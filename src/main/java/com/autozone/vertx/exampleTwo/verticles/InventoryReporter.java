package com.autozone.vertx.exampleTwo.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

public class InventoryReporter extends AbstractVerticle {
	@Override
	public void start() throws Exception {
		JsonObject properties = vertx.getOrCreateContext().config();
		
		String storeName = properties.getString("store_name","default store");
		
		EventBus bus = vertx.eventBus();
		
		vertx.setPeriodic(5000, handler -> {
			JsonObject message = new JsonObject().put("message", storeName + 
					" reports a inventory change of SKU" + (int)Math.floor(Math.random() * 10000000));
			bus.publish("inventory-change", message);
		});
	}
}
