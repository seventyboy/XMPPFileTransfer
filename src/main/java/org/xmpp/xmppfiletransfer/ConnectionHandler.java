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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.bytestreams.socks5.packet.Bytestream;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.iqregister.AccountManager;

/**
 *
 * @author ivo.dipumpo
 */
public class ConnectionHandler {
    private final XMPPTCPConnection connessione;
    private final Transfers transfers;
    private volatile boolean enabled = false;
    private volatile boolean noStreamHost = false;
    private final List<String> proxyBlacklist = Collections.synchronizedList(new LinkedList<String>());
    private String IDByteStream ="";
    private final Object lock = new Object();
     private CopyOnWriteArrayList<ConnectionListener> listeners = 
              new CopyOnWriteArrayList<> ();
    

    public ConnectionHandler(XMPPTCPConnection connessione, Transfers transfers) {
        this.connessione = connessione;
        this.transfers = transfers;
        
    }
    public void start(boolean creaPassword){
        try {
            
           
            connessione.connect();
            System.out.println("creaz supp" +   AccountManager.getInstance(connessione).getAccountAttributes());
            if (creaPassword){
               AccountManager.getInstance(connessione).sensitiveOperationOverInsecureConnection(true);
               AccountManager.getInstance(connessione).createAccount(
                       (String)connessione.getConfiguration().getUsername(),
                       connessione.getConfiguration().getPassword());
            }    
          
           
       
       
            connessione.login();
          Logger.getLogger(XMPP.class.getName());
        } catch (SmackException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMPPException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
     
    }

    public XMPPTCPConnection getConnessione() {
        return connessione;
    }
    public Sender createNewOutgoingFileTransfer(String JID, File file){
        return new Sender(connessione, JID, file);
    }

    
    public void findUser(String user)  {
        
        Roster roster = Roster.getInstanceFor(connessione);
        try {
          
            roster.createEntry(user, user, null);
        } catch (SmackException.NotLoggedInException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SmackException.NoResponseException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMPPException.XMPPErrorException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SmackException.NotConnectedException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
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
    

    

   

   


    public void addConnectionListener(ConnectionListener connectionListener) {
        connessione.addConnectionListener(connectionListener);
    }

   public ArrayList verifcaProxies(){
       ArrayList listaProxy = new ArrayList();
         
        XMPPConnection connection = this.connessione;
        ServiceDiscoveryManager serviceDiscoveryManager = ServiceDiscoveryManager.getInstanceFor(connection);

        ArrayList<String> proxies = new ArrayList<String>();

        // get all items from XMPP server
        DiscoverItems discoverItems;
        try {
            discoverItems = serviceDiscoveryManager.discoverItems(connessione.getServiceName());
            for (DiscoverItems.Item item : discoverItems.getItems()) {
            // skip blacklisted servers
            if (proxyBlacklist.contains(item.getEntityID())) {
                continue;
            }

            DiscoverInfo proxyInfo;
            try {
                proxyInfo = serviceDiscoveryManager.discoverInfo(item.getEntityID());
            }
            catch (SmackException.NoResponseException|XMPPErrorException e) {
                // blacklist errornous server
                proxyBlacklist.add(item.getEntityID());
                continue;
            }

            if (proxyInfo.hasIdentity("proxy", "bytestreams")) {
                proxies.add(item.getEntityID());
                listaProxy.add(item.getEntityID());
            } else {
                /*
                 * server is not a SOCKS5 proxy, blacklist server to skip next time a Socks5
                 * bytestream should be established
                 */
                proxyBlacklist.add(item.getEntityID());
            }
        }

        } catch (SmackException.NoResponseException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMPPErrorException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SmackException.NotConnectedException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        // query all items if they are SOCKS5 proxies
        
        System.out.println( " i proxy " +proxies ) ;
        return proxies;
    }

    /**
     * Returns a list of stream hosts containing the IP address an the port for the given list of
     * SOCKS5 proxy JIDs. The order of the returned list is the same as the given list of JIDs
     * excluding all SOCKS5 proxies who's network settings could not be determined. If a local
     * SOCKS5 proxy is running it will be the first item in the list returned.
     * 
     * @param proxies a list of SOCKS5 proxy JIDs
     * @return a list of stream hosts containing the IP address an the port
     */
    private List<Bytestream.StreamHost> determineStreamHostInfos(List<String> proxies) {
        List<Bytestream.StreamHost> streamHosts = new ArrayList<Bytestream.StreamHost>();

        // add local proxy on first position if exists
       

        // query SOCKS5 proxies for network settings
        for (String proxy : proxies) {
            Bytestream streamHostRequest = createStreamHostRequest(proxy);
            try {
                Bytestream response = (Bytestream) connessione.createPacketCollectorAndSend(
                                streamHostRequest).nextResultOrThrow();
                streamHosts.addAll(response.getStreamHosts());
            }
            catch (Exception e) {
                // blacklist errornous proxies
                this.proxyBlacklist.add(proxy);
            }
        }

        return streamHosts;
    }
    private Bytestream createStreamHostRequest(String proxy) {
        Bytestream request = new Bytestream();
        request.setType(IQ.Type.get);
        request.setTo(proxy);
        return request;
    }
}
