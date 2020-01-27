package com.autozone.vertx.exampleTwo.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

public class HttpServer extends AbstractVerticle {
	
	@Override
	public void start() throws Exception {
		
		JsonObject properties = vertx.getOrCreateContext().config(); 
		
		vertx.createHttpServer().requestHandler(this::handler).listen(properties.getInteger("server_port"));
	}
	
	public void handler (HttpServerRequest request) {
		if("/sse".equals(request.path())) {
			sse(request);
		} else {
			
		}
	}
	
	public void sse(HttpServerRequest request) {
		HttpServerResponse response = request.response();
		
		response.headers().add("Content-Type", "text/event-stream");
		response.headers().add("Cache-Control", "no-cache");
		response.setChunked(true);
		
		MessageConsumer<JsonObject> consumer = vertx.eventBus().consumer("inventory-change");
		
		consumer.handler(handler -> {
			response.write("event: changeReport \n");
			response.write("data: " + handler.body().encode() + "\n\n");
		});
		
		response.endHandler(h ->{
			consumer.unregister();
		});
	}
}
