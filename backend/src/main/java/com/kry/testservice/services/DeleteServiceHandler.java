package com.kry.testservice.services;

import com.kry.testservice.controller.ServicesApiController;
import com.kry.testservice.db.DbResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class DeleteServiceHandler implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(DeleteServiceHandler.class);
  private final Pool db;

  public DeleteServiceHandler(final Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext routingContext) {

    var serviceName = ServicesApiController.getServiceName(routingContext);

    SqlTemplate.forUpdate(db,
      "DELETE FROM simpleservicepoller.kry_services where name=#{name}")
      .execute(Collections.singletonMap("name", serviceName))
      .onFailure(DbResponse.errorHandler(routingContext, "Failed to delete service : " + serviceName))
      .onSuccess(result -> {
        LOG.debug("Deleted {} rows for serviceName {}", result.rowCount(), serviceName);
        routingContext.response()
          .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
          .end();
      });
  }
}
