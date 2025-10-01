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
        if (automata == null) 
        {
            r.errores.add(new ErrorLexico("", 1, 1, "Automata no configurado"));
            return r;
        }
        int fila = 1;
        int col = 1;
        int i = 0;
        while (i < texto.length())
        {
            automata.reiniciar();
            StringBuilder lex = new StringBuilder();
            int inicioFila = fila, inicioCol = col;
            int ultimoAceptIdx = -1;
            TipoToken tipoAcept = null;
            int j = i;
            while (j < texto.length())
            {
                char c = texto.charAt(j);
                int estadoAntes = automata.estadoActual();
                boolean ok = automata.transitar(c);
                if (!ok) break;
                lex.append(c);
                // Avanzar posicion
                if (c == '\n') { fila++; col = 1; }
                else if (c == '\t') { col += 4; }
                else { col++; }
                if (automata.estaEnAceptacion()) 
                {
                    ultimoAceptIdx = j;
                    TipoToken t = tipoDeAceptacion(automata);
                    tipoAcept = t;
                }
                if (listener != null) listener.onPaso(estadoAntes, c, automata.estadoActual());
                j++;
            }
            if (ultimoAceptIdx >= i)
            {
                // Retroceso donde se devuelve lo leido despues del ultimo aceptado
                int regreso = j - (ultimoAceptIdx + 1);
                while (regreso-- > 0)
                {
                    char c = texto.charAt(--j);
                }
                String lexema = lex.substring(0, (ultimoAceptIdx - i) + 1);
                if (tipoAcept != null) // Si el token fue espacios no se emite
                {
                    if (tipoAcept == TipoToken.IDENTIFICADOR && esReservada(lexema))
                    {
                        Token t = new Token(TipoToken.RESERVADA, lexema, inicioFila, inicioCol);
                        r.tokens.add(t);
                        if (listener != null) listener.onToken(t);
                    }
                    else if (tipoAcept != null)
                    {
                        Token t = new Token(tipoAcept, lexema, inicioFila, inicioCol);
                        r.tokens.add(t);
                        if (listener != null) listener.onToken(t);
                    }
                }
                i = ultimoAceptIdx + 1; // continuar despues del ultimo aceptado
            }
            else
            {
                // sino hubo aceptacion entonces error en el caracter actual
                char c = texto.charAt(i);
                if (Character.isISOControl(c) && c != '\n' && c != '\t' && c != '\r') // no errores de control
                {
                    ErrorLexico err = new ErrorLexico(String.valueOf(c), fila, col, "Carácter de control no permitido");
                    r.errores.add(err);
                    if (listener != null) listener.onError(err);
                }
                else
                {
                    ErrorLexico err = new ErrorLexico(String.valueOf(c), fila, col, "Símbolo no reconocido");
                    r.errores.add(err);
                    if (listener != null) listener.onError(err);
                }
                // avanzar un caracter
                if (c == '\n') { fila++; col = 1; }
                else if (c == '\t') { col += 4; }
                else { col++; }
                i++;
            }
        }
        return r;
    }
    
    private boolean esReservada(String lexema) 
    {
        for (String rsv : RESERVADAS)
        {
            if (iguales(lexema, rsv)) return true;
        }
        return false;
    }
    
    private boolean iguales(String a, String b) 
    {
        if (a.length() != b.length()) return false;
        for (int k = 0; k < a.length(); k++) 
        {
            if (a.charAt(k) != b.charAt(k)) return false;
        }
        return true;
    }
    
    private TipoToken tipoDeAceptacion(Automata a) 
    {
        if (a instanceof ReconocedorLexico) 
        {
            ReconocedorLexico rl = (ReconocedorLexico) a;
            return rl.tipoDeAceptacion(rl.estadoActual());
        }
        return null;
    }
}
