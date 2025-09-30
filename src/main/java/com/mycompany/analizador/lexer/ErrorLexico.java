package com.mycompany.analizador.lexer;
public class ErrorLexico  // Representa un error lexico detectado durante el escaneo
{
    private final String lexema;
    private final int fila;
    private final int columna;
    private final String descripcion;
    
    public ErrorLexico(String lexema, int fila, int columna, String descripcion) 
    {
        this.lexema = lexema;
        this.fila = fila;
        this.columna = columna;
        this.descripcion = descripcion;
    }
    
    public String getLexema() { return lexema; }
    public int getFila() { return fila; }
    public int getColumna() { return columna; }
    public String getDescripcion() { return descripcion; }
    
    @Override public String toString() 
    {
        return "ERROR('" + lexema + "')@" + fila + ":" + columna + " -> " + descripcion;
    }
}
