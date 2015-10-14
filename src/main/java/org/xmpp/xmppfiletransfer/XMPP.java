package org.xmpp.xmppfiletransfer;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.iqrequest.AbstractIqRequestHandler;
import org.jivesoftware.smack.iqrequest.IQRequestHandler;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ivo.dipumpo
 */
public class XMPP 
{
    

    public String DIR="C:\\Users";
    
 public SenderHandler createHandler(){
     
      ConnectionConfiguration config;  
        XMPPTCPConnection conn = null;
        SSLContext sc = null;
    final SenderHandler handler ;
       
     
      sc = this.buildSSLContext();





        SmackConfiguration.DEBUG = true;




        XMPPTCPConnectionConfiguration conf =
                XMPPTCPConnectionConfiguration.builder()
                .setHost(server)
                .setServiceName(server)
                .setUsernameAndPassword(user, password)
                .setSecurityMode(SecurityMode.ifpossible)

                .setCustomSSLContext(sc)
                .setSendPresence(true)
                .setPort(5222)
                
               
                .build();




        conn = new XMPPTCPConnection (conf);
        
       Transfers transfers = new Transfers();
          
        transfers.DIR = this.DIR;
    ProviderManager.addIQProvider(
         IQRemoteSize.ELEMENT, IQRemoteSize.NAMESPACE,new RemoteSizeProvider());
   
     ReconnectionManager.getInstanceFor(conn).enableAutomaticReconnection();
     
    handler = new SenderHandler(conn, transfers);
    ReceiveFileRequestHandler receiverHandler = new ReceiveFileRequestHandler (
             transfers, IQRemoteSize.ELEMENT, IQRemoteSize.NAMESPACE,IQ.Type.get, 
                 IQRequestHandler.Mode.async);
    
    FileReceiverHandler fileReceiver = new FileReceiverHandler(transfers);
    
    FileTransferManager.getInstanceFor(conn).addFileTransferListener( fileReceiver);
     
     conn.registerIQRequestHandler(receiverHandler);
     
    System.out.println("clietn avviato");
    return handler;
 }
            
       
  
       
    

    public XMPP(String server, int port, String user, String password) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    private final String server  ;

    public String getServer() {
        return server;
    }
   
    private final int port  ;

    public int getPort() {
        return port;
    }

    private final String user  ;


    private final String password  ;

    public String getPassword() {
        return password;
    }

 
      private CopyOnWriteArrayList<ConnectionListener> listeners = 
              new CopyOnWriteArrayList<> ();
    
    public void addListener(ConnectionListener l) {
       listeners.add(l);
   
       
    }


    private SSLContext buildSSLContext(){
         SSLContext sc = null;
          final TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {

                public void checkClientTrusted( final X509Certificate[] chain,
                        final String authType ) {
                }

                public void checkServerTrusted( final X509Certificate[] chain,
                        final String authType ) {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            } };
     
        // Install the all-trusting trust manager
        final SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance( "SSL" );
          sslContext.init(null, trustAllCerts,new java.security.SecureRandom());
            sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new SecureRandom());

         final SSLSocketFactory sslSocketFactory =sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (KeyManagementException ex) {
            Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        return sc;
    }
}
