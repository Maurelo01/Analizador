package com.mycompany.analizador.lexer;

import java.util.ArrayList;
import java.util.List;

public class Traza implements EscanerLexico.ListenerTraza
{
    private final List<String> pasos = new ArrayList<>();
    private final List<String> eventos = new ArrayList<>();
    @Override
    public void onPaso(int estadoAnterior, char c, int estadoSiguiente) 
    {
        pasos.add("δ(" + estadoAnterior + ", '" + mostrar(c) + "') -> " + estadoSiguiente);
    }

    @Override
    public void onToken(Token t) 
    {
        eventos.add("TOKEN " + t.toString());
    }

    @Override
    public void onError(ErrorLexico e) 
    {
        eventos.add("ERROR [" + e.getFila() + ":" + e.getColumna() + "] " + e.getDescripcion() + " ('" + e.getLexema() + "')");
    }
    
    public String basura() 
    {
        StringBuilder sb = new StringBuilder();
        sb.append("--- PASOS ---\n");
        for (String p : pasos) sb.append(p).append('\n');
        sb.append("--- EVENTOS ---\n");
        for (String ev : eventos) sb.append(ev).append('\n');
        return sb.toString();
    }

    private String mostrar(char c) 
    {
        if (c == '\n') return "\\n";
        if (c == '\t') return "\\t";
        if (c == '\r') return "\\r";
        return String.valueOf(c);
    }
}
