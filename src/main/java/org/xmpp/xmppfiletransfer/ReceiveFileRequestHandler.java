



package org.xmpp.xmppfiletransfer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.iqrequest.AbstractIqRequestHandler;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.si.packet.StreamInitiation;



/**
 *
 * @author ivo.dipumpo
 */
public class ReceiveFileRequestHandler extends AbstractIqRequestHandler {
    public String DIR="c:";
    final Transfers transfers;

    public ReceiveFileRequestHandler(Transfers transfers, String element, String namespace, IQ.Type type, Mode mode) {
        super(element, namespace, type, mode);
        this.DIR = transfers.DIR;
        this.transfers = transfers;
    }

    @Override
    public IQ handleIQRequest(IQ iqRequest) {
         long size;
       IQRemoteSize reply = null;
        IQRemoteSize p = (IQRemoteSize) iqRequest;
        if (p.getType() == IQ.Type.get){
            String filename = p.getFilename();
            File f = new File(DIR + File.separator + filename);
            System.out.println(" la stringa" + DIR + File.separator + filename);
            if (!f.exists())

                    try {
                        f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null, ex);
            }

           size = f.length();
           reply = IQRemoteSize.createResponse(p, p.getFilename(), size);
           transfers.putIncomingFile(new RemoteFile(
                                    p.getFrom(), p.getFilename()), size);
               
         }
         else if  (p.getType() == IQ.Type.set){
                  
          }
         return reply;
      }
   
    }
          
    

