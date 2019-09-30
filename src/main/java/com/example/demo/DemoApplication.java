package com.example.demo;

import com.example.demo.adapters.db.HutRepository;
import com.example.demo.adapters.rest.CustomHandlerFilterFunction;
import com.example.demo.adapters.rest.HutApi;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.RouterFunctions;
import reactor.ipc.netty.http.server.HttpServer;

import java.util.concurrent.ConcurrentHashMap;

public class DemoApplication {

	public static void main(String[] args) {
		final HutRepository hutRepository = new HutRepository(new ConcurrentHashMap<>());
		final HutApi hutApi = new HutApi(hutRepository);
		final CustomHandlerFilterFunction cHFF= new CustomHandlerFilterFunction();

		final HttpHandler httpHandler = RouterFunctions.toHttpHandler(hutApi.apiRoot.filter(cHFF));
		final ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);

		HttpServer
				.create("127.0.0.1", 8080)
				.startAndAwait(adapter);
	}
}
