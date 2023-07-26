/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.sql.SQLException;
import java.util.ArrayList;
import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author gtaan
 */
public class ActividadDAO {

    Session sesion;

    public ActividadDAO(Session sesion) {
        this.sesion = sesion;
    }

    public ArrayList<Actividad> listaActividades() throws Exception {
        Transaction transaction = sesion.beginTransaction();
        Query consulta = sesion.createNamedQuery("Actividad.Todos", Actividad.class);
        ArrayList<Actividad> actividades = (ArrayList<Actividad>) consulta.getResultList();
        transaction.commit();

        return actividades;
    }

    public Actividad getActividadByID(String idActividad) {
        Transaction transaction = sesion.beginTransaction();
        Actividad actividad = sesion.get(Actividad.class, idActividad);
        transaction.commit();
        return actividad;
    }
    
    public Actividad getActividadByNombre(String nomActividad) {
        Transaction transaction = sesion.beginTransaction();
        Query consulta = sesion.createNamedQuery("Actividad.findByNombre",Actividad.class);
        consulta.setParameter("nombre", nomActividad);
        Actividad actividad = (Actividad) consulta.getSingleResult();
        transaction.commit();
        return actividad;
    }

    public DefaultTableModel ObtenerSociosInscritosOracle(String actividad) throws SQLException {

        Transaction transaction = sesion.beginTransaction();
        StoredProcedureQuery llamada = sesion.createStoredProcedureCall("ObtenerSociosInscritos")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, Class.class, ParameterMode.REF_CURSOR)
                .setParameter(1, actividad);
        llamada.execute();
        ArrayList<Object[]> resultado = (ArrayList<Object[]>) llamada.getResultList();
        transaction.commit();

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Nombre");
        tableModel.addColumn("Correo");
        for (Object[] objeto : resultado) {
            Object[] row = new Object[2];
            row[0] = (String) objeto[0];
            row[1] = (String) objeto[1];
            tableModel.addRow(row);
        }
        return tableModel;
    }
    
    
    
    public DefaultTableModel ObtenerSociosInscritosMariaDB(String actividad) throws SQLException {

        Transaction transaction = sesion.beginTransaction();
        StoredProcedureQuery llamada = sesion.createStoredProcedureCall("ObtenerSociosInscritos")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .setParameter(1, actividad);
        llamada.execute();
        ArrayList<Object[]> resultado = (ArrayList<Object[]>) llamada.getResultList();
        transaction.commit();

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Nombre");
        tableModel.addColumn("Correo");
        for (Object[] objeto : resultado) {
            Object[] row = new Object[2];
            row[0] = (String) objeto[0];
            row[1] = (String) objeto[1];
            tableModel.addRow(row);
        }
        return tableModel;
    }
}
