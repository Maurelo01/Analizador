package com.mycompany.analizador.lexer;
public class Token 
{
    private final TipoToken tipo;
    private final String lexema;
    private final int fila;
    private final int columna;
    // Represta un token lexico (es un DTO)
    public Token(TipoToken tipo, String lexema, int fila, int columna)
    {
        this.tipo = tipo;
        this.lexema = lexema;
        this.fila = fila;
        this.columna = columna;
    }
    
    public TipoToken getTipo() { return tipo; }
    public String getLexema() { return lexema; }
    public int getFila() { return fila; }
    public int getColumna() { return columna; }
    
    @Override public String toString() 
    {
        return tipo + "('" + lexema + "')@" + fila + ":" + columna;
    }
}
