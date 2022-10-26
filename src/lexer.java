import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class lexer {
    static ArrayList<String> tokens = new ArrayList<>();
    static Set<String> ids = new HashSet<>();
    static Set<String> txt = new HashSet<>();
    static Set<String> vls = new HashSet<>();

    public static void main(String[] args) throws Exception {
        File fichero = new File("programa.mio");
        Scanner s = null;
        int linea = 0;
        try {
            s = new Scanner(fichero);
            while (s.hasNextLine()) {
                linea++;
                obtenerDatos(s.nextLine(), linea);
            }
            s.close();
        } catch (Exception ex) {
            System.out.println("Mensaje " + ex.getMessage());
        }

        generarArchivoLex();
        generarArchivoSim();
    }

    public static void obtenerDatos(String cadena, int linea) {
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
                if (aux != 0){
                    endIndex = aux; 
                }
            }

            if (cadena.charAt(endIndex) == ' ' || endIndex + 1 == length || isOperador(cadena.charAt(endIndex))) {
                auxCadena = cadena.substring(beginIndex, endIndex).trim();
                // Para obtener el último caracter de la linea
                if (endIndex + 1 == length)
                    auxCadena = cadena.substring(beginIndex, endIndex + 1).trim();

                if (isPalabraReservada(auxCadena)) {
                    tokens.add(auxCadena);
                } else if (isIdentificador(auxCadena)) {
                    tokens.add("[id]");
                    ids.add(auxCadena);
                } else if (isLiteralTexto(auxCadena)) {
                    tokens.add("[litalfnum]");
                    txt.add(auxCadena);
                } else if (isLiteralNumerica(auxCadena)) {
                    tokens.add("[valorn]");
                    vls.add(auxCadena);
                } else {
                    if (auxCadena.length() != 0) {
                        if (isOperador(auxCadena.charAt(0)))
                            tokens.add(auxCadena);
                        else 
                            tokens.add("ERROR en LINEA " + linea + " simbolo invalido: " + auxCadena);
                    }
                }
                beginIndex = endIndex;
            }
            endIndex++;
        }
    }

    public static void generarArchivoLex() throws IOException{
        FileWriter archivo = new FileWriter("prueba.lex"); 
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

    public static void generarArchivoSim() throws IOException{
        FileWriter archivo = new FileWriter("factorial.sim"); 
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
            for (String string: txt){
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

    public static int obtenerValorDecimal(String num){
        int valor = 0; 
        if (num.charAt(0) == '0' && num.charAt(1) == 'x'){
            for (int i = 2; i < num.length(); i++){
                if(((int)num.charAt(i)-48)<= 9){
                    valor = (int) (valor + ((int)num.charAt(i)-48)*Math.pow(16, i-2));  
                } else {
                    valor = (int) (valor + ((int)num.charAt(i)-55)*Math.pow(16, i-2));
                }
            }
        }else {
            valor = Integer.parseInt(num); 
        }

        return valor; 
    }



    public static int aux(int i, String cadena) {
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

    public static boolean isIdentificador(String cadena) {
        boolean bandera = false;
        Pattern pat = Pattern.compile("[a-zA-Z]+[0-9]*");
        if (isMatch(cadena, pat) && cadena.length() <= 16) {
            bandera = true;
        }
        return bandera;
    }

    public static boolean isPalabraReservada(String cadena) {
        Pattern pat = Pattern.compile("PROGRAMA|IMPRIME|LEE|FINPROG");

        return isMatch(cadena, pat);
    }

    public static boolean isLiteralNumerica(String cadena) {
        Pattern pat = Pattern.compile("[0-9]+|0x([0-9]|[A-F])+");
        return isMatch(cadena, pat);
    }

    public static boolean isLiteralTexto(String cadena) {
        Pattern pat = Pattern.compile("\"[a-zA-Z0-9 ]+\"");

        return isMatch(cadena, pat);
    }

    // Devuelve true si la cadena pertence al LR
    public static boolean isMatch(String cadena, Pattern pat) {
        boolean bandera = false;
        Matcher mat = pat.matcher(cadena);
        if (mat.matches()) {
            bandera = true;
        }

        return bandera;

    }

    public static boolean isOperador(char c) {
        boolean bandera = false;
        if (c == '+' || c == '-' || c == '*' || c == '/' || c == '=') {
            bandera = true;
        }

        return bandera;
    }
}
