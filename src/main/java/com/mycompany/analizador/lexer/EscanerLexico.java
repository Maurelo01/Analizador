package com.mycompany.analizador.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EscanerLexico // Recorrera el texto y emitira tokens/errores
{
    public static final String[] RESERVADAS = {"SI","si","ENTONCES","entonces","PARA","para","ESCRIBIR","escribir"};
    public static class Resultado // Resultados del analisis
    {
        public final List<Token> tokens = new ArrayList<>();
        public final List<ErrorLexico> errores = new ArrayList<>();
        public boolean hayErrores() 
        { 
            return !errores.isEmpty(); 
        }
        @Override public String toString() 
        {
            return "Resultado{tokens=" + tokens.size() + ", errores=" + errores.size() + '}';
        }
    }
    
    public interface ListenerTraza 
    {
        void onPaso(int estadoAnterior, char c, int estadoSiguiente);
        void onToken(Token t);
        void onError(ErrorLexico e);
    }
    
    private Automata automata; // Se inserta desde fuera
    private ListenerTraza listener;// para Modo depuracion
    
    public void setAutomata(Automata automata) // Permite meter el Reconocedor lexico
    {
        this.automata = Objects.requireNonNull(automata, "automata");
    }
    
    // Permite meter el listener para trazar
    public void setListener(ListenerTraza listener) { this.listener = listener; }
    
    public Resultado analizar(String texto)
    {
        Resultado r = new Resultado();
        if (texto == null || texto.isEmpty()) return r;
        int fila = 1;
        int col = 1;
        for (int i = 0; i < texto.length(); i++)
        {
            char c = texto.charAt(i);
            // Control de fila/columna
            if (c == '\n')
            {
                fila++; col = 1; 
                continue;
            }
            if (c == '\t') 
            { 
                col += 4; 
                continue; 
            }
            if (Character.isISOControl(c) && c != '\n' && c != '\t' && c != '\r')
            {
                ErrorLexico err = new ErrorLexico(String.valueOf(c), fila, col, "Carácter de control no permitido");
                r.errores.add(err);
                if (listener != null) listener.onError(err);
            }
            col++;
        }
        return r;
    }
}
