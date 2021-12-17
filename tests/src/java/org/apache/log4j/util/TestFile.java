package org.apache.log4j.util;

public class TestFile {
  public static String byClassName(String baseName, Class testClass) {
    String className = testClass.getName();
    className = className.substring(className.lastIndexOf(".")+1);
    return baseName + "." + className;
  }

  public static String temp(Class testClass) {
    return byClassName("output/temp", testClass);
  }

  public static String filtered(Class testClass) {
    return byClassName("output/filtered", testClass);
  }
}
