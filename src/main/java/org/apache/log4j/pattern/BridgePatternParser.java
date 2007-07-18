/*
 * Copyright 1999,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.pattern;

import org.apache.log4j.ULogger;
import org.apache.log4j.spi.LoggerRepository;


/**
 * The class implements the pre log4j 1.3 org.apache.log4j.helpers.PatternConverter
 * contract by delegating to the log4j 1.3 pattern implementation.
 *
 *
 * @author Curt Arnold
 * @since 1.3
 *
 */
public final class BridgePatternParser
  extends org.apache.log4j.helpers.PatternParser {
  /**
   * Logger repository.
   */
  private final LoggerRepository repository;

  /**
   * Internal logger.
   */
  private final ULogger logger;

  /**
   * Create a new instance.
   * @param conversionPattern pattern, may not be null.
   * @param repository repository, may be null.
   * @param logger internal logger, may be null.
   */
  public BridgePatternParser(
    final String conversionPattern, final LoggerRepository repository,
    final ULogger logger) {
    super(conversionPattern);
    this.repository = repository;
    this.logger = logger;
  }

  /**
   * Create new pattern converter.
   * @return pattern converter.
   */
  public org.apache.log4j.helpers.PatternConverter parse() {
    return new BridgePatternConverter(pattern, repository, logger);
  }
}
