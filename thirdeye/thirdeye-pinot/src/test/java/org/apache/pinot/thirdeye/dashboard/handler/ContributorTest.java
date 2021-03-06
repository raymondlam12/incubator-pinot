/**
 * Copyright (C) 2014-2018 LinkedIn Corp. (pinot-core@linkedin.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.pinot.thirdeye.dashboard.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pinot.thirdeye.common.time.TimeGranularity;
import org.apache.pinot.thirdeye.dashboard.views.contributor.ContributorViewHandler;
import org.apache.pinot.thirdeye.dashboard.views.contributor.ContributorViewRequest;
import org.apache.pinot.thirdeye.dashboard.views.contributor.ContributorViewResponse;
import org.apache.pinot.thirdeye.datasource.MetricExpression;
import org.apache.pinot.thirdeye.datasource.ThirdEyeDataSource;
import org.apache.pinot.thirdeye.datasource.cache.QueryCache;
import org.apache.pinot.thirdeye.datasource.pinot.PinotThirdEyeDataSource;

/** Manual test for verifying code works as expected (ie without exceptions thrown) */
public class ContributorTest {
  public static void main(String[] args) throws Exception {
    ContributorViewRequest request = new ContributorViewRequest();

    String collection = "thirdeyeAbook";
    DateTime baselineStart = new DateTime(2016, 3, 23, 00, 00);
    List<MetricExpression> metricExpressions = new ArrayList<>();
    metricExpressions.add(new MetricExpression("__COUNT", "__COUNT"));

    request.setCollection(collection);
    request.setBaselineStart(baselineStart);
    request.setBaselineEnd(baselineStart.plusDays(1));
    request.setCurrentStart(baselineStart.plusDays(7));
    request.setCurrentEnd(baselineStart.plusDays(8));
    request.setTimeGranularity(new TimeGranularity(1, TimeUnit.HOURS));
    request.setMetricExpressions(metricExpressions);

    PinotThirdEyeDataSource pinotThirdEyeDataSource = PinotThirdEyeDataSource.getDefaultTestDataSource(); // TODO
                                                                                          // make
                                                                                          // this
    // configurable;
    Map<String, ThirdEyeDataSource> dataSourceMap = new HashMap<>();
    dataSourceMap.put(PinotThirdEyeDataSource.class.getSimpleName(), pinotThirdEyeDataSource);
    QueryCache queryCache = new QueryCache(dataSourceMap, Executors.newFixedThreadPool(10));

    ContributorViewHandler handler = new ContributorViewHandler(queryCache);
    ContributorViewResponse response = handler.process(request);
    ObjectMapper mapper = new ObjectMapper();
    String jsonResponse = mapper.writeValueAsString(response);
    System.out.println(jsonResponse);
  }
}
