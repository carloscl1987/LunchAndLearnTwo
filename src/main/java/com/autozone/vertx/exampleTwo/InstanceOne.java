package com.autozone.vertx.exampleTwo;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.cluster.infinispan.InfinispanClusterManager;

public class InstanceOne {
	
	public static void main(String[] args) {
		
		ClusterManager clusterManager = new InfinispanClusterManager();
		
		VertxOptions options = new VertxOptions().setClusterManager(clusterManager);
		
		Vertx.clusteredVertx(options, handler -> {
			if (handler.succeeded()) {
				Vertx vertx = handler.result();

				vertx.deployVerticle("com.autozone.vertx.exampleTwo.verticles.InventoryReporter", 
						new DeploymentOptions().setInstances(4)
											   .setConfig(new JsonObject().put("store_name", "Memphis Store")));
				
				vertx.deployVerticle("com.autozone.vertx.exampleTwo.verticles.InventoryListener");
				
				vertx.deployVerticle("com.autozone.vertx.exampleTwo.verticles.InventoryData");
				
				vertx.deployVerticle("com.autozone.vertx.exampleTwo.verticles.HttpServer",
						new DeploymentOptions().setConfig(new JsonObject().put("server_port", 8080)));
			}
		});
	}

}
