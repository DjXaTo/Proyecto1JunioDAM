package modelo;

import clases.Empleado;
import clases.Proyecto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Modelo {

    Conexion conn;
    Statement stm;
    ResultSet rst;

    public Modelo() {
        this.conn = new Conexion();
    }

    //EMPLEADOS
    public ArrayList<Empleado> leerEmpleados() {
        ArrayList<Empleado> listaEmple = new ArrayList<>();
        try {
            stm = conn.conectado().createStatement();
            rst = stm.executeQuery("SELECT * FROM empleado");
            while (rst.next()) {
                Empleado e = new Empleado();
                e.setId(rst.getInt("id"));
                e.setNif(rst.getString("Nif"));
                e.setNombre(rst.getString("Nombre"));
                e.setApellido(rst.getString("Apellido"));
                e.setFechaNacimiento(rst.getString("Fecha_Nacimiento"));
                listaEmple.add(e);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaEmple;
    }

    public void agregarEmpleado(String nif, String nombre, String apellido, String fecha) {
        try {
            stm = conn.conectado().createStatement();
            String sql;
            sql = "INSERT into empleado (Nombre, Apellido, NIF, Fecha_Nacimiento) VALUES ('" + nombre + "','" + apellido + "','" + nif + "','" + fecha + "')";
            stm.executeUpdate(sql);
        } catch (SQLException ex) {
            Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void modificarEmpleado(int id, String nif, String nombre, String apellido, String fecha) {
        try {
            stm = conn.conectado().createStatement();
            String sql;
            sql = "UPDATE empleado SET Nombre='" + nombre + "',Apellido = '" + apellido + "' ,NIF = " + nif + " , Fecha_Nacimiento='" + fecha + "' WHERE id LIKE " + id + "";
            stm.executeUpdate(sql);
        } catch (SQLException ex) {
            Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void borrarEmpleado(int id) {
        try {
            stm = conn.conectado().createStatement();
            String sql;
            sql = "SELECT count(empleado) AS total FROM empleado_proyecto where empleado=" + id; 
            ResultSet ex = stm.executeQuery(sql);
            ex.first();
            int total = ex.getInt("total");
            if (total == 0) {
                int i = JOptionPane.showConfirmDialog(null, "¿Seguro que desear despedir al empleado?");
//Descomentar para comprobar el resultado de los botones del ConfirmDIalog                
//System.out.println(i); 
                if (i == 0) {
                    sql = "DELETE FROM empleado where id=" + id;
                    stm.executeUpdate(sql);
                }
            } else {
                JOptionPane.showMessageDialog(null, "No se puede borrar un empleado asignado a un proyecto");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //PROYECTOS
    public ArrayList<Proyecto> leerProyectos() {
        ArrayList<Proyecto> listaProy = new ArrayList<>();
        try {
            stm = conn.conectado().createStatement();
            rst = stm.executeQuery("SELECT * FROM proyecto");
            while (rst.next()) {
                Proyecto p = new Proyecto();
                p.setId(rst.getInt("id"));
                p.setTitulo(rst.getString("Titulo"));
                p.setDescripcion(rst.getString("Descripcion"));
                p.setFechaInicio(rst.getString("Fecha_Inicio"));
                p.setFechaFin(rst.getString("Fecha_Fin"));
                p.setMaxEmple(rst.getInt("max_emple"));
                listaProy.add(p);
            }

        } catch (SQLException ex) {
            Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaProy;
    }

    public void agregarProyecto(String titulo, String fch_inicio, String fch_entrega, String descripcion, int max_emple) {
        try {
            stm = conn.conectado().createStatement();
            String sql;
            sql = "INSERT into proyecto (Titulo,Fecha_Inicio,Fecha_Fin,Descripcion,max_emple) VALUES ('" + titulo + "','" + fch_inicio + "','" + fch_entrega + "','" + descripcion + "'," + max_emple + ")";
            stm.executeUpdate(sql);
        } catch (SQLException ex) {
            Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void modificarProyecto(int id, String titulo, String fch_inicio, String fch_entrega, String descripcion, int max_emple) {
        try {
            stm = conn.conectado().createStatement();
            String sql;
            sql = "UPDATE proyecto SET Titulo='" + titulo + "',Fecha_Inicio='" + fch_inicio + "',Fecha_Fin='" + fch_entrega + "',Descripcion='" + descripcion + "',max_emple=" + max_emple + " WHERE id LIKE " + id + "";
            stm.executeUpdate(sql);
        } catch (SQLException ex) {
            Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void borrarProyecto(int id) {
        try {
            stm = conn.conectado().createStatement();
            String sql;
            sql = "SELECT count(proyecto) AS total FROM empleado_proyecto where proyecto=" + id;
            ResultSet ex = stm.executeQuery(sql);
            ex.first();
            int total = ex.getInt("total");
            if (total == 0) {
                int i = JOptionPane.showConfirmDialog(null, "¿Seguro que desear eliminar el proyecto?");
                if (i == 0) {
                    sql = "DELETE FROM proyecto where id=" + id;
                    stm.executeUpdate(sql);
                    // System.out.println("BORRADO EL PROYECTO");
                }
            } else {
                JOptionPane.showMessageDialog(null, "No se puede borrar un proyecto que tenga empleados asignados");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//RELACIÓN
    public ArrayList<Empleado> leerEmpleProy(int id) {
        ArrayList<Empleado> listaEmpleProy = new ArrayList<>();
        try {
            stm = conn.conectado().createStatement();
            rst = stm.executeQuery("SELECT * FROM empleado WHERE id in (SELECT empleado FROM empleado_proyecto WHERE proyecto like " + id + ")");
            while (rst.next()) {
                Empleado e = new Empleado();
                e.setId(rst.getInt("id"));
                e.setNif(rst.getString("Nif"));
                e.setNombre(rst.getString("Nombre"));
                e.setApellido(rst.getString("Apellido"));
                e.setFechaNacimiento(rst.getString("Fecha_Nacimiento"));
                listaEmpleProy.add(e);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaEmpleProy;
    }

    public void agregarEmpleProy(int id_emple, int id_proy) {
        try {
            stm = conn.conectado().createStatement();
            String sql;
            sql = "INSERT into empleado_proyecto (empleado,proyecto) VALUES (" + id_emple + "," + id_proy + ")";
            stm.executeUpdate(sql);
        } catch (SQLException ex) {
            Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void eliminarEmpleProy(int id_emple, int id_proy) {
        try {
            stm = conn.conectado().createStatement();
            String sql;
            sql = "DELETE FROM empleado_proyecto WHERE empleado like " + id_emple + " AND proyecto like " + id_proy + "";
            stm.executeUpdate(sql);
        } catch (SQLException ex) {
            Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int maximoEmple(int id) throws SQLException {
        int n;

        stm = conn.conectado().createStatement();
        String sql;
        sql = "SELECT COUNT(*) as total FROM empleado_proyecto WHERE proyecto LIKE " + id;
        ResultSet ex = stm.executeQuery(sql);
        ex.first();
        n = ex.getInt("total");

        return n;
    }
}
