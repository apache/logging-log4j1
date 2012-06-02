package org.apache.log4j.net;

/**
This class contains all constants which are necessary to define a columns log-type.
*/
public class LogType
{
  //A column of this type will receive the message.
  public static final int MSG = 1;

  //A column of this type will be a unique identifier of the logged row.
  public static final int ID = 2;

  //A column of this type will contain a static, one-time-defined value.
  public static final int STATIC = 3;

    //A column of this type will be filled with an actual timestamp depending by the time the logging will be done.
  public static final int TIMESTAMP = 4;

  //A column of this type will contain no value and will not be included in logging insert-statement.
   //This could be a column which will be filled not by creation but otherwhere...
  public static final int EMPTY = 5;


  public static boolean isLogType(int _lt)
  {
    if(_lt == MSG || _lt == STATIC || _lt == ID || _lt == TIMESTAMP || _lt == EMPTY) return true;

    return false;
  }

   public static int parseLogType(String _lt)
   {
    if(_lt.equals("MSG")) return MSG;
    if(_lt.equals("ID")) return ID;
    if(_lt.equals("STATIC")) return STATIC;
    if(_lt.equals("TIMESTAMP")) return TIMESTAMP;
    if(_lt.equals("EMPTY")) return EMPTY;

      return -1;
   }
}