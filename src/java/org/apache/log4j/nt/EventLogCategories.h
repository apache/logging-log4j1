//
//  Values are 32 bit values layed out as follows:
//
//   3 3 2 2 2 2 2 2 2 2 2 2 1 1 1 1 1 1 1 1 1 1
//   1 0 9 8 7 6 5 4 3 2 1 0 9 8 7 6 5 4 3 2 1 0 9 8 7 6 5 4 3 2 1 0
//  +---+-+-+-----------------------+-------------------------------+
//  |Sev|C|R|     Facility          |               Code            |
//  +---+-+-+-----------------------+-------------------------------+
//
//  where
//
//      Sev - is the severity code
//
//          00 - Success
//          01 - Informational
//          10 - Warning
//          11 - Error
//
//      C - is the Customer code flag
//
//      R - is a reserved bit
//
//      Facility - is the facility code
//
//      Code - is the facility's status code
//
//
// Define the facility codes
//


//
// Define the severity codes
//


//
// MessageId: 0x0000C350L (No symbolic name defined)
//
// MessageText:
//
//  Fatal
//


//
// MessageId: 0x00009C40L (No symbolic name defined)
//
// MessageText:
//
//  Error
//


//
// MessageId: 0x00007530L (No symbolic name defined)
//
// MessageText:
//
//  Warn
//


//
// MessageId: 0x00004E20L (No symbolic name defined)
//
// MessageText:
//
//  Info
//


//
// MessageId: 0x00002710L (No symbolic name defined)
//
// MessageText:
//
//  Debug
//


//
// MessageId: 0x00001000L (No symbolic name defined)
//
// MessageText:
//
//  %1
//


