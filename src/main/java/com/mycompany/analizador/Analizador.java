package com.mycompany.analizador;
import javax.swing.SwingUtilities;

public class Analizador 
{
    public static void main(String[] args) 
    {   
        SwingUtilities.invokeLater(() -> new com.mycompany.analizador.ui.PestañaPrincipal().setVisible(true));
    }
}
