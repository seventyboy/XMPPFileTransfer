/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.models;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JProgressBar;
import javax.swing.table.AbstractTableModel;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.xmpp.xmppfiletransfer.TransferMonitor;
import org.xmpp.xmppfiletransfer.listeners.FileReceiverListener;
import org.xmpp.xmppfiletransfer.listeners.FileSenderListener;

/**
 *
 * @author ivo.dipumpo
 */
public class TableModelOutcomingFileTransfers extends AbstractTableModel implements
        FileSenderListener{
      private static final String[] columnNames = {
                "requestor",
                "file name",
                "size ",
                "local size",
                "avanzamento",
                "bytes inviati",
                "velocità",
                "transfer status"} ;
 
   private static final Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class,java.lang.Long.class, java.lang.Long.class, 
                javax.swing.JProgressBar.class,java.lang.Long.class ,java.lang.Double.class,
                java.lang.String.class
              
            };
    private final CopyOnWriteArrayList<TransferMonitor> outcomingFiles = 
            new CopyOnWriteArrayList();

    public CopyOnWriteArrayList<TransferMonitor> getOutcomingFiles() {
        return outcomingFiles;
    }

   
    @Override
    public int getRowCount() {
      return outcomingFiles.size();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return types[columnIndex].getClass(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    @Override
    public int getColumnCount() {
         return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      Object ritorno = null;
      
            
            switch (columnIndex){
               case 0 :
                       ritorno = outcomingFiles.get(rowIndex).getFileTransfer().getPeer();
                       break;
               case 1 :
                       ritorno = outcomingFiles.get(rowIndex).getFileTransfer().getFileName();
                       break;
               case 2 :
                       ritorno = outcomingFiles.get(rowIndex).getFileTransfer().getFileSize();
                       break;
               case 3 :
                       ritorno = outcomingFiles.get(rowIndex).getSize();
                       break;
               case 4 :
                      JProgressBar bar = new JProgressBar();
                   
                      bar.setValue(outcomingFiles.get(rowIndex).getProgress());
                       ritorno = bar ;
                       break;
               case 5 :
                       ritorno =outcomingFiles.get(rowIndex).getTransferredBytes() ;
                    break;
               case 6:
                       ritorno = outcomingFiles.get(rowIndex).getSpeed();
                       
                    break;
                   
               case 7 :
                        ritorno = outcomingFiles.get(rowIndex).getStatus();
                    break;     
              
     
            
            }
         
        return ritorno;
    } 

    @Override
    public void newOutcomingFile(TransferMonitor inFile) {
      outcomingFiles.add(inFile);
        this.fireTableDataChanged();
    }

    @Override
    public void outcomingFileExpired(TransferMonitor inFile) {
       outcomingFiles.remove(inFile);
        this.fireTableDataChanged();
    }
  
   
  
}
