/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.e2e.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import java.nio.file.Path;
import lombok.experimental.Delegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ContainerLogger implements Logger {
    @Delegate
    private final Logger delegate;

    public ContainerLogger(final Path logDirectory, final String container) {

        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        final PatternLayoutEncoder encoder = new PatternLayoutEncoder();

        encoder.setPattern("%d{HH:mm:ss.SSS} %-5level %logger{36}.%M - %msg%n");
        encoder.setContext(context);
        encoder.start();

        final FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
        fileAppender.setFile(logDirectory.resolve(container).toAbsolutePath().toString());
        fileAppender.setEncoder(encoder);
        fileAppender.setContext(context);
        fileAppender.start();

        final ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(container);
        logger.addAppender(fileAppender);
        logger.setLevel(Level.DEBUG);
        logger.setAdditive(false);

        this.delegate = logger;
    }
}
