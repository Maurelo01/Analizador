package com.mycompany.analizador.util;

import com.mycompany.analizador.lexer.ErrorLexico;
import com.mycompany.analizador.lexer.Token;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ToolTipManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

public class GestorSugerencias 
{
    public interface Sugeridor 
    {
        String sugerirCercana(String palabra); // Devuelve una sugerencia o null si no hay
    }

    private final JTextComponent editor;
    private final Highlighter resaltador;
    private final List<RangoError> rangos = new ArrayList<>();
    private final Sugeridor sugeridor;

    // Pintor subrayado ondulado rojo
    private final Highlighter.HighlightPainter pintorError = new PintorSubrayadoOndulado(new Color(200, 0, 0));
    
    public GestorSugerencias(JTextComponent editor, Sugeridor sugeridor) 
    {
        this.editor = editor;
        this.resaltador = editor.getHighlighter();
        this.sugeridor = sugeridor;

        // Habilitar tooltips en el editor
        ToolTipManager.sharedInstance().registerComponent(editor);

        // Necesario para que Swing consulte el tooltip del componente
        editor.setToolTipText("");

        // Decidir el tooltip en función de la posición del mouse
        editor.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() 
        {
            @Override public void mouseMoved(MouseEvent e)
            {
                actualizarToolTip(e);
            }
        });
    }

    public void limpiar()  // Limpiar todos los subrayados y tooltips previos.
    {
        for (RangoError r : rangos) 
        {
            try 
            {
                resaltador.removeHighlight(r.marca);
            } 
            catch (Exception ignore) {}
        }
        rangos.clear();
        editor.setToolTipText("");
    }

    public void aplicarErrores(List<ErrorLexico> errores, List<Token> tokens, String fuente) 
    {
        limpiar();
        if (errores == null || errores.isEmpty() || fuente == null) return;

        for (ErrorLexico err : errores) 
        {
            int inicio = filaColumnaAOffset(fuente, err.getFila(), err.getColumna());
            // Si el lexema es null o vacio, al menos subraya un caracter
            int longitud = Math.max(1, (err.getLexema() != null ? err.getLexema().length() : 1));
            int fin = Math.min(inicio + longitud, fuente.length());

            try 
            {
                Object marca = resaltador.addHighlight(inicio, fin, pintorError);
                String tooltip = construirTooltip(err);
                rangos.add(new RangoError(inicio, fin, tooltip, marca));
            } 
            catch (BadLocationException ignore) {}
        }
    }

    private void actualizarToolTip(MouseEvent e) 
    {
        try 
        {
            int pos = editor.viewToModel2D(e.getPoint());
            String tip = null;
            for (RangoError r : rangos) 
            {
                if (pos >= r.inicio && pos < r.fin) 
                { 
                    tip = r.tooltip; break; 
                }
            }
            editor.setToolTipText(tip); // null oculta el tooltip
        } 
        catch (Exception ex) 
        {
            editor.setToolTipText(null);
        }
    }

    private int filaColumnaAOffset(String texto, int fila1, int col1) 
    {
        int fila = Math.max(1, fila1);
        int col  = Math.max(1, col1);
        int f = 1;
        int off = 0;
        while (off < texto.length() && f < fila) 
        {
            char c = texto.charAt(off++);
            if (c == '\n') f++;
        }
        return Math.min(off + (col - 1), texto.length());
    }

    private String construirTooltip(ErrorLexico err) 
    {
        String base = err.getDescripcion();
        String lx = (err.getLexema() != null) ? err.getLexema() : "";

        // Si hay sugeridor y el lexema parece un identificador  propone una sugerencia
        String sug = null;
        if (sugeridor != null && pareceIdentificador(lx)) 
        {
            sug = sugeridor.sugerirCercana(lx);
        }
        if (sug != null) 
        {
            return base + "  •  ¿Quisiste decir: " + sug + " ?";
        }
        return base;
    }

    private boolean pareceIdentificador(String s) 
    {
        if (s == null || s.isEmpty()) return false;
        if (!Character.isLetter(s.charAt(0))) return false;
        for (int i = 1; i < s.length(); i++) 
        {
            char c = s.charAt(i);
            if (!Character.isLetterOrDigit(c)) return false;
        }
        return true;
    }

    private static class RangoError 
    {
        final int inicio, fin;
        final String tooltip;
        final Object marca;
        RangoError(int inicio, int fin, String tooltip, Object marca) 
        {
            this.inicio = inicio;
            this.fin = fin;
            this.tooltip = tooltip;
            this.marca   = marca;
        }
    }

    public static class PintorSubrayadoOndulado extends DefaultHighlighter.DefaultHighlightPainter 
    {
        public PintorSubrayadoOndulado(Color color) 
        {
            super(color);
        }

        @Override
        public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) 
        {
            try 
            {
                Rectangle r0 = c.modelToView(p0);
                Rectangle r1 = c.modelToView(p1);
                if (r0 == null || r1 == null) return;
                g.setColor(getColor());
                // Soporte multilinea
                int start = p0;
                while (start < p1) 
                {
                    Rectangle rA = c.modelToView(start);
                    int endLineOffset = getFinDeLinea(c, start, p1);
                    Rectangle rB = c.modelToView(endLineOffset);
                    if (rA != null && rB != null)
                    {
                        dibujarOndulado(g, rA.x, rB.x, rA.y + rA.height - 2);
                    }
                    start = endLineOffset;
                }
            } 
            catch (BadLocationException ignore) {}
        }

        private int getFinDeLinea(JTextComponent c, int desde, int hastaMax) throws BadLocationException 
        {
            Document doc = c.getDocument();
            Element root = doc.getDefaultRootElement();
            int linea = root.getElementIndex(desde);
            Element elem = root.getElement(linea);
            int fin = Math.min(elem.getEndOffset(), hastaMax);
            return Math.max(desde + 1, fin); // asegurar progreso
        }

        private void dibujarOndulado(Graphics g, int xIni, int xFin, int yBase) 
        {
            int x = xIni;
            int amplitud = 2;
            boolean arriba = true;
            while (x < xFin)
            {
                int y2 = yBase + (arriba ? -amplitud : amplitud);
                g.drawLine(x, yBase, x + 2, y2);
                arriba = !arriba;
                x += 2;
            }
        }
    }
}
