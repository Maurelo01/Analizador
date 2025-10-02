package com.mycompany.analizador.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Evaluador 
{
    public static class Entorno
    {
        private final Map<String, Object> vars = new HashMap<>();
        public Object get(String nombre) { return vars.get(nombre); }
        public void set(String nombre, Object valor) { vars.put(nombre, valor); }
        @Override public String toString() { return vars.toString(); }
    }
    
    private final Entorno env = new Entorno();
    private long pasosMax = 100000; // protección anti-bucles

    public Entorno getEntorno() 
    { 
        return env; 
    }
    public void setPasosMax(long pasosMax) 
    { 
        this.pasosMax = Math.max(1, pasosMax); 
    }

    public void ejecutarPrograma(Programa prog, Consumer<String> salida) 
    {
        if (prog == null) return;
        for (Stmt s : prog.getSentencias()) 
        {
            ejecutarStmt(s, salida);
        }
    }

    // Sentencias
    private void ejecutarStmt(Stmt s, Consumer<String> salida) 
    {
        if (s == null) return;
        if (s instanceof StmtAsignar) 
        {
            StmtAsignar a = (StmtAsignar) s;
            Object val = eval(a.getValor(), salida);
            env.set(a.getNombre(), val);
            return;
        }

        if (s instanceof StmtEscribir) 
        {
            Object val = eval(((StmtEscribir) s).getValor(), salida);
            salida.accept(String.valueOf(val));
            return;
        }

        if (s instanceof StmtBloque) 
        {
            for (Stmt h : ((StmtBloque) s).getSentencias()) ejecutarStmt(h, salida);
            return;
        }

        if (s instanceof StmtSi) 
        {
            StmtSi si = (StmtSi) s;
            if (esVerdadero(eval(si.getCondicion(), salida))) 
            {
                ejecutarStmt(si.getEntonces(), salida);
            } 
            else if (si.getSino() != null) 
            {
                ejecutarStmt(si.getSino(), salida);
            }
            return;
        }
        if (s instanceof StmtPara) 
        {
            ejecutarPara((StmtPara) s, salida);
            return;
        }
        salida.accept("[AVISO] Sentencia no ejecutable: " + s);
    }

    private void ejecutarPara(StmtPara p, Consumer<String> salida) 
    {
        if (p.getInit() != null) ejecutarStmt(p.getInit(), salida);

        long pasos = 0;
        while (true) {
            if (pasos++ > pasosMax) 
            {
                salida.accept("[ERROR] Se superó el máximo de iteraciones del bucle (protección anti-bucles).");
                break;
            }
            // condición (si es null, se asume true)
            boolean cond = true;
            if (p.getCondicion() != null) 
            {
                cond = esVerdadero(eval(p.getCondicion(), salida));
            }
            if (!cond) break;
            // cuerpo
            ejecutarStmt(p.getCuerpo(), salida);
            // post
            if (p.getPost() != null) eval(p.getPost(), salida);
        }
    }

    // Expresiones

    private Object eval(Expr e, Consumer<String> salida) 
    {
        if (e == null) return null;

        if (e instanceof ExprNumero) 
        {
            String lx = ((ExprNumero) e).getLexema();
            try 
            {
                return Double.valueOf(lx);
            } 
            catch (NumberFormatException ex) 
            {
                salida.accept("[ERROR] Número inválido: " + lx + " => 0");
                return 0.0;
            }
        }

        if (e instanceof ExprCadena) 
        {
            return ((ExprCadena) e).getLexema();
        }

        if (e instanceof ExprIdent) 
        {
            String n = ((ExprIdent) e).getNombre();
            Object v = env.get(n);
            if (v == null) 
            {
                salida.accept("[AVISO] Variable no definida: " + n + " => null");
            }
            return v;
        }

        if (e instanceof ExprParen) 
        {
            return eval(((ExprParen) e).getDentro(), salida);
        }

        if (e instanceof ExprUnaria) 
        {
            ExprUnaria u = (ExprUnaria) e;
            Object v = eval(u.getExpr(), salida);
            if ("+".equals(u.getOp())) 
            {
                return toNumero(v, salida);
            }
            else if ("-".equals(u.getOp())) 
            {
                return -toNumero(v, salida);
            }
            else if ("!".equals(u.getOp())) 
            {
                return esVerdadero(v) ? 0.0 : 1.0;
            }
            salida.accept("[ERROR] Operador unario no soportado: " + u.getOp());
            return null;
        }
        
        if (e instanceof ExprAsign) 
        {
            ExprAsign as = (ExprAsign) e;
            Object val = eval(as.getValor(), salida);
            env.set(as.getNombre(), val);
            return val;
        }
        
        if (e instanceof ExprBinaria) 
        {
            ExprBinaria b = (ExprBinaria) e;
            
            String op = b.getOp();
            if ("||".equals(op)) 
            {
                Object li = eval(b.getIzq(), salida);
                if (esVerdadero(li)) return li; // corto si ya es true
                Object ld = eval(b.getDer(), salida);
                return esVerdadero(ld) ? ld : 0.0; // representamos false como 0.0
            }
            if ("&&".equals(op)) 
            {
                Object li = eval(b.getIzq(), salida);
                if (!esVerdadero(li)) return 0.0; // corto si ya es false
                Object ld = eval(b.getDer(), salida);
                return esVerdadero(ld) ? ld : 0.0;
            }
            Object li = eval(b.getIzq(), salida);
            Object ld = eval(b.getDer(), salida);
            
            // Igualdad
            if ("==".equals(op)) 
            {
                return iguales(li, ld) ? 1.0 : 0.0;
            }
            if ("!=".equals(op)) 
            {
                return iguales(li, ld) ? 0.0 : 1.0;
            }

            if ("<".equals(op) || "<=".equals(op) || ">".equals(op) || ">=".equals(op)) 
            {
                int cmp = comparar(li, ld, salida);
                switch (op) 
                {
                    case "<":  return (cmp < 0)  ? 1.0 : 0.0;
                    case "<=": return (cmp <= 0) ? 1.0 : 0.0;
                    case ">":  return (cmp > 0)  ? 1.0 : 0.0;
                    case ">=": return (cmp >= 0) ? 1.0 : 0.0;
                }
            }
            
            if ("+".equals(op)) 
            {
                if (li instanceof String || ld instanceof String) 
                {
                    return aTexto(li) + aTexto(ld);
                }
                return toNumero(li, salida) + toNumero(ld, salida);
            }
            if ("-".equals(op)) 
            {
                return toNumero(li, salida) - toNumero(ld, salida);
            }
            if ("*".equals(op)) 
            {
                return toNumero(li, salida) * toNumero(ld, salida);
            }
            if ("/".equals(op)) 
            {
                double d = toNumero(ld, salida);
                if (d == 0.0) { salida.accept("[ERROR] División entre cero => Infinity"); return Double.POSITIVE_INFINITY; }
                return toNumero(li, salida) / d;
            }
            if ("%".equals(op)) 
            {
                double d = toNumero(ld, salida);
                if (d == 0.0) { salida.accept("[ERROR] Módulo con cero => NaN"); return Double.NaN; }
                return toNumero(li, salida) % d;
            }
            salida.accept("[ERROR] Operador no soportado: " + op);
            return null;
        }
        salida.accept("[AVISO] Expresión no evaluable: " + e);
        return null;
    }
    
    private boolean esVerdadero(Object v)
    {
        if (v == null) return false;
        if (v instanceof Double) return ((Double) v) != 0.0;
        if (v instanceof String) return !((String) v).isEmpty();
        return true; // otros objetos = true
    }

    private double toNumero(Object v, Consumer<String> salida) 
    {
        if (v == null) return 0.0;
        if (v instanceof Double) return (Double) v;
        if (v instanceof String) 
        {
            try 
            { 
                return Double.parseDouble((String) v); 
            }
            catch (NumberFormatException ex) 
            {
                salida.accept("[ERROR] No numérico: \"" + v + "\" => 0");
                return 0.0;
            }
        }
        salida.accept("[ERROR] Tipo no numérico: " + v.getClass().getSimpleName() + " => 0");
        return 0.0;
    }
    
    private String aTexto(Object v) 
    {
        return String.valueOf(v);
    }
    
    private boolean iguales(Object a, Object b) 
    {
        if (a == b) return true;
        if (a == null || b == null) return false;

        // Si ambos son numericos
        Double da = tryNumero(a);
        Double db = tryNumero(b);
        if (da != null && db != null) 
        {
            return Double.compare(da, db) == 0;
        }
        return aTexto(a).equals(aTexto(b)); // Si alguno es cadena compara texto
    }

    private int comparar(Object a, Object b, java.util.function.Consumer<String> salida) 
    {
        Double da = tryNumero(a);
        Double db = tryNumero(b);
        if (da != null && db != null) 
        {
            return Double.compare(da, db);
        }
        return aTexto(a).compareTo(aTexto(b)); // compara como texto
    }

    private Double tryNumero(Object v) 
    {
        if (v instanceof Double) return (Double) v;
        if (v instanceof String) 
        {
            try 
            { 
                return Double.parseDouble((String) v); 
            } 
            catch (NumberFormatException ex) 
            { 
                return null; 
            }
        }
        return null;
    }
}
