package com.mycompany.analizador.lexer;
public enum TipoToken // Tipos de token manejados por el analizador
{
    IDENTIFICADOR,
    NUMERO,
    DECIMAL,
    CADENA,
    RESERVADA,
    PUNTUACION,
    OPERADOR,
    AGRUPACION,
    COMENTARIO,
    DESCONOCIDO
}
