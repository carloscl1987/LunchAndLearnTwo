package com.autozone.vertx.exampleTwo.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.TimeoutStream;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class HttpServer extends AbstractVerticle {
	
	@Override
	public void start() throws Exception {
		
		JsonObject properties = vertx.getOrCreateContext().config(); 
		
		//vertx.createHttpServer().requestHandler(this::handler).listen(properties.getInteger("server_port"));
		Router router = Router.router(vertx);
		
		router.get("/").handler(req -> {
			req.response().sendFile("index.html");
		});
		
		router.get("/sse").handler(this::sse);
		
		vertx.createHttpServer().requestHandler(router).listen(properties.getInteger("server_port"));
	}
	
	public void sse(RoutingContext request) {
		HttpServerResponse response = request.response();
		
		response.headers().add("Content-Type", "text/event-stream");
		response.headers().add("Cache-Control", "no-cache");
		response.setChunked(true);
		
		MessageConsumer<JsonObject> consumer = vertx.eventBus().consumer("inventory.change");
		
		consumer.handler(handler -> {
			System.out.println(handler.body());
			response.write("event: changeReport\n");
			response.write("data: " + handler.body().encode() + "\n\n");
		});
		
		TimeoutStream ticks = vertx.periodicStream(10000); 
		ticks.handler( tick -> {
			vertx.eventBus().<JsonObject>request("inventory.quantity", "", reply -> {
				if(reply.succeeded()) {
					System.out.println("[sse]: " + reply.result().body());
					response.write("event: inventoryQuantity\n");
					response.write("data: " + reply.result().body().encode() + "\n\n");
				}
			});
		});
		
		response.endHandler(h ->{
			consumer.unregister();
			ticks.cancel();
		});
	}
}
