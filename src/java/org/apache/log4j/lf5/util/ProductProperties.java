/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.
 */
package org.apache.log4j.lf5.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ProductProperties holds the properties dealing with
 * this product.  There is never a need
 * for multiple instances of this class, thus this class
 * is a singleton.
 *
 * The properties file format is as follows:
 *
 *  #Any arbitrary comments are denoted with a #
 *
 *  #Mandatory properties
 *  product.name=<Product name>
 *  product.version.number=<Product version number>
 *  product.release.date=<Product release date>
 *  product.release.type=<Product release type>
 *
 *
 * Note that this class will throw an ExceptionInInitializerError if
 * initialization fails, or if the required properties are absent.
 *
 * @author Robert Shaw
 * @author Michael J. Sikorsky
 * @author Brent Sprecher
 */

// Contributed by ThoughtWorks Inc.

public class ProductProperties {
  //--------------------------------------------------------------------------
  //   Constants:
  //--------------------------------------------------------------------------

  //--------------------------------------------------------------------------
  //   Protected Variables:
  //--------------------------------------------------------------------------

  /** Collection of properties. */
  protected Properties _productProperties;
  //protected final boolean _validFlag;

  //--------------------------------------------------------------------------
  //   Private Variables:
  //--------------------------------------------------------------------------
  private static ProductProperties _reference = null;
  private static Object _synchronizingObject = new Object();

  //--------------------------------------------------------------------------
  //   Constructors:
  //--------------------------------------------------------------------------

  /**
   * Construct an ProductProperties with the mandatory properties.
   * Use the constructor that configures a ProductProperties
   * off of a properties file if you wish to take advantage
   * of additional properties.
   *
   */
  private ProductProperties() {
    this(new Resource("org/apache/log4j/lf5/lf5.properties"));
  }

  private ProductProperties(Resource resource) {
    super();

    _productProperties = new Properties();

    try {
      InputStream source = getSource(resource);
      byte[] contents = StreamUtils.getBytes(source);
      _productProperties = getProperties(contents);
      source.close();
      validateProductProperties();
    } catch (Exception e) {
      String error = e.getMessage();
      throw new ExceptionInInitializerError(error);
    }
  }


  //--------------------------------------------------------------------------
  //   Public Methods:
  //--------------------------------------------------------------------------

  public static ProductProperties getInstance() {
    if (_reference == null) {
      synchronized (_synchronizingObject) {
        if (_reference == null) {
          _reference = new ProductProperties();
        }
      }
    }
    return _reference;
  }

  /**
   * Mandatory internally recognized property.
   */
  public static String getPropertyNameOfProductName() {
    return "product.name";
  }

  /**
   * Mandatory internally recognized property.
   */
  public static String getPropertyNameOfProductVersionNumber() {
    return "product.version.number";
  }

  /**
   * Mandatory internally recognized property.
   */
  public static String getPropertyNameOfProductReleaseDate() {
    return "product.release.date";
  }

  /**
   * Get the product name.
   */
  public String getProductName() {
    return _productProperties.getProperty(getPropertyNameOfProductName());
  }

  /**
   * Set the product name.
   */
  public void setProductName(String productName) {
    _productProperties.setProperty(getPropertyNameOfProductName(),
        productName);
  }

  /**
   * Get the product version number.
   */
  public String getProductVersionNumber() {
    return
        _productProperties.getProperty(getPropertyNameOfProductVersionNumber());
  }

  /**
   * Set the product version number.
   */
  public void setProductVersionNumber(String productVersionNumber) {
    _productProperties.setProperty(getPropertyNameOfProductVersionNumber(),
        productVersionNumber);
  }

  /**
   * Get the product release date.
   */
  public String getProductReleaseDate() {
    return
        _productProperties.getProperty(getPropertyNameOfProductReleaseDate());
  }

  /**
   * Set the product release date.
   */
  public void setProductReleaseDate(String productReleaseDate) {
    _productProperties.setProperty(getPropertyNameOfProductReleaseDate(),
        productReleaseDate);
  }

  /**
   * Retrieve any of the mandatory properties, or any additional
   * properties that were placed in the database configuration file.
   */
  public String get(String name) {
    return _productProperties.getProperty(name);
  }

  /**
   * Set any property.
   */
  public Object set(String name, String value) {
    return _productProperties.setProperty(name, value);
  }

  public String getLogFactor5() {
    return getString("lf5");
  }

  public String getString(String propertyName) {
    return String.valueOf(get(propertyName));
  }

  //--------------------------------------------------------------------------
  //   Protected Methods:
  //--------------------------------------------------------------------------
  /**
   * @throws java.lang.Exception
   */
  protected void validateProductProperties() throws Exception {
    String value = null;

    value = getProductVersionNumber();
    if ((value == null) || value.equals("")) {
      throw new Exception("Product version number is null.");
    }

    value = getProductReleaseDate();
    if ((value == null) || value.equals("")) {
      throw new Exception("Product release date is null.");
    }

  }

  //--------------------------------------------------------------------------
  //   Private Methods:
  //--------------------------------------------------------------------------

  private InputStream getSource(Resource resource) {
    return new BufferedInputStream(
        ResourceUtils.getResourceAsStream(this, resource));
  }

  private Properties getProperties(byte[] contents) throws IOException {
    ByteArrayInputStream source = new ByteArrayInputStream(contents);
    Properties result = new Properties();
    result.load(source);
    source.close();
    return result;
  }

  //--------------------------------------------------------------------------
  //   Nested Top-Level Classes or Interfaces
  //--------------------------------------------------------------------------
}






