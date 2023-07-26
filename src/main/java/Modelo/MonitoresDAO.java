/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.util.ArrayList;
import java.util.Set;
import javax.persistence.Query;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author gtaan
 */
public class MonitoresDAO {

//    Statement stmt = null;
    //ResultSet consulta = null;
//    DefaultTableModel tableModel;
    Session sesion;

    public MonitoresDAO(Session sesion) {
        this.sesion = sesion;
    }

    public void insertarMonitor(Monitor monitor) throws Exception {
        Transaction transaction = sesion.beginTransaction();
        sesion.save(monitor);
        transaction.commit();
    }

    public void eliminarMonitor(String codMonitor) throws Exception {
        Transaction transaction = sesion.beginTransaction();
        Monitor monitor = sesion.get(Monitor.class, codMonitor);
        sesion.delete(monitor);
        transaction.commit();
    }

    public void actualizarMonitor(Monitor monitor) throws Exception {
        Transaction transaction = sesion.beginTransaction();
        Monitor monitorExistente = sesion.get(Monitor.class, monitor.getCodmonitor());
        monitorExistente.setCorreo(monitor.getCorreo());
        monitorExistente.setDni(monitor.getDni());
        monitorExistente.setFechaentrada(monitor.getFechaentrada());
        monitorExistente.setNick(monitor.getNick());
        monitorExistente.setNombre(monitor.getNombre());
        monitorExistente.setTelefono(monitor.getTelefono());
        sesion.update(monitorExistente);
        transaction.commit();
    }

    public ArrayList<Monitor> listaMonitores() throws Exception {
        Transaction transaction = sesion.beginTransaction();
        Query consulta = sesion.createNamedQuery("Monitor.Todos", Monitor.class);
        ArrayList<Monitor> monitores = (ArrayList<Monitor>) consulta.getResultList();
        transaction.commit();

        return monitores;
    }
    public DefaultTableModel GetActividad(Monitor monitor) {
        Transaction transaction = sesion.beginTransaction();
        Set<Actividad> actividades = monitor.getActividadesResponsable();
        transaction.commit();
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Nombre");
        tableModel.addColumn("Precio");
        tableModel.addColumn("Socios");
        for (Actividad actividad : actividades) {
            Object[] row = new Object[3];
            row[0] = (String) actividad.getNombre();
            row[1] = actividad.getPreciobasemes();
            row[2] = actividad.getSocios().size();
            tableModel.addRow(row);
        }
        return tableModel;
    }
    
    public Monitor getMonitorByID(String codmonitor){
        Transaction transaction = sesion.beginTransaction();
        Monitor monitor = sesion.get(Monitor.class, codmonitor);
        transaction.commit();

        return monitor;
    }

}
