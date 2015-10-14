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
public class SenderHandler implements ConnectionListener{
    private final XMPPTCPConnection connessione;
    public String DIR="C:\\Users";
    private final Transfers transfers;
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

    public String getDIR() {
        return DIR;
    }

    
    public void requestRemoteFileSize(String JID, File file) {
 
    
        file.getName();
        IQRemoteSize iq = new IQRemoteSize(file.getName());
        Roster roster = Roster.getInstanceFor(connessione);
        String receiver = roster.getPresence(JID).getFrom();
        iq.setTo(receiver);
        iq.setType(IQ.Type.get);
        IQRemoteSize reply=null;
        try {
            reply = connessione.createPacketCollectorAndSend(iq).nextResultOrThrow();
            if (reply != null){
                RemoteFile rf = new RemoteFile(receiver, file.getName());
                transfers.putOutcomingFile(rf, reply.getFileSize());
                System.out.println("file size" + reply.getFileSize() + " " +
                        rf.getJID() + " " + rf);

            }
        } catch (SmackException.NotConnectedException ex) {
            Logger.getLogger(SenderHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SmackException.NoResponseException ex) {
            Logger.getLogger(SenderHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMPPException.XMPPErrorException ex) {
            Logger.getLogger(SenderHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
            
}
  public long getLastRemoteFileSize(String receiver, File file) {
          Roster roster = Roster.getInstanceFor(connessione);
        String JID = roster.getPresence(receiver).getFrom();
        System.out.println("getta da" + JID + " file " + file.getName());
        
      return transfers.getOutcomingFile(
              new RemoteFile(JID, file.getName())).longValue();
      
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
     OutputStream os = null;
    
         OutgoingFileTransfer outFile;
        RandomAccessFile f = null;
        InputStream fin = null;
      
        Roster roster = Roster.getInstanceFor(connessione);
        String receiver = roster.getPresence(JID).getFrom();
        outFile =FileTransferManager.getInstanceFor(connessione).
                createOutgoingFileTransfer(receiver);
        try {
            f = new RandomAccessFile(file.getAbsolutePath(),"r");
            f.seek(transfers.getOutcomingFile(new RemoteFile(receiver, file.getName())));
           
        
        } catch (FileNotFoundException ex) {
            Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null, ex);
            return;
        } catch (IOException ex) {
            Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        try {
            os = outFile.sendFile(file.getName(), file.length(), "invio file");
        } catch (XMPPException | SmackException ex) {
            Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
            final byte[] b = new byte[512];
            int count = 0;
            int amountWritten = 0;
            try {
                while ((count = f.read(b)) > 0 ) {
                    os.write(b, 0, count);
                    amountWritten += count;
                }
            } catch (IOException ex) {
                Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null, ex);
            }finally {
                    try {
                        os.close();
                        f.close();
                    } catch (IOException ex) {
                        Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null, ex);
                    }
             }
       }   

    @Override
    public void connected(XMPPConnection connection) {
      
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
       
    }

    @Override
    public void connectionClosed() {
       
    }

    @Override
    public void connectionClosedOnError(Exception e) {
       
    }

    @Override
    public void reconnectionSuccessful() {
        
    }

    @Override
    public void reconnectingIn(int seconds) {
        
    }

    @Override
    public void reconnectionFailed(Exception e) {
       
    }

   
    public void sendFile(String JID, File file){
            OutputStream os = null;
    
            OutgoingFileTransfer outFile;
            RandomAccessFile f = null;
            InputStream fin = null;
 
        try {
            file.getName();
            IQRemoteSize iq = new IQRemoteSize(file.getName());
            Roster roster = Roster.getInstanceFor(connessione);
            String receiver = roster.getPresence(JID).getFrom();
            iq.setTo(receiver);
            iq.setType(IQ.Type.get);
            IQRemoteSize reply=null;
            
            reply = connessione.createPacketCollectorAndSend(iq).nextResultOrThrow();
            if (reply == null) 
                throw new Exception("reply non tulll") ;
            RemoteFile rf = new RemoteFile(receiver, file.getName());
            transfers.putOutcomingFile(rf, reply.getFileSize());
            System.out.println("file size" + reply.getFileSize() + " " +
                    rf.getJID() + " " + rf);

         


            outFile =FileTransferManager.getInstanceFor(connessione).
                    createOutgoingFileTransfer(receiver);
            f = new RandomAccessFile(file.getAbsolutePath(),"r");
            f.seek(transfers.getOutcomingFile(new RemoteFile(receiver, file.getName())));
            os = outFile.sendFile(file.getName(), file.length(), "invio file");
             final byte[] b = new byte[512];
            int count = 0;
            int amountWritten = 0;
            try {
                while ((count = f.read(b)) > 0 ) {
                    os.write(b, 0, count);
                    amountWritten += count;
                }
            } catch (IOException ex) {
                Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null, ex);
            }finally {
                    try {
                        os.close();
                        f.close();
                    } catch (IOException ex) {
                        Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null, ex);
                    }
             }
        } catch (SmackException.NotConnectedException ex) {
            Logger.getLogger(SenderHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SmackException.NoResponseException ex) {
            Logger.getLogger(SenderHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMPPException.XMPPErrorException ex) {
            Logger.getLogger(SenderHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SenderHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        } 
  

}
