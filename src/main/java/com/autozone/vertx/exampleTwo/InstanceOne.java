package com.autozone.vertx.exampleTwo;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class InstanceOne {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();

		vertx.deployVerticle("com.autozone.vertx.exampleTwo.verticles.InventoryReporter", 
				new DeploymentOptions().setInstances(4)
									   .setConfig(new JsonObject().put("store_name", "Memphis Store")));
		
		
		vertx.deployVerticle("com.autozone.vertx.exampleTwo.verticles.InventoryListener");
		
		vertx.deployVerticle("com.autozone.vertx.exampleTwo.verticles.HttpServer",
				new DeploymentOptions().setConfig(new JsonObject().put("server_port", 8080)));
	}

}
