package com.mycompany.analizador.ui.modelo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class RecuentoTableModel extends AbstractTableModel
{
    private final String[] cols = {"#", "Lexema", "Frecuencia"};
    private final List<String> claves = new ArrayList<>();
    private final List<Integer> valores = new ArrayList<>();

    public RecuentoTableModel(LinkedHashMap<String, Integer> ordenado) 
    {
        if (ordenado != null) 
        {
            for (var e: ordenado.entrySet()) 
            {
                claves.add(e.getKey());
                valores.add(e.getValue());
            }
        }
    }

    @Override public int getRowCount() 
    { 
        return claves.size(); 
    }
    @Override public int getColumnCount() 
    { 
        return cols.length; 
    }
    @Override public String getColumnName(int c) 
    { 
        return cols[c]; 
    }

    @Override public Object getValueAt(int r, int c) 
    {
        switch (c) 
        {
            case 0: return r + 1;
            case 1: return claves.get(r);
            case 2: return valores.get(r);
        }
        return null;
    }
}
