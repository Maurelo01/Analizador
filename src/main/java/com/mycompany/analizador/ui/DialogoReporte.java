package com.mycompany.analizador.ui;

import com.mycompany.analizador.lexer.ErrorLexico;
import com.mycompany.analizador.lexer.Token;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class DialogoReporte extends javax.swing.JDialog 
{
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DialogoReporte.class.getName());
    
    
    private final DefaultTableModel modeloErrores = new DefaultTableModel(new Object[]{"Fila","Columna","Descripción","Lexema"}, 0) 
    {
        @Override public boolean isCellEditable(int r, int c) 
        { 
            return false; 
        }
    };
    private final DefaultTableModel modeloTokens = new DefaultTableModel( new Object[]{"Tipo","Lexema","Fila","Columna"}, 0) 
    {
        @Override public boolean isCellEditable(int r, int c) 
        { 
            return false; 
        }
    };
    private final DefaultTableModel modeloRecuento = new DefaultTableModel(new Object[]{"Lexema","Frecuencia"}, 0) 
    {
        @Override public boolean isCellEditable(int r, int c) 
        { 
            return false; 
        }
    };
    
    public DialogoReporte(java.awt.Frame parent, boolean modal) 
    {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(parent);
        tablaErrores.setModel(modeloErrores);
        tablaTokens.setModel(modeloTokens);
        tablaRecuento.setModel(modeloRecuento);

        // Ajuste: selección por filas completas
        tablaErrores.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tablaTokens.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tablaRecuento.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    public void setErrores(List<ErrorLexico> errores) 
    {
        modeloErrores.setRowCount(0);
        if (errores == null) return;
        for (ErrorLexico e : errores) 
        {
            modeloErrores.addRow(new Object[] 
            {
                    e.getFila(), e.getColumna(), e.getDescripcion(), e.getLexema()
            });
        }
    }
    
    public void setTokens(List<Token> tokens) 
    {
        modeloTokens.setRowCount(0);
        if (tokens == null) return;
        for (Token t : tokens) 
        {
            modeloTokens.addRow(new Object[] 
            {
                    t.getTipo(), t.getLexema(), t.getFila(), t.getColumna()
            });
        }
    }
    
    public void setRecuento(LinkedHashMap<String,Integer> recuento) 
    {
        modeloRecuento.setRowCount(0);
        if (recuento == null) return;
        for (Map.Entry<String,Integer> e : recuento.entrySet()) 
        {
            modeloRecuento.addRow(new Object[]{ e.getKey(), e.getValue() });
        }
    }
    
    private void exportarCSVDePestañaActual() 
    {
        int idx = tabs.getSelectedIndex();
        JTable tabla = obtenerTablaPorIndice(idx);
        if (tabla == null) 
        {
            JOptionPane.showMessageDialog(this, "No hay tabla en esta pestaña.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser ch = new JFileChooser();
        ch.setSelectedFile(new File("reporte.csv"));
        if (ch.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) 
        {
            File f = ch.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith(".csv"))
            {
                f = new File(f.getParentFile(), f.getName() + ".csv");
            }
            try 
            {
                escribirCSV(tabla, f);
                JOptionPane.showMessageDialog(this, "CSV exportado: " + f.getName(), "OK", JOptionPane.INFORMATION_MESSAGE);
            } 
            catch (IOException ex) 
            {
                JOptionPane.showMessageDialog(this, "No se pudo exportar CSV: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportarHTMLDePestañaActual() 
    {
        int idx = tabs.getSelectedIndex();
        JTable tabla = obtenerTablaPorIndice(idx);
        if (tabla == null) 
        {
            JOptionPane.showMessageDialog(this, "No hay tabla en esta pestaña.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser ch = new JFileChooser();
        ch.setSelectedFile(new File("reporte.html"));
        if (ch.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) 
        {
            File f = ch.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith(".html")) 
            {
                f = new File(f.getParentFile(), f.getName() + ".html");
            }
            try 
            {
                escribirHTML(tabla, f, "Reporte - " + tabs.getTitleAt(idx));
                JOptionPane.showMessageDialog(this, "HTML exportado: " + f.getName(), "OK", JOptionPane.INFORMATION_MESSAGE);
            } 
            catch (IOException ex) 
            {
                JOptionPane.showMessageDialog(this, "No se pudo exportar HTML: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JTable obtenerTablaPorIndice(int idx) 
    {
        if (idx == 0) return tablaErrores;
        if (idx == 1) return tablaTokens;
        if (idx == 2) return tablaRecuento;
        return null;
    }
    
    private void escribirCSV(JTable tabla, File f) throws IOException 
    {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))) 
        {
            // Cabeceras
            for (int c = 0; c < tabla.getColumnCount(); c++) 
            {
                if (c > 0) bw.write(',');
                bw.write(escaparCSV(tabla.getColumnName(c)));
            }
            bw.write('\n');

            // Filas
            for (int r = 0; r < tabla.getRowCount(); r++) 
            {
                for (int c = 0; c < tabla.getColumnCount(); c++) 
                {
                    if (c > 0) bw.write(',');
                    Object val = tabla.getValueAt(r, c);
                    bw.write(escaparCSV(val == null ? "" : val.toString()));
                }
                bw.write('\n');
            }
        }
    }
    
    private String escaparCSV(String s)
    {
        if (s == null) return "";
        boolean necesitaCitas = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        if (necesitaCitas) 
        {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }

    private void escribirHTML(JTable tabla, File f, String titulo) throws IOException 
    {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))) 
        {
            bw.write("<!DOCTYPE html><html><head><meta charset='utf-8'>");
            bw.write("<title>" + escapeHTML(titulo) + "</title>");
            bw.write("<style>table{border-collapse:collapse} th,td{border:1px solid #999;padding:4px 8px;font-family:Arial, sans-serif;font-size:13px}</style>");
            bw.write("</head><body>");
            bw.write("<h2>" + escapeHTML(titulo) + "</h2>");
            bw.write("<table>");
            // Cabeceras
            bw.write("<tr>");
            for (int c = 0; c < tabla.getColumnCount(); c++) 
            {
                bw.write("<th>" + escapeHTML(tabla.getColumnName(c)) + "</th>");
            }
            bw.write("</tr>");
            // Filas
            for (int r = 0; r < tabla.getRowCount(); r++)
            {
                bw.write("<tr>");
                for (int c = 0; c < tabla.getColumnCount(); c++)
                {
                    Object val = tabla.getValueAt(r, c);
                    bw.write("<td>" + escapeHTML(val == null ? "" : val.toString()) + "</td>");
                }
                bw.write("</tr>");
            }
            bw.write("</table></body></html>");
        }
    }
    
    private String escapeHTML(String s) 
    {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;");
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnExportarCSV = new javax.swing.JButton();
        btnExportarHTML = new javax.swing.JButton();
        btnCopiar = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();
        tabs = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaErrores = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaTokens = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaRecuento = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Reporte de análisis");
        setModal(true);
        setPreferredSize(new java.awt.Dimension(405, 505));

        jPanel1.setLayout(new java.awt.BorderLayout());

        jToolBar1.setRollover(true);

        btnExportarCSV.setText("Exportar CSV");
        btnExportarCSV.setFocusable(false);
        btnExportarCSV.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExportarCSV.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExportarCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarCSVActionPerformed(evt);
            }
        });
        jToolBar1.add(btnExportarCSV);

        btnExportarHTML.setText("Exportar HTML");
        btnExportarHTML.setFocusable(false);
        btnExportarHTML.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExportarHTML.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExportarHTML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarHTMLActionPerformed(evt);
            }
        });
        jToolBar1.add(btnExportarHTML);

        btnCopiar.setText("Copiar selección");
        btnCopiar.setFocusable(false);
        btnCopiar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCopiar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCopiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCopiarActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCopiar);

        btnCerrar.setText("Cerrar");
        btnCerrar.setFocusable(false);
        btnCerrar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCerrar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCerrar);

        jPanel1.add(jToolBar1, java.awt.BorderLayout.NORTH);

        tablaErrores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tablaErrores);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 393, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 423, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)))
        );

        tabs.addTab("Errores", jPanel2);

        tablaTokens.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tablaTokens);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 393, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 423, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)))
        );

        tabs.addTab("Tokens", jPanel3);

        tablaRecuento.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tablaRecuento);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabs.addTab("Recuento", jPanel4);

        jPanel1.add(tabs, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExportarCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarCSVActionPerformed
        exportarCSVDePestañaActual();
    }//GEN-LAST:event_btnExportarCSVActionPerformed

    private void btnExportarHTMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarHTMLActionPerformed
        exportarHTMLDePestañaActual();
    }//GEN-LAST:event_btnExportarHTMLActionPerformed

    private void btnCopiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCopiarActionPerformed
        int idx = tabs.getSelectedIndex();
        JTable tabla = obtenerTablaPorIndice(idx);
        if (tabla == null) return;
        StringBuilder sb = new StringBuilder();
        // Cabeceras
        for (int c = 0; c < tabla.getColumnCount(); c++)
        {
            if (c > 0) sb.append('\t');
            sb.append(tabla.getColumnName(c));
        }
        sb.append('\n');
        // Filas seleccionadas o todas si no hay seleccion
        int[] sel = tabla.getSelectedRows();
        if (sel == null || sel.length == 0) 
        {
            for (int r = 0; r < tabla.getRowCount(); r++) 
            {
                for (int c = 0; c < tabla.getColumnCount(); c++) 
                {
                    if (c > 0) sb.append('\t');
                    Object val = tabla.getValueAt(r, c);
                    sb.append(val == null ? "" : val.toString());
                }
                sb.append('\n');
            }
        } 
        else 
        {
            for (int r : sel) 
            {
                for (int c = 0; c < tabla.getColumnCount(); c++) 
                {
                    if (c > 0) sb.append('\t');
                    Object val = tabla.getValueAt(r, c);
                    sb.append(val == null ? "" : val.toString());
                }
                sb.append('\n');
            }
        }
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new java.awt.datatransfer.StringSelection(sb.toString()), null);
        JOptionPane.showMessageDialog(this, "Copiado al portapapeles.", "OK", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnCopiarActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnCopiar;
    private javax.swing.JButton btnExportarCSV;
    private javax.swing.JButton btnExportarHTML;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTable tablaErrores;
    private javax.swing.JTable tablaRecuento;
    private javax.swing.JTable tablaTokens;
    private javax.swing.JTabbedPane tabs;
    // End of variables declaration//GEN-END:variables
}
