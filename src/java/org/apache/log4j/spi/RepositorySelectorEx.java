/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.spi;


/**
 * The <code>LogManager</code> uses one (and only one)
 * <code>RepositorySelector</code> implementation to select the
 * {@link LoggerRepository} for a particular application context.
 *
 * <p>It is the responsability of the <code>RepositorySelector</code>
 * implementation to track the application context. Log4j makes no assumptions
 * about the application context or on its management.
 *
 * <p>See also {@link org.apache.log4j.LogManager LogManager}.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @since 1.3
 * */
public interface RepositorySelectorEx extends RepositorySelector {


  /**
   * Remove the repository with the given name from the list maintained by the
   * respository selector.
   *
   * <p>When applications are stopped or recycled, this method should be called
   * to ensure that the associated repository is recycled as well. After the
   * repository is detached from this selector, the returned value, i.e. the
   * detached repository, can be used to shutdown the repository.
   *
   * <p>If more than one application share the same logging context, then the
   * applications need to coordinate their actions.
   *
   * @return The LoggerRepository instance that was detached.
   * @since 1.3
   */
  LoggerRepository detachRepository(String name);
}
