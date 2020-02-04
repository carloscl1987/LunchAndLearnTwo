package com.autozone.vertx.exampleTwo.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class InventoryListener extends AbstractVerticle {
	@Override
	public void start() throws Exception {
		EventBus bus = vertx.eventBus();
		
		bus.<JsonObject>consumer("inventory.change", this::logInventoryChange);
	}
	
	public void logInventoryChange(Message<JsonObject> message) {
		System.out.println("[InventoryListener]: " + message.body().getString("message"));
	}
}
