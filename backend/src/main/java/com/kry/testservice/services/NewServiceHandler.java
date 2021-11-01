package com.kry.testservice.services;

import com.kry.testservice.db.DbResponse;
import com.kry.testservice.model.Service;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class NewServiceHandler implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(NewServiceHandler.class);
  private final Pool db;

  public NewServiceHandler(final Pool db) {
    this.db = db;
  }

  @Override
  public void handle(final RoutingContext context) {
    var json = context.getBodyAsJson();
    var service = json.mapTo(Service.class);
    if (validateUrl(service.getUrl())) {
      final Map<String, Object> parameters = new HashMap<>();
      parameters.put("name", service.getName());
      parameters.put("url", service.getUrl());

      // Transaction
      db.withTransaction(client -> {
          return SqlTemplate.forUpdate(client,
            "INSERT INTO simpleservicepoller.kry_services (name,url) VALUES (#{name},#{url})"
          ).execute(parameters).onFailure(DbResponse.errorHandler(context, "Failed to insert"))
            .onSuccess(result ->
              context.response()
                .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
                .end()
            );
        }
      );
    } else {
      DbResponse.notFound(context, "Invalid Url");
    }
  }

  private boolean validateUrl(String urlString) {
    try {
      URL url = new URL(urlString);
      return true;
    } catch (MalformedURLException e) {
      return false;
    }
  }
}
