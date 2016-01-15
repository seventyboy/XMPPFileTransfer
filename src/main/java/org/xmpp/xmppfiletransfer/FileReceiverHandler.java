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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.xmpp.xmppfiletransfer.listeners.FileReceiverListener;
import org.xmpp.xmppfiletransfer.XMPP.*;
/**
 *
 * @author ivo.dipumpo
 */
public class FileReceiverHandler implements FileTransferListener{
 
      private final Transfers transfers;
     
    
    public FileReceiverHandler(Transfers transfers) {
       
    
        this.transfers = transfers;
    }
      public void fileTransferRequest(final FileTransferRequest request) {
      
     
        request.getFileName();
        final long fileSize = request.getFileSize();
        request.getDescription();
        request.getRequestor();
        final IncomingFileTransfer transfer = request.accept();
        Thread transferThread;
        transferThread = new Thread(new Runnable() {
           
    
              
           public void run() {
              InputStream is = null;
              TransferMonitor monitor  = null;
              int count = 0;
              long amountRead = 0;
              double speed = 0;
              RandomAccessFile raf = null;
              long localSize = 0;

                Logger.getLogger(XMPP.class.getName()).log(Level.INFO, "disconnesso" + transfer.getStreamID());

              try {
                  is = transfer.recieveFile();






                  raf = new RandomAccessFile(transfers.getDIR()+ File.separator + request.getFileName(),
                          "rw");
                  raf.seek(transfers.getIncomingFile(
                          new RemoteFile(request.getRequestor(), request.getFileName())));
                  System.out.println("il file parte da" + raf.getFilePointer());
                  localSize = raf.getFilePointer();
                  monitor = new TransferMonitor(transfer);
                  listener.newIncomingFile(monitor);


                  long startingTime = System.currentTimeMillis();
                  final byte[] b = new byte[512];




                  while ((count = is.read(b)) >= 0 ) {



                      raf.write(b, 0, count);
                      amountRead += count;
                      if (System.currentTimeMillis()  > startingTime)
                          speed = amountRead/(System.currentTimeMillis() - startingTime);


                      monitor.setTransferData( localSize, amountRead,
                              (int)((double)(localSize + amountRead)/(double)fileSize*100), speed, transfer.getStreamID() + " " + Thread.activeCount());
                  }
              } catch (FileNotFoundException ex) {
                  Logger.getLogger(FileReceiverHandler.class.getName()).log(Level.SEVERE, null, ex);
              } catch (IOException ex) {
                  Logger.getLogger(FileReceiverHandler.class.getName()).log(Level.SEVERE, null, ex);
              }  catch (SmackException ex) {
                  Logger.getLogger(FileReceiverHandler.class.getName()).log(Level.SEVERE, null, ex);
              } catch (XMPPException.XMPPErrorException ex) {
                  Logger.getLogger(FileReceiverHandler.class.getName()).log(Level.SEVERE, null, ex);
              } finally{
                  try {

                      if (is != null)
                          is.close();
                      if (raf != null)
                          raf.close();
                      File f = new File(Transfers.getDIR() + File.separator + request.getFileName());
                      System.out.println("stream chisui" + f.length());

                  } catch (IOException e) {
                      Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null,e);

                  }
                  listener.incomingFileExpired(monitor);
                  System.out.println("chiuso ransfer" + transfer.getStreamID());
                    Logger.getLogger(XMPP.class.getName()).log(Level.INFO, "disconnesso" );
              }

               Logger.getLogger(XMPP.class.getName()).log(Level.INFO, "disconnesso" );
          }

        });
        transferThread.start();
    }
     
    

    private FileReceiverListener listener;

    public FileReceiverListener getListener() {
        return listener;
    }

    public void setListener(FileReceiverListener listener) {
        this.listener = listener;
       
    }


   

   

     
}
