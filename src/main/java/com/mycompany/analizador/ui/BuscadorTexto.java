package com.mycompany.analizador.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.*;

public class BuscadorTexto 
{
    private final JTextComponent editor;
    private final Highlighter.HighlightPainter painterCoincidencia = new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 153, 0));
    private final List<Object> marcas = new ArrayList<>();
    private final List<int[]> rangos = new ArrayList<>(); // [inicio, fin)
    private int indiceActual = -1;

    public BuscadorTexto(JTextComponent editor) 
    {
        this.editor = editor;
    }

    public void limpiar() 
    {
        Highlighter h = editor.getHighlighter();
        for (Object o : marcas) 
        {
            h.removeHighlight(o);
        }
        marcas.clear();
        rangos.clear();
        indiceActual = -1;
    }

    public int resaltarTodo(String patron, boolean sensibleMayus) 
    {
        limpiar();
        if (patron == null || patron.isEmpty()) return 0;
        try 
        {
            Document doc = editor.getDocument();
            String texto = doc.getText(0, doc.getLength());

            String fuente = sensibleMayus ? texto : texto.toLowerCase();
            String needle = sensibleMayus ? patron : patron.toLowerCase();

            int idx = 0;
            while ((idx = fuente.indexOf(needle, idx)) >= 0) 
            {
                int fin = idx + needle.length();
                Object o = editor.getHighlighter().addHighlight(idx, fin, painterCoincidencia);
                marcas.add(o);
                rangos.add(new int[]{idx, fin});
                idx = fin; // avanzar
            }
        } 
        catch (BadLocationException ignored) {}
        if (!rangos.isEmpty()) 
        {
            indiceActual = 0; // colocar en la primera
            enfocarIndice(indiceActual);
        }
        return rangos.size();
    }

    public void siguiente() // salta a la siguiente coincidencia
    {
        if (rangos.isEmpty()) return;
        indiceActual = (indiceActual + 1) % rangos.size();
        enfocarIndice(indiceActual);
    }

    public void anterior()  // salta a la anterior coincidencia
    {
        if (rangos.isEmpty()) return;
        indiceActual = (indiceActual - 1 + rangos.size()) % rangos.size();
        enfocarIndice(indiceActual);
    }

    public String posicionHumanReadable() // devuelve el indice
    {
        if (rangos.isEmpty()) return "0/0";
        return (indiceActual + 1) + "/" + rangos.size();
    }

    private void enfocarIndice(int i)
    {
        if (i < 0 || i >= rangos.size()) return;
        int[] r = rangos.get(i);
        editor.setCaretPosition(r[0]);
        editor.moveCaretPosition(r[1]);
        editor.requestFocusInWindow();
    }
}
