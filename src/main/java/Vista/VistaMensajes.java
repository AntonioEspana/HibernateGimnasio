/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vista;

import javax.swing.JOptionPane;

/**
 *
 * @author gtaan
 */
public class VistaMensajes {

    public VistaMensajes(String titulo,String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }
    public VistaMensajes(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje);
    }

}
