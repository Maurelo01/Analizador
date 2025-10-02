package com.mycompany.analizador.ui;
public class EditorPanel extends javax.swing.JPanel 
{
    public EditorPanel() 
    {
        initComponents();
    }
    
    public String getTexto() // Obtiene el texto del editor
    {
        return textPane.getText();
    }
    
    public void setTexto(String s) // Coloca texto en el editor 
    {
        textPane.setText(s);
        textPane.setCaretPosition(0);
    }
    
    public javax.swing.text.JTextComponent getComponenteTexto() 
    {
        return textPane; 
    }
    
    public javax.swing.JLabel getEtiquetaPosicion() // acceso al label de posicion
    {
        return posLabel;
    }
    
    public void seleccionarTodo() 
    {
        textPane.selectAll();
    }
    
    public void irA(int fila, int columna)
    {
        try
        {
            javax.swing.text.Element base = textPane.getDocument().getDefaultRootElement();
            int offInicio = base.getElement(Math.max(0, fila - 1)).getStartOffset();
            int offset = Math.max(offInicio + Math.max(0, columna - 1), 0);
            textPane.setCaretPosition(Math.min(offset, textPane.getDocument().getLength()));
        }
        catch (Exception ignored) {}
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollEditor = new javax.swing.JScrollPane();
        textPane = new javax.swing.JTextPane();
        statusPanel = new javax.swing.JPanel();
        posLabel = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        textPane.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                textPaneCaretUpdate(evt);
            }
        });
        scrollEditor.setViewportView(textPane);

        add(scrollEditor, java.awt.BorderLayout.CENTER);

        posLabel.setText("Fila 1, Col 1");
        statusPanel.add(posLabel);

        add(statusPanel, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void textPaneCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_textPaneCaretUpdate
        try
        {
            int pos = textPane.getCaretPosition();
            javax.swing.text.Element base = textPane.getDocument().getDefaultRootElement();
            int fila = base.getElementIndex(pos) + 1;
            int inicioLinea = base.getElement(fila - 1).getStartOffset();
            int col = pos - inicioLinea + 1;
            posLabel.setText("Fila " + fila + ", Col " + col);
        }
        catch (Exception ex)
        {
            posLabel.setText("Fila ?, Col ?");
        }
    }//GEN-LAST:event_textPaneCaretUpdate


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel posLabel;
    private javax.swing.JScrollPane scrollEditor;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTextPane textPane;
    // End of variables declaration//GEN-END:variables
}
