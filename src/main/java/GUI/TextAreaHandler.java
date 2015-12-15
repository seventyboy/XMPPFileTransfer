/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.LogRecord;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author ivo.dipumpo
 */
public class TextAreaHandler  extends java.util.logging.Handler{

   private final JTextArea textArea ;

    public TextAreaHandler(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void publish(final LogRecord record) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
              
        
                textArea.append(record.getMessage());
            }

        });
    }

    public JTextArea getTextArea() {
        return this.textArea;
    }


    @Override
    public void close() throws SecurityException {
      
    }

    @Override
    public void flush() {
      
    }
    
}
