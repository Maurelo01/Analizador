package com.mycompany.analizador.lexer;
public final class TablaRL 
{
    private TablaRL() {}
    public static ReconocedorLexico crear()
    {
        //cantidad estados y clases
        final int ESTADOS = 22;
        final int CLASES = 13;
        
        // transicion inicializada a -1
        int[][] transicion = new int[ESTADOS][CLASES];
        for (int i = 0; i < ESTADOS; i++) 
        {
            for (int j = 0; j < CLASES; j++) 
            {
                transicion[i][j] = -1;
            }
        }
        
        // Estados: 
        // 0 inicio
        // 1 identificador
        // 2 entero
        // 7 punto después de entero
        // 8 decimal
        // 3 operador
        // 4 agrupacion
        // 5 puntuacion
        // 6 espacios
        // 9 slash visto / o inicio de comentario
        // 10 comentario de línea //
        // 11 comentario de bloque /*
        // 12 posible cierre de bloque *
        // 13 fin de bloque */ 
        // 20 cadena abierta " 
        // 21 cadena fin "
        // 22 escape en cadena \
        
        // Transiciones desde 0
        transicion[0][ReconocedorLexico.C_LETRA] = 1; // empieza identificador
        transicion[0][ReconocedorLexico.C_DIGITO] = 2; // empieza número
        transicion[0][ReconocedorLexico.C_OPERADOR]= 3; // operador de 1 char
        transicion[0][ReconocedorLexico.C_ASTERISCO] = 3;// operador de 1 char
        transicion[0][ReconocedorLexico.C_AGRUP] = 4; // agrupación de 1 char
        transicion[0][ReconocedorLexico.C_PUNT] = 5; // puntuación de 1 char
        transicion[0][ReconocedorLexico.C_PUNTO] = 5; // .
        transicion[0][ReconocedorLexico.C_ESPACIO] = 6; // espacios
        transicion[0][ReconocedorLexico.C_SALTO] = 6; // Saltos de linea
        transicion[0][ReconocedorLexico.C_SLASH] = 9; // slash /
        transicion[0][ReconocedorLexico.C_COMILLA] = 20; // comillas
        // C_COMILLA y C_OTRO quedan en -1 ya que no hay transición
        
        // Estado 1: Identificador
        transicion[1][ReconocedorLexico.C_LETRA] = 1;
        transicion[1][ReconocedorLexico.C_DIGITO] = 1;
        
        // Estado 2: Entero
            // Entero
        transicion[2][ReconocedorLexico.C_DIGITO] = 2;
        transicion[2][ReconocedorLexico.C_PUNTO] = 7; // Posible decimal
        
        // Estado 8: Decimal
            // Decimal
        transicion[7][ReconocedorLexico.C_DIGITO] = 8; // debe haber un digito tras el punto
        transicion[8][ReconocedorLexico.C_DIGITO] = 8; // Digitos despues del decimal

        // Estado 3 (operador), 4(Agrupacion), 5 (Puntuacion)
        // Son de 1 caracter y no tienen transiciones
        
        // Estado 6: Espacios
        transicion[6][ReconocedorLexico.C_ESPACIO] = 6;
        transicion[6][ReconocedorLexico.C_SALTO] = 6;
        
        // Estado 10 y 11: Comentarios
        transicion[9][ReconocedorLexico.C_SLASH] = 10; //  //
        transicion[9][ReconocedorLexico.C_ASTERISCO] = 11; // /*
        
        // Si es un comentario de linea se consume todo menos el salto de linea
        transicion[10][ReconocedorLexico.C_ESPACIO] = 10;
        transicion[10][ReconocedorLexico.C_LETRA] = 10;
        transicion[10][ReconocedorLexico.C_DIGITO] = 10;
        transicion[10][ReconocedorLexico.C_OPERADOR] = 10;
        transicion[10][ReconocedorLexico.C_AGRUP] = 10;
        transicion[10][ReconocedorLexico.C_PUNT] = 10;
        transicion[10][ReconocedorLexico.C_SLASH] = 10;
        transicion[10][ReconocedorLexico.C_PUNTO]= 10;
        transicion[10][ReconocedorLexico.C_COMILLA] = 10;
        transicion[10][ReconocedorLexico.C_ASTERISCO] = 10;
        transicion[10][ReconocedorLexico.C_OTRO] = 10;
        
        // En 11, todo se toma como comentario y si vemos * pasamos a 12 como posible cierre
        transicion[11][ReconocedorLexico.C_SALTO] = 11;
        transicion[11][ReconocedorLexico.C_ESPACIO] = 11;
        transicion[11][ReconocedorLexico.C_LETRA] = 11;
        transicion[11][ReconocedorLexico.C_DIGITO] = 11;
        transicion[11][ReconocedorLexico.C_OPERADOR] = 11;
        transicion[11][ReconocedorLexico.C_AGRUP] = 11;
        transicion[11][ReconocedorLexico.C_PUNT] = 11;
        transicion[11][ReconocedorLexico.C_SLASH] = 11;
        transicion[11][ReconocedorLexico.C_PUNTO]= 11;
        transicion[11][ReconocedorLexico.C_COMILLA] = 11;
        transicion[11][ReconocedorLexico.C_ASTERISCO] = 12;
        transicion[11][ReconocedorLexico.C_OTRO] = 11;
        
        // En 12 se repite lo anterior pero / cierra el bloque
        transicion[12][ReconocedorLexico.C_SALTO] = 11;
        transicion[12][ReconocedorLexico.C_ESPACIO] = 11;
        transicion[12][ReconocedorLexico.C_LETRA]  = 11;
        transicion[12][ReconocedorLexico.C_DIGITO]  = 11;
        transicion[12][ReconocedorLexico.C_OPERADOR] = 11;
        transicion[12][ReconocedorLexico.C_AGRUP] = 11;
        transicion[12][ReconocedorLexico.C_PUNT] = 11;
        transicion[12][ReconocedorLexico.C_SLASH] = 13;
        transicion[12][ReconocedorLexico.C_PUNTO]= 11;
        transicion[12][ReconocedorLexico.C_COMILLA] = 11;
        transicion[12][ReconocedorLexico.C_ASTERISCO] = 12;
        transicion[12][ReconocedorLexico.C_OTRO] = 11;
        
        // Estado 20: Cadena
        // Todo se consume menos el salto de linea y cuando es otra comilla se cierra el comentario
        transicion[20][ReconocedorLexico.C_ESPACIO] = 20;
        transicion[20][ReconocedorLexico.C_LETRA]  = 20;
        transicion[20][ReconocedorLexico.C_DIGITO]  = 20;
        transicion[20][ReconocedorLexico.C_OPERADOR] = 20;
        transicion[20][ReconocedorLexico.C_AGRUP] = 20;
        transicion[20][ReconocedorLexico.C_PUNT] = 20;
        transicion[20][ReconocedorLexico.C_SLASH] = 20;
        transicion[20][ReconocedorLexico.C_PUNTO]= 20;
        transicion[20][ReconocedorLexico.C_COMILLA] = 21;
        transicion[20][ReconocedorLexico.C_ASTERISCO] = 20;
        transicion[20][ReconocedorLexico.C_BACKSLASH] = -1;
        transicion[20][ReconocedorLexico.C_OTRO] = 20;
        
        // Aceptación por estado
        boolean[] acept = new boolean[ESTADOS];
        
        acept[1]  = true;  // Identificador
        acept[2]  = true;  // Entero
        acept[8]  = true;  // Decimal
        acept[3]  = true;  // Operador
        acept[4]  = true;  // Agrupacion
        acept[5]  = true;  // Puntuacion
        acept[6]  = true;  // Espacios / salto de liea
        acept[9]  = true;  // / sino es operador // o /*
        acept[10] = true;  // comentario de linea
        acept[13] = true;  // comentario de bloque
        acept[21] = true;  // Cadena
        
        // Tipo por estado
        TipoToken[] tipos = new TipoToken[ESTADOS];
        tipos[1] = TipoToken.IDENTIFICADOR;
        tipos[2] = TipoToken.NUMERO;
        tipos[3] = TipoToken.OPERADOR;
        tipos[4] = TipoToken.AGRUPACION;
        tipos[5] = TipoToken.PUNTUACION;
        tipos[6] = null;
        tipos[8]  = TipoToken.DECIMAL;
        tipos[9]  = TipoToken.OPERADOR;
        tipos[10] = TipoToken.COMENTARIO;
        tipos[13] = TipoToken.COMENTARIO;
        tipos[21] = TipoToken.CADENA;
        
        int estadoInicial = 0;
        return new ReconocedorLexico(transicion, estadoInicial, acept, tipos);
    }
}
