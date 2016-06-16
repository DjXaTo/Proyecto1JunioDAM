package controlador;

import clases.Empleado;
import clases.Proyecto;
import modelo.Modelo;
import vista.JDInsertarProy;
import vista.JDNuevoEmple;
import vista.JDNuevoProy;
import vista.VistaPrincipal;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controlador {

    private final VistaPrincipal p;
    private final Modelo consultas;
    JDNuevoEmple jdne;
    JDNuevoProy jdnp;
    JDInsertarProy jdip;
    ArrayList<Proyecto> proyectos;
    ArrayList<Empleado> empleados;

    public Controlador(VistaPrincipal p) {
        this.p = p;
        this.consultas = new Modelo();
    }

    public void iniciar() {
    //BOTONES DE LOS EMPLEADOS
        // BOTON NUEVO EMPLEADO
        p.btnNuevoEmple.addActionListener((ActionEvent) -> {

            jdne = new JDNuevoEmple(p, true);
            jdne.txtNIF.setText("");
            jdne.txtNombre.setText("");
            jdne.txtApellidos.setText("");
            jdne.txtFechaNac.setText("--/--/--");

            //BOTON ACEPTA DE NUEVO EMPLEADO
            jdne.btnAceptar.addActionListener((ActionEvent e) -> {
                String nif, nombre, apell, fecha;
                nif = jdne.txtNIF.getText();
                nombre = jdne.txtNombre.getText();
                apell = jdne.txtApellidos.getText();
                fecha = jdne.txtFechaNac.getText();
                consultas.agregarEmpleado(nif, nombre, apell, fecha);
                p.TablaEmpleados.setModel(this.getTablaEmple());
                jdne.dispose();
            });
            //BOTON CANCELAR DE NUEVO EMPLEADO
            jdne.btnCancelar.addActionListener((ActionEvent e) -> {
                jdne.dispose();
            });
            jdne.setVisible(true);
        });

        //MODIFICAR EMPLEADO
        p.btnModificarEmple.addActionListener((ActionEvent e) -> {
            jdne = new JDNuevoEmple(p, true);
            empleados = consultas.leerEmpleados();
            if (p.TablaEmpleados.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(jdne, "Error, seleccione un empleado de la tabla primero");
            } else {
                Empleado modEmple = empleados.get(p.TablaEmpleados.getSelectedRow());
                jdne.txtNIF.setText(modEmple.getNif());
                jdne.txtApellidos.setText(modEmple.getApellido());
                jdne.txtFechaNac.setText(modEmple.getFechaNacimiento());
                jdne.txtNombre.setText(modEmple.getNombre());
                //BOTON ACEPTAR MODIFICAR EMPLEADO
                jdne.btnAceptar.addActionListener((ActionEvent a) -> {
                    String nif, nombre, apell, fecha;
                    int id = modEmple.getId();
                    nif = jdne.txtNIF.getText();
                    nombre = jdne.txtNombre.getText();
                    apell = jdne.txtApellidos.getText();
                    fecha = jdne.txtFechaNac.getText();
                    consultas.modificarEmpleado(id, nif, nombre, apell, fecha);
                    p.TablaEmpleados.setModel(this.getTablaEmple());
                    jdne.dispose();
                });
                //BOTON CANCELAR MODIFICAR EMPLEADO
                jdne.btnCancelar.addActionListener((ActionEvent a) -> {
                    jdne.dispose();
                });
                jdne.setVisible(true);
            }
        });

        //DESPEDIR EMPLEADO
        p.btnDespedir.addActionListener((ActionEvent e) -> {
            empleados = consultas.leerEmpleados();
            if (p.TablaEmpleados.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(jdne, "Error, seleccione un empleado de la tabla primero");
            } else {
                Empleado despedido = empleados.get(p.TablaEmpleados.getSelectedRow());
                consultas.borrarEmpleado(despedido.getId());
                p.TablaEmpleados.setModel(this.getTablaEmple());
            }
        });

        //BOTON AGREGAR PROYECTO A EMPLEADO
        p.btnAgregaProy.addActionListener((ActionEvent e) -> {
            jdip = new JDInsertarProy(p, true);
            proyectos = consultas.leerProyectos();
            empleados = consultas.leerEmpleados();
            if (p.TablaEmpleados.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(jdne, "Error, seleccione un empleado de la tabla primero");
            } else {
                Empleado empleProy = empleados.get(p.TablaEmpleados.getSelectedRow());
                Proyecto proyecto;

                jdip.JComboInsert.setModel(this.getComboBox());
                proyecto = proyectos.get(jdip.JComboInsert.getSelectedIndex());
                jdip.txtDescripcionInsert.setText(proyecto.getDescripcion());

                jdip.JComboInsert.addActionListener((ActionEvent e1) -> {
                    Proyecto proy = proyectos.get(jdip.JComboInsert.getSelectedIndex());
                    jdip.txtDescripcionInsert.setText(proy.getDescripcion());

                });

                jdip.btnConfirmarInsert.addActionListener((ActionEvent e1) -> {
                    Proyecto proy = proyectos.get(jdip.JComboInsert.getSelectedIndex());
                    int maximo;
                    try {
                        maximo = consultas.maximoEmple(proy.getId());

                        if (maximo < proy.getMaxEmple()) {
                            consultas.agregarEmpleProy(empleProy.getId(), proy.getId());
                            jdip.dispose();
                        } else {
                            JOptionPane.showMessageDialog(p, "Ya esta el maximo de empleados por proyecto, seleccione otro o elimine un empleado del proyecto primero");
                            jdip.dispose();
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });

                jdip.btnCancelarInsert.addActionListener((ActionEvent e1) -> {
                    jdip.dispose();
                });
                jdip.setVisible(true);
            }
        });

    //BOTONES DE LOS PROYECTOS
        //ACTION EVENT DEL JCOMBOBOX
        p.jComboBox1.addActionListener((ActionEvent e) -> {
            this.setTxtsProyecto(proyectos.get(p.jComboBox1.getSelectedIndex()));
            p.TablaEmpleProyec.setModel(this.getTablaEmpleProy());
        });

        //NUEVO PROYECTO
        p.btnNuevoProy.addActionListener((ActionEvent e) -> {
            jdnp = new JDNuevoProy(p, true);

            jdnp.txtNameProyect.setText("");
            jdnp.txtDescripcion.setText("");
            jdnp.txtFchEntrega.setText("--/--/--");
            jdnp.txtFchInicio.setText("--/--/--");
            jdnp.txtMaxEmple.setText("1");

            //BOTON ACEPTAR
            jdnp.btnAceptar.addActionListener((ActionEvent e1) -> {
                String titulo, descripcion, fchIn, fchEntr;
                int max_emple;
                titulo = jdnp.txtNameProyect.getText();
                max_emple = Integer.parseInt(jdnp.txtMaxEmple.getText());
                fchIn = jdnp.txtFchInicio.getText();
                fchEntr = jdnp.txtFchEntrega.getText();
                descripcion = jdnp.txtDescripcion.getText();
                consultas.agregarProyecto(titulo, fchIn, fchEntr, descripcion, max_emple);
                p.jComboBox1.setModel(this.getComboBox());
                jdnp.dispose();
            });
            //BOTON CANCELAR
            jdnp.btnCancelar.addActionListener((ActionEvent a) -> {
                jdnp.dispose();
            });
            jdnp.setVisible(true);
        });

        //BOTON MODIFICAR PROYECTO
        p.btnModificarProy.addActionListener((ActionEvent e) -> {
            jdnp = new JDNuevoProy(p, true);
            proyectos = consultas.leerProyectos();
            Proyecto proyecto = proyectos.get(p.jComboBox1.getSelectedIndex());
            jdnp.txtDescripcion.setText(proyecto.getDescripcion());
            jdnp.txtNameProyect.setText(proyecto.getTitulo());
            jdnp.txtFchEntrega.setText(proyecto.getFechaFin());
            jdnp.txtFchInicio.setText(proyecto.getFechaInicio());
            jdnp.txtMaxEmple.setText(String.valueOf(proyecto.getMaxEmple()));

            //BOTON ACEPTAR
            jdnp.btnAceptar.addActionListener((ActionEvent e1) -> {
                String titulo, descripcion, fchIn, fchEntr;
                int max_emple, id;
                id = proyecto.getId();
                titulo = jdnp.txtNameProyect.getText();
                max_emple = Integer.parseInt(jdnp.txtMaxEmple.getText());
                fchIn = jdnp.txtFchInicio.getText();
                fchEntr = jdnp.txtFchEntrega.getText();
                descripcion = jdnp.txtDescripcion.getText();
                consultas.modificarProyecto(id, titulo, fchIn, fchEntr, descripcion, max_emple);
                p.jComboBox1.setModel(this.getComboBox());
                this.setTxtsProyecto(proyectos.get(p.jComboBox1.getSelectedIndex()));
                jdnp.dispose();
            });
            //BOTON CANCELAR
            jdnp.btnCancelar.addActionListener((ActionEvent a) -> {
                jdnp.dispose();
            });
            jdnp.setVisible(true);
        });

        //BOTON BORRAR PROYECTO
        p.btnBorrarProy.addActionListener((ActionEvent e) -> {
            proyectos = consultas.leerProyectos();
            Proyecto borrado = proyectos.get(p.jComboBox1.getSelectedIndex());
            consultas.borrarProyecto(borrado.getId());
            p.jComboBox1.setModel(this.getComboBox());
            this.setTxtsProyecto(proyectos.get(p.jComboBox1.getSelectedIndex()));
        });
        //BOTON ELIMINA RELACION
        p.btnEliminaRelacion.addActionListener((ActionEvent e) -> {
            proyectos = consultas.leerProyectos();
            empleados = consultas.leerEmpleProy(proyectos.get(p.jComboBox1.getSelectedIndex()).getId());

            if (p.TablaEmpleProyec.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(p, "Error, seleccione un empleado de la tabla primero");
            } else {
                //System.out.println(empleados.get(p.TablaEmpleProyec.getSelectedRow()).getId() + " " + proyectos.get(p.jComboBox1.getSelectedIndex()).getId());
                consultas.eliminarEmpleProy(empleados.get(p.TablaEmpleProyec.getSelectedRow()).getId(), proyectos.get(p.jComboBox1.getSelectedIndex()).getId());
                p.TablaEmpleProyec.setModel(this.getTablaEmpleProy());
            }
        });

        //GENERAR PDF
        p.btnGenerarPdf.addActionListener((ActionEvent e) -> {
            this.generaPdf();
        });

        //INICIALIZADORES DE LOS MODELOS
        //Inicializadores de las Tablas y el ComboBox de los proyectos
        p.jComboBox1.setModel(this.getComboBox());
        this.setTxtsProyecto(proyectos.get(p.jComboBox1.getSelectedIndex()));
        p.TablaEmpleProyec.setModel(this.getTablaEmpleProy());
        p.TablaEmpleados.setModel(this.getTablaEmple());
        p.setVisible(true);
    }

    //Tabla Empleados
    public DefaultTableModel getTablaEmple() {
        DefaultTableModel modeloEmple = new DefaultTableModel();
        modeloEmple.addColumn("NIF");
        modeloEmple.addColumn("Nombre");
        modeloEmple.addColumn("Apellidos");
        modeloEmple.addColumn("Fecha Nacimiento");
        Object[] fila = new Object[4];

        ArrayList<Empleado> recogerEmpleados = consultas.leerEmpleados();
        for (Empleado e : recogerEmpleados) {
            fila[0] = e.getNif();
            fila[1] = e.getNombre();
            fila[2] = e.getApellido();
            fila[3] = e.getFechaNacimiento();
            modeloEmple.addRow(fila);
        }
        return modeloEmple;
    }

    //Tabla empleados de proyecto
    public DefaultTableModel getTablaEmpleProy() {
        DefaultTableModel modeloEmpleProy = new DefaultTableModel();
        modeloEmpleProy.addColumn("NIF");
        modeloEmpleProy.addColumn("Nombre");
        modeloEmpleProy.addColumn("Apellidos");
        modeloEmpleProy.addColumn("Fecha Nacimiento");
        Object[] fila = new Object[4];

        ArrayList<Empleado> recogerEmpleados = consultas.leerEmpleProy(proyectos.get(p.jComboBox1.getSelectedIndex()).getId());
        for (Empleado e : recogerEmpleados) {
            fila[0] = e.getNif();
            fila[1] = e.getNombre();
            fila[2] = e.getApellido();
            fila[3] = e.getFechaNacimiento();
            modeloEmpleProy.addRow(fila);
        }
        return modeloEmpleProy;
    }

    //Combo proyectos
    public DefaultComboBoxModel getComboBox() {
        DefaultComboBoxModel ComboModel = new DefaultComboBoxModel();
        proyectos = consultas.leerProyectos();
        for (Proyecto Pro : proyectos) {
            ComboModel.addElement(Pro.getTitulo());
        }
        return ComboModel;
    }

    public void setTxtsProyecto(Proyecto proyec) {
        p.txtFchEntrega.setText(proyec.getFechaFin());
        p.txtFchInicio.setText(proyec.getFechaInicio());
        p.txtNMaximo.setText(String.valueOf(proyec.getMaxEmple()));
        p.txtDescripcion.setText(proyec.getDescripcion());
    }
//informes

    public void generaPdf() {
        Document documento = new Document();
        FileOutputStream ficheroPdf;
        PdfPTable tabla_emple, tabla_proyec, tabla_relacional;
        empleados = consultas.leerEmpleados();
        proyectos = consultas.leerProyectos();
        ArrayList<Empleado> relacional;

        try {
            //nombre que tomara el fichero
            ficheroPdf = new FileOutputStream("informe.PDF");
            PdfWriter.getInstance(documento, ficheroPdf).setInitialLeading(20);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        try {
            //abrimos el documento para editarlo
            documento.open();
            //aqui agregamos todo el contenido del PDF

            documento.add(new Paragraph("Este informe es una manera de imprimir la informacion, en forma de tablas, de la base de datos"));
            documento.add(new Paragraph(" "));
            //TABLA DE LOS EMPLEADOS
            documento.add(new Paragraph("Empleados de la compañia:"));
            documento.add(new Paragraph(" "));
                //inicio la tabla con las 4 columnas de los empleados
            tabla_emple = new PdfPTable(4);
                //cada 4 addCell, es una fila nueva
            tabla_emple.addCell("NIF");
            tabla_emple.addCell("Nombre");
            tabla_emple.addCell("Apellidos");
            tabla_emple.addCell("Fecha de Nacimiento");
            for (Empleado e : empleados) {
                tabla_emple.addCell(e.getNif());
                tabla_emple.addCell(e.getNombre());
                tabla_emple.addCell(e.getApellido());
                tabla_emple.addCell(e.getFechaNacimiento());
            }
            documento.add(tabla_emple);
            documento.add(new Paragraph(" "));

            //TABLA DE LOS PROYECTOS
            documento.add(new Paragraph("Proyectos de la compañia:"));
            documento.add(new Paragraph(" "));
            tabla_proyec = new PdfPTable(5);
            tabla_proyec.addCell("Titulo");
            tabla_proyec.addCell("Fecha de Inicio");
            tabla_proyec.addCell("Fecha de Entrega");
            tabla_proyec.addCell("Descripcion");
            tabla_proyec.addCell("Nº Maximo de empleados");
            for (Proyecto p : proyectos) {
                tabla_proyec.addCell(p.getTitulo());
                tabla_proyec.addCell(p.getFechaInicio());
                tabla_proyec.addCell(p.getFechaFin());
                tabla_proyec.addCell(p.getDescripcion());
                tabla_proyec.addCell(String.valueOf(p.getMaxEmple()));
            }
            documento.add(tabla_proyec);
            documento.add(new Paragraph(" "));

            //TABLA RELACIONAL
            documento.add(new Paragraph("Tabla relacional de empleados por proyecto:"));
            documento.add(new Paragraph(" "));
            tabla_relacional = new PdfPTable(4);

            for (Proyecto p : proyectos) {
                Paragraph titulos = new Paragraph(p.getTitulo());
                titulos.setAlignment(1);
                PdfPCell celda = new PdfPCell(titulos);
                celda.setColspan(4);
                celda.setRowspan(2);
                tabla_relacional.addCell(celda);
                tabla_relacional.addCell("NIF");
                tabla_relacional.addCell("Nombre");
                tabla_relacional.addCell("Apellidos");
                tabla_relacional.addCell("Fecha de Nacimiento");
                relacional = consultas.leerEmpleProy(p.getId());
                for (Empleado er : relacional) {
                    tabla_relacional.addCell(er.getNif());
                    tabla_relacional.addCell(er.getNombre());
                    tabla_relacional.addCell(er.getApellido());
                    tabla_relacional.addCell(er.getFechaNacimiento());
                }
                Paragraph fin = new Paragraph(" ");
                titulos.setAlignment(1);

                PdfPCell celda2 = new PdfPCell(fin);
                celda2.setColspan(4);
                celda2.setRowspan(2);
            }
            documento.add(tabla_relacional);

            documento.add(new Paragraph(" "));
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("Alumno: Alberto Manuel Moreno"));
            documento.add(new Paragraph("Asignatura: Programación"));
            documento.add(new Paragraph("Proyecto 1"));
            documento.add(new Paragraph(""));

            //cerramos el documento y se genera
            JOptionPane.showMessageDialog(null, "El archivo PDF se ha generado correctamente");
            documento.close();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

    }
}

//CODIGO PARA TRABAJAR CON EL PDF

//  documento.add(new Paragraph(""));
//  nombreDeLaTabla = new PdfPTable(numero de columnas);  este define cada cuantas celdas hace un cambio de fila
//  tabla_proyec.addCell("lo que quieres que ponga en la celda (se usa para poner los nombres de los campos)");
//  for (Proyecto p : proyectos  (arraylist de los objetos con los que vas a rellenar los campos) ) {
//      tabla_proyec.addCell(p.getTitulo());
//      tabla_proyec.addCell(p.getFechaInicio());
//      tabla_proyec.addCell(p.getFechaFin());
//      tabla_proyec.addCell(p.getDescripcion());
//      tabla_proyec.addCell(String.valueOf(p.getMaxEmple()));
//  }
//  documento.add(tabla_proyec);
//  documento.add(new Paragraph(" "));