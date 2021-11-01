package com.kry.testservice.services;

import com.kry.testservice.db.DbResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetPollerServiceHandler implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(GetPollerServiceHandler.class);
  private final Pool db;
  private final String STATUS_FAIL = "FAIL";
  private final String STATUS_OK = "OK";
  private List<Map<String, Object>> statusMap;

  public GetPollerServiceHandler(final Pool db, final List statusMap) {
    this.statusMap = statusMap;
    this.db = db;
  }

  @Override
  public void handle(final RoutingContext context) {
    final Map<String, Object> parameter = new HashMap<>();
    db.query("SELECT a.name,a.url,a.created,a.is_active FROM simpleservicepoller.kry_services a")
      .execute()
      .onFailure(DbResponse.errorHandler(context, "Failed to get services from db!"))
      .onSuccess(result -> {
        result.forEach(row -> checkStatus(row.getString("name"), row.getString("url"), parameter));
        var response = getResponseObject(parameter);
        LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer());
      })
    ;
  }

  private String checkStatus(final String serviceName, final String urlStr, final Map parameter) {
    URL url;
    try {
      url = new URL(urlStr);
      HttpURLConnection huc = (HttpURLConnection) url.openConnection();
      huc.setRequestMethod("HEAD");
      int responseCode = huc.getResponseCode();
      switch (responseCode) {
        case HttpURLConnection.HTTP_OK: {
          parameter.put(serviceName, STATUS_OK);
          return STATUS_OK;
        }
        default: {
          parameter.put(serviceName, STATUS_FAIL);
          return STATUS_FAIL;
        }
      }
    } catch (IOException e) {
      return STATUS_FAIL;
    }
  }

  private JsonObject getResponseObject(final Map<String, Object> parmeter) {
    var response = new JsonObject();
    for (Map.Entry<String, Object> item : parmeter.entrySet()) {
      response.put(item.getKey(), item.getValue());
    }

    statusMap.add(parmeter);
    return response;
  }
}
