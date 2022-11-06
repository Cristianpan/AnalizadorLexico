package Controlador;

import Modelo.Lexer;
import Vista.Ventana;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class CtrlVista implements ActionListener {
    private Ventana frm;
    private Lexer lexer;
    private String rutaArchivo = null;
    private String nombreArchivo = null; 

    public CtrlVista(Ventana frm, Lexer lexer) {
        this.frm = frm;
        this.lexer = lexer;
        this.frm.getBtnBuscar().addActionListener(this);
        this.frm.getBtnAnalizar().addActionListener(this);
        this.frm.getBtnAnalizar().setEnabled(false);

        this.lexer.setTerminal(this.frm.getTxtTerminal());
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.frm.getBtnBuscar()) {
            limpiarCampos();

            JFileChooser selectorArchivos = new JFileChooser();
            selectorArchivos.setFileSelectionMode(JFileChooser.FILES_ONLY);
            selectorArchivos.showOpenDialog(frm);

            File archivo = selectorArchivos.getSelectedFile(); // obtiene el archivo seleccionado

            if ((archivo == null) || (archivo.getName().equals(""))) {
                JOptionPane.showMessageDialog(null, "Nombre de archivo inválido", "Nombre de archivo inválido",
                JOptionPane.ERROR_MESSAGE);
            } else {
                this.rutaArchivo = archivo.getAbsolutePath();
                this.nombreArchivo = archivo.getName(); 
                this.frm.getTxtDireccion().setText(archivo.getAbsolutePath());
                this.frm.getBtnAnalizar().setEnabled(true);
            }
        } else if (event.getSource() == this.frm.getBtnAnalizar()) {
           if (nombreArchivo.substring(nombreArchivo.length() - 4).equals(".mio")){
                try {
                    this.lexer.Lexer(rutaArchivo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
           } else {
                JOptionPane.showMessageDialog(frm, "El dominio del archivo no es .mio");
                limpiarCampos(); 
           }

        }
    }

    public void limpiarCampos(){
        this.frm.getTxtDireccion().setText(null);
        this.frm.getTxtTerminal().setText(null);
    }
}