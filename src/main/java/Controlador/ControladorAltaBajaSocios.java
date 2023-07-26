/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Actividad;
import Modelo.ActividadDAO;
import Modelo.RealizaDAO;
import Modelo.Socio;
import Modelo.SociosDAO;
import Vista.VistaAltaBajaActividad;
import Vista.VistaMensajes;
import Vista.VistaPrincipal;
import Vista.VistaSeleccionSocio;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Set;
import org.hibernate.Session;

/**
 *
 * @author gtaan
 */
public class ControladorAltaBajaSocios implements ActionListener {

    VistaAltaBajaActividad vAltaBaja;
    String opcion;
    Session sesion;
    VistaMensajes vMensajes;
    ActividadDAO actividades;
    SociosDAO socios;
    RealizaDAO realiza;
    VistaSeleccionSocio vSeleccion;

    /**
     * Constructor de ControladorAltaBajaSocios.
     *
     * @param sesion La sesión de Hibernate.
     */
    public ControladorAltaBajaSocios(Session sesion) {
        this.sesion = sesion;
        actividades = new ActividadDAO(sesion);
        socios = new SociosDAO(sesion);
        realiza = new RealizaDAO(sesion);
    }

    private void addListenersAltaBaja() {
        vAltaBaja.jButtonAltaBaja.addActionListener(this);
        vAltaBaja.jButtonVolver.addActionListener(this);
        vAltaBaja.jComboBoxActividad.addActionListener(this);

    }

    private void addListenersSeleccion() {
        vSeleccion.jComboBoxSocio.addActionListener(this);
        vSeleccion.jButtonAlta.addActionListener(this);
        vSeleccion.jButtonBaja.addActionListener(this);
        vSeleccion.jButtonCerrar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Cancelar":
                vAltaBaja.dispose();
                break;
            case "Cerrar":
                vSeleccion.dispose();
                break;
            case "Baja de Actividad":
                try {
                opcion = "Baja";
                actividadesParaAltaBaja(socios.GetSocioByNumSocio((String) vSeleccion.jComboBoxSocio.getSelectedItem()));
            } catch (Exception ex) {
                System.out.println("oaos");
            }
            break;
            case "Alta de Actividad":
                try {
                opcion = "Alta";
                actividadesParaAltaBaja(socios.GetSocioByNumSocio((String) vSeleccion.jComboBoxSocio.getSelectedItem()));
            } catch (Exception ex) {
                System.out.println("oaos");
            }
            break;
            case "comboBoxSocioChanged": {
                try {
                    socios = new SociosDAO(sesion);
                    Socio socio = socios.GetSocioByNumSocio((String) vSeleccion.jComboBoxSocio.getSelectedItem());
                    vSeleccion.jTextFieldNomSocio.setText(socio.getNombre());

                } catch (Exception ex) {
                    vMensajes = new VistaMensajes("ERROR CON LA SELECCION DE SOCIO", ex.getMessage());
                }
            }

            break;

            case "comboBoxActividadChanged":
                vAltaBaja.jTextFieldIdActividad.setText(actividades.getActividadByNombre((String) vAltaBaja.jComboBoxActividad.getSelectedItem()).getIdactividad());
                break;
            case "Dar de Alta":

                Socio socioAlta = socios.GetSocioByNumSocio((String) vSeleccion.jComboBoxSocio.getSelectedItem());
                Actividad actividadAlta = actividades.getActividadByID((String) vAltaBaja.jTextFieldIdActividad.getText());
                realiza.altaRealiza(socioAlta, actividadAlta);
                vMensajes = new VistaMensajes("Operación completada con éxito");
                vAltaBaja.dispose();
                break;
            case "Dar de Baja":

                Socio socioBaja = socios.GetSocioByNumSocio((String) vSeleccion.jComboBoxSocio.getSelectedItem());
                Actividad actividadBaja = actividades.getActividadByID((String) vAltaBaja.jTextFieldIdActividad.getText());
                realiza.bajaRealiza(socioBaja, actividadBaja);
                vMensajes = new VistaMensajes("Operación completada con éxito");
                vAltaBaja.dispose();
                break;
        }
    }

    /**
     * Método para mostrar la interfaz de alta y baja de socios en la ventana
     * principal.
     *
     * @param vPrincipal La vista principal de la aplicación.
     */
    public void AltaBajaSocio(VistaPrincipal vPrincipal) {
        vSeleccion = new VistaSeleccionSocio(vPrincipal, true);
        vSeleccion.setLocationRelativeTo(null);

        try {

            SociosDAO socios = new SociosDAO(sesion);
            ArrayList<Socio> listaSocios = socios.listaSocios();
            //vSeleccion.jComboBoxSocio.removeAllItems();
            for (Socio socio : listaSocios) {
                vSeleccion.jComboBoxSocio.addItem(socio.getNumerosocio());
            }

            Socio socio = socios.GetSocioByNumSocio((String) vSeleccion.jComboBoxSocio.getSelectedItem());
            vSeleccion.jTextFieldNomSocio.setText(socio.getNombre());

            //actividadesParaAltaBaja(socio);
        } catch (Exception ex) {
            vMensajes = new VistaMensajes("ERROR EN MUESTREO " + opcion.toUpperCase() + " DE SOCIOS", ex.getMessage());
        }

        //vAltaBaja.jTextFieldIdActividad.setText(actividades.getActividadByNombre((String) vAltaBaja.jComboBoxActividad.getSelectedItem()).getIdactividad());
        addListenersSeleccion();
        vSeleccion.setVisible(true);

    }

    /**
     * Obtiene y muestra las actividades disponibles para dar de alta o baja a
     * un socio.
     *
     * @param socio El socio para el cual se mostrarán las actividades
     * disponibles.
     * @throws Exception Si ocurre un error al obtener las actividades.
     */
    private void actividadesParaAltaBaja(Socio socio) throws Exception {

        vAltaBaja = new VistaAltaBajaActividad(vSeleccion, true);
        vAltaBaja.setLocationRelativeTo(null);
        vAltaBaja.jTextFieldNombre.setText(socio.getNombre());

        Set<Actividad> actividadesSocio = realiza.GetRealiza(socio);
        actividades = new ActividadDAO(sesion);
        ArrayList<Actividad> listaActividades = actividades.listaActividades();

        vAltaBaja.jComboBoxActividad.removeAllItems();

        for (Actividad actividad : listaActividades) {
            boolean matriculada = false;
            for (Actividad actividadMatriculada : actividadesSocio) {
                if (actividad.getNombre().equals(actividadMatriculada.getNombre())) {
                    matriculada = true;
                }
            }
            if (!matriculada && opcion.equals("Alta")) {
                vAltaBaja.jComboBoxActividad.addItem(actividad.getNombre());
            } else if (matriculada && opcion.equals("Baja")) {
                vAltaBaja.jComboBoxActividad.addItem(actividad.getNombre());
            }
        }
        if (vAltaBaja.jComboBoxActividad.getSelectedItem() != null) {
            vAltaBaja.jTextFieldIdActividad.setText((actividades.getActividadByNombre((String) vAltaBaja.jComboBoxActividad.getSelectedItem())).getIdactividad());
        }
        vAltaBaja.jButtonAltaBaja.setText("Dar de " + opcion);
        vAltaBaja.jButtonAltaBaja.setActionCommand("Dar de " + opcion);
        addListenersAltaBaja();
        vAltaBaja.setVisible(true);

    }
}
