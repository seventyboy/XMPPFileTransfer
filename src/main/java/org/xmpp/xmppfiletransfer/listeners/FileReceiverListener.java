/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xmpp.xmppfiletransfer.listeners;

import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.xmpp.xmppfiletransfer.TransferMonitor;

/**
 *
 * @author ivo.dipumpo
 */
public interface FileReceiverListener {
  void newIncomingFile(TransferMonitor inFile);
 
  void incomingFileExpired(TransferMonitor inFile);
}
