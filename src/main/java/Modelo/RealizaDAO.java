/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.util.Set;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author gtaan
 */
public class RealizaDAO {

    Session sesion;

    public RealizaDAO(Session sesion) {
        this.sesion = sesion;
    }
    
    public void altaRealiza(Socio socio, Actividad actividad){
        Transaction transaction = sesion.beginTransaction();
        socio.getActividades().add(actividad);
        actividad.getSocios().add(socio);
        transaction.commit();
    }
    
    public void bajaRealiza(Socio socio, Actividad actividad){
        Transaction transaction = sesion.beginTransaction();
        socio.getActividades().remove(actividad);
        actividad.getSocios().remove(socio);
        transaction.commit();
    }

    public Set<Actividad> GetRealiza(Socio socio) {
        Transaction transaction = sesion.beginTransaction();
        Set<Actividad> actividades = socio.getActividades();
        transaction.commit();
        return actividades;
    }
    
    public DefaultTableModel GetSocios(Actividad actividad) {
        Transaction transaction = sesion.beginTransaction();
        Set<Socio> socios = actividad.getSocios();
        transaction.commit();
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Nombre");
        tableModel.addColumn("Categoría");
        for (Socio socioAUX : socios) {
            Object[] row = new Object[2];
            row[0] = (String) socioAUX.getNombre();
            row[1] = socioAUX.getCategoria();
            tableModel.addRow(row);
        }
        return tableModel;
    }
    
}
