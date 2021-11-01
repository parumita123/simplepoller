package com.kry.testservice.controller;

import com.kry.testservice.services.GetPollerServiceHandler;
import com.kry.testservice.services.UpdateStatusHandler;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PollerApiController {
  private static final Logger LOG = LoggerFactory.getLogger(PollerApiController.class);
  private static final List<Map<String, Object>> statusMap = new ArrayList<>();

  public static void attach(Router parent, final Pool db) {
    parent.get("/listen").handler(new GetPollerServiceHandler(db, statusMap));
    parent.get("/batchUpdate").handler(new UpdateStatusHandler(db, statusMap));
  }
}
