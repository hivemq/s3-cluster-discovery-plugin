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

package com.hivemq.plugin.plugin;

import com.hivemq.plugin.callbacks.S3DiscoveryCallback;
import com.hivemq.spi.PluginEntryPoint;
import com.hivemq.spi.callback.registry.CallbackRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * This is the main class of the plugin, which is instanciated during the HiveMQ start up process.
 *
 * @author Christian Götz
 */
public class S3DiscoveryPluginEntryPoint extends PluginEntryPoint {

    Logger log = LoggerFactory.getLogger(S3DiscoveryPluginEntryPoint.class);

    private final S3DiscoveryCallback discoveryCallback;

    @Inject
    public S3DiscoveryPluginEntryPoint(final S3DiscoveryCallback discoveryCallback) {
        this.discoveryCallback = discoveryCallback;
    }

    /**
     * This method is executed after the instanciation of the whole class. It is used to initialize
     * the implemented callbacks and make them known to the HiveMQ core.
     */
    @PostConstruct
    public void postConstruct() {

        CallbackRegistry callbackRegistry = getCallbackRegistry();
        callbackRegistry.addCallback(discoveryCallback);

    }

}
