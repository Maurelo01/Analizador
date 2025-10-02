package com.mycompany.analizador.util;
public class SugeridorReservadas 
{
    private static final String[] RS = {"SI","si","ENTONCES","entonces","PARA","para","ESCRIBIR","escribir","SINO","sino"};

    private SugeridorReservadas() {}

    public static String sugerirCercana(String lexema, int umbral) 
    {
        return sugerirCercana(lexema, umbral, /*normalizar minusculas*/ false);
    }

    public static String sugerirCercana(String lexema) 
    {
        return sugerirCercana(lexema, /*umbral*/ 2, /*normalizar minusculas*/ false);
    }
    
    public static String sugerirCercana(String lexema, int umbral, boolean normalizarMinusculas) 
    {
        if (lexema == null || lexema.isEmpty()) return null;
        String probe = normalizarMinusculas ? lexema.toLowerCase() : lexema;
        // Si ya coincide exactamente con una reservada no se sugiere
        for (String r : RS)
        {
            if (equalsCaseAware(probe, r, normalizarMinusculas)) 
            {
                return null;
            }
        }

        String mejor = null;
        int mejorDist = Integer.MAX_VALUE;
        for (String r : RS) 
        {
            String ref = normalizarMinusculas ? r.toLowerCase() : r;
            int d = distanciaEdicion(probe, ref);
            if (d < mejorDist) 
            {
                mejorDist = d;
                mejor = r; // devuelve la reservada original
            } 
            else if (d == mejorDist && mejor != null) 
            {
                // opcional en caso de igualdad elige la mas corta
                if (r.length() < mejor.length()) 
                {
                    mejor = r;
                }
            }
        }

        return (mejorDist <= umbral) ? mejor : null;
    }

    private static int distanciaEdicion(String a, String b) 
    {
        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;
        for (int i = 1; i <= n; i++) 
        {
            char ca = a.charAt(i - 1);
            for (int j = 1; j <= m; j++) 
            {
                char cb = b.charAt(j - 1);
                int costoSust = (ca == cb) ? 0 : 1;
                int borrar = dp[i - 1][j] + 1;
                int insertar = dp[i][j - 1] + 1; 
                int sustituir = dp[i - 1][j - 1] + costoSust;
                int mejor = borrar;
                if (insertar < mejor) mejor = insertar;
                if (sustituir < mejor) mejor = sustituir;
                dp[i][j] = mejor;
            }
        }
        return dp[n][m];
    }

    private static boolean equalsCaseAware(String a, String reservada, boolean normalizarMinusculas) 
    {
        return normalizarMinusculas ? a.equals(reservada.toLowerCase()) : a.equals(reservada);
    }
}
