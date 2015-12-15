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
    private long bytesRead;

    public long getBytesRead() {
        return bytesRead;
    }
    private String status;

    public String getStatus() {
        return status;
    }

    private long localSize;

    public long getLocalSize() {
        return localSize;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setBytesRead(long bytesRead) {
        this.bytesRead = bytesRead;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLocalSize(long localSize) {
        this.localSize = localSize;
    }
    private double speed;

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setTransferData(long localSize, long bytesRead, int progress, double speed, String status) {
        
            setLocalSize(localSize);
           setBytesRead(bytesRead);
           setProgress(progress);
           setSpeed(speed);
           setStatus(status);
    
    }
}
