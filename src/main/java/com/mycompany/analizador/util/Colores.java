package com.mycompany.analizador.util;

import java.awt.Color;

public final class Colores 
{
    private Colores() {}
    public static final Color RESERVADA = new Color(51, 153, 255); // azul
    public static final Color IDENTIFICADOR = new Color(153, 102, 0); // café
    public static final Color NUMERO = new Color(0, 204, 0); // verde
    public static final Color DECIMAL = Color.BLACK; // negro
    public static final Color COMENTARIO = new Color(0, 102, 0); // verde oscuro
    public static final Color OPERADOR = new Color(255, 255, 153); // amarillo
    public static final Color AGRUPACION = new Color(102, 0, 153); // morado
    public static final Color ERROR = Color.RED; // rojo
}
