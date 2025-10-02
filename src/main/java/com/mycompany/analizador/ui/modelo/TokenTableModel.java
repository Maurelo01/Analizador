package com.mycompany.analizador.ui.modelo;

import com.mycompany.analizador.lexer.Token;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class TokenTableModel extends AbstractTableModel
{
    private final String[] cols = {"#", "Tipo", "Lexema", "Fila", "Col"};
    private final List<Token> datos;

    public TokenTableModel(List<Token> datos) { this.datos = datos; }

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
        Token t = datos.get(r);
        switch (c) 
        {
            case 0: return r + 1;
            case 1: return t.getTipo();
            case 2: return t.getLexema();
            case 3: return t.getFila();
            case 4: return t.getColumna();
        }
        return null;
    }
}
