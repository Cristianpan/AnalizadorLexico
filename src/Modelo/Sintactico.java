package Modelo;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;

public class Sintactico {

    private String rutaLex;
    private ArrayList<String> lineasCodigo = new ArrayList<>();
    private JTextArea terminal;

    public Sintactico(String rutaLex, JTextArea terminal) {
        this.rutaLex = rutaLex;
        this.terminal = terminal;
    }

    public boolean analizarGramática() {
        obtenerDatos();
        String numLinea = "";
        int cont = this.lineasCodigo.size();

        if (!this.lineasCodigo.get(1).equals("PROGRAMA[id]")){
            this.terminal.append("Error sintactico detectado.\nVerifique que su programa contenga al inicio: \"PROGRAMA nombrePrograma\"");
            return false; 
        } else if (!this.lineasCodigo.get(cont - 1).equals("FINPROG")){
            this.terminal.append("Error sintactico detectado.\nVerifique que su programa contenga al final: \"FINPROG\"");
            return false; 
        } else {
            for (int i = 2; i <cont - 2; i++){
                if ((i + 1) % 2 == 1){
                    numLinea = this.lineasCodigo.get(i); 
                } else {
                    if (!isExpresion(this.lineasCodigo.get(i)) && !isSentencia(this.lineasCodigo.get(i))){
                        this.terminal.append("Error sintactico en la linea: " + numLinea);
                        this.terminal.append("\nVerifique que la sentencia este escrito correctamente");
                        return false; 
                    }
                }

            }
        }

        return true; 
    }
    public boolean isExpresion(String codigo) {
        Pattern pat = Pattern.compile("\\[id\\]=(\\[valorn\\]|\\[id\\])([*+/-](\\[valorn\\]|\\[id\\]))*");
        return isMatch(codigo, pat);
    }

    public boolean isSentencia(String codigo) {
        Pattern pat = Pattern.compile("(IMPRIME)(\\[id\\]|\\[valorn\\]|\\[litalfnum\\])");
        Pattern pat2 = Pattern.compile("(LEE)(\\[id\\])");

        if (isMatch(codigo, pat) || isMatch(codigo, pat2)) {
            return true;
        }

        return false;
    }

    public void obtenerDatos() {
        File fichero = new File(this.rutaLex + "prueba.lex");
        Scanner s = null;
        try {
            s = new Scanner(fichero);
            while (s.hasNextLine()) {
                this.lineasCodigo.add(s.nextLine());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            s.close();
        }
    }

    public boolean isMatch(String cadena, Pattern pat) {
        boolean bandera = false;
        Matcher mat = pat.matcher(cadena);
        if (mat.matches()) {
            bandera = true;
        }
        return bandera;
    }
}
