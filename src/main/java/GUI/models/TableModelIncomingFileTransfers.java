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

/**
 *
 * @author ivo.dipumpo
 */
public class TableModelIncomingFileTransfers extends AbstractTableModel implements
        FileReceiverListener{
      private static final String[] columnNames = {
                "requestor",
                "file name",
                "size ",
                "local size",
                "avanzamento",
                "bytes ricevuti",
                "velocità",
                "transfer status"} ;
 
   private static final Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class,java.lang.Long.class, java.lang.Long.class, 
                javax.swing.JProgressBar.class,java.lang.Long.class ,java.lang.Double.class,
                java.lang.String.class
              
            };
    private final CopyOnWriteArrayList<TransferMonitor> incomingFiles = 
            new CopyOnWriteArrayList();

    public CopyOnWriteArrayList<TransferMonitor> getIncomingFiles() {
        return incomingFiles;
    }

    @Override
    public void newIncomingFile(TransferMonitor inFile) {
        
        incomingFiles.add(inFile);
        this.fireTableDataChanged();
       
    }

   

    @Override
    public void incomingFileExpired(TransferMonitor inFile) {
        
        
          incomingFiles.remove(inFile);
        this.fireTableDataChanged();
  
    }
  
    @Override
    public int getRowCount() {
      return incomingFiles.size();
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
                       ritorno = incomingFiles.get(rowIndex).getFileTransfer().getPeer();
                       break;
               case 1 :
                       ritorno = incomingFiles.get(rowIndex).getFileTransfer().getFileName();
                       break;
               case 2 :
                       ritorno = incomingFiles.get(rowIndex).getFileTransfer().getFileSize();
                       break;
               case 3 :
                       ritorno = incomingFiles.get(rowIndex).getLocalSize();
                       break;
               case 4 :
                      JProgressBar bar = new JProgressBar();
                   
                      bar.setValue(incomingFiles.get(rowIndex).getProgress());
                       ritorno = bar ;
                       break;
               case 5 :
                       ritorno =incomingFiles.get(rowIndex).getBytesRead() ;
                    break;
               case 6:
                       ritorno = incomingFiles.get(rowIndex).getSpeed();
                       
                    break;
                   
               case 7 :
                        ritorno = incomingFiles.get(rowIndex).getStatus();
                    break;     
              
     
            
            }
         
        return ritorno;
    } 
  
   
  
}
