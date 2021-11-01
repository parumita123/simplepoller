package com.kry.testservice.controller;

import com.kry.testservice.services.DeleteServiceHandler;
import com.kry.testservice.services.GetServicesHandler;
import com.kry.testservice.services.NewServiceHandler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServicesApiController {
  private static final Logger LOG = LoggerFactory.getLogger(ServicesApiController.class);

  public static void attach(Router parent, final Pool db) {
    parent.get("/services").handler(new GetServicesHandler(db));
    parent.post("/newservice").handler(new NewServiceHandler(db));
    parent.delete("/delete/:name").handler(new DeleteServiceHandler(db));
  }

  public static String getServiceName(final RoutingContext context) {
    var serviceName = context.pathParam("name");
    LOG.debug("{} for serviceName {}", context.normalizedPath(), serviceName);
    return serviceName;
  }
}
