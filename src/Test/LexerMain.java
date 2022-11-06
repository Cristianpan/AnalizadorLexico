package Test;

import Controlador.CtrlVista;
import Modelo.Lexer;
import Vista.Ventana;

public class LexerMain {
    public static void main(String[] args) {
        Lexer lexer = new Lexer(); 
        Ventana frm = new Ventana(); 

        CtrlVista CtrlVista = new CtrlVista(frm, lexer); 

        frm.setVisible(true);

        
    }
}
