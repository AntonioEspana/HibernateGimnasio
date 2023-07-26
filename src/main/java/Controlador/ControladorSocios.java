/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Socio;
import Modelo.SociosDAO;
import Vista.VistaBuscarSocio;
import Vista.VistaCambiosSocios;
import Vista.VistaMensajes;
import Vista.VistaPrincipal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Session;

/**
 *
 * @author gtaan
 */
public class ControladorSocios implements ActionListener {

    SociosDAO socios;
    VistaCambiosSocios vSocios;
    VistaPrincipal ventana;
    VistaMensajes vMensajes;
    SimpleDateFormat dateFormat; //Para hacer conversiones String <--> Date
    Session sesion;
    DefaultTableModel modeloTablaSocios;
    VistaBuscarSocio vBuscar;

    /**
     * Constructor de la clase ControladorSocios.
     *
     * @param sesion La sesión de Hibernate.
     */
    public ControladorSocios(Session sesion) {
        this.sesion = sesion;
        TableModelNoEditable();
    }

    /**
     * Crea un nuevo modelo de tabla donde las celdas no son editables.
     */
    private void TableModelNoEditable() {
        modeloTablaSocios = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

        };
    }

    /**
     * Actualiza la información de un socio en la tabla de socios.
     *
     * @param rowActual Índice de la fila seleccionada en la tabla.
     * @param tableModel Modelo de la tabla que contiene los datos de los
     * socios.
     */
    public void ActualizarSocio(int rowActual, JTable tableModel) {
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        vSocios = new VistaCambiosSocios(ventana, true);
        vSocios.jTextFieldCodigo.setText((String) tableModel.getValueAt(rowActual, 0));
        vSocios.jTextFieldNombre.setText((String) tableModel.getValueAt(rowActual, 1));
        vSocios.jTextFieldDNI.setText((String) tableModel.getValueAt(rowActual, 2));
        vSocios.jTextFieldTelefono.setText((String) tableModel.getValueAt(rowActual, 4));
        vSocios.jTextFieldCorreo.setText((String) tableModel.getValueAt(rowActual, 5));
        vSocios.jTextFieldCategoria.setText(String.valueOf(tableModel.getValueAt(rowActual, 7)));

        try {
            vSocios.jDateChooserFechaNacimiento.setDate(dateFormat.parse((String) tableModel.getValueAt(rowActual, 3)));
            vSocios.jDateChooserFechaEntrada.setDate(dateFormat.parse((String) tableModel.getValueAt(rowActual, 6)));
        } catch (ParseException ex) {
            vMensajes = new VistaMensajes("LA CADENA FECHA NO TIENE EL FORMATO CORRECTO", ex.getMessage());
        }

        vSocios.jTextFieldCodigo.setEditable(false);
        vSocios.jButtonInsertar.setActionCommand("Actualizar");
        vSocios.jButtonInsertar.setText("Actualizar");
        addListeners();
        vSocios.setVisible(true);

    }

    /**
     * Abre la ventana de registro de un nuevo socio.
     */
    public void NuevoSocio() {
        vSocios = new VistaCambiosSocios(ventana, true);
        vSocios.setLocationRelativeTo(null);
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

        //En caso de que el código que hay que elegir para los nuevos socios sea
        //el mayor + 1 se realiza con el código desde:
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            //System.out.println(Integer.parseInt(((String)tableModel.getValueAt(i, 0)).substring(1)));
            int aux = Integer.parseInt(((String) tableModel.getValueAt(i, 0)).substring(1));
            if (nuevoNumero < aux) {
                nuevoNumero = aux;
                System.out.println(nuevoNumero);
            }
        }
        nuevoNumero++;
        //Hasta aquí. Por lo que si no fuera así hay que comentar este bloque
        //y descomentar el anterior
        String codigoNuevo = "S" + String.format("%03d", nuevoNumero);
        vSocios.jTextFieldCodigo.setText(codigoNuevo);
        vSocios.jTextFieldCodigo.setEditable(false);
        vSocios.jButtonInsertar.setActionCommand("Insertar");
        vSocios.jButtonInsertar.setText("Insertar");

        addListeners();
        vSocios.setVisible(true);
    }

    /**
     * Borra un socio de la base de datos.
     *
     * @param numeroSocio Número de socio a borrar.
     */
    public void BorrarSocio(String numeroSocio) {
        try {
            socios.eliminarSocio(numeroSocio);
        } catch (Exception ex) {
            vMensajes = new VistaMensajes("NO SE HA PODIDO CUMPLIR SU SOLICITUD", ex.getMessage());
        }
    }

    private void addListeners() {
        vSocios.jButtonCancelar.addActionListener(this);
        vSocios.jButtonInsertar.addActionListener(this);
    }

    /**
     * Maneja los eventos generados por los componentes de la vista
     *
     * @param e Evento generado.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Cancelar":
                vSocios.dispose();
                break;
            case "Actualizar":
                try {
                Socio socio = obtencionDatos();
                socios.actualizarSocio(socio);

                vSocios.dispose();
            } catch (NullPointerException ex) {
                vMensajes = new VistaMensajes("FALTAN DATOS", ex.getMessage());
            } catch (Exception ex) {
                vMensajes = new VistaMensajes("ERROR AL ACTUALIZAR SOCIO", "Revise que haya rellenado los campos 'Nombre', 'DNI', 'Fecha de Entrada', 'Fecha de Nacimiento' y 'Categoría(A,B,C,D,E)'");
                System.out.println(ex.getMessage());
            }
            break;
            case "Insertar":
                try {
                Socio socio = obtencionDatos();
                socios.insertarSocio(socio);

                vSocios.dispose();
            } catch (NullPointerException ex) {
                vMensajes = new VistaMensajes("FALTAN DATOS", ex.getMessage());

            } catch (Exception ex) {
                vMensajes = new VistaMensajes("ERROR AL INSERTAR SOCIO", ex.getMessage());
            }
            break;
            case "Cancelar2":
                vBuscar.dispose();
                break;
            case "Buscar":
                buscar();
                vBuscar.dispose();
                break;
        }
    }

    /**
     * Obtiene los datos ingresados en la vista de cambios de socios y los
     * devuelve como un objeto Socio.
     *
     * @return Objeto Socio con los datos ingresados.
     * @throws NullPointerException Si falta algún dato obligatorio.
     */
    private Socio obtencionDatos() {
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String[] Datos = new String[8];

        if (vSocios.jDateChooserFechaNacimiento.getDate() == null) {
            throw new NullPointerException("La Fecha de Nacimiento no puede estar vacía");
        }
        if (vSocios.jDateChooserFechaEntrada.getDate() == null) {
            throw new NullPointerException("La Fecha de Entrada no puede estar vacía");
        }
        if (vSocios.jDateChooserFechaEntrada.getDate().compareTo(new Date()) > 0
                || vSocios.jDateChooserFechaNacimiento.getDate().compareTo(new Date()) > 0) {
            throw new NullPointerException("La fecha introducida no puede ser mayor a la fecha actual");
        }

        Datos[0] = vSocios.jTextFieldNombre.getText();
        Datos[1] = vSocios.jTextFieldDNI.getText();
        Datos[2] = dateFormat.format(vSocios.jDateChooserFechaNacimiento.getDate());
        Datos[3] = vSocios.jTextFieldTelefono.getText();
        Datos[4] = vSocios.jTextFieldCorreo.getText();
        Datos[5] = dateFormat.format(vSocios.jDateChooserFechaEntrada.getDate());
        Datos[6] = vSocios.jTextFieldCategoria.getText().toUpperCase();
        Datos[7] = vSocios.jTextFieldCodigo.getText();

        System.out.println(Datos[5].getClass().getSimpleName());

        if (Datos[0].isEmpty()) {
            throw new NullPointerException("El Nombre no puede estar vacío");
        }
        if (Datos[1].isEmpty()) {
            throw new NullPointerException("El DNI no puede estar vacío");
        }

        if (Datos[6].isEmpty()) {
            throw new NullPointerException("La Categoría no puede estar vacía");
        } else if (Datos[6].charAt(0) != 'A' && Datos[6].charAt(0) != 'B'
                && Datos[6].charAt(0) != 'C' && Datos[6].charAt(0) != 'D'
                && Datos[6].charAt(0) != 'E' || Datos[6].length() > 1) {
            throw new NullPointerException("La Categoría debe ser 'A, B, C, D o E'");
        }

        {

        }
        Socio socio = new Socio(Datos[7], Datos[0], Datos[1], Datos[2], Datos[3], Datos[4], Datos[5], Datos[6].charAt(0));
        return socio;
    }

    /**
     * Construye el modelo de la tabla de socios.
     *
     * @return Modelo de la tabla de socios.
     */
    public DefaultTableModel ConstruirTabla() {
        TableModelNoEditable(); //Vuelvo a inicializar el DefaultTableModel con la característica de que las celdas no son editables
        try {
            socios = new SociosDAO(sesion);
            ArrayList<Socio> listaSocios = socios.listaSocios();

            modeloTablaSocios.addColumn("Número de Socio");
            modeloTablaSocios.addColumn("Nombre");
            modeloTablaSocios.addColumn("DNI");
            modeloTablaSocios.addColumn("Fecha de Nacimiento");
            modeloTablaSocios.addColumn("Teléfono");
            modeloTablaSocios.addColumn("Correo");
            modeloTablaSocios.addColumn("Fecha de Entrada");
            modeloTablaSocios.addColumn("Categoría");

            for (Socio socio : listaSocios) {
                Object[] row = new Object[8];
                row[0] = socio.getNumerosocio();
                row[1] = socio.getNombre();
                row[2] = socio.getDni();
                row[3] = socio.getFechanacimiento();
                row[4] = socio.getTelefono();
                row[5] = socio.getCorreo();
                row[6] = socio.getFechaentrada();
                row[7] = socio.getCategoria();

                modeloTablaSocios.addRow(row);
            }
        } catch (Exception ex) {
            vMensajes = new VistaMensajes("ERROR DE BD", "Error en la construccion de la tabla Socios: " + ex.getMessage());
        }
        return modeloTablaSocios;
    }

    /**
     * Abre la ventana de búsqueda de socios.
     *
     * @param vPrincipal Vista principal de la aplicación.
     */
    public void buscarSocio(VistaPrincipal vPrincipal) {
        vBuscar = new VistaBuscarSocio(vPrincipal, true);
        addListenersBusqueda();
        vBuscar.setVisible(true);
    }

    private void addListenersBusqueda() {
        vBuscar.jButtonBuscar.addActionListener(this);
        vBuscar.jButtonCancelar.addActionListener(this);
    }

    /**
     * Obtiene el modelo de la tabla de socios.
     *
     * @return Modelo de la tabla de socios.
     */
    public DefaultTableModel getModeloTablaSocios() {
        return modeloTablaSocios;
    }

    /**
     * Realiza la búsqueda de socios según los criterios ingresados en la vista
     * de búsqueda de socios.
     */
    public void buscar() {
        String campoBusqueda = (String) vBuscar.jComboBoxCampo.getSelectedItem();
        String campo = null;
        String namedQuery = null;
        switch (campoBusqueda) {
            case "Número de Socio" -> {
                campo = "numerosocio";
                namedQuery = "Numerosocio";
            }
            case "Nombre" -> {
                campo = "nombre";
                namedQuery = "Nombre";
            }
            case "DNI" -> {
                campo = "dni";
                namedQuery = "Dni";
            }
            case "Fecha de Nacimiento" -> {
                campo = "fechanacimiento";
                namedQuery = "Fechanacimiento";
            }
            case "Teléfono" -> {
                campo = "telefono";
                namedQuery = "Telefono";
            }
            case "Correo" -> {
                campo = "correo";
                namedQuery = "Correo";
            }
            case "Fecha de Entrada" -> {
                campo = "fechaentrada";
                namedQuery = "Fechaentrada";
            }
            case "Categoría" -> {
                campo = "categoria";
                namedQuery = "Categoria";
            }
        }
        ArrayList<Socio> busquedaSocios = socios.busquedaSocios(namedQuery, campo, vBuscar.jTextFieldBusqueda.getText());

        TableModelNoEditable(); //Vuelvo a inicializar el DefaultTableModel con la característica de que las celdas no son editables
        try {
            socios = new SociosDAO(sesion);

            modeloTablaSocios.addColumn("Número de Socio");
            modeloTablaSocios.addColumn("Nombre");
            modeloTablaSocios.addColumn("DNI");
            modeloTablaSocios.addColumn("Fecha de Nacimiento");
            modeloTablaSocios.addColumn("Teléfono");
            modeloTablaSocios.addColumn("Correo");
            modeloTablaSocios.addColumn("Fecha de Entrada");
            modeloTablaSocios.addColumn("Categoría");

            for (Socio socio : busquedaSocios) {
                Object[] row = new Object[8];
                row[0] = socio.getNumerosocio();
                row[1] = socio.getNombre();
                row[2] = socio.getDni();
                row[3] = socio.getFechanacimiento();
                row[4] = socio.getTelefono();
                row[5] = socio.getCorreo();
                row[6] = socio.getFechaentrada();
                row[7] = socio.getCategoria();

                modeloTablaSocios.addRow(row);
            }
        } catch (Exception ex) {
            vMensajes = new VistaMensajes("ERROR DE BD", "Error en la construccion de la tabla Socios: " + ex.getMessage());
        }
    }

}
