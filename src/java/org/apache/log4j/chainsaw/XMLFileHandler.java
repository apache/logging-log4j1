/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
 */
package org.apache.log4j.chainsaw;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.StringTokenizer;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A content handler for document containing Log4J events logged using the
 * XMLLayout class. It will create events and add them to a supplied model.
 *
 * @author <a href="mailto:oliver@puppycrawl.com">Oliver Burn</a>
 * @version 1.0
 */
class XMLFileHandler
    extends DefaultHandler
{
    private static final Logger LOG = Logger.getLogger(XMLFileHandler.class);

    /** represents the event tag **/
    private static final String TAG_EVENT = "log4j:event";
    /** represents the message tag **/
    private static final String TAG_MESSAGE = "log4j:message";
    /** represents the ndc tag **/
    private static final String TAG_NDC = "log4j:NDC";
    /** represents the throwable tag **/
    private static final String TAG_THROWABLE = "log4j:throwable";
    /** represents the location info tag **/
    private static final String TAG_LOCATION_INFO = "log4j:locationInfo";

    /** where to put the events **/
    private final EventDetailSink mEventSink;
    /** the number of events in the document **/
    private int mNumEvents;

    /** the time of the event **/
    private long mTimeStamp;
    /** the priority (level) of the event **/
    private Priority mPriority;
    /** the category of the event **/
    private String mCategoryName;
    /** the NDC for the event **/
    private String mNDC;
    /** the thread for the event **/
    private String mThreadName;
    /** the msg for the event **/
    private String mMessage;
    /** the throwable details the event **/
    private String[] mThrowableStrRep;
    /** the location details for the event **/
    private String mLocationDetails;
    /** buffer for collecting text **/
    private final StringBuffer mBuf = new StringBuffer();

    /**
     * Creates a new <code>XMLFileHandler</code> instance.
     *
     * @param aModel where to add the events
     */
    XMLFileHandler(EventDetailSink aEventSink) {
        mEventSink = aEventSink;
    }

    /** @see DefaultHandler **/
    public void startDocument()
        throws SAXException
    {
        mNumEvents = 0;
    }

    /** @see DefaultHandler **/
    public void characters(char[] aChars, int aStart, int aLength) {
        mBuf.append(String.valueOf(aChars, aStart, aLength));
    }

    /** @see DefaultHandler **/
    public void endElement(String aNamespaceURI,
                           String aLocalName,
                           String aQName)
    {
        if (TAG_EVENT.equals(aQName)) {
            addEvent();
            resetData();
        } else if (TAG_NDC.equals(aQName)) {
            mNDC = mBuf.toString();
        } else if (TAG_MESSAGE.equals(aQName)) {
            mMessage = mBuf.toString();
        } else if (TAG_THROWABLE.equals(aQName)) {
            final StringTokenizer st =
                new StringTokenizer(mBuf.toString(), "\n\t");
            mThrowableStrRep = new String[st.countTokens()];
            if (mThrowableStrRep.length > 0) {
                mThrowableStrRep[0] = st.nextToken();
                for (int i = 1; i < mThrowableStrRep.length; i++) {
                    mThrowableStrRep[i] = "\t" + st.nextToken();
                }
            }
        }
    }

    /** @see DefaultHandler **/
    public void startElement(String aNamespaceURI,
                             String aLocalName,
                             String aQName,
                             Attributes aAtts)
    {
        mBuf.setLength(0);

        if (TAG_EVENT.equals(aQName)) {
            mThreadName = aAtts.getValue("thread");
            mTimeStamp = Long.parseLong(aAtts.getValue("timestamp"));
            mCategoryName = aAtts.getValue("logger");
            if (mCategoryName == null) {
                mCategoryName = aAtts.getValue("category");
            }
            mPriority = Priority.toPriority(aAtts.getValue("level"));
        } else if (TAG_LOCATION_INFO.equals(aQName)) {
            mLocationDetails = aAtts.getValue("class") + "."
                + aAtts.getValue("method")
                + "(" + aAtts.getValue("file") + ":" + aAtts.getValue("line")
                + ")";
        }
    }

    /** @return the number of events in the document **/
    int getNumEvents() {
        return mNumEvents;
    }

    public int loadFile(File file)
        throws SAXException, IOException, ParserConfigurationException {

        final String absPath = file.getAbsolutePath();
        LOG.info("loading the contents of " + absPath);
        final int num = loadFile(absPath);
        Preferences.getInstance().fileLoaded(absPath);
        return num;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Private methods
    ////////////////////////////////////////////////////////////////////////////

    /** Add an event to the model **/
    private void addEvent() {
        mEventSink.addEvent(new EventDetails(mTimeStamp,
                                         mPriority,
                                         mCategoryName,
                                         mNDC,
                                         mThreadName,
                                         mMessage,
                                         mThrowableStrRep,
                                         mLocationDetails));
        mNumEvents++;
    }

    /** Reset the data for an event **/
    private void resetData() {
        mTimeStamp = 0;
        mPriority = null;
        mCategoryName = null;
        mNDC = null;
        mThreadName = null;
        mMessage = null;
        mThrowableStrRep = null;
        mLocationDetails = null;
    }

    /**
     * Loads the contents of file into the model
     *
     * @param aFile the file to extract events from
     * @return the number of events loaded
     * @throws SAXException if an error occurs
     * @throws IOException if an error occurs
     */
    private int loadFile(String aFile)
        throws SAXException, IOException, ParserConfigurationException {
        final XMLReader parser =
            SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        parser.setContentHandler(this);

        synchronized (parser) {
            // Create a dummy document to parse the file
            final StringBuffer buf = new StringBuffer();
            buf.append("<?xml version=\"1.0\" standalone=\"yes\"?>\n");
            buf.append("<!DOCTYPE log4j:eventSet ");
            buf.append("[<!ENTITY data SYSTEM \"file:///");
            buf.append(aFile);
            buf.append("\">]>\n");
            buf.append("<log4j:eventSet xmlns:log4j=\"Claira\">\n");
            buf.append("&data;\n");
            buf.append("</log4j:eventSet>\n");

            final InputSource is =
                new InputSource(new StringReader(buf.toString()));
            parser.parse(is);
            return getNumEvents();
        }
    }
}
