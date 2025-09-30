package com.mycompany.analizador.ui;
public class PestañaPrincipal extends javax.swing.JFrame 
{
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PestañaPrincipal.class.getName());
    public PestañaPrincipal() 
    {
        initComponents();
        setTitle("Analizador Léxico");
        setLocationRelativeTo(null);
    }
    
    private void abrirArchivo()
    {
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Texto (*.txt)", "txt"));
        if (chooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) 
        {
            java.io.File f = chooser.getSelectedFile();
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(f), java.nio.charset.StandardCharsets.UTF_8)))
            {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line).append('\n');
                editorTextPane.setText(sb.toString());
                editorTextPane.setCaretPosition(0);
                consolaArea.append("Archivo abierto: " + f.getName() + "\n");
            }
            catch (java.io.IOException ex) 
            {
                javax.swing.JOptionPane.showMessageDialog(this, "No se pudo abrir: " + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void guardarArchivo()
    {
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Texto (*.txt)", "txt"));
        if (chooser.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION)
        {
            java.io.File f = chooser.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith(".txt")) 
            {
                f = new java.io.File(f.getParentFile(), f.getName() + ".txt");
            }
            try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(f), java.nio.charset.StandardCharsets.UTF_8)))
            {
                bw.write(editorTextPane.getText());
                consolaArea.append("Archivo guardado: " + f.getName() + "\n");
            }
            catch (java.io.IOException ex) 
            {
                javax.swing.JOptionPane.showMessageDialog(this, "No se pudo guardar: " + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        statusPanel = new javax.swing.JPanel();
        posLabel = new javax.swing.JLabel();
        splitPrincipal = new javax.swing.JSplitPane();
        splitEditorConsola = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        editorTextPane = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        consolaArea = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        resultadosArea = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuArchivo = new javax.swing.JMenu();
        menuAbrir = new javax.swing.JMenuItem();
        menuGuardar = new javax.swing.JMenuItem();
        menuVer = new javax.swing.JMenu();
        menuLimpiarConsola = new javax.swing.JMenuItem();
        menuLimpiarResultados = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(430, 340));
        setPreferredSize(new java.awt.Dimension(430, 340));

        statusPanel.setLayout(new java.awt.BorderLayout());

        posLabel.setText("Fila 1, Col 1");
        statusPanel.add(posLabel, java.awt.BorderLayout.CENTER);

        getContentPane().add(statusPanel, java.awt.BorderLayout.PAGE_END);

        splitPrincipal.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        splitEditorConsola.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        editorTextPane.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                editorTextPaneCaretUpdate(evt);
            }
        });
        jScrollPane1.setViewportView(editorTextPane);

        splitEditorConsola.setTopComponent(jScrollPane1);

        consolaArea.setEditable(false);
        consolaArea.setColumns(20);
        consolaArea.setRows(6);
        jScrollPane2.setViewportView(consolaArea);

        splitEditorConsola.setRightComponent(jScrollPane2);

        splitPrincipal.setLeftComponent(splitEditorConsola);
        splitEditorConsola.setResizeWeight(0.8);

        resultadosArea.setEditable(false);
        resultadosArea.setColumns(20);
        resultadosArea.setRows(8);
        jScrollPane3.setViewportView(resultadosArea);

        splitPrincipal.setRightComponent(jScrollPane3);

        getContentPane().add(splitPrincipal, java.awt.BorderLayout.PAGE_START);
        splitPrincipal.setResizeWeight(0.9);

        menuArchivo.setText("Archivo");

        menuAbrir.setText("Abrir");
        menuAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAbrirActionPerformed(evt);
            }
        });
        menuArchivo.add(menuAbrir);

        menuGuardar.setText("Guardar");
        menuGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuGuardarActionPerformed(evt);
            }
        });
        menuArchivo.add(menuGuardar);

        jMenuBar1.add(menuArchivo);

        menuVer.setText("Ver");

        menuLimpiarConsola.setText("Limpiar Consola");
        menuLimpiarConsola.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuLimpiarConsolaActionPerformed(evt);
            }
        });
        menuVer.add(menuLimpiarConsola);

        menuLimpiarResultados.setText("Limpiar Resultados");
        menuLimpiarResultados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuLimpiarResultadosActionPerformed(evt);
            }
        });
        menuVer.add(menuLimpiarResultados);

        jMenuBar1.add(menuVer);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuLimpiarResultadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuLimpiarResultadosActionPerformed
        consolaArea.setText("");
    }//GEN-LAST:event_menuLimpiarResultadosActionPerformed

    private void menuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuGuardarActionPerformed
        guardarArchivo();
    }//GEN-LAST:event_menuGuardarActionPerformed

    private void menuAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAbrirActionPerformed
        abrirArchivo();
    }//GEN-LAST:event_menuAbrirActionPerformed

    private void menuLimpiarConsolaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuLimpiarConsolaActionPerformed
        resultadosArea.setText("");
    }//GEN-LAST:event_menuLimpiarConsolaActionPerformed

    private void editorTextPaneCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_editorTextPaneCaretUpdate
        try 
        {
            int pos = editorTextPane.getCaretPosition();
            javax.swing.text.Element base = editorTextPane.getDocument().getDefaultRootElement();
            int fila = base.getElementIndex(pos) + 1;
            int inicioLinea = base.getElement(fila - 1).getStartOffset();
            int col = pos - inicioLinea + 1;
            posLabel.setText("Fila " + fila + ", Col " + col);
        }
        catch (Exception ex)
        {
            posLabel.setText("Fila ?, Col ?");
        }
    }//GEN-LAST:event_editorTextPaneCaretUpdate

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new PestañaPrincipal().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea consolaArea;
    private javax.swing.JTextPane editorTextPane;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JMenuItem menuAbrir;
    private javax.swing.JMenu menuArchivo;
    private javax.swing.JMenuItem menuGuardar;
    private javax.swing.JMenuItem menuLimpiarConsola;
    private javax.swing.JMenuItem menuLimpiarResultados;
    private javax.swing.JMenu menuVer;
    private javax.swing.JLabel posLabel;
    private javax.swing.JTextArea resultadosArea;
    private javax.swing.JSplitPane splitEditorConsola;
    private javax.swing.JSplitPane splitPrincipal;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
}
