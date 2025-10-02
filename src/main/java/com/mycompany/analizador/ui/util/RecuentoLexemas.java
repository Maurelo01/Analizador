package com.mycompany.analizador.ui.util;

import com.mycompany.analizador.lexer.Token;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RecuentoLexemas 
{
    private RecuentoLexemas() {} // clase utilitaria

    public static LinkedHashMap<String, Integer> contar(List<Token> tokens, boolean normalizarMinusculas) 
    {
        LinkedHashMap<String, Integer> resultadoOrdenado = new LinkedHashMap<>();

        // 1) Contar
        Map<String, Integer> mapa = new HashMap<>();
        if (tokens != null) {
            for (Token t : tokens) 
            {
                if (t == null) continue;
                String lx = t.getLexema();
                if (lx == null) continue;
                if (normalizarMinusculas) lx = lx.toLowerCase();
                Integer actual = mapa.get(lx);
                if (actual == null) actual = 0;
                mapa.put(lx, actual + 1);
            }
        }

        // 2) Pasar a lista de entries para poder ordenar
        ArrayList<Map.Entry<String, Integer>> lista = new ArrayList<>(mapa.entrySet());

        // 3) Ordenar frecuencia desc, luego lexema asc
        Collections.sort(lista, new Comparator<Map.Entry<String, Integer>>() 
        {
            @Override
            public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) 
            {
                int cmp = e2.getValue().compareTo(e1.getValue()); // desc por frecuencia
                if (cmp != 0) return cmp;
                return e1.getKey().compareTo(e2.getKey());        // asc por lexema
            }
        });

        // 4) Construir LinkedHashMap en ese orden
        for (Map.Entry<String, Integer> e : lista) 
        {
            resultadoOrdenado.put(e.getKey(), e.getValue());
        }

        return resultadoOrdenado;
    }
}
