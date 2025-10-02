package com.mycompany.analizador.ui.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class ExportadorTabla 
{
    private ExportadorTabla(){}
    
    public static void exportarCSV(JTable tabla, File destino) throws IOException 
    {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destino), StandardCharsets.UTF_8))) 
        {

            TableModel m = tabla.getModel();
            // Encabezados
            for (int c = 0; c < m.getColumnCount(); c++)
            {
                if (c > 0) bw.write(';');
                bw.write(esc(m.getColumnName(c)));
            }
            bw.write("\n");
            // Filas
            for (int r = 0; r < m.getRowCount(); r++) 
            {
                for (int c = 0; c < m.getColumnCount(); c++) 
                {
                    if (c > 0) bw.write(';');
                    Object v = m.getValueAt(r, c);
                    bw.write(esc(v));
                }
                bw.write("\n");
            }
        }
    }

    private static String esc(Object v) 
    {
        String s = (v == null) ? "" : String.valueOf(v);
        // Escapar ; y "
        boolean necesitaCitas = s.contains(";") || s.contains("\"") || s.contains("\n");
        s = s.replace("\"", "\"\"");
        return necesitaCitas ? ("\"" + s + "\"") : s;
    }

    public static void exportarHTML(JTable tabla, File destino, String titulo) throws IOException 
    {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destino), StandardCharsets.UTF_8))) 
        {

            var m = tabla.getModel();
            bw.write("<!DOCTYPE html><html><head><meta charset=\"UTF-8\">");
            bw.write("<title>" + (titulo==null?"Reporte":titulo) + "</title>");
            bw.write("<style>table{border-collapse:collapse;font-family:sans-serif} th,td{border:1px solid #888;padding:4px 8px} th{background:#eee}</style>");
            bw.write("</head><body>");
            bw.write("<h2>" + (titulo==null?"Reporte":titulo) + "</h2>");
            bw.write("<table>");
            // Encabezados
            bw.write("<tr>");
            for (int c = 0; c < m.getColumnCount(); c++) 
            {
                bw.write("<th>");
                bw.write(escapeHtml(m.getColumnName(c)));
                bw.write("</th>");
            }
            bw.write("</tr>");
            // Filas
            for (int r = 0; r < m.getRowCount(); r++) 
            {
                bw.write("<tr>");
                for (int c = 0; c < m.getColumnCount(); c++) 
                {
                    bw.write("<td>");
                    Object v = m.getValueAt(r, c);
                    bw.write(escapeHtml(v));
                    bw.write("</td>");
                }
                bw.write("</tr>");
            }
            bw.write("</table></body></html>");
        }
    }

    private static String escapeHtml(Object v) 
    {
        String s = (v == null) ? "" : String.valueOf(v);
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}
