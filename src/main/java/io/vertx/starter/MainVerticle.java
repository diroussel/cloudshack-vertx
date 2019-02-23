package io.vertx.starter;

import io.vertx.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start() {
    vertx.createHttpServer()
        .requestHandler(req -> req.response().end("Hello Vert.x!"))
      .listen(
        Integer.parseInt(System.getenv().getOrDefault("PORT", "8080")),
        System.getProperty("http.address", "0.0.0.0"));
  }

}
