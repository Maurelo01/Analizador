package com.mycompany.analizador.debug;

import com.mycompany.analizador.lexer.ReconocedorLexico;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.IntPredicate;
import javax.imageio.ImageIO;

public class VisualizadorAutomata 
{
    private VisualizadorAutomata(){}
    
    public static File dibujarCamino(java.util.List<DepuradorModelo.Paso> pasos, String nombreBase, File archivoDestino, IntPredicate esAcept) throws Exception 
    {
        if (pasos == null || pasos.isEmpty()) return null;
        int margen = 20, sepX = 80, radio = 18, alto = 160;
        int ancho = margen*2 + (pasos.size()+1)*sepX;
        BufferedImage img = new BufferedImage(Math.max(ancho, 300), alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0,0,img.getWidth(),img.getHeight());
        g.setColor(Color.BLACK);
        g.drawString("Camino de estados (δ)", 10, 15);
        int y = alto/2;
        int x = margen;
        int estado = pasos.get(0).estadoAntes;
        dibujarNodo(g, x, y, radio, estado, esAcept != null && esAcept.test(estado));

        for (int i=0;i<pasos.size();i++) 
        {
            DepuradorModelo.Paso p = pasos.get(i);
            int x2 = margen + (i+1)*sepX;
            dibujarFlecha(g, x+radio, y, x2-radio, y);
            g.drawString("'" + p.c + "'", (x+x2)/2 - 6, y-8);
            boolean fin = (esAcept != null && esAcept.test(p.estadoDespues));
            dibujarNodo(g, x2, y, radio, p.estadoDespues, fin);
            x = x2;
        }
        g.dispose();
        File out = archivoDestino;
        if (out == null)
        {
            out = File.createTempFile(nombreBase == null ? "camino_" : nombreBase, ".png");
        }
        else 
        {
            if (!out.getName().toLowerCase().endsWith(".png")) 
            {
                out = new File(out.getParentFile(), out.getName() + ".png");
            }
        }
        javax.imageio.ImageIO.write(img, "PNG", out);
        return out;
    }

    public static File dibujarCamino(ReconocedorLexico dfa, java.util.List<DepuradorModelo.Paso> pasos, File archivoDestino) throws java.io.IOException 
    {
        BufferedImage img = dibujarCaminoImagen(dfa, pasos);
        return guardarPNG(img, null, archivoDestino);
    }

    private static void dibujarNodo(Graphics2D g, int cx, int cy, int r, int estado, boolean esAcept) 
    {
        if (estado >= 0) 
        {
            g.setColor(new Color(230,230,255));
            g.fillOval(cx-r, cy-r, r*2, r*2);
            g.setColor(Color.BLUE);
            g.drawOval(cx-r, cy-r, r*2, r*2);
            if (esAcept) 
            {
                int r2 = r - 4;
                g.drawOval(cx-r2, cy-r2, r2*2, r2*2); // anillo interior
            }
            g.setColor(Color.BLACK);
            String t = String.valueOf(estado);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(t, cx - fm.stringWidth(t)/2, cy + fm.getAscent()/2 - 2);
        } 
        else 
        {
            // nodo de error
            g.setColor(new Color(255,220,220));
            g.fillOval(cx-r, cy-r, r*2, r*2);
            g.setColor(Color.RED);
            g.drawOval(cx-r, cy-r, r*2, r*2);
            g.drawLine(cx-r, cy-r, cx+r, cy+r);
            g.drawLine(cx+r, cy-r, cx-r, cy+r);
        }
    }
    
    private static void dibujarFlecha(Graphics2D g, int x1, int y1, int x2, int y2) 
    {
        g.setColor(Color.DARK_GRAY);
        g.drawLine(x1,y1,x2,y2);
        // punta simple
        int tam = 6;
        g.drawLine(x2, y2, x2-tam, y2-tam);
        g.drawLine(x2, y2, x2-tam, y2+tam);
    }
        
    public static BufferedImage dibujarCaminoImagen(ReconocedorLexico dfa, java.util.List<DepuradorModelo.Paso> pasos)
    {
        if (pasos == null || pasos.isEmpty()) return null;
        int margen = 20, sepX = 80, radio = 18, alto = 160;
        int ancho = margen*2 + (pasos.size()+1)*sepX;
        BufferedImage img = new BufferedImage(Math.max(ancho, 300), alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0,0,img.getWidth(),img.getHeight());
        g.setColor(Color.BLACK);
        g.drawString("Camino de estados (δ)", 10, 15);
        int y = alto/2;
        int x = margen;
        int estado = pasos.get(0).estadoAntes;
        boolean acept = (estado >= 0 && dfa != null && dfa.tipoDeAceptacion(estado) != null);
        dibujarNodo(g, x, y, radio, estado, acept);
        for (int i=0; i<pasos.size(); i++) 
        {
            DepuradorModelo.Paso p = pasos.get(i);
            int x2 = margen + (i+1)*sepX;
            dibujarFlecha(g, x+radio, y, x2-radio, y);
            g.drawString("'" + DepuradorModelo.etiquetaChar(p.c) + "'", (x+x2)/2 - 6, y-8);
            boolean acept2 = (p.estadoDespues >= 0 && dfa != null && dfa.tipoDeAceptacion(p.estadoDespues) != null);
            dibujarNodo(g, x2, y, radio, p.estadoDespues, acept2);
            x = x2;
        }
        g.dispose();
        return img;
    }
    
    public static File guardarPNG(BufferedImage img, String nombreBase, File destino) throws java.io.IOException 
    {
        if (img == null) return null;
        File out;
        if (destino != null) 
        {
            out = destino;
            if (!out.getName().toLowerCase().endsWith(".png")) 
            {
                out = new File(out.getParentFile(), out.getName() + ".png");
            }
        } 
        else 
        {
            out = File.createTempFile(nombreBase == null ? "camino_" : nombreBase, ".png");
        }
        javax.imageio.ImageIO.write(img, "PNG", out);
        return out;
    }
}
