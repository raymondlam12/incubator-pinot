/*
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

package com.linkedin.thirdeye.detection.baseline;

import java.lang.reflect.Constructor;
import java.util.Map;


public class BaselineProviderLoader {
  private static final String PROP_CLASS_NAME = "className";

  public static BaselineProvider from(Map<String, Object> properties) throws Exception {
    String className = properties.get(PROP_CLASS_NAME).toString();
    Constructor<?>
        constructor = Class.forName(className).getConstructor();
    BaselineProvider baselineProvider = (BaselineProvider) constructor.newInstance();
    baselineProvider.init(properties);
    return baselineProvider;
  }
}