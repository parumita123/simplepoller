package com.kry.testservice.services;

import com.kry.testservice.AbstractRestApiTest;
import com.kry.testservice.model.Service;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class ServiceHandlerTest extends AbstractRestApiTest {

  @Test
  void addNewService_success(Vertx vertx, VertxTestContext context) {
    var client = webClient(vertx);
    var serviceName = "google";
    var url = "https://google.com";
    JsonObject jsonObject = new JsonObject();
    jsonObject.put("name", serviceName);
    jsonObject.put("url", url);
    client.post("/newservice")
      .sendJsonObject(jsonObject)
      .onComplete(context.succeeding(response -> {
        assertEquals(204, response.statusCode());
        context.completeNow();
      }));
  }

  @Test
  void deleteService_success(Vertx vertx, VertxTestContext context) {
    var client = webClient(vertx);
    var serviceName = "google";
    JsonObject jsonObject = new JsonObject();
    jsonObject.put(serviceName, "google");
    client.delete("/delete/" + serviceName)
      .sendJsonObject(jsonObject)
      .onComplete(context.succeeding(response -> {
        assertEquals(204, response.statusCode());
        context.completeNow();
      }));
  }

  private WebClient webClient(final Vertx vertx) {
    return WebClient.create(vertx, new WebClientOptions().setDefaultPort(TEST_SERVER_PORT));
  }
}
