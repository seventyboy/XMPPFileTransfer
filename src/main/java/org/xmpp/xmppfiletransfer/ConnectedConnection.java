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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.iqrequest.AbstractIqRequestHandler;
import org.jivesoftware.smack.iqrequest.IQRequestHandler;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
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
public class ConnectedConnection implements ConnectionListener{
    
   private final XMPPConnection connessione; 
   
  
   
   private final HashMap<RemoteFile,Long> outcomingFiles = new HashMap();

    public ConnectedConnection(XMPPConnection connessione) {
        this.connessione = connessione;
    }

    public XMPPConnection getConnessione() {
        return connessione;
    }

    @Override
    public void connected(XMPPConnection connection) {
       try {
           if (!(connection instanceof XMPPTCPConnection) )
               return;
           XMPPTCPConnection conn = (XMPPTCPConnection) connection;
           conn.login();
       } catch (XMPPException ex) {
           Logger.getLogger(ConnectedConnection.class.getName()).log(Level.SEVERE, null, ex);
       } catch (SmackException ex) {
           Logger.getLogger(ConnectedConnection.class.getName()).log(Level.SEVERE, null, ex);
       } catch (IOException ex) {
           Logger.getLogger(ConnectedConnection.class.getName()).log(Level.SEVERE, null, ex);
       }
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        Roster roster = Roster.getInstanceFor(connection);
        roster.addRosterListener(new RosterListener() {
            // Ignored events public void entriesAdded(Collection<String> addresses) {}
            public void entriesDeleted(Collection<String> addresses) {}
            public void entriesUpdated(Collection<String> addresses) {}
            public void presenceChanged(Presence presence) {
                System.out.println("Presence changed: " + presence.getFrom() +
                        " " + presence);
            }

            public void entriesAdded(Collection<String> clctn) {

            }
        });
            
            
         
     
     

    }

    @Override
    public void connectionClosed() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reconnectionSuccessful() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reconnectingIn(int seconds) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reconnectionFailed(Exception e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public String DIR="C:\\Users";
    private final HashMap<RemoteFile,Long> incomingFiles = new HashMap();
   

 

  public void requestRemoteFileSize(String JID , File file) throws 
         SmackException.NotConnectedException, 
         SmackException.NoResponseException, XMPPException.XMPPErrorException{
    if ( connessione == null)return;
    
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
                this.outcomingFiles.put(rf, reply.getFileSize());
                System.out.println("file size" + reply.getFileSize() + " " +
                        rf.getJID() + " " + rf);

            }
}
 
  public void findUser(String user) throws SmackException.NotLoggedInException, 
        SmackException.NoResponseException, XMPPException.XMPPErrorException, 
        SmackException.NotConnectedException {
        if ( connessione == null) return;
        Roster roster = Roster.getInstanceFor(connessione);
        roster.createEntry(user, user, null);
  
}

    public HashMap<RemoteFile, Long> getOutcomingFiles() {
        return outcomingFiles;
    }
 public ArrayList<String> getUsers(){
     
   ArrayList<String> lista = new ArrayList<String>();
   if ( connessione == null) return lista;
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
        if ( connessione == null)
            return;
        Roster roster = Roster.getInstanceFor(connessione);
        String receiver = roster.getPresence(JID).getFrom();
        outFile =FileTransferManager.getInstanceFor(connessione).
                createOutgoingFileTransfer(receiver);
        try {
            f = new RandomAccessFile(file.getAbsolutePath(),"r");
            f.seek(this.outcomingFiles.get(new RemoteFile(receiver, file.getName())));
           
        
        } catch (FileNotFoundException ex) {
            Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null, ex);
            return;
        } catch (IOException ex) {
            Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        try {
            os = outFile.sendFile(file.getName(), file.length(), "invio file");
        } catch (XMPPException ex) {
            Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null, ex);
            return;
        } catch (SmackException ex) {
            Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null, ex);
            return ;
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
}
  

