/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Conexion;
import Modelo.HibernateUtilMariaDB;
import Modelo.HibernateUtilOracle;
import Vista.VistaLogin;
import Vista.VistaMensajes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import org.hibernate.Session;

/**
 *
 * @author gtaan
 */
public class ControladorLogin implements ActionListener {

    VistaLogin vLogin;
    VistaMensajes avisos;
    ControladorPrincipal cPrincipal;
    Session sesion;

    String server;

    private void addListeners() {
        vLogin.ButtonConectar.addActionListener(this);
        vLogin.ButtonSalir.addActionListener(this);
    }

    /**
     * Constructor de la clase ControladorLogin.
     * Crea una instancia de VistaLogin y muestra la vista.
     * Agrega los listeners a los componentes de la vista.
     */
    public ControladorLogin() {
        vLogin = new VistaLogin();
        vLogin.setLocationRelativeTo(null);
        vLogin.setVisible(true);
        addListeners();
    }

    /**
     * Maneja los eventos generados por los componentes de la vista.
     * 
     * @param e El evento generado.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Conectar":
                sesion = conectar();
                if (sesion != null) {
                    avisos = new VistaMensajes("Conexión correcta con Hibernate");
                    vLogin.dispose();
                    ControladorPrincipal cPrincipal = new ControladorPrincipal(sesion, server);
                } else {
                    avisos = new VistaMensajes("ERROR", "Error en la conexión. No se ha podido crear una sesión\n");
                }

                break;
            case "Salir":
                vLogin.dispose();
                break;

        }
    }

    /**
     * Establece la conexión con la base de datos seleccionada en la vista.
     * 
     * @return La sesión de Hibernate establecida después de una conexión exitosa, o null si ocurre un error.
     */
    public Session conectar() {
        server = (String) vLogin.ComboBoxServidor.getSelectedItem();

        if ("Oracle".equals(server)) {
            sesion = HibernateUtilOracle.getSessionFactory().openSession();
        } else {
            sesion = HibernateUtilMariaDB.getSessionFactory().openSession();
        }

        return sesion;
    }

}
