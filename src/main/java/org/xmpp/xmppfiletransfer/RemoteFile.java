/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xmpp.xmppfiletransfer;

/**
 *
 * @author ivo.dipumpo
 */
public class RemoteFile {
  
    private final String JID ;

    public String getJID() {
        return JID;
    }
  
    private final String fileName  ;

    public String getFileName() {
        return fileName;
    }

    public RemoteFile(String user, String fileName) {
        this.JID = user;
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)                return false;
    if(!(obj instanceof RemoteFile)) return false;

    RemoteFile other = (RemoteFile) obj;

    if(! this.JID.equals(other.JID)) return false;
    if(! this.fileName.equals(other.fileName))   return false;

    return true;

    }

    @Override
    public int hashCode() {
        return this.fileName.hashCode()*this.JID.hashCode();
    }

    

}
