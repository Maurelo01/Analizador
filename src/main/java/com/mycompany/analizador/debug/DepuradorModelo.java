package com.mycompany.analizador.debug;

import com.mycompany.analizador.lexer.Automata;
import com.mycompany.analizador.lexer.ReconocedorLexico;
import com.mycompany.analizador.lexer.TipoToken;
import java.util.ArrayList;
import java.util.List;

public class DepuradorModelo 
{
    public static class Paso 
    {
        public final int estadoAntes;
        public final char c;
        public final int estadoDespues;
        public final String lexemaParcial;

        public Paso(int antes, char c, int despues, String lexemaParcial) 
        {
            this.estadoAntes = antes;
            this.c = c;
            this.estadoDespues = despues;
            this.lexemaParcial = lexemaParcial;
        }
    }

    private final Automata automata;
    private final String entrada;
    private int idx;
    private int estadoActual;
    private final List<Paso> pasos = new ArrayList<>();
    private final StringBuilder lexema = new StringBuilder();
    private int ultimoAceptIdx = -1;
    private TipoToken ultimoAceptTipo = null;

    public DepuradorModelo(Automata automata, String entrada) 
    {
        this.automata = automata;
        this.entrada = (entrada == null ? "" : entrada);
        reiniciar();
    }

    public void reiniciar() 
    {
        automata.reiniciar();
        idx = 0;
        pasos.clear();
        lexema.setLength(0);
        estadoActual = automata.estadoActual();
        ultimoAceptIdx = -1;
        ultimoAceptTipo = null;
    }

    public boolean puedeAvanzar() 
    { 
        return idx < entrada.length(); 
    }

    public boolean avanzar() 
    {
        if (!puedeAvanzar()) return false;
        char c = entrada.charAt(idx);
        int antes = automata.estadoActual();
        boolean ok = automata.transitar(c);
        int despues = ok ? automata.estadoActual() : -1;

        if (ok) 
        {
            lexema.append(c);
            // si esta en aceptacion, guardar checkpoint
            if (automata.estaEnAceptacion() && automata instanceof ReconocedorLexico rl) 
            {
                ultimoAceptIdx = idx;
                ultimoAceptTipo = rl.tipoDeAceptacion(automata.estadoActual());
            }
            pasos.add(new Paso(antes, c, despues, lexema.toString()));
            idx++;
            estadoActual = despues;
        }
        else 
        {
            // transicion no valida la registra con -1
            pasos.add(new Paso(antes, c, -1, lexema.toString()));
        }
        return ok;
    }

    public boolean puedeRegresar() 
    { 
        return !pasos.isEmpty(); 
    }

    public boolean regresar() 
    {
        if (!puedeRegresar()) return false;
        // deshacer un paso reconstruyendo desde cero hasta pasos.size()-1
        int hasta = pasos.size() - 1;
        String entradaParcial = entrada.substring(0, hasta);
        reiniciar();
        for (int i = 0; i < entradaParcial.length(); i++) 
        {
            char c = entradaParcial.charAt(i);
            int antes = automata.estadoActual();
            boolean ok = automata.transitar(c);
            int despues = ok ? automata.estadoActual() : -1;
            if (!ok) break;
            lexema.append(c);
            if (automata.estaEnAceptacion() && automata instanceof ReconocedorLexico rl) 
            {
                ultimoAceptIdx = i;
                ultimoAceptTipo = rl.tipoDeAceptacion(automata.estadoActual());
            }
            pasos.add(new Paso(antes, c, despues, lexema.toString()));
            idx++;
            estadoActual = despues;
        }
        return true;
    }

    public void hastaFin() 
    {
        while (puedeAvanzar()) 
        {
            boolean ok = avanzar();
            if (!ok) break;
        }
    }

    public int getEstadoActual() 
    { 
        return estadoActual; 
    }
    public int getIndice() 
    { 
        return idx; 
    }
    public String getLexemaParcial() 
    { 
        return lexema.toString(); 
    }
    public List<Paso> getPasos() 
    { 
        return pasos; 
    }
    public int getUltimoAceptIdx() 
    { 
        return ultimoAceptIdx; 
    }
    public TipoToken getUltimoAceptTipo() 
    { 
        return ultimoAceptTipo; 
    }
    public boolean enAceptacion() 
    { 
        return automata.estaEnAceptacion(); 
    }
}
