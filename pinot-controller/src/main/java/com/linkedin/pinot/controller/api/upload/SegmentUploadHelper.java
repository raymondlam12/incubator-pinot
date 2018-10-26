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
package com.linkedin.pinot.controller.api.upload;

import com.linkedin.pinot.common.segment.SegmentMetadata;
import com.linkedin.pinot.filesystem.PinotFS;
import com.linkedin.pinot.filesystem.PinotFSFactory;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import javax.ws.rs.core.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SegmentUploadHelper {
  private static final Logger LOGGER = LoggerFactory.getLogger(SegmentUploadHelper.class);

  public SegmentUploadHelper() {
  }

  public void uploadSegment(SegmentMetadata segmentMetadata, File currentSegmentLocation, URI segmentLocationURI,
      File segmentMetadataTarFile, URI segmentMetaDataLocationURI, boolean enableParallelPushProtection, HttpHeaders headers) {
    try {
      copyToPermanentDirectory(currentSegmentLocation, segmentLocationURI);
    } catch (Exception e) {
      LOGGER.error("Could not move segment {} of table {} from {} to permanent directory",
          segmentMetadata.getName(), segmentMetadata.getTableName(), currentSegmentLocation.getAbsolutePath(), e);
      throw new RuntimeException(e);
    }

    try {
      copyToPermanentDirectory(segmentMetadataTarFile, segmentMetaDataLocationURI);
    } catch (Exception e) {
      LOGGER.error("Could not move segment metadata {} of table {} from {} to permanent directory",
          segmentMetadata.getName(), segmentMetadata.getTableName(), segmentMetadataTarFile.getAbsolutePath(), e);
      throw new RuntimeException(e);
    }

  }

  public boolean mkdir(URI uri) throws IOException {
    PinotFS pinotFS = PinotFSFactory.create(uri.getScheme());
    return pinotFS.mkdir(uri);
  }

  public boolean exists(URI fileUri) throws IOException {
    PinotFS pinotFS = PinotFSFactory.create(fileUri.getScheme());
    return pinotFS.exists(fileUri);
  }

  public String[] listFiles(URI fileUri) throws IOException {
    PinotFS pinotFS = PinotFSFactory.create(fileUri.getScheme());
    return pinotFS.listFiles(fileUri);
  }

  public void copyToLocalFile(URI srcUri, File dstFile) throws Exception {
    PinotFS pinotFS = PinotFSFactory.create(srcUri.getScheme());
    pinotFS.copyToLocalFile(srcUri, dstFile);
  }


  public boolean move(SegmentMetadata segmentMetadata, URI srcUri, URI dstUri, boolean overwrite) throws IOException {
    PinotFS pinotFS = PinotFSFactory.create(dstUri.getScheme());
    String segmentName = segmentMetadata.getName();
    String tableName = segmentMetadata.getTableName();
    try {
      return pinotFS.move(srcUri, dstUri, true);
    } catch (IOException e) {
      LOGGER.error("Could not move segment {} of table {} from {} to {} ", segmentName, tableName, srcUri, dstUri, e);
      throw e;
    }
  }

  public boolean delete(URI fileUri) throws IOException {
    PinotFS pinotFS = PinotFSFactory.create(fileUri.getScheme());
    return pinotFS.delete(fileUri);
  }

  private void copyToPermanentDirectory(File currentSegmentLocation, URI segmentLocationURI) throws Exception {
    PinotFS pinotFS = PinotFSFactory.create(segmentLocationURI.getScheme());

    // Overwrites current segment file
    LOGGER.info("Copying segment from {} to {}", currentSegmentLocation.getAbsolutePath(), segmentLocationURI.toString());
    pinotFS.copyFromLocalFile(currentSegmentLocation, segmentLocationURI);
  }
}
