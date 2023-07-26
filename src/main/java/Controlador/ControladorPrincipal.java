/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Vista.VistaMensajes;
import Vista.VistaPrincipal;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Statement;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import org.hibernate.Session;

/**
 *
 * @author gtaan
 */
public class ControladorPrincipal implements ActionListener {

    VistaPrincipal vPrincipal;
//    Conexion conexion;
    VistaMensajes vMensajes;
    Statement stmt;
    String tablaActual;
    JTable table;
    ControladorMonitores cMonitores;
    ControladorSocios cSocios;
    ControladorSociosPorActividad cActividad;
    Session sesion;
    ControladorAltaBajaSocios cAltaBaja;
    String server;

    /**
     * Constructor de la clase ControladorPrincipal.
     *
     * @param sesion El objeto de sesión de Hibernate.
     * @param server El servidor de la base de datos.
     */
    public ControladorPrincipal(Session sesion, String server) {
        this.server = server;
        vPrincipal = new VistaPrincipal();
        vPrincipal.setVisible(true);
        addListeners();
        this.sesion = sesion;
        cMonitores = new ControladorMonitores(sesion);
        cSocios = new ControladorSocios(sesion);
        cActividad = new ControladorSociosPorActividad(sesion, server);
        cAltaBaja = new ControladorAltaBajaSocios(sesion);

        tablaMonitores();
    }

    private void addListeners() {
        vPrincipal.ButtonCerrar.addActionListener(this);
        vPrincipal.jMenuItemGestionSocios.addActionListener(this);
        vPrincipal.jMenuItemGestionMonitores.addActionListener(this);
        vPrincipal.jButtonActualizacionElemento.addActionListener(this);
        vPrincipal.jButtonBajaElemento.addActionListener(this);
        vPrincipal.jButtonNuevoElemento.addActionListener(this);
        vPrincipal.jMenuItemSociosActividad.addActionListener(this);
        vPrincipal.jMenuItemAltaBajaSocioActividad.addActionListener(this);
        vPrincipal.jMenuItemBuscarSocio.addActionListener(this);
        vPrincipal.jMenuItemCuotaActividad.addActionListener(this);
        vPrincipal.jMenuItemCuotaMonitor.addActionListener(this);
    }

