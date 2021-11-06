package com.kry.testservice;

import com.kry.testservice.config.BrokerConfig;
import com.kry.testservice.config.ConfigLoader;
import com.kry.testservice.controller.PollerApiController;
import com.kry.testservice.db.DBPools;
import com.kry.testservice.controller.ServicesApiController;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.handler.CorsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;

public class RestApiVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(RestApiVerticle.class);

  @Override
  public void start(final Promise<Void> startPromise) throws Exception {
    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess(configuration -> {
        LOG.info("Retrieved Configuration: {}", configuration);
        startHttpServerAndAttachRoutes(startPromise, configuration);
      });
  }

  private void startHttpServerAndAttachRoutes(final Promise<Void> startPromise,
                                              final BrokerConfig configuration) {
    final Pool db = DBPools.createMySQLPool(configuration, vertx);

    final Router restApi = Router.router(vertx);
    restApi.route().handler(CorsHandler.create("*")
      .allowedMethod(HttpMethod.GET)
      .allowedMethod(HttpMethod.POST)
      .allowedMethod(HttpMethod.PUT)
      .allowedMethod(HttpMethod.DELETE)
      .allowedHeader("Access-Control-Request-Method")
      .allowedHeader("Access-Control-Allow-Origin")
      .allowedHeader("Access-Control-Allow-Headers")
      .allowedHeader("Content-Type"))
      .handler(BodyHandler.create())
      .failureHandler(handleFailure());
    ServicesApiController.attach(restApi, db);
    PollerApiController.attach(restApi, db);

    vertx.createHttpServer()
      .requestHandler(restApi)
      .exceptionHandler(error -> LOG.error("HTTP Server error: ", error))
      .listen(configuration.getServerPort(), http -> {
        if (http.succeeded()) {
          startPromise.complete();
          LOG.info("HTTP server started on port {}", configuration.getServerPort());
        } else {
          startPromise.fail(http.cause());
        }
      });
  }

  private Handler<RoutingContext> handleFailure() {
    return errorContext -> {
      if (errorContext.response().ended()) {
        // Ignore completed response
        return;
      }
      LOG.error("Route Error:", errorContext.failure());
      errorContext.response()
        .setStatusCode(500)
        .end(new JsonObject().put("message", "Something went wrong :(").toBuffer());
    };
  }

}
