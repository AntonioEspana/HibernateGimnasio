/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author gtaan
 */
public class Conexion {

    Connection conexion;

    public Conexion(String sgbd, String ip, String servicio_bd, String usuario,
            String password) throws SQLException {
        if(sgbd.equals("Oracle")){
        conexion = DriverManager.getConnection(
                "jdbc:oracle:thin:@" + ip + ":etsi", usuario, password);}
        else{
            conexion = DriverManager.getConnection(
                "jdbc:mariadb://172.18.1.241:3306/DDSI_042", "DDSI_042","DDSI_042" );
        }

    }

    public Connection getConexion() {
        return conexion;
    }

    public void desconexion() throws SQLException {
        conexion.close();
    }

}
