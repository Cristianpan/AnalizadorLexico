package Modelo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.ObjectInputFilter.Status;
import java.sql.SQLInvalidAuthorizationSpecException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;

public class Lexer {
    private ArrayList<String> tokens = new ArrayList<>();
    private Set<String> ids = new HashSet<>();
    private Set<String> txt = new HashSet<>();
    private Set<String> vls = new HashSet<>();
    private JTextArea terminal;

    public void setTerminal(JTextArea terminal) {
        this.terminal = terminal;
    }

    public boolean Lexer(String ruta) throws Exception {
        File fichero = new File(ruta);
        borrarFicheros(obtenerRuta(ruta)); 
        Scanner s = null;
        int linea = 0;

        try {
            s = new Scanner(fichero);
            while (s.hasNextLine()) {
                linea++;
                if(obtenerDatos(s.nextLine(), linea) == false){
                    return false; 
                }
            }
            s.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        this.terminal.append("It's okay");
        generarArchivoLex(obtenerRuta(ruta));
        generarArchivoSim(obtenerRuta(ruta));

        return true; 


    }

    public boolean obtenerDatos(String cadena, int linea) {
        int endIndex = 0, beginIndex = 0, length = 0, aux;
        String auxCadena = "";
        cadena = cadena.trim();
        length = cadena.length();

        endIndex = 0;
        beginIndex = 0;

        while (endIndex < length && cadena.charAt(0) != '#') {

            // Intenta encontrar el par de " " para ignorar los separadores
            if (cadena.charAt(endIndex) == '\"') {
                aux = aux(endIndex + 1, cadena);
                if (aux != 0) {
                    endIndex = aux;
                }
            }

            if (cadena.charAt(endIndex) == ' ' || endIndex + 1 == length || isOperador(cadena.charAt(endIndex))) {
                // Para obtener el último caracter de la linea
                if (endIndex + 1 == length) {
                    auxCadena = cadena.substring(beginIndex, endIndex + 1).trim();
                } else {
                    auxCadena = cadena.substring(beginIndex, endIndex).trim();
                }

                if (auxCadena.length() != 0) {
                    if (isOperador(auxCadena.charAt(0))) {
                        if (analizarCadena(auxCadena.substring(0, 1).trim(), linea) == false || analizarCadena(auxCadena.substring(1, auxCadena.length()).trim(), linea) == false)
                            return false; 
                    } else {
                        if (analizarCadena(auxCadena, linea) == false)
                            return false; 
                    }
                }
                beginIndex = endIndex;
            }
            endIndex++;
        }

        return true; 
    }

    public boolean analizarCadena(String cadena, int linea) {

        if (isPalabraReservada(cadena)) {
            tokens.add(cadena);
        } else if (isIdentificador(cadena)) {
            tokens.add("[id]");
            ids.add(cadena);
        } else if (isLiteralTexto(cadena)) {
            tokens.add("[litalfnum]");
            txt.add(cadena);
        } else if (isLiteralNumerica(cadena)) {
            tokens.add("[valorn]");
            vls.add(cadena);
        } else {
            if (cadena.length() != 0) {
                if (isOperador(cadena.charAt(0)))
                    tokens.add(cadena);
                else {
                    this.terminal.append("Error en la linea " + linea + ", simbolo invalido: " + cadena + "\n\n"); 
                    return false; 
                }
            }
        }

        return true; 
    }

    public void generarArchivoLex(String ruta) throws IOException {
        ruta = ruta + "prueba.lex"; 
        FileWriter archivo = new FileWriter(ruta);
        PrintWriter out = new PrintWriter(archivo);
        try {
            for (String string : tokens) {
                out.println(string);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        archivo.close();

    }

    public void generarArchivoSim(String ruta) throws IOException {
        ruta = ruta + "factorial.sim"; 
        FileWriter archivo = new FileWriter(ruta);
        PrintWriter out = new PrintWriter(archivo);
        int i = 1;
        try {
            out.println("IDS");
            for (String string : ids) {
                out.println(string + " id" + i);
                i++;
            }
            i = 1;
            out.println("\nTXTS");
            for (String string : txt) {
                out.println(string + "  txt" + i);
                i++;
            }
            i = 1;
            out.println("\nNUM");
            for (String string : vls) {
                out.println(string + "  " + obtenerValorDecimal(string));
                i++;
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        archivo.close();

    }

    public int obtenerValorDecimal(String num) {
        int valor = 0;
        if (num.charAt(0) == '0' && num.charAt(1) == 'x') {
            for (int i = 2; i < num.length(); i++) {
                if (((int) num.charAt(i) - 48) <= 9) {
                    valor = (int) (valor + ((int) num.charAt(i) - 48) * Math.pow(16, i - 2));
                } else {
                    valor = (int) (valor + ((int) num.charAt(i) - 55) * Math.pow(16, i - 2));
                }
            }
        } else {
            valor = Integer.parseInt(num);
        }

        return valor;
    }

    public int aux(int i, String cadena) {
        boolean existe = false;
        int j = 0;
        while (i < cadena.length() && existe == false) {

            if (cadena.charAt(i) == '\"') {
                existe = true;
                j = i;
            }
            i++;
        }

        return j;
    }

    public boolean isIdentificador(String cadena) {
        boolean bandera = false;
        Pattern pat = Pattern.compile("[a-zA-Z]+[0-9]*");
        if (isMatch(cadena, pat) && cadena.length() <= 16) {
            bandera = true;
        }
        return bandera;
    }

    public boolean isPalabraReservada(String cadena) {
        Pattern pat = Pattern.compile("PROGRAMA|IMPRIME|LEE|FINPROG");

        return isMatch(cadena, pat);
    }

    public boolean isLiteralNumerica(String cadena) {
        Pattern pat = Pattern.compile("[0-9]+|0x([0-9]|[A-F])+");
        return isMatch(cadena, pat);
    }

    public boolean isLiteralTexto(String cadena) {
        Pattern pat = Pattern.compile("\"[a-zA-Z0-9 ]+\"");

        return isMatch(cadena, pat);
    }

    // Devuelve true si la cadena pertence al LR
    public boolean isMatch(String cadena, Pattern pat) {
        boolean bandera = false;
        Matcher mat = pat.matcher(cadena);
        if (mat.matches()) {
            bandera = true;
        }

        return bandera;

    }

    public boolean isOperador(char c) {
        boolean bandera = false;
        if (c == '+' || c == '-' || c == '*' || c == '/' || c == '=') {
            bandera = true;
        }

        return bandera;
    }

    public String obtenerRuta(String ruta) {
        int i = ruta.length();
        while (ruta.charAt(i - 1) != '\\') {
            i--;
        }

        return ruta.substring(0, i);
    }

    public void borrarFicheros(String ruta) throws IOException{
        File archivoSim = new File(ruta + "factorial.sim");
        File archivoLex = new File(ruta + "prueba.lex"); 

        if (archivoSim != null){
            archivoSim.delete(); 
        }

        if (archivoLex != null){
            archivoLex.delete(); 
        }

    }

}
