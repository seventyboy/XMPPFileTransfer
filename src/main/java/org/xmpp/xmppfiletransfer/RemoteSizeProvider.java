package org.xmpp.xmppfiletransfer;


import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smackx.si.packet.StreamInitiation;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.smackx.xdata.provider.DataFormProvider;
import org.jxmpp.util.XmppDateTime;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmpp.xmppfiletransfer.IQRemoteSize;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ivo.dipumpo
 */
public class RemoteSizeProvider extends IQProvider<IQRemoteSize>{

    @Override
    public IQRemoteSize parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {

            String filename = parser.getAttributeValue("", "filename");

            IQRemoteSize iq = new IQRemoteSize(filename);
		
            int eventType;
            String elementName;
            String namespace;
            Long size = null;
            boolean done = false;
            while (!done) {
                    eventType = parser.next();
                    elementName = parser.getName();
                    namespace = parser.getNamespace();
                    if (eventType == XmlPullParser.START_TAG) {
                            if (elementName.equals("size")) {
                                 size = Long.parseLong(parser.nextText());
                            }  
                    } 
                    else if (eventType == XmlPullParser.END_TAG) {
                            if (elementName.equals("remoteFileSize")) {
                                    done = true;
                            }


                         }
                    
              
		
             }
		
             if ( size != null) 
                 return new IQRemoteSize(filename,size);
             else return new IQRemoteSize(filename);
    

    }
}