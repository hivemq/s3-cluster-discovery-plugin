/*
 * Copyright 2015 dc-square GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hivemq.plugin.configuration;

import com.amazonaws.regions.Regions;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author Christian Götz
 */
public class Configuration {

    private static final Logger log = LoggerFactory.getLogger(Configuration.class);

    private final Properties properties;

    @Inject
    public Configuration(PluginReader pluginReader) {
        properties = pluginReader.getProperties();
    }


    public AuthenticationType getAuthenticationType() {

        try {
            final String property = getProperty("credentials-type");
            if (property == null) {
                return null;
            }

            return AuthenticationType.fromName(property);
        } catch (IllegalArgumentException e) {
            log.error("Not able to initialize S3 Plugin", e);
            return null;
        }
    }

    public Regions getRegion() {
        try {
            final String property = getProperty("s3-bucket-region");
            if (property == null) {
                return null;
            }

            return Regions.fromName(property);
        } catch (IllegalArgumentException e) {
            log.error("Not able to initialize S3 Plugin", e);
            return null;
        }
    }

    public String getFilePrefix() {
        final String property = getProperty("file-prefix");
        if (property == null) {
            return "";
        }
        return property;
    }

    public long getExpirationMinutes() {
        final String property = getProperty("file-expiration");
        if (property == null) {
            return 0L;
        }

        try {
            final long value = Long.parseLong(property);
            if (value < 0) {
                log.error("Value for S3 expiration configuration must be positive or zero, disabling expiration");
                return 0;
            }
            return value;
        } catch (NumberFormatException e) {
            log.error("Not able to parse S3 expiration configuration, disabling expiration");
            return 0L;
        }
    }

    public long getOwnInformationUpdateInterval() {
        final String property = getProperty("update-interval");
        if (property == null) {
            return 0L;
        }

        try {
            final long value = Long.parseLong(property);
            if (value < 0) {
                log.error("Value for S3 update interval configuration must be positive or zero, disabling update interval");
                return 0;
            }
            return value;
        } catch (NumberFormatException e) {
            log.error("Not able to parse S3 update interval configuration, disabling update interval");
            return 0L;
        }
    }

    public String getBucketName() {
        return getProperty("s3-bucket-name");
    }

    public String getAccessKeyId() {
        return getProperty("credentials-access-key-id");
    }

    public String getSecretAccessKey() {
        return getProperty("credentials-secret-access-key");
    }

    public String getSessionToken() {
        return getProperty("credentials-session-token");
    }

    private String getProperty(final String key) {
        if (properties == null) {
            return null;
        }

        return properties.getProperty(key);
    }
}
