package com.mycompany.analizador.ui;

import com.mycompany.analizador.lexer.ErrorLexico;
import com.mycompany.analizador.lexer.EscanerLexico;
import com.mycompany.analizador.lexer.TablaRL;
import com.mycompany.analizador.lexer.Token;
import com.mycompany.analizador.util.Colores;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

public class Colorizador implements DocumentListener
{
    private final JTextComponent editor;
    private final Highlighter resaltador;
    private final Timer temporizadorDebounce;
    private static final int RETARDO_MS = 10;
    private boolean habilitado = true;
    
    private final Highlighter.HighlightPainter pReservada = new DefaultHighlighter.DefaultHighlightPainter(Colores.RESERVADA);
    private final Highlighter.HighlightPainter pIdentificador = new DefaultHighlighter.DefaultHighlightPainter(Colores.IDENTIFICADOR);
    private final Highlighter.HighlightPainter pNumero = new DefaultHighlighter.DefaultHighlightPainter(Colores.NUMERO);
    private final Highlighter.HighlightPainter pDecimal = new DefaultHighlighter.DefaultHighlightPainter(Colores.DECIMAL);
    private final Highlighter.HighlightPainter pCadena = new DefaultHighlighter.DefaultHighlightPainter(Colores.RESALTADOR);
    private final Highlighter.HighlightPainter pOperador = new DefaultHighlighter.DefaultHighlightPainter(Colores.OPERADOR);
    private final Highlighter.HighlightPainter pAgrupacion = new DefaultHighlighter.DefaultHighlightPainter(Colores.AGRUPACION);
    private final Highlighter.HighlightPainter pError = new DefaultHighlighter.DefaultHighlightPainter(Colores.ERROR);
    private final Highlighter.HighlightPainter pComentario = new DefaultHighlighter.DefaultHighlightPainter(Colores.COMENTARIO);
    
    
    public Colorizador(JTextComponent editor) 
    {
        this.editor = editor;
        this.resaltador = editor.getHighlighter();
        // Debounce de 150 ms desde el ultimo cambio
        temporizadorDebounce = new Timer(RETARDO_MS, e -> aplicarColores());
        temporizadorDebounce.setRepeats(false);
        editor.getDocument().addDocumentListener(this);
    }
    
    public void setHabilitado(boolean on) 
    {
        this.habilitado = on;
        if (!on) 
        {
            limpiar();
        } 
        else 
        {
            aplicarColores();
        }
    }
    
    public void limpiar() 
    {
        resaltador.removeAllHighlights();
    }
    
    private void aplicarColores() 
    {
        if (!habilitado) return;
        SwingUtilities.invokeLater(() -> {
            try {
                String texto = editor.getDocument().getText(0, editor.getDocument().getLength());
                resaltador.removeAllHighlights();
                // 1) Comentarios
                colorearComentarios(texto);

                // 2) Tokens del analizador lexico:
                EscanerLexico sc = new EscanerLexico();
                sc.setAutomata(TablaRL.crear());
                EscanerLexico.Resultado r = sc.analizar(texto);

                for (Token t : r.tokens) 
                {
                    Highlighter.HighlightPainter p = switch (t.getTipo()) 
                    {
                        case RESERVADA -> pReservada;
                        case IDENTIFICADOR -> pIdentificador;
                        case NUMERO -> pNumero;
                        case DECIMAL -> pDecimal;
                        case CADENA -> pCadena;
                        case OPERADOR -> pOperador;
                        case AGRUPACION -> pAgrupacion;
                        case PUNTUACION -> pAgrupacion;
                        default -> null;
                    };
                    if (p != null) 
                    {
                        int ini = offsetDesdeFilaCol(texto, t.getFila(), t.getColumna());
                        if (ini >= 0) 
                        {
                            int fin = Math.min(texto.length(), ini + t.getLexema().length());
                            agregar(ini, fin, p);
                        }
                    }
                }

                // 3) Errores léxicos
                for (ErrorLexico e : r.errores) 
                {
                    int ini = offsetDesdeFilaCol(texto, e.getFila(), e.getColumna());
                    if (ini >= 0) {
                        int fin = Math.min(texto.length(), ini + Math.max(1, e.getLexema().length()));
                        agregar(ini, fin, pError);
                    }
                }
            } 
            catch (Exception ex) 
            {
                // no interrumpir la edicion por un fallo de coloreado
            }
        });
    }
    
    private void agregar(int ini, int fin, Highlighter.HighlightPainter p) {
        try {
            resaltador.addHighlight(ini, fin, p);
        } catch (BadLocationException ignored) {}
    }
    
    private void colorearComentarios(String s) 
    {
        // // comentario hasta fin de linea
        int i = 0;
        while (i < s.length()) 
        {
            int pos = s.indexOf("//", i);
            if (pos < 0) break;
            int finLinea = s.indexOf('\n', pos);
            if (finLinea < 0) finLinea = s.length();
            agregar(pos, finLinea, pComentario);
            i = finLinea + 1;
        }
        // /* comentario de bloque */
        i = 0;
        while (i < s.length()) 
        {
            int abre = s.indexOf("/*", i);
            if (abre < 0) break;
            int cierra = s.indexOf("*/", abre + 2);
            if (cierra < 0) 
            {
                // colorear hasta el final
                agregar(abre, s.length(), pComentario);
                break;
            } 
            else 
            {
                agregar(abre, cierra + 2, pComentario);
                i = cierra + 2;
            }
        }
    }
       
    private static int offsetDesdeFilaCol(String s, int fila1, int col1) 
    {
        int fila = 1, col = 1;
        for (int i = 0; i < s.length(); i++) 
        {
            if (fila == fila1 && col == col1) return i;
            char c = s.charAt(i);
            if (c == '\n') 
            { 
                fila++; col = 1; 
            }
            else if (c == '\t') 
            { 
                col += 4; 
            }
            else 
            { 
                col++; 
            }
        }
        if (fila == fila1 && col == col1) return s.length();
        return -1;
    }
    
    @Override
    public void insertUpdate(DocumentEvent de) 
    {
        if (habilitado) temporizadorDebounce.restart();
    }

    @Override
    public void removeUpdate(DocumentEvent de) 
    {
        if (habilitado) temporizadorDebounce.restart();
    }

    @Override
    public void changedUpdate(DocumentEvent de) {}
    
    
}
