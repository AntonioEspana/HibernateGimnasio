/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Actividad;
import Modelo.ActividadDAO;
import Modelo.RealizaDAO;
import Modelo.Socio;
import Vista.VistaMensajes;
import Vista.VistaPrincipal;
import Vista.VistaActividad;
import Vista.VistaAltaBajaActividad;
import Vista.VistaCuotaActividad;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Set;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Session;

/**
 *
 * @author gtaan
 */
public class ControladorSociosPorActividad implements ActionListener {

    VistaActividad vistaSociosActividad;
    VistaMensajes vMensajes;
    DefaultTableModel tableModel;
    JTable table;
    Session sesion;
    ActividadDAO actividades;
    String server;
    RealizaDAO realiza;
    VistaCuotaActividad vCuota;

    public ControladorSociosPorActividad(Session sesion, String server) {

        this.sesion = sesion;
        this.server = server;
        actividades = new ActividadDAO(sesion);
        realiza = new RealizaDAO(sesion);
    }

    /**
     * Genera la vista de actividad y muestra la tabla de socios inscritos a la
     * primera actividad
     *
     * @param vPrincipal Vista principal de la aplicación.
     */
    public void generarVistaActividad(VistaPrincipal vPrincipal) {
        vistaSociosActividad = new VistaActividad(vPrincipal, true);
        vistaSociosActividad.setLocationRelativeTo(null);
        addListeners();
        try {
            if (server.equals("Oracle")) {
                tableModel = actividades.ObtenerSociosInscritosOracle("AC01");
            } else {
                tableModel = actividades.ObtenerSociosInscritosMariaDB("AC01");
            }
        } catch (SQLException ex) {
            vMensajes = new VistaMensajes("ERROR EN MUESTREO DE SOCIOS POR ACTIVIDAD", ex.getMessage());
        }
        mostrarTabla(tableModel);
        vistaSociosActividad.setVisible(true);
    }

    private void addListeners() {
        vistaSociosActividad.jComboBoxActividades.addActionListener(this);
        vistaSociosActividad.jButtonCerrar.addActionListener(this);
    }

    /**
     * Maneja los eventos generados por los componentes de la vista de
     * actividad.
     *
     * @param e Evento generado.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Cerrar":
                vistaSociosActividad.dispose();
                break;
            case "comboBoxChanged":

                String actividad = (String) vistaSociosActividad.jComboBoxActividades.getSelectedItem();
                String ID = ConversorActividadID(actividad);

                try {

                    if (server.equals("Oracle")) {
                        tableModel = actividades.ObtenerSociosInscritosOracle(ID);
                    } else {
                        tableModel = actividades.ObtenerSociosInscritosMariaDB(ID);
                    }
                    mostrarTabla(tableModel);
                } catch (SQLException ex) {
                    vMensajes = new VistaMensajes("FALLO EN VISUALIZACION", ex.getMessage());
                }
                break;
            case "comboBoxChangedCuota":

                String actividadCuota = (String) vCuota.jComboBoxActividades.getSelectedItem();
                String IDCuota = ConversorActividadID(actividadCuota);

                tableModel = realiza.GetSocios(actividades.getActividadByID(IDCuota));
                mostrarTablaCuota(tableModel);

                break;
            case "CerrarCuota":
                vCuota.dispose();
                break;

        }

    }

    /**
     * Convierte el nombre de la actividad en su correspondiente identificador.
     *
     * @param actividad Nombre de la actividad.
     * @return Identificador de la actividad.
     */
    private String ConversorActividadID(String actividad) {
        String ID = null;
        switch (actividad) {
            case "Body Combat" ->
                ID = "AC01";
            case "Body Pump" ->
                ID = "AC02";
            case "HIIT" ->
                ID = "AC03";
            case "Kick Boxing" ->
                ID = "AC04";
            case "Boxeo" ->
                ID = "AC05";
            case "Zumba" ->
                ID = "AC06";
            case "Pilates" ->
                ID = "AC07";
            case "Yoga" ->
                ID = "AC08";
            case "Tai Chí" ->
                ID = "AC09";
            case "Spinning" ->
                ID = "AC10";
        }
        return ID;
    }

