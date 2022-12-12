package Controlador;

import Modelo.Lexer;
import Modelo.Sintactico;
import Vista.Ventana;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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
            this.frm.getTxtTerminal().setText(null);
           if (nombreArchivo.substring(nombreArchivo.length() - 4).equals(".mio")){
                try {
                    this.rutaArchivo = obtenerRuta(rutaArchivo);
                    if(this.lexer.Lexer(rutaArchivo + nombreArchivo, rutaArchivo)){
                        Sintactico sintactico = new Sintactico(rutaArchivo, this.frm.getTxtTerminal()); 

                        if (sintactico.analizarGramática()){
                            this.frm.getTxtTerminal().append("It's okay");
                        }
                    }
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


    public String obtenerRuta(String ruta) {
        int i = ruta.length();
        while (ruta.charAt(i - 1) != '\\') {
            i--;
        }

        return ruta.substring(0, i);
    }
}