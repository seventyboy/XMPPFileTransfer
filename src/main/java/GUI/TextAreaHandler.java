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
                StringWriter text = new StringWriter();
                PrintWriter out = new PrintWriter(text);
                out.println(textArea.getText());
                out.printf("[%s] [Thread-%d]: %s.%s -> %s", record.getLevel(),
                        record.getThreadID(), record.getSourceClassName(),
                        record.getSourceMethodName(), record.getMessage());
                textArea.setText(text.toString());
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
