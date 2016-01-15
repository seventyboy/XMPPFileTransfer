/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xmpp.xmppfiletransfer;

import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

/**
 *
 * @author ivo.dipumpo
 */
public class TransferMonitor {

    private final FileTransfer fileTransfer  ;

    public FileTransfer getFileTransfer() {
        return fileTransfer;
    }

    public TransferMonitor(FileTransfer fileTransfer) {
        this.fileTransfer = fileTransfer;
    }

     private int progress;

    public int getProgress() {
        return progress;
    }
    private long transferredBytes;

    public long getTransferredBytes() {
        return transferredBytes;
    }
    private String status;

    public String getStatus() {
        return status;
    }

    private long size;

    public long getSize() {
        return size;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setTransferredBytes(long transferredBytes) {
        this.transferredBytes = transferredBytes;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSize(long size) {
        this.size = size;
    }
    private double speed;

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setTransferData(long localSize, long bytesRead, int progress, double speed, String status) {
        
            setSize(localSize);
           setTransferredBytes(bytesRead);
           setProgress(progress);
           setSpeed(speed);
           setStatus(status);
    
    }
}