    /**
     * Muestra la tabla de socios en la vista de actividad.
     *
     * @param tableModel Modelo de la tabla de socios.
     */
    private void mostrarTabla(DefaultTableModel tableModel) {
        table = new JTable(tableModel);
        JScrollPane tabla = new JScrollPane(table);

        vistaSociosActividad.jPanelMuestreo.removeAll();

        Dimension sizeJPanel = vistaSociosActividad.jPanelMuestreo.getSize();//Para obtener el tamaño del JPanel para luego ajustar la visualización de los datos en concordancia
        vistaSociosActividad.jPanelMuestreo.add(tabla);
        tabla.setBounds(0, 0, sizeJPanel.width, sizeJPanel.height);

        vistaSociosActividad.jPanelMuestreo.revalidate();
        vistaSociosActividad.jPanelMuestreo.repaint();
    }

//    public void getSociosCuotas(String actividadID){
//        Actividad actividad = actividades.getActividadByID(actividadID);
//        Set<Socio> GetSocios = realiza.GetSocios(actividad);
//    }
    public void generarVistaCuotaActividad(VistaPrincipal vPrincipal) {
        vCuota = new VistaCuotaActividad(vPrincipal, true);
        vCuota.setLocationRelativeTo(null);
        addListenersCuota();
        InicioCuota();
        vCuota.setVisible(true);

    }

    private void InicioCuota() {
        String actividadCuota = (String) vCuota.jComboBoxActividades.getSelectedItem();
        String IDCuota = ConversorActividadID(actividadCuota);

        tableModel = realiza.GetSocios(actividades.getActividadByID(IDCuota));
            System.out.println("a");
        mostrarTablaCuota(tableModel);
    }

    private void addListenersCuota() {
        vCuota.jComboBoxActividades.addActionListener(this);
        vCuota.jButtonCerrar.addActionListener(this);
        
    }

    private void mostrarTablaCuota(DefaultTableModel tableModel) {
        table = new JTable(tableModel);
        JScrollPane tabla = new JScrollPane(table);

        vCuota.jTextFieldNumSocios.setText(String.valueOf(tableModel.getRowCount()));
        String actividadCuota = (String) vCuota.jComboBoxActividades.getSelectedItem();
        String IDCuota = ConversorActividadID(actividadCuota);
        int precioBase = actividades.getActividadByID(IDCuota).getPreciobasemes().intValueExact();
        vCuota.jTextFieldPrecioBase.setText(String.valueOf(precioBase) + "€");
        vCuota.jTextFieldCuotaNormal.setText(String.valueOf(precioBase * tableModel.getRowCount()) + "€");
        int cuotaDescuento = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            switch ((Character) tableModel.getValueAt(i, 1)) {
                case 'A' ->
                    cuotaDescuento += precioBase;
                case 'B' ->
                    cuotaDescuento += (precioBase * 0.9);
                case 'C' ->
                    cuotaDescuento += (precioBase * 0.8);
                case 'D' ->
                    cuotaDescuento += (precioBase * 0.7);
                case 'E' ->
                    cuotaDescuento += (precioBase * 0.6);
            }
        }
        vCuota.jTextFieldCuotaDescuentos.setText(String.valueOf(cuotaDescuento) + "€");
        vCuota.jPanelMuestreo.removeAll();

        Dimension sizeJPanel = vCuota.jPanelMuestreo.getSize();//Para obtener el tamaño del JPanel para luego ajustar la visualización de los datos en concordancia
        vCuota.jPanelMuestreo.add(tabla);
        tabla.setBounds(0, 0, sizeJPanel.width, sizeJPanel.height);

        vCuota.jPanelMuestreo.revalidate();
        vCuota.jPanelMuestreo.repaint();
    }
}
