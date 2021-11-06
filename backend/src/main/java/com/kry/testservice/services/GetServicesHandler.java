package com.kry.testservice.services;

import com.kry.testservice.db.DbResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GetServicesHandler implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(GetServicesHandler.class);
  private final Pool db;

  public GetServicesHandler(final Pool db) {
    this.db = db;
  }

  @Override
  public void handle(final RoutingContext context) {
    db.query("SELECT a.name,a.url,a.created,a.is_active FROM simpleservicepoller.kry_services a")
      .execute()
      .onFailure(DbResponse.errorHandler(context, "No data found!"))
      .onSuccess(result -> {
        var response = new JsonArray();
        result.forEach(row -> {
          var jsonObject = new JsonObject();
          SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
          Date date = null;
          try {
            date = df.parse(row.getValue("created").toString());
          } catch (ParseException e) {
            LOG.error("Failed to parse: " + e.getLocalizedMessage());
          }
          jsonObject.put("name", row.getValue("name"));
          jsonObject.put("url", row.getValue("url"));
          jsonObject.put("created", date);
          jsonObject.put("status", row.getValue("is_active"));
          response.add(jsonObject);
        });
        LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer());
      })
    ;
  }
}
