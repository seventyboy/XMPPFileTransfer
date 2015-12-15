/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package GUI.models;

import java.util.ArrayList;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 *
 * @author ivo.dipumpo
 */
public class ComboModel extends AbstractListModel implements ComboBoxModel {
    
 ArrayList elementi = new ArrayList();
 String selection;

    public ComboModel(ArrayList elems) {
        elementi = elems;
    }
 
    @Override
    public int getSize() {
      return elementi.size();
    }

    @Override
    public Object getElementAt(int index) {
       return elementi.get(index);
    }

    @Override
    public void setSelectedItem(Object anItem) {
      selection = (String)anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selection;
    }
    
}
