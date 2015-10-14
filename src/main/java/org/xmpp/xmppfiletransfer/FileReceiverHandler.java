/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

/**
 *
 * @author ivo.dipumpo
 */
public class FileReceiverHandler implements FileTransferListener{
 
    private final String  DIR;
      private final Transfers transfers;

    public FileReceiverHandler(Transfers transfers) {
        this.DIR = transfers.DIR;
        this.transfers = transfers;
    }
      public void fileTransferRequest(FileTransferRequest request) {
      InputStream is =  null;
  
            request.getFileName();
            request.getDescription();
            request.getRequestor();
            IncomingFileTransfer transfer = request.accept();
        try {
            is = transfer.recieveFile();
        } catch (SmackException ex) {
            Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null, ex);
            return;
        } catch (XMPPException.XMPPErrorException ex) {
            Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
            RandomAccessFile raf;
            try {
        raf = new RandomAccessFile(DIR + File.separator + request.getFileName(),
                "rw");
          raf.seek(transfers.getIncomingFile(
                new RemoteFile(request.getRequestor(), request.getFileName())));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null, ex);
            return;
        } catch (IOException ex) {
            Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
           
        
            final byte[] b = new byte[512];
            int count = 0;
            int amountWritten = 0;
            try {
                while ((count = is.read(b)) > 0 ) {
                    raf.write(b, 0, count);
                    amountWritten += count;
                }
            } catch (IOException ex) {
              Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null,ex);
            }finally{
                    try {

                            if (is != null) 
                                is.close();
                            if (raf != null)
                                raf.close();
                    } catch (IOException e) {
               Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null,e);
                  
                    }
            }
           	
    }
}
