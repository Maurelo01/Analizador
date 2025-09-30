package com.mycompany.analizador.lexer;
public interface Automata // AFD trabajando a nivel char
{
    void reiniciar(); // Reinica al estado inicial
    /*
        Intenta transitar con un caracter dado c
        si existe transicion y el automata avanza es True
        sino false
    */
    boolean transitar(char c);
    boolean estaEnAceptacion(); // si el estado actual es de aceptacio es True
    int estadoActual(); // Identificado del estado actual
    TipoToken tipoDeEstadoActual();
}
