/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.pinot.core.startree.v2;

import org.apache.pinot.common.data.FieldSpec.DataType;
import org.apache.pinot.core.data.aggregator.PercentileTDigestValueAggregator;
import org.apache.pinot.core.data.aggregator.ValueAggregator;
import com.tdunning.math.stats.TDigest;
import java.util.Random;

import static org.testng.Assert.*;


public class PercentileTDigestStarTreeV2Test extends BaseStarTreeV2Test<Object, TDigest> {

  @Override
  ValueAggregator<Object, TDigest> getValueAggregator() {
    return new PercentileTDigestValueAggregator();
  }

  @Override
  DataType getRawValueType() {
    return DataType.LONG;
  }

  @Override
  Object getRandomRawValue(Random random) {
    return random.nextLong();
  }

  @Override
  void assertAggregatedValue(TDigest starTreeResult, TDigest nonStarTreeResult) {
    for (int i = 0; i <= 100; i++) {
      assertEquals(starTreeResult.quantile(i / 100), nonStarTreeResult.quantile(i / 100));
    }
  }
}