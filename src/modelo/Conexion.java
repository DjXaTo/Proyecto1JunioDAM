package modelo;

import java.sql.Connection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public final class Conexion {

    String usuario;
    String pass;
    String URL;
    String bd;

    public Conexion() {
    }

    public void RecogeDatos() {
        try {
            File fe = new File("datos_conexion.txt");
            FileReader fr = new FileReader(fe);
            BufferedReader br = new BufferedReader(fr);

            usuario = br.readLine();
            pass = br.readLine();
            URL = br.readLine();
            bd = br.readLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al coger los datos de conexion");
        }
    }

    public Connection conectado() {
        RecogeDatos();
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://" + URL + "/" + bd, usuario, pass);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return conn;
    }
}
