package com.kry.testservice.services;

import com.kry.testservice.db.DbResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateStatusHandler implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(NewServiceHandler.class);
  private final Pool db;
  private final List<Map<String, Object>> parameterList = new ArrayList<>();
  private List<Map<String, Object>> statusMap;
  private final String STATUS_OK = "OK";

  public UpdateStatusHandler(final Pool db, final List statusMap) {
    this.statusMap = statusMap;
    this.db = db;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    updateStatus(routingContext);
  }

  private void updateStatus(final RoutingContext context) {
    for (Map item : statusMap) {
      final Map<String, Object> parameter = new HashMap<>();
      item.forEach((key, value) -> {
        parameter.put("name", key);
        parameter.put("is_active", value);
        parameterList.add(parameter);
      });
    }

    if (parameterList.size() != 0) {
      db.withTransaction(client -> {
        return SqlTemplate.forUpdate(client,
          "UPDATE simpleservicepoller.kry_services SET is_active=#{is_active} WHERE name=#{name}"
        ).executeBatch(parameterList)
          .onFailure(DbResponse.errorHandler(context, "Failed to update")).onSuccess(success -> {
            var response = new JsonObject();
            response.put("status", STATUS_OK);
            LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
            context.response()
              .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
              .end(response.toBuffer());
          });
      });
    } else {
      DbResponse.notFound(context, "Nothing to update, please run /listen before update");
    }

  }
}
