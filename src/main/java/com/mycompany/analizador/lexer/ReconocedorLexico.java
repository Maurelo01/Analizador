package com.mycompany.analizador.lexer;
public class ReconocedorLexico implements Automata
{
    private final int[][] transicion; // [estado][clase]
    private final int estadoInicial; // índice del estado inicial
    private final boolean[] aceptacion; // aceptacion[i] = true si el estado i es de aceptacion
    private final TipoToken[] tipoPorEstado; // tipoPorEstado[i] = TipoToken si estado i es final; null si no
    private int estado; // estado actual
    
    public ReconocedorLexico(int[][] transicion, int estadoInicial, boolean[] aceptacion, TipoToken[] tipoPorEstado)
    {
        this.transicion = transicion;
        this.estadoInicial = estadoInicial;
        this.aceptacion = aceptacion;
        this.tipoPorEstado = tipoPorEstado;
        this.estado = estadoInicial;
    }
    
    public static int clasificar(char c)
    {
        if (c == '\n') return C_SALTO;                
        if (c == '\t' || c == ' ' || c == '\r') return C_ESPACIO;
        if (Character.isLetter(c)) return C_LETRA;
        if (Character.isDigit(c)) return C_DIGITO;
        switch (c)
        {
            case '+': case '-': case '%': case '=': case '!': case '<': case '>': case '&': case '|': return C_OPERADOR;
            case '(': case ')': case '[': case ']': case '{': case '}': return C_AGRUP;
            case ',': case ';': case ':': return C_PUNT;
            case '"': return C_COMILLA;
            case '/': return C_SLASH;
            case '.': return C_PUNTO;
            case '*': return C_ASTERISCO;
            case '\\': return C_BACKSLASH;
            default: return C_OTRO;
        }
    }
    
    public static final int C_ESPACIO = 0; // espacios en blanco
    public static final int C_LETRA = 1; // a-z A-Z
    public static final int C_DIGITO = 2; // 0-9
    public static final int C_OPERADOR = 3; // + - % =
    public static final int C_AGRUP = 4; // ( ) [ ] { }
    public static final int C_PUNT = 5; // . , ; :
    public static final int C_COMILLA = 6; // "
    public static final int C_OTRO = 7; // cualquier otro
    public static final int C_SLASH = 8; // /
    public static final int C_PUNTO = 9; // . para decimales
    public static final int C_ASTERISCO = 10; // *
    public static final int C_SALTO = 11; // \n
    public static final int C_BACKSLASH = 12; // \
    
    @Override
    public void reiniciar() 
    {
        this.estado = estadoInicial;
    }

    @Override
    public boolean transitar(char c) 
    {
        int clase = clasificar(c);
        if (estado < 0 || estado >= transicion.length)
        {
            return false;
        }
        if (clase < 0 || clase >= transicion[estado].length) 
        {
            return false; // clase fuera de rango
        }
        int siguiente = transicion[estado][clase];
        if (siguiente < 0) 
        {
            return false; // no hay transición
        }
        estado = siguiente;
        return true;
    }

    @Override
    public boolean estaEnAceptacion() 
    {
        return (estado >= 0 && estado < aceptacion.length) && aceptacion[estado];
    }

    @Override
    public int estadoActual() 
    {
        return estado;
    }

    @Override
    public TipoToken tipoDeEstadoActual() 
    {
        if (estado >= 0 && estado < tipoPorEstado.length) return tipoPorEstado[estado];
        return null;
    }
    
    public TipoToken tipoDeAceptacion(int estado) 
    {
        if (estado >= 0 && estado < tipoPorEstado.length) return tipoPorEstado[estado];
        return null;
    }
    
    public boolean esEstadoDeAceptacion(int s) 
    {
        return (s >= 0 && s < aceptacion.length) && aceptacion[s];
    }
}
