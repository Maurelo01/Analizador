package com.mycompany.analizador.ui.modelo;

import com.mycompany.analizador.lexer.ErrorLexico;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class ErrorTableModel extends AbstractTableModel
{
    private final String[] cols = {"#", "Descripción", "Lexema", "Fila", "Col"};
    private final List<ErrorLexico> datos;

    public ErrorTableModel(List<ErrorLexico> datos) { this.datos = datos; }

    @Override public int getRowCount() 
    { 
        return datos == null ? 0 : datos.size(); 
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
        ErrorLexico e = datos.get(r);
        switch (c) 
        {
            case 0: return r + 1;
            case 1: return e.getDescripcion();
            case 2: return e.getLexema();
            case 3: return e.getFila();
            case 4: return e.getColumna();
        }
        return null;
    }
}
