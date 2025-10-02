package com.mycompany.analizador.ui;

import com.mycompany.analizador.lexer.ErrorLexico;
import com.mycompany.analizador.lexer.EscanerLexico;
import com.mycompany.analizador.lexer.TablaRL;
import com.mycompany.analizador.lexer.Token;
import com.mycompany.analizador.lexer.Traza;
import com.mycompany.analizador.parser.Evaluador;
import com.mycompany.analizador.parser.Parser;
import com.mycompany.analizador.parser.ParserUI;
import com.mycompany.analizador.parser.Programa;
import javax.swing.JOptionPane;

public class PestañaPrincipal extends javax.swing.JFrame 
{
    private EditorPanel editorPanel;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PestañaPrincipal.class.getName());
    private boolean mostrarTraza = false;
    private Colorizador colorizador;
    private BuscadorTexto buscador;
    private PanelBusqueda panelBusqueda;
    
    public PestañaPrincipal() 
    {
        initComponents();
        setTitle("Analizador Léxico");
        setLocationRelativeTo(null);
        panelPlaceholder.setLayout(new java.awt.BorderLayout());
        editorPanel = new EditorPanel();
        buscador = new BuscadorTexto(editorPanel.getComponenteTexto());
        panelBusqueda = new PanelBusqueda(buscador);
        panelPlaceholder.add(editorPanel, java.awt.BorderLayout.CENTER);
        colorizador = new Colorizador(editorPanel.getComponenteTexto());
        splitEditorConsola.setResizeWeight(0.8);
        splitPrincipal.setResizeWeight(0.9);
        getContentPane().add(splitPrincipal, java.awt.BorderLayout.CENTER);
        getContentPane().add(panelBusqueda, java.awt.BorderLayout.SOUTH);
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
                editorPanel.setTexto(sb.toString());
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
                bw.write(editorPanel.getTexto());
                consolaArea.append("Archivo guardado: " + f.getName() + "\n");
            }
            catch (java.io.IOException ex) 
            {
                javax.swing.JOptionPane.showMessageDialog(this, "No se pudo guardar: " + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void ejecutarAnalisis() 
    {
        try 
        {
            final String texto = editorPanel.getTexto();

            // Configurar escaner
            EscanerLexico sc = new EscanerLexico();
            sc.setAutomata(TablaRL.crear());
            Traza traza = null;
            if (mostrarTraza)
            {
                traza = new Traza();
                sc.setListener(traza);  
            }
            else
            {
                sc.setListener(null);
            }
            // Analizar
            EscanerLexico.Resultado res = sc.analizar(texto);

            // Tokens
            StringBuilder sbTok = new StringBuilder();
            sbTok.append(String.format("Tokens: %d%n", res.tokens.size()));
            for (Token t : res.tokens) 
            {
                sbTok.append(String.format("%-12s '%s'\t@%d:%d%n", t.getTipo(), t.getLexema(), t.getFila(), t.getColumna()));
            }
            resultadosArea.setText(sbTok.toString());

            // Errores
            StringBuilder sbErr = new StringBuilder();
            sbErr.append(String.format("Errores: %d%n", res.errores.size()));
            for (ErrorLexico e : res.errores) 
            {
                sbErr.append(String.format("[%d:%d] %s (lexema='%s')%n", e.getFila(), e.getColumna(), e.getDescripcion(), e.getLexema()));
            }
            if (mostrarTraza && traza != null)
            {
                sbErr.append("\n--- TRAZA ---\n").append(traza.basura());
            }
            consolaArea.setText(sbErr.toString());
        } 
        catch (Exception ex) 
        {
            JOptionPane.showMessageDialog(this, "Error durante el análisis: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void analizarEjecutar() 
    {
        resultadosArea.setText("");
        consolaArea.append("=== Analizando ===\n");

        String src = editorPanel.getTexto();

        // Léxico
        EscanerLexico sc = new EscanerLexico();
        sc.setAutomata(TablaRL.crear());
        EscanerLexico.Resultado res = sc.analizar(src);

        if (!res.errores.isEmpty()) 
        {
            consolaArea.append("Errores léxicos: " + res.errores.size() + "\n");
            for (com.mycompany.analizador.lexer.ErrorLexico e : res.errores) 
            {
                consolaArea.append(String.format("  [%d:%d] %s (lexema='%s')\n", e.getFila(), e.getColumna(), e.getDescripcion(), e.getLexema()));
            }
        } 
        else 
        {
            consolaArea.append("Léxico OK. Tokens: " + res.tokens.size() + "\n");
        }

        // Parser
        Parser p = new Parser(res.tokens);
        Programa prog = p.analizarPrograma();

        if (!p.getErrores().isEmpty()) 
        {
            consolaArea.append("Errores sintácticos: " + p.getErrores().size() + "\n");
            for (String s : p.getErrores()) consolaArea.append("  - " + s + "\n");
        } 
        else 
        {
            consolaArea.append("Parser OK.\n");
        }
        
        ParserUI pu = new ParserUI();
        resultadosArea.setText(pu.imprimir(prog));

        // Ejecutar
        Evaluador ev = new Evaluador();
        ev.ejecutarPrograma(prog, (linea) -> consolaArea.append(linea + "\n"));
        consolaArea.append("=== Fin ===\n");
    }
    
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        btnAnalizar = new javax.swing.JButton();
        splitPrincipal = new javax.swing.JSplitPane();
        splitEditorConsola = new javax.swing.JSplitPane();
        panelPlaceholder = new javax.swing.JPanel();
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
        menuMostrarTraza = new javax.swing.JCheckBoxMenuItem();
        menuAnalisis = new javax.swing.JMenu();
        menuAnalizar = new javax.swing.JMenuItem();
        menuEditar = new javax.swing.JMenu();
        itemBuscar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(430, 340));
        setPreferredSize(new java.awt.Dimension(430, 340));

        jToolBar1.setRollover(true);

        btnAnalizar.setText("Analizar");
        btnAnalizar.setFocusable(false);
        btnAnalizar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAnalizar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAnalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnalizarActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAnalizar);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.NORTH);

        splitPrincipal.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        splitEditorConsola.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitEditorConsola.setLeftComponent(panelPlaceholder);

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

        menuMostrarTraza.setText("Mostrar Traza");
        menuMostrarTraza.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMostrarTrazaActionPerformed(evt);
            }
        });
        menuVer.add(menuMostrarTraza);

        jMenuBar1.add(menuVer);

        menuAnalisis.setText("Análisis");

        menuAnalizar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        menuAnalizar.setText("Analizar (F5)");
        menuAnalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAnalizarActionPerformed(evt);
            }
        });
        menuAnalisis.add(menuAnalizar);

        jMenuBar1.add(menuAnalisis);

        menuEditar.setText("Editar");

        itemBuscar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        itemBuscar.setText("Buscar (Ctrl+F)");
        itemBuscar.setToolTipText("Abrir panel de búsqueda");
        itemBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemBuscarActionPerformed(evt);
            }
        });
        menuEditar.add(itemBuscar);

        jMenuBar1.add(menuEditar);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuLimpiarResultadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuLimpiarResultadosActionPerformed
        resultadosArea.setText("");
    }//GEN-LAST:event_menuLimpiarResultadosActionPerformed

    private void menuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuGuardarActionPerformed
        guardarArchivo();
    }//GEN-LAST:event_menuGuardarActionPerformed

    private void menuAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAbrirActionPerformed
        abrirArchivo();
    }//GEN-LAST:event_menuAbrirActionPerformed

    private void menuLimpiarConsolaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuLimpiarConsolaActionPerformed
        consolaArea.setText("");
    }//GEN-LAST:event_menuLimpiarConsolaActionPerformed

    private void menuAnalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAnalizarActionPerformed
        ejecutarAnalisis();
        analizarEjecutar();
    }//GEN-LAST:event_menuAnalizarActionPerformed

    private void btnAnalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalizarActionPerformed
        ejecutarAnalisis();
        analizarEjecutar();
    }//GEN-LAST:event_btnAnalizarActionPerformed

    private void menuMostrarTrazaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMostrarTrazaActionPerformed
        mostrarTraza = menuMostrarTraza.isSelected();
    }//GEN-LAST:event_menuMostrarTrazaActionPerformed

    private void itemBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemBuscarActionPerformed
        panelBusqueda.setVisible(true);
        panelBusqueda.enfocar();
    }//GEN-LAST:event_itemBuscarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAnalizar;
    private javax.swing.JTextArea consolaArea;
    private javax.swing.JMenuItem itemBuscar;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenuItem menuAbrir;
    private javax.swing.JMenu menuAnalisis;
    private javax.swing.JMenuItem menuAnalizar;
    private javax.swing.JMenu menuArchivo;
    private javax.swing.JMenu menuEditar;
    private javax.swing.JMenuItem menuGuardar;
    private javax.swing.JMenuItem menuLimpiarConsola;
    private javax.swing.JMenuItem menuLimpiarResultados;
    private javax.swing.JCheckBoxMenuItem menuMostrarTraza;
    private javax.swing.JMenu menuVer;
    private javax.swing.JPanel panelPlaceholder;
    private javax.swing.JTextArea resultadosArea;
    private javax.swing.JSplitPane splitEditorConsola;
    private javax.swing.JSplitPane splitPrincipal;
    // End of variables declaration//GEN-END:variables
}
