/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Monitor;
import Modelo.MonitoresDAO;
import Vista.VistaCambiosMonitores;
import Vista.VistaCuotaMonitor;
import Vista.VistaMensajes;
import Vista.VistaPrincipal;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Session;

/**
 *
 * @author gtaan
 */
public class ControladorMonitores implements ActionListener {

//    Statement stmt;
    MonitoresDAO monitores;
    VistaCambiosMonitores vMonitores;
    VistaPrincipal ventana;
    VistaMensajes vMensajes;
    Session sesion;
    SimpleDateFormat dateFormat; //Para hacer conversiones String <--> Date
    DefaultTableModel modeloTablaMonitores;
    VistaCuotaMonitor vCuota;
    DefaultTableModel tableModel = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

    };

    /**
     * Constructor de la clase ControladorMonitores.
     *
     * @param sesion La sesión de Hibernate utilizada para acceder a los datos.
     */
    public ControladorMonitores(Session sesion) {
        this.sesion = sesion;
        TableModelNoEditable();
    }

    /**
     * Crea un nuevo modelo de tabla donde las celdas no son editables.
     */
    private void TableModelNoEditable() {
        modeloTablaMonitores = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

        };
    }

    /**
     * Actualiza los campos de la vista con los datos del monitor seleccionado
     * en la tabla.
     *
     * @param rowActual La fila seleccionada en la tabla.
     * @param tableModel El JTable utilizado para mostrar los datos de los
     * monitores.
     */
    public void ActualizarMonitor(int rowActual, JTable tableModel) {
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        vMonitores = new VistaCambiosMonitores(ventana, true);
        vMonitores.jTextFieldCodigo.setText((String) tableModel.getValueAt(rowActual, 0));
        vMonitores.jTextFieldNombre.setText((String) tableModel.getValueAt(rowActual, 1));
        vMonitores.jTextFieldDNI.setText((String) tableModel.getValueAt(rowActual, 2));
        vMonitores.jTextFieldTelefono.setText((String) tableModel.getValueAt(rowActual, 3));
        vMonitores.jTextFieldCorreo.setText((String) tableModel.getValueAt(rowActual, 4));

        try {
            vMonitores.jDateChooserFechaEntrada.setDate(dateFormat.parse((String) tableModel.getValueAt(rowActual, 5)));
        } catch (ParseException ex) {
            vMensajes = new VistaMensajes("LA CADENA FECHA NO TIENE EL FORMATO CORRECTO", ex.getMessage());
        }

        vMonitores.jTextFieldNick.setText((String) tableModel.getValueAt(rowActual, 6));
        vMonitores.jTextFieldCodigo.setEditable(false);
        vMonitores.jButtonInsertar.setActionCommand("Actualizar");
        vMonitores.jButtonInsertar.setText("Actualizar");
        addListeners();
        vMonitores.setVisible(true);

    }

    /**
     * Crea un nuevo monitor en la vista para su inserción.
     */
    public void NuevoMonitor() {
        vMonitores = new VistaCambiosMonitores(ventana, true);
        vMonitores.setLocationRelativeTo(null);
        DefaultTableModel tableModel = ConstruirTabla();

        int nuevoNumero = 0;

        /*//En caso de que el código que hay que elegir para los nuevos monitores sea
        //el menor posible se realizaría con el código comentado acontinuación:
        int i = 0;
        while(i < tableModel.getRowCount()){
            
            if(Integer.parseInt(((String)tableModel.getValueAt(i, 0)).substring(1))==nuevoNumero){
                nuevoNumero++;
                i=0;
                continue;
            }
            i++;
        }*/
        //En caso de que el código que hay que elegir para los nuevos monitores sea
        //el mayor + 1 se realiza con el código desde:
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            //System.out.println(Integer.parseInt(((String)tableModel.getValueAt(i, 0)).substring(1)));
            int aux = Integer.parseInt(((String) tableModel.getValueAt(i, 0)).substring(1));
            if (nuevoNumero < aux) {
                nuevoNumero = aux;
            }
        }
        nuevoNumero++;

        //Hasta aquí. Por lo que si no fuera así hay que comentar este bloque
        //y descomentar el anterior
        String codigoNuevo = "M" + String.format("%03d", nuevoNumero);
        vMonitores.jTextFieldCodigo.setText(codigoNuevo);
        vMonitores.jTextFieldCodigo.setEditable(false);
        vMonitores.jButtonInsertar.setActionCommand("Insertar");
        vMonitores.jButtonInsertar.setText("Insertar");

        addListeners();
        vMonitores.setVisible(true);
    }

    /**
     * Elimina un monitor de la base de datos.
     *
     * @param codMonitor El código del monitor a borrar.
     */
    public void BorrarMonitor(String codMonitor) {

        try {
            monitores.eliminarMonitor(codMonitor);
        } catch (Exception ex) {
            vMensajes = new VistaMensajes("NO SE HA PODIDO CUMPLIR SU SOLICITUD", ex.getMessage());
        }
    }

    private void addListeners() {
        vMonitores.jButtonCancelar.addActionListener(this);
        vMonitores.jButtonInsertar.addActionListener(this);
    }

    /**
     * Maneja los eventos generados por los componentes de la vista.
     *
     * @param e El evento generado.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Cancelar":
                vMonitores.dispose();
                break;
            case "Actualizar":
                try {
                Monitor monitor = obtencionDatos();
                monitores.actualizarMonitor(monitor);

                vMonitores.dispose();
            } catch (NullPointerException ex) {
                vMensajes = new VistaMensajes("FALTAN DATOS", ex.getMessage());
            } catch (Exception ex) {
                vMensajes = new VistaMensajes("ERROR AL ACTUALIZAR MONITOR", "Revise que haya rellenado los campos 'Nombre', 'DNI' y 'Fecha de Entrada'");
                System.out.println(ex.getMessage());
            }
            break;
            case "Insertar":
                try {
                Monitor monitor = obtencionDatos();
                monitores.insertarMonitor(monitor);

                vMonitores.dispose();
            } catch (NullPointerException ex) {
                vMensajes = new VistaMensajes("FALTAN DATOS", ex.getMessage());

            } catch (Exception ex) {
                vMensajes = new VistaMensajes("ERROR AL INSERTAR MONITOR", ex.getMessage());
            }
            break;
            case "BuscarCuota":
                try{
                    if(vCuota.jTextFieldCodMonitor.getText().isEmpty())
                        throw new NullPointerException("Debe rellenar el campo codMonitor");
                String monitorCuota = (String) vCuota.jTextFieldCodMonitor.getText().toUpperCase();
                tableModel = monitores.GetActividad(monitores.getMonitorByID(monitorCuota));
                mostrarTablaCuota(tableModel);}
                catch(NullPointerException ex){
                    vMensajes = new VistaMensajes("ERROR", ex.getMessage());
                }

                break;
            case "CerrarCuota":
                vCuota.dispose();
                break;

        }
    }

    /**
     * Obtiene los datos ingresados en la vista y crea un objeto Monitor.
     *
     * @return El objeto Monitor con los datos ingresados.
     */
    private Monitor obtencionDatos() {
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String[] Datos = new String[7];
        Datos[0] = vMonitores.jTextFieldNombre.getText();
        Datos[1] = vMonitores.jTextFieldDNI.getText();
        Datos[2] = vMonitores.jTextFieldTelefono.getText();
        Datos[3] = vMonitores.jTextFieldCorreo.getText();

        if (vMonitores.jDateChooserFechaEntrada.getDate() == null) {
            throw new NullPointerException("La Fecha de Entrada no puede estar vacía");
        }
        Datos[4] = dateFormat.format(vMonitores.jDateChooserFechaEntrada.getDate());
        if (vMonitores.jDateChooserFechaEntrada.getDate().compareTo(new Date()) > 0) {
            throw new NullPointerException("La fecha introducida no puede ser mayor a la fecha actual");
        }
        Datos[5] = vMonitores.jTextFieldNick.getText();
        Datos[6] = vMonitores.jTextFieldCodigo.getText();
        if (Datos[0].isEmpty()) {
            throw new NullPointerException("El Nombre no puede estar vacío");
        }
        if (Datos[1].isEmpty()) {
            throw new NullPointerException("El DNI no puede estar vacío");
        }
        Monitor monitor = new Monitor(Datos[6], Datos[0], Datos[1], Datos[2], Datos[3], Datos[4], Datos[5]);
        return monitor;
    }

    /**
     * Construye y devuelve el modelo de tabla para mostrar los monitores.
     *
     * @return El modelo de tabla construido.
     */
    public DefaultTableModel ConstruirTabla() {
        TableModelNoEditable(); //Vuelvo a inicializar el DefaultTableModel con la característica de que las celdas no son editables
        try {
            monitores = new MonitoresDAO(sesion);
            ArrayList<Monitor> listaMonitores = monitores.listaMonitores();

            modeloTablaMonitores.addColumn("Código del Monitor");
            modeloTablaMonitores.addColumn("Nombre");
            modeloTablaMonitores.addColumn("DNI");
            modeloTablaMonitores.addColumn("Teléfono");
            modeloTablaMonitores.addColumn("Correo");
            modeloTablaMonitores.addColumn("Fecha de Entrada");
            modeloTablaMonitores.addColumn("Nick");

            for (Monitor monitor : listaMonitores) {
                Object[] row = new Object[7];
                row[0] = monitor.getCodmonitor();
                row[1] = monitor.getNombre();
                row[2] = monitor.getDni();
                row[3] = monitor.getTelefono();
                row[4] = monitor.getCorreo();
                row[5] = monitor.getFechaentrada();
                row[6] = monitor.getNick();

                modeloTablaMonitores.addRow(row);
            }
        } catch (Exception ex) {
            vMensajes = new VistaMensajes("ERROR DE BD", "Error en la construccion de la tabla Monitores: " + ex.getMessage());
        }
        return modeloTablaMonitores;
    }

    public void generarVistaCuotaActividad(VistaPrincipal vPrincipal) {
        vCuota = new VistaCuotaMonitor(vPrincipal, true);
        vCuota.setLocationRelativeTo(null);
        addListenersCuota();
        vCuota.setVisible(true);
//        try {
//            InicioCuota();
//        } catch (Exception e) {
//
//        }

    }


    private void addListenersCuota() {
        vCuota.jButtonBuscarCuota.addActionListener(this);
        vCuota.jButtonCerrar.addActionListener(this);
    }

    private void mostrarTablaCuota(DefaultTableModel tableModel) {
        JTable table = new JTable(tableModel);
        JScrollPane tabla = new JScrollPane(table);

        String codMon = vCuota.jTextFieldCodMonitor.getText().toUpperCase();
        vCuota.jTextFieldMonitor.setText(monitores.getMonitorByID(codMon).getNombre());
        vCuota.jTextFieldNumActividades.setText(String.valueOf(tableModel.getRowCount()));
        int numSocios = 0;
        int cuota = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            numSocios += (int)tableModel.getValueAt(i, 2);
            cuota += ((BigInteger)tableModel.getValueAt(i, 1)).intValue() * (int)tableModel.getValueAt(i, 2);
        }
        vCuota.jTextFieldNumSocios.setText(String.valueOf(numSocios));
        vCuota.jTextFieldCuotaMensual.setText(String.valueOf(cuota+"€"));
        vCuota.jPanelMuestreo.removeAll();

        Dimension sizeJPanel = vCuota.jPanelMuestreo.getSize();//Para obtener el tamaño del JPanel para luego ajustar la visualización de los datos en concordancia
        vCuota.jPanelMuestreo.add(tabla);
        tabla.setBounds(0, 0, sizeJPanel.width, sizeJPanel.height);

        
        
        vCuota.jPanelMuestreo.revalidate();
        vCuota.jPanelMuestreo.repaint();
    }
    

}
