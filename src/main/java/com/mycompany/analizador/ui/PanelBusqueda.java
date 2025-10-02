package com.mycompany.analizador.ui;
public class PanelBusqueda extends javax.swing.JPanel 
{
    private final BuscadorTexto buscador;
    public PanelBusqueda(BuscadorTexto buscador) 
    {
        this.buscador = buscador;
        initComponents();
        setVisible(false);
    }
    
    public void enfocar() 
    {
        txtBuscar.requestFocusInWindow();
        txtBuscar.selectAll();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        chkMayus = new javax.swing.JCheckBox();
        btnResaltar = new javax.swing.JButton();
        btnAnterior = new javax.swing.JButton();
        btnSiguiente = new javax.swing.JButton();
        lblEstado = new javax.swing.JLabel();
        btnCerrar = new javax.swing.JButton();

        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 4));

        lblBuscar.setText("Buscar");
        add(lblBuscar);

        txtBuscar.setColumns(18);
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtBuscarKeyPressed(evt);
            }
        });
        add(txtBuscar);

        chkMayus.setText("May/Min");
        add(chkMayus);

        btnResaltar.setText("Buscar");
        btnResaltar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResaltarActionPerformed(evt);
            }
        });
        add(btnResaltar);

        btnAnterior.setText("Anterior");
        btnAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnteriorActionPerformed(evt);
            }
        });
        add(btnAnterior);

        btnSiguiente.setText("Siguiente");
        btnSiguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSiguienteActionPerformed(evt);
            }
        });
        add(btnSiguiente);

        lblEstado.setText("0/0");
        add(lblEstado);

        btnCerrar.setText("Cerrar");
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });
        add(btnCerrar);
    }// </editor-fold>//GEN-END:initComponents

    private void btnResaltarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResaltarActionPerformed
        int n = buscador.resaltarTodo(txtBuscar.getText(), chkMayus.isSelected());
        lblEstado.setText(buscador.posicionHumanReadable());
        if (n == 0) 
        {
            javax.swing.JOptionPane.showMessageDialog(this, "No se encontraron coincidencias.", "Búsqueda", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnResaltarActionPerformed

    private void btnSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSiguienteActionPerformed
        buscador.siguiente();
        lblEstado.setText(buscador.posicionHumanReadable());
    }//GEN-LAST:event_btnSiguienteActionPerformed

    private void btnAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnteriorActionPerformed
        buscador.anterior();
        lblEstado.setText(buscador.posicionHumanReadable());
    }//GEN-LAST:event_btnAnteriorActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        buscador.limpiar();
        setVisible(false);
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void txtBuscarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) 
        {
            if ("0/0".equals(lblEstado.getText()))
            {
                btnResaltarActionPerformed(null);
            }
            else 
            {
                btnSiguienteActionPerformed(null);
            }
        }
        else if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) 
        {
            btnCerrarActionPerformed(null);
        }
    }//GEN-LAST:event_txtBuscarKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAnterior;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnResaltar;
    private javax.swing.JButton btnSiguiente;
    private javax.swing.JCheckBox chkMayus;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