    /**
     * Método actionPerformed para manejar los eventos de la VistaPrincipal.
     *
     * @param e El evento de acción.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Cerrar":
                // Acción: Cerrar la ventana principal y cerrar la sesión de Hibernate
                try {
                vPrincipal.dispose();
                sesion.close();
            } catch (NullPointerException e2) {
                // Manejo de excepción: Mostrar un mensaje de error si no se ha conectado a ninguna base de datos previamente
                vMensajes = new VistaMensajes("FALLO DE DESCONEXION", "No se ha conectado a ninguna BD previamente");
            }
            break;

            case "GestionSocios":
                // Acción: Construir y mostrar la tabla de socios
                tablaSocios();
                break;
            case "GestionMonitores":
                // Acción: Construir y mostrar la tabla de monitores
                tablaMonitores();
                break;
            case "ActualizacionElemento":
                switch (tablaActual) {
                    case "Monitores":
                        try {
                        // Acción: Actualizar un monitor seleccionado en la tabla
                        int aux = table.getSelectedRow();
                        cMonitores.ActualizarMonitor(aux, table);
                        tablaMonitores();
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        // Manejo de excepción: Mostrar un mensaje de error si no se ha seleccionado un monitor previamente
                        vMensajes = new VistaMensajes("Seleccione un monitor a modificar",
                                "Necesita seleccionar un monitor previamente");
                    }
                    break;
                    case "Socios":
                        try {
                        // Acción: Actualizar un socio seleccionado en la tabla
                        int aux = table.getSelectedRow();
                        cSocios.ActualizarSocio(aux, table);
                        tablaSocios();
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        // Manejo de excepción: Mostrar un mensaje de error si no se ha seleccionado un socio previamente
                        vMensajes = new VistaMensajes("Seleccione un socio a modificar",
                                "Necesita seleccionar un socio previamente");
                    }
                    break;
                }
                break;
            case "NuevoElemento":
                switch (tablaActual) {
                    case "Monitores":
                        // Acción: Crear un nuevo monitor
                        cMonitores.NuevoMonitor();
                        tablaMonitores();
                        break;
                    case "Socios":
                        // Acción: Crear un nuevo socio
                        cSocios.NuevoSocio();
                        tablaSocios();
                        break;
                }
                break;
            case "BajaElemento":
                switch (tablaActual) {
                    case "Monitores":
                        try {
                        // Acción: Borrar un monitor seleccionado de la tabla
                        int aux = table.getSelectedRow();
                        if (0 == JOptionPane.showConfirmDialog(null, "¿Seguro que quiere dar de baja a "
                                + (String) table.getValueAt(aux, 1) + "?",
                                "ATENCION", YES_NO_OPTION,
                                WARNING_MESSAGE)) {
                            cMonitores.BorrarMonitor((String) table.getValueAt(aux, 0));
                            tablaMonitores();
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        // Manejo de excepción: Mostrar un mensaje de error si no se ha seleccionado un monitor previamente
                        vMensajes = new VistaMensajes("Seleccione un monitor a modificar",
                                "Necesita seleccionar un monitor previamente");
                    }
                    break;
                    case "Socios":
                        try {
                        // Acción: Borrar un socio seleccionado de la tabla
                        int aux = table.getSelectedRow();
                        if (0 == JOptionPane.showConfirmDialog(null, "¿Seguro que quiere dar de baja a "
                                + (String) table.getValueAt(aux, 1) + "?",
                                "ATENCION", YES_NO_OPTION,
                                WARNING_MESSAGE)) {
                            cSocios.BorrarSocio((String) table.getValueAt(aux, 0));
                            tablaSocios();
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        // Manejo de excepción: Mostrar un mensaje de error si no se ha seleccionado un socio previamente
                        vMensajes = new VistaMensajes("Seleccione un socio a modificar",
                                "Necesita seleccionar un socio previamente");
                    }
                    break;
                }
                break;

            case "Socios por Actividad":
                // Acción: Generar vista de actividades de socios
                cActividad.generarVistaActividad(vPrincipal);
                break;
            case "AltaBajaSocioActividad":
                // Acción: Realizar alta o baja de socio en actividad
                cAltaBaja.AltaBajaSocio(vPrincipal);
                break;
            case "Buscar Socio":
                // Acción: Mostrar tabla de socios y realizar búsqueda de socio
                tablaSocios();
                cSocios.buscarSocio(vPrincipal);
                mostrarTabla(cSocios.getModeloTablaSocios());
                break;
            case "Cuota de Actividad":
                cActividad.generarVistaCuotaActividad(vPrincipal);
                break;
            case "Cuota de Monitor":
                cMonitores.generarVistaCuotaActividad(vPrincipal);
                break;

        }

    }

    /**
     * Método para mostrar la tabla de socios.
     */
    public void tablaSocios() {
        mostrarTabla(cSocios.ConstruirTabla());
        vPrincipal.jButtonActualizacionElemento.setText("Actualización de Socio");
        vPrincipal.jButtonBajaElemento.setText("Baja de Socio");
        vPrincipal.jButtonNuevoElemento.setText("Nuevo Socio");
        tablaActual = "Socios";

    }

    /**
     * Método para mostrar la tabla de monitores.
     */
    public void tablaMonitores() {

        mostrarTabla(cMonitores.ConstruirTabla());
        vPrincipal.jButtonActualizacionElemento.setText("Actualización de Monitor");
        vPrincipal.jButtonBajaElemento.setText("Baja de Monitor");
        vPrincipal.jButtonNuevoElemento.setText("Nuevo Monitor");
        tablaActual = "Monitores";

    }

    /**
     * Método para mostrar una tabla en la interfaz gráfica.
     *
     * @param tableModel El TableModel a mostrar.
     */
    public void mostrarTabla(TableModel tableModel) {

        table = new JTable(tableModel);
        JScrollPane tabla = new JScrollPane(table);

        // Limpiar el contenido anterior del JPanel
        vPrincipal.jPanelMuestreo.removeAll();

        // Ajustar el tamaño del JPanel según el tamaño de la ventana principal
        Dimension newSize = vPrincipal.getSize();
        newSize.height -= 120;
        newSize.width -= 10;

        // Establecer el mismo tamaño para el JPanel
        vPrincipal.jPanelMuestreo.setPreferredSize(newSize);

        // Actualizar la ventana principal
        vPrincipal.revalidate();
        vPrincipal.repaint();

        // Obtener el tamaño del JPanel para ajustar la visualización de los datos en concordancia
        Dimension sizeJPanel = vPrincipal.jPanelMuestreo.getSize();

        // Agregar la tabla al JPanel y establecer su posición y tamaño
        vPrincipal.jPanelMuestreo.add(tabla);
        tabla.setBounds(5, 0, sizeJPanel.width - 10, sizeJPanel.height);

        // Actualizar el JPanel
        vPrincipal.jPanelMuestreo.revalidate();
        vPrincipal.jPanelMuestreo.repaint();

    }
}
