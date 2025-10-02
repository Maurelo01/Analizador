package com.mycompany.analizador.debug;

import com.mycompany.analizador.lexer.ReconocedorLexico;
import com.mycompany.analizador.lexer.TablaRL;

public class DialogoDepuracion extends javax.swing.JDialog 
{
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DialogoDepuracion.class.getName());
    private com.mycompany.analizador.debug.DepuradorModelo modelo;
    
    public DialogoDepuracion(java.awt.Frame parent, boolean modal) 
    {
        super(parent, modal);
        initComponents();
    }
    
    public void preparar(String entrada) 
    {
        ReconocedorLexico dfa = TablaRL.crear();   // tu AFD real
        modelo = new DepuradorModelo(dfa, entrada);
        refrescar(); // puebla etiquetas y área
    }

    private void refrescar() 
    {
        if (modelo == null) return;
        lblEstado.setText("Estado actual: " + modelo.getEstadoActual());
        lblIndice.setText("Índice: " + modelo.getIndice());
        lblLexema.setText("Lexema: " + modelo.getLexemaParcial());

        String acept = (modelo.getUltimoAceptIdx() >= 0) ? ("Sí (tipo=" + modelo.getUltimoAceptTipo() + ", idx=" + modelo.getUltimoAceptIdx() + ")") : "No";
        lblAceptacion.setText("¿Hubo aceptación?: " + acept);
        StringBuilder sb = new StringBuilder();
        for (com.mycompany.analizador.debug.DepuradorModelo.Paso p : modelo.getPasos()) 
        {
            sb.append(String.format("δ(%d, '%s') = %d   lex=\"%s\"%n", p.estadoAntes, p.c, p.estadoDespues, p.lexemaParcial));
        }
        areaTraza.setText(sb.toString());
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblEstado = new javax.swing.JLabel();
        lblIndice = new javax.swing.JLabel();
        lblLexema = new javax.swing.JLabel();
        lblAceptacion = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        areaTraza = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        btnAvanzar = new javax.swing.JButton();
        btnRegresar = new javax.swing.JButton();
        btnHastaFin = new javax.swing.JButton();
        btnReiniciar = new javax.swing.JButton();
        btnExportar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setLayout(new java.awt.GridLayout(2, 2, 8, 8));

        lblEstado.setText("Estado actual:");
        jPanel1.add(lblEstado);

        lblIndice.setText("Índice:");
        jPanel1.add(lblIndice);

        lblLexema.setText("Lexema:");
        jPanel1.add(lblLexema);

        lblAceptacion.setText("¿Hubo aceptación?:");
        jPanel1.add(lblAceptacion);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        areaTraza.setEditable(false);
        areaTraza.setColumns(60);
        areaTraza.setRows(12);
        jScrollPane1.setViewportView(areaTraza);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        btnAvanzar.setText("Avanzar");
        btnAvanzar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAvanzarActionPerformed(evt);
            }
        });
        jPanel2.add(btnAvanzar);

        btnRegresar.setText("Regresar");
        btnRegresar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegresarActionPerformed(evt);
            }
        });
        jPanel2.add(btnRegresar);

        btnHastaFin.setText("Hasta fin");
        btnHastaFin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHastaFinActionPerformed(evt);
            }
        });
        jPanel2.add(btnHastaFin);

        btnReiniciar.setText("Reiniciar");
        btnReiniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReiniciarActionPerformed(evt);
            }
        });
        jPanel2.add(btnReiniciar);

        btnExportar.setText("Exportar PNG");
        btnExportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarActionPerformed(evt);
            }
        });
        jPanel2.add(btnExportar);

        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAvanzarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAvanzarActionPerformed
        if (modelo != null) 
        { 
            modelo.avanzar(); refrescar(); 
        }
    }//GEN-LAST:event_btnAvanzarActionPerformed

    private void btnRegresarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegresarActionPerformed
        if (modelo != null) 
        { 
            modelo.regresar(); refrescar(); 
        }
    }//GEN-LAST:event_btnRegresarActionPerformed

    private void btnHastaFinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHastaFinActionPerformed
        if (modelo != null) 
        { 
            modelo.hastaFin(); refrescar(); 
        }
    }//GEN-LAST:event_btnHastaFinActionPerformed

    private void btnReiniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReiniciarActionPerformed
        if (modelo != null) 
        { 
            modelo.reiniciar(); refrescar(); 
        }
    }//GEN-LAST:event_btnReiniciarActionPerformed

    private void btnExportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarActionPerformed
        
    }//GEN-LAST:event_btnExportarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea areaTraza;
    private javax.swing.JButton btnAvanzar;
    private javax.swing.JButton btnExportar;
    private javax.swing.JButton btnHastaFin;
    private javax.swing.JButton btnRegresar;
    private javax.swing.JButton btnReiniciar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAceptacion;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblIndice;
    private javax.swing.JLabel lblLexema;
    // End of variables declaration//GEN-END:variables
}
