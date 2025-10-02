package com.mycompany.analizador.util;
public class DistanciaEdicion 
{
    private DistanciaEdicion() {}
    public static int distancia(String a, String b) 
    {
        if (a == null) a = "";
        if (b == null) b = "";
        int n = a.length(), m = b.length();
        int[][] dp = new int[n+1][m+1];
        for (int i=0;i<=n;i++) dp[i][0] = i;
        for (int j=0;j<=m;j++) dp[0][j] = j;
        for (int i=1;i<=n;i++) 
        {
            char ca = a.charAt(i-1);
            for (int j=1;j<=m;j++) 
            {
                char cb = b.charAt(j-1);
                int costo = (ca == cb) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i-1][j] + 1, // borrar
                    dp[i][j-1] + 1), // insertar
                    dp[i-1][j-1] + costo); // sustituir
            }
        }
        return dp[n][m];
    }
}
