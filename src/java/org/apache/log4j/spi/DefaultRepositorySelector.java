


package org.apache.log4j.spi;

import org.apache.log4j.Logger;

public class DefaultRepositorySelector implements RepositorySelector {

  final LoggerRepository repository;
  
  public 
  DefaultRepositorySelector(LoggerRepository repository) {
    this.repository = repository;
  }

  public
  LoggerRepository getLoggerRepository() {
    return repository;
  }
}

