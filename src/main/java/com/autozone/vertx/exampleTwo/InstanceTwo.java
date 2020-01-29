package com.autozone.vertx.exampleTwo;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.cluster.infinispan.InfinispanClusterManager;

public class InstanceTwo {
	
	public static void main(String[] args) {
		ClusterManager clusterManager = new InfinispanClusterManager();
		
		VertxOptions options = new VertxOptions().setClusterManager(clusterManager);
		
		Vertx.clusteredVertx(options, resultHandler -> {
			if(resultHandler.succeeded()) {
				Vertx vertx = resultHandler.result();
				
				vertx.deployVerticle("com.autozone.vertx.exampleTwo.verticles.InventoryReporter", 
						new DeploymentOptions().setInstances(4)
											   .setConfig(new JsonObject().put("store_name", "Chihuahua Store")));				
			}
		});
	}

}
