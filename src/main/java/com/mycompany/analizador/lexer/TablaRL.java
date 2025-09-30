package com.mycompany.analizador.lexer;
public final class TablaRL 
{
    private TablaRL() {}
    public static ReconocedorLexico crear()
    {
        //cantidad estados y clases
        final int ESTADOS = 7;
        final int CLASES = 8;
        // transicion inicializada a -1
        int[][] transicion = new int[ESTADOS][CLASES];
        for (int i = 0; i < ESTADOS; i++) 
        {
            for (int j = 0; j < CLASES; j++) 
            {
                transicion[i][j] = -1;
            }
        }
        
        // Transiciones desde 0
        transicion[0][ReconocedorLexico.C_LETRA]   = 1; // empieza identificador
        transicion[0][ReconocedorLexico.C_DIGITO]  = 2; // empieza número
        transicion[0][ReconocedorLexico.C_OPERADOR]= 3; // operador de 1 char
        transicion[0][ReconocedorLexico.C_AGRUP]   = 4; // agrupación de 1 char
        transicion[0][ReconocedorLexico.C_PUNT]    = 5; // puntuación de 1 char
        transicion[0][ReconocedorLexico.C_ESPACIO] = 6; // espacios (uno o más)
        // C_COMILLA y C_OTRO quedan en -1 ya que no hay transición
        // Estado 1: Identificador
        transicion[1][ReconocedorLexico.C_LETRA]  = 1;
        transicion[1][ReconocedorLexico.C_DIGITO] = 1;
        
        // Estado 2: Número
        transicion[2][ReconocedorLexico.C_DIGITO] = 2;
        
        // Estado 3 (operador), 4(Agrupacion), 5 (Puntuacion)
        // Son de 1 caracter y no tienen transiciones
        
        // Estado 6: Espacios
        transicion[6][ReconocedorLexico.C_ESPACIO] = 6;
        // Aceptación por estado
        boolean[] acept = new boolean[ESTADOS];
        // 1: identificador  | 2: numero | 3: operador | 4: agrup | 5: puntuación | 6: espacios
        acept[1] = true;
        acept[2] = true;
        acept[3] = true;
        acept[4] = true;
        acept[5] = true;
        acept[6] = true;
        
        // Tipo por estado
        TipoToken[] tipos = new TipoToken[ESTADOS];
        tipos[1] = TipoToken.IDENTIFICADOR;
        tipos[2] = TipoToken.NUMERO;
        tipos[3] = TipoToken.OPERADOR;
        tipos[4] = TipoToken.AGRUPACION;
        tipos[5] = TipoToken.PUNTUACION;
        tipos[6] = null;
        
        int estadoInicial = 0;
        return new ReconocedorLexico(transicion, estadoInicial, acept, tipos);
    }
}
