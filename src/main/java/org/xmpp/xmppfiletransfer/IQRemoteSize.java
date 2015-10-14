/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xmpp.xmppfiletransfer;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.time.packet.Time;
import org.jxmpp.util.XmppDateTime;

/**
 *
 * @author ivo.dipumpo
 */
public class IQRemoteSize extends IQ{
    public static final String NAMESPACE = "urn:xmpp:remoteFileTransfer";
    public static final String ELEMENT = "remoteFileSize";

    private static final Logger LOGGER = Logger.getLogger(Time.class.getName());

    private final String filename;
    private Long  fileSize = null;

    

   
    public IQRemoteSize(String filename) {
        super(ELEMENT, NAMESPACE);
      
        
        this.filename = filename;
    }

    public IQRemoteSize(String filename, Long size) {
         super(ELEMENT, NAMESPACE);
        this.filename = filename;
        this.fileSize = size;
    }

    
  
 
    public String getFilename() {
        return filename;
    }

    public Long getFileSize() {
        return fileSize;
    }

    
  

  

    public static IQRemoteSize createResponse(IQ request, String filename, long size) {
        IQRemoteSize rs = new IQRemoteSize(filename, size);
        rs.setType(IQ.Type.result);
        rs.setTo(request.getFrom());
        rs.setStanzaId(request.getStanzaId());
        return rs;
    }

    @Override
    protected IQ.IQChildElementXmlStringBuilder getIQChildElementBuilder(IQ.IQChildElementXmlStringBuilder buf) {
        buf.attribute("filename", filename);
        if ( fileSize != null){
            buf.rightAngleBracket();
            buf.element("size", fileSize.toString());
        }
        else buf.setEmptyElement();;
     

       

        return buf;
    } 
}
