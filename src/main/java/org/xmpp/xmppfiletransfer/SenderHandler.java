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
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

/**
 *
 * @author ivo.dipumpo
 */
public class SenderHandler {
    private final XMPPTCPConnection connessione;
    private final Transfers transfers;
    private volatile boolean enabled = false;
     private CopyOnWriteArrayList<ConnectionListener> listeners = 
              new CopyOnWriteArrayList<> ();
    
    public void addListener(ConnectionListener l) {
       listeners.add(l);
   
       
    }

    public SenderHandler(XMPPTCPConnection connessione, Transfers transfers) {
        this.connessione = connessione;
        this.transfers = transfers;
        
    }
    public void start(){
        try {
            connessione.connect();
            connessione.login();
        } catch (SmackException ex) {
            Logger.getLogger(SenderHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SenderHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMPPException ex) {
            Logger.getLogger(SenderHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
     
    }

    public XMPPTCPConnection getConnessione() {
        return connessione;
    }


    
    private void requestRemoteFileSize(String JID, File file) throws 
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
            transfers.putOutcomingFile(rf, reply.getFileSize());
            System.out.println("file size" + reply.getFileSize() + " " +
                    rf.getJID() + " " + rf);

        }
      
            
}
    public void findUser(String user)  {
        
        Roster roster = Roster.getInstanceFor(connessione);
        try {
            roster.createEntry(user, user, null);
        } catch (SmackException.NotLoggedInException ex) {
            Logger.getLogger(SenderHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SmackException.NoResponseException ex) {
            Logger.getLogger(SenderHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMPPException.XMPPErrorException ex) {
            Logger.getLogger(SenderHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SmackException.NotConnectedException ex) {
            Logger.getLogger(SenderHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
  
}

   
    public ArrayList<String> getUsers(){
     
       ArrayList<String> lista = new ArrayList<String>();
  
        Roster roster = Roster.getInstanceFor(connessione);
        Iterator<RosterEntry>it = roster.getEntries().iterator();
        while (it.hasNext()){
            RosterEntry entry = it.next();
            System.out.println(" entry " + entry.getUser() + " presenza " +
                    roster.getPresence(entry.getUser()).isAvailable() );
            if (roster.getPresence(entry.getUser()).isAvailable() ){
                  lista.add(entry.getUser());

            }
          
    }
    return lista;
}
    public void transferFile(String JID, File file)  {
       
       long remoteSize = 0;
        Roster roster = Roster.getInstanceFor(connessione);
            String receiver = roster.getPresence(JID).getFrom();
       while (remoteSize  < file.length()){
            try {
                requestRemoteFileSize(JID, file);
                System.out.println("transfer" + transfers);
                remoteSize =transfers.getOutcomingFile(
                       new RemoteFile(receiver, file.getName()));
                if ( remoteSize < file.length())
                   sendFile(JID,file);
            } catch (SmackException.NotConnectedException ex) {
                Logger.getLogger(SenderHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SmackException.NoResponseException ex) {
                Logger.getLogger(SenderHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (XMPPErrorException ex) {
                Logger.getLogger(SenderHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(SenderHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
                  
       }       
        
    }
    

    

   

   
    private void sendFile(String JID, File file) throws Exception{
            OutputStream os = null;
    
            OutgoingFileTransfer outFile;
            RandomAccessFile f = null;
            InputStream fin = null;
 
      
        
          
            Roster roster = Roster.getInstanceFor(connessione);
            String receiver = roster.getPresence(JID).getFrom();
            

            outFile =FileTransferManager.getInstanceFor(connessione).
                    createOutgoingFileTransfer(receiver);
            f = new RandomAccessFile(file.getAbsolutePath(),"r");
            f.seek(transfers.getOutcomingFile(new RemoteFile(receiver, file.getName())));
       
            os = outFile.sendFile(file.getName(), file.length(), "invio file");
             final byte[] b = new byte[512];
            int count = 0;
           
            int amountWritten = 0;
               System.out.println("file poniter" + f.getFilePointer() + "lunghezza" + f.length());
            try {
                    
                while ((count = f.read(b)) > 0 ) {
            
                           os.write(b, 0, count);
                   
                    amountWritten += count;
               
                }
                             
            }finally {
                  System.out.println("file ponte5r" + f.getFilePointer() + " ultimo count"
               + count + " ulrtimo qmonuy" +     amountWritten );
                    try {
                        
                        f.close();
                    } catch (IOException ex) {
                        Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null, ex);
                    }
             }
        
        } 

    public synchronized void connect()  {
        try {
            connessione.connect();
            connessione.login();
        } catch (SmackException ex) {
            Logger.getLogger(SenderHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SenderHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMPPException ex) {
            Logger.getLogger(SenderHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addConnectionListener(ConnectionListener connectionListener) {
        connessione.addConnectionListener(connectionListener);
    }

   
   
}
