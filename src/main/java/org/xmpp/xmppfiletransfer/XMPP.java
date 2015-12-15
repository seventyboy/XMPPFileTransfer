package org.xmpp.xmppfiletransfer;


import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
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
import org.jivesoftware.smack.iqrequest.IQRequestHandler;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.ProviderManager;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.ping.PingManager;
import org.xmpp.xmppfiletransfer.listeners.FileReceiverListener;


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
    

    
 public ConnectionHandler createHandler(){
     
      ConnectionConfiguration config;  
        XMPPTCPConnection conn = null;
        SSLContext sc = null;
    
       
     
      sc = this.buildSSLContext();





        SmackConfiguration.DEBUG = true;




        XMPPTCPConnectionConfiguration conf =
                XMPPTCPConnectionConfiguration.builder()
      
                .setServiceName(server)
                .setUsernameAndPassword(user, password)
                .setSecurityMode(SecurityMode.ifpossible)
                 .allowEmptyOrNullUsernames()
                .setCustomSSLContext(sc)
                .setSendPresence(true)
                .setPort(5222)
               
        
                .build()
                ;




        conn = new XMPPTCPConnection (conf);
        conn.setPacketReplyTimeout(10000);
    
        PingManager.getInstanceFor(conn).setPingInterval(300);
       Transfers transfers = new Transfers();
       

        
    ProviderManager.addIQProvider(
         IQRemoteSize.ELEMENT, IQRemoteSize.NAMESPACE,new RemoteSizeProvider());
   
     ReconnectionManager.getInstanceFor(conn).enableAutomaticReconnection();
    
     ConnectionHandler handler = new ConnectionHandler((XMPPTCPConnection) conn, transfers);
     
   
   
    ReceiveFileRequestHandler receiverHandler = new ReceiveFileRequestHandler (
             transfers, IQRemoteSize.ELEMENT, IQRemoteSize.NAMESPACE,IQ.Type.get, 
                 IQRequestHandler.Mode.async);
    
    FileReceiverHandler fileReceiver = new FileReceiverHandler(transfers);
    fileReceiver.setListener(receiverListener);
    
   
    FileTransferManager.getInstanceFor(conn).addFileTransferListener( fileReceiver);
          
     conn.registerIQRequestHandler(receiverHandler);
       Logger.getLogger(XMPP.class.getName()).log(Level.INFO, "connesso cazzarola!");
   
     
     return handler;
 }
            
       
  
       
    

    public XMPP(String server, int port, String user, String password, boolean creaUser) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.password = password;
        this.creaUser = creaUser;
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
    private final boolean creaUser ;
 
      private CopyOnWriteArrayList<ConnectionListener> listeners = 
              new CopyOnWriteArrayList<> ();
    


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
    public void addFileReceiverListener(FileReceiverListener listener){
        receiverListener = listener;
    }
    
    private FileReceiverListener receiverListener;

    public FileReceiverListener getReceiverListener() {
        return receiverListener;
    }

}
