package com.psibt.framework.net;

import java.io.*;
import java.net.*;

/**
 * This interface defines all methods that have to be implemented for a HTTPRequestHandler for the
 * PluggableHTTPServer.
 *
 * @author <a HREF="mailto:V.Mentzner@psi-bt.de">Volker Mentzner</a>
 */
public interface HTTPRequestHandler {

 /**
   * Gets the title for html page
   */
  public String getTitle();

 /**
   * Sets the title for html page
   */
  public void setTitle(String title);

 /**
   * Gets the description for html page
   */
  public String getDescription();

 /**
   * Sets the description for html page
   */
  public void setDescription(String description);

 /**
   * Gets the virtual path in the HTTP server that ist handled in this HTTPRequestHandler.
   * So the root path handler will return "/" (without brackets) because it handles the path
   * "http://servername/" or a handler for "http://servername/somepath/" will return "/somepath/"
   * It is important to include the trailing "/" because all HTTPRequestHandler have to serve a path!
   */
  public String getHandledPath();

 /**
   * Sets the virtual path in the HTTP server that ist handled in this HTTPRequestHandler.
   * So set the path to "/" for the root path handler because it handles the path
   * "http://servername/" or set it to "/somepath/" for a handler for "http://servername/somepath/".
   * It is important to include the trailing "/" because all HTTPRequestHandler have to serve a path!
   */
  public void setHandledPath(String path);

 /**
   * Handles the given request and writes the reply to the given out-stream. Every handler has to check
   * the request for the right path info.
   *
   * @param request - client browser request
   * @param out - Out stream for sending data to client browser
   * @return if the request was handled by this handler : true, else : false
   */
  public boolean handleRequest(String request, Writer out);
}