/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.util.ArrayList;
import javax.persistence.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author gtaan
 */
public class SociosDAO {

    Session sesion;

    public SociosDAO(Session sesion) {
        this.sesion = sesion;
    }

    public void insertarSocio(Socio socio) {
        Transaction transaction = sesion.beginTransaction();
        sesion.save(socio);
        transaction.commit();
    }

    public void eliminarSocio(String numSocio) throws Exception {
        Transaction transaction = sesion.beginTransaction();
        Socio socio = sesion.get(Socio.class, numSocio);
        sesion.delete(socio);
        transaction.commit();
    }

    public void actualizarSocio(Socio socio) throws Exception {
        Transaction transaction = sesion.beginTransaction();
        Socio socioExistente = sesion.get(Socio.class, socio.getNumerosocio());
        socioExistente.setNombre(socio.getNombre());
        socioExistente.setCorreo(socio.getCorreo());
        socioExistente.setDni(socio.getDni());
        socioExistente.setFechaentrada(socio.getFechaentrada());
        socioExistente.setFechanacimiento(socio.getFechanacimiento());
        socioExistente.setTelefono(socio.getTelefono());
        socioExistente.setCategoria(socio.getCategoria());
        sesion.update(socioExistente);
        transaction.commit();
    }

    public ArrayList<Socio> listaSocios() throws Exception {
        Transaction transaction = sesion.beginTransaction();
        Query consulta = sesion.createNamedQuery("Socio.Todos", Socio.class);
        ArrayList<Socio> socios = (ArrayList<Socio>) consulta.getResultList();
        transaction.commit();

        return socios;
    }

    public Socio GetSocioByNumSocio(String numSocio) {
Transaction transaction = sesion.beginTransaction();
        Socio socio = sesion.get(Socio.class, numSocio);
        
//        Query consulta = sesion.createNamedQuery("Socio.findByNumerosocio",Socio.class);
//        consulta.setParameter("numerosocio", numSocio);
//        Socio socio = (Socio) consulta.getSingleResult();
        transaction.commit();

        return socio;
    }

    public ArrayList<Socio> busquedaSocios(String namedQuery, String campo, String busqueda){
        Transaction transaction = sesion.beginTransaction();
        String comando = "Socio.findBy"+namedQuery;
        Query consulta = sesion.createNamedQuery(comando,Socio.class);
        if(campo.equals("categoria")){
            consulta.setParameter(campo, busqueda.toUpperCase().charAt(0));
        } else{
            busqueda = "%"+busqueda+"%";
            consulta.setParameter(campo, busqueda);
        }
        
        ArrayList<Socio> listaSocios = (ArrayList<Socio>) consulta.getResultList();
        transaction.commit();
        return listaSocios;
    }
    
}
