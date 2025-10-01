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
                    char cR = texto.charAt(--j);
                }
                String lexema = lex.substring(0, (ultimoAceptIdx - i) + 1);
                if (tipoAcept == null || tipoAcept == TipoToken.COMENTARIO)
                {
                    // no emitir token solo avanzar
                }
                else if (tipoAcept == TipoToken.IDENTIFICADOR && esReservada(lexema))
                {
                    Token t = new Token(TipoToken.RESERVADA, lexema, inicioFila, inicioCol);
                    r.tokens.add(t);
                    if (listener != null) listener.onToken(t);
                }
                else if (tipoAcept == TipoToken.OPERADOR && "/".equals(lexema)) 
                {
                    if (j < texto.length()) 
                    {
                        char next = texto.charAt(j);
                        if (next == '/') 
                        {
                            // Comentario de linea consumir hasta \n y no emitir token
                            while (j < texto.length() && texto.charAt(j) != '\n') 
                            { 
                                j++; 
                            }
                            i = j; // continuar desde el fin del comentario
                            continue; // saltar emision
                        } 
                        else if (next == '*') 
                        {
                            // Comentario de bloque consumir hasta */ si existe
                            int k = j + 1; // ya vimos /* se busca cierre
                            boolean cerrado = false;
                            while (k < texto.length()) 
                            {
                                char cprev = texto.charAt(k - 1);
                                char ccur  = texto.charAt(k);
                                if (cprev == '*' && ccur == '/') 
                                {
                                    cerrado = true;
                                    k++; 
                                    break; 
                                }
                                k++;
                            }
                            if (!cerrado)
                            {
                                r.errores.add(new ErrorLexico("/", inicioFila, inicioCol, "Comentario de bloque sin cerrar"));
                                if (listener != null) listener.onError(r.errores.get(r.errores.size()-1));
                                i = texto.length();
                                break;
                            } 
                            else 
                            {
                                i = k;
                                continue;
                            }
                        }
                    }
                }
                else if (tipoAcept == TipoToken.NUMERO) 
                {
                    if (j < texto.length() && texto.charAt(j) == '.') 
                    {
                        // Si no hay digito despues del punto es error
                        if (j + 1 >= texto.length() || !Character.isDigit(texto.charAt(j + 1))) 
                        {
                            r.errores.add(new ErrorLexico(".", fila, col, "Decimal incompleto"));
                            if (listener != null) listener.onError(r.errores.get(r.errores.size()-1));
                            i = j + 1;
                            continue;
                        }
                    }
                }
                else if (tipoAcept == TipoToken.PUNTUACION && ".".equals(lexema))
                {
                    boolean p1 = (j < texto.length()) && texto.charAt(j) == '.';
                    boolean p2 = (j + 1 < texto.length()) && texto.charAt(j + 1) == '.';
                    if (p1 && p2) 
                    {
                        // formar un token con lexema ...
                        lexema = "...";
                        // consumir 2 puntos adicionales de entrada
                        i = j + 2; // saltam los dos . extra
                        col += 2; // avance aproximado de columna
                        Token t = new Token(TipoToken.PUNTUACION, lexema, inicioFila, inicioCol);
                        r.tokens.add(t);
                        if (listener != null) listener.onToken(t);
                        continue;
                    }
                    if (j < texto.length() && Character.isDigit(texto.charAt(j))) 
                    {
                        r.errores.add(new ErrorLexico(".", fila, col, "Formato decimal inválido"));
                        if (listener != null) listener.onError(r.errores.get(r.errores.size()-1));
                        i = ultimoAceptIdx + 1;
                        continue;
                    }
                    Token t = new Token(TipoToken.PUNTUACION, lexema, inicioFila, inicioCol);
                    r.tokens.add(t);
                    if (listener != null) listener.onToken(t);
                }
                else
                {
                    Token t = new Token(tipoAcept, lexema, inicioFila, inicioCol);
                    r.tokens.add(t);
                    if (listener != null) listener.onToken(t);
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
                    String desc = "Símbolo no reconocido";
                    if (c == '\"') 
                    {
                        desc = "Cadena sin cerrar";
                        // saltar hasta fin de cadena o salto de linea
                        int k = i + 1;
                        while (k < texto.length()) 
                        {
                            char ck = texto.charAt(k);
                            if (ck == '\n') 
                            { 
                                k++; 
                                break; 
                            } // si es comilla se consume
                            k++;
                        }
                        r.errores.add(new ErrorLexico(String.valueOf(c), fila, col, desc));
                        if (listener != null) listener.onError(r.errores.get(r.errores.size()-1));
                        // avanza i al punto sincronizado y actualiza fila/col
                        while (i < k) 
                        {
                            char adv = texto.charAt(i++);
                            if (adv == '\n') 
                            { 
                                fila++; col = 1; 
                            } 
                            else if (adv == '\t') 
                            { 
                                col += 4; 
                            } 
                            else 
                            { 
                                col++; 
                            }
                        }
                        continue;
                    }
                    else if (c == '/' && i+1 < texto.length() && texto.charAt(i+1) == '*')
                    {
                        desc = "Comentario de bloque sin cerrar";
                        ErrorLexico err = new ErrorLexico(String.valueOf(c), fila, col, desc);
                        r.errores.add(err);
                        if (listener != null) listener.onError(err);
                        i = texto.length();
                        break;
                    }
                    ErrorLexico err = new ErrorLexico(String.valueOf(c), fila, col, desc);
                    r.errores.add(err);
                    if (listener != null) listener.onError(err);
                    // avanzar un caracter
                    if (c == '\n') { fila++; col = 1; }
                    else if (c == '\t') { col += 4; }
                    else { col++; }
                    i++;
                }
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
