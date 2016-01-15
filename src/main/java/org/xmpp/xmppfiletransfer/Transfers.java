/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xmpp.xmppfiletransfer;

import java.util.HashMap;

/**
 *
 * @author ivo.dipumpo
 */
public class Transfers {
    private  final HashMap<RemoteFile,Long> incomingFiles = new HashMap();
    private  final HashMap<RemoteFile,Long> outcomingFiles = new HashMap();
    private static   String DIR = "c:\\users";

    public Transfers() {
    }

    public Long putIncomingFile(RemoteFile key, Long value) {
        synchronized(incomingFiles){
           return incomingFiles.put(key, value); 
        }
        
    }

    public static String getDIR() {
        return DIR;
    }

    public static void setDIR(String DIR) {
        Transfers.DIR = DIR;
    }

    public Long getIncomingFile(Object key) {
        synchronized(incomingFiles){
              return incomingFiles.get(key); 
        }
     
    }

    public Long getOutcomingFile(Object key) {
        synchronized(outcomingFiles){
          return outcomingFiles.get(key);
    }
      
    }

    public Long putOutcomingFile(RemoteFile key, Long value) {
        synchronized(outcomingFiles){
          return outcomingFiles.put(key, value);
        }
    }
    
}
