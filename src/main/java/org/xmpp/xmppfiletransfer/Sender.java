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
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromMatchesFilter;
import org.jivesoftware.smack.filter.IQTypeFilter;
import org.jivesoftware.smack.filter.StanzaExtensionFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaIdFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.xmpp.xmppfiletransfer.listeners.FileReceiverListener;
import org.xmpp.xmppfiletransfer.listeners.FileSenderListener;

/**
 *
 * @author ivo.dipumpo
 */
public class Sender {
    private final XMPPTCPConnection connessione;
    private volatile boolean enabled = false;
    private volatile boolean noStreamHost = false;
    private volatile long remoteFileSize = 0;
    private String IDByteStream ="";
    private final Object lock = new Object();
   
    private final String JID ;

    public String getJID() {
        return JID;
    }

       private final File file  ;

    public File getFile() {
        return file;
    }
 

    public Sender(XMPPTCPConnection connessione, String JID, File file) {
        this.connessione = connessione;
       this.JID =JID;
       this.file = file;
        
    }

    public XMPPTCPConnection getConnessione() {
        return connessione;
    }


    
    private void requestRemoteFileSize() throws 
            SmackException.NotConnectedException, 
            SmackException.NoResponseException, 
            XMPPErrorException {
 
    
        file.getName();
        IQRemoteSize iq = new IQRemoteSize(file.getName());
        Roster roster = Roster.getInstanceFor(connessione);
        String receiver = roster.getPresence(JID).getFrom();
        iq.setTo(receiver);
        iq.setType(IQ.Type.get);
        IQRemoteSize reply=null;
   
        reply = connessione.createPacketCollectorAndSend(iq).nextResultOrThrow();
        if (reply != null){
            RemoteFile rf = new RemoteFile(receiver, file.getName());
           
            remoteFileSize = reply.getFileSize().longValue();
            System.out.println("file size" + reply.getFileSize() + " " +
                    rf.getJID() + " " + rf);

        }
      
            
}

   
    public void transferFile()  {
       
  
        Roster roster = Roster.getInstanceFor(connessione);
            String receiver = roster.getPresence(JID).getFrom();
          
            this.addSendingListener();
       while ((remoteFileSize  < file.length()) && (noStreamHost==false)){
            try {
   
                requestRemoteFileSize();
 
              
                if ( remoteFileSize < file.length())
                   sendFile(JID,file);
            } catch (SmackException.NotConnectedException ex) {
                Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SmackException.NoResponseException ex) {
                Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
            } catch (XMPPErrorException ex) {
                Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
            }
                  
       }       
        
    }
    

    

   

   
    private void sendFile(String JID, File file) {
            OutputStream os = null;
              TransferMonitor monitor  = null;
              int count = 0;
              long amountWritten = 0;
              double speed = 0;

              long remoteSize = 0;
            OutgoingFileTransfer outFile;
            RandomAccessFile f = null;
            InputStream fin = null;
 
      
        
          
            Roster roster = Roster.getInstanceFor(connessione);
            String receiver = roster.getPresence(JID).getFrom();
            

            outFile =FileTransferManager.getInstanceFor(connessione).
                    createOutgoingFileTransfer(receiver);
            
              
            outFile.getStreamID();
              try {
            f = new RandomAccessFile(file.getAbsolutePath(),"r");
             
            f.seek(remoteFileSize);
       
            os = outFile.sendFile(file.getName(), file.length(), "invio file");
            
            monitor = new TransferMonitor(outFile);
            listener.newOutcomingFile(monitor);
            remoteSize = remoteFileSize;
            
            final byte[] b = new byte[512];
            long startingTime = System.currentTimeMillis();
           
           
               System.out.println("file poniter" + f.getFilePointer() + "lunghezza" + f.length());
       
                    
                while ((count = f.read(b)) > 0 ) {
            
                           os.write(b, 0, count);
                   
                    amountWritten += count;
                     if (System.currentTimeMillis()  > startingTime)
                                      speed = amountWritten/(System.currentTimeMillis() - startingTime);
                                  
                                  
                    monitor.setTransferData( remoteSize, amountWritten,
                          (int)((double)(remoteSize + amountWritten)/(double)file.length()*100),
                          speed, outFile.getStreamID() + " " + Thread.activeCount());
                }
                             
            } catch (FileNotFoundException ex) {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMPPException ex) {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SmackException ex) {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
               
                    try {
                           System.out.println("file ponte5r" + f.getFilePointer() + " ultimo count"
               + count + " ulrtimo qmonuy" +     amountWritten );
                       
                           if ( f!= null)
                               f.close();
                           if (os != null)
                               os.close();
                    } catch (IOException ex) {
                        Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null, ex);
                    }
         }
        listener.outcomingFileExpired(monitor);
    } 


    public void addConnectionListener(ConnectionListener connectionListener) {
        connessione.addConnectionListener(connectionListener);
    }

    private void addPacketListener(String IDIQ){
     
        StanzaFilter filter = new AndFilter(IQTypeFilter.ERROR,
                        new FromMatchesFilter(JID, true));

        StanzaFilter filterAND = new AndFilter(filter,new StanzaIdFilter(IDIQ));
                     

        PacketCollector myCollector = connessione.createPacketCollector(filterAND);



        StanzaListener myListener = new StanzaListener() {


            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {

                  IQ reply = (IQ)packet;
     
                  Logger.getLogger(XMPP.class.getName()).log(Level.INFO, "ricevuto errore!!" + packet.getError().getType() + " code " 
                  
                        + 
                          reply.getError().getErrorGenerator() +

                         " " + packet.getStanzaId());
                  
    
                              noStreamHost = true;
                                 Logger.getLogger(XMPP.class.getName()).log(Level.INFO, "fermo tutto" +
                                         " " + packet.getStanzaId());
              

                
            }
        };

        connessione.addAsyncStanzaListener(myListener, filterAND); 
    }
    private void addSendingListener(){
            StanzaFilter filterSend = new StanzaTypeFilter(IQ.class);
            PacketCollector collectorSend = connessione.createPacketCollector(filterSend);
            StanzaListener listenerSend = new StanzaListener() {
		

                @Override
                public void processPacket(Stanza packet) throws SmackException.NotConnectedException {



                      IQ sendingIQ = (IQ)packet;
                    
                     if (sendingIQ.getChildElementNamespace().equals("http://jabber.org/protocol/bytestreams")){
                         synchronized(lock){
                             IDByteStream = sendingIQ.getStanzaId();
                             addPacketListener(IDByteStream);
                         }; Logger.getLogger(XMPP.class.getName()).log(Level.INFO, "ricevuto il send cazzo!!" +   " code " +
                              sendingIQ.getChildElementNamespace() + " ID" + packet.getStanzaId()); 
                     }
                }
	    }; 
            connessione.addPacketSendingListener(listenerSend, filterSend); 
    }




   private FileSenderListener listener;

    public FileSenderListener getListener() {
        return listener;
    }

    public void setListener(FileSenderListener listener) {
        this.listener = listener;
       
    }
  
   
}
