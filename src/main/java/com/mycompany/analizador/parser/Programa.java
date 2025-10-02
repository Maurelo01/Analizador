package com.mycompany.analizador.parser;

import java.util.ArrayList;
import java.util.List;

public class Programa // Lista de sentencias
{
    private final List<Stmt> sentencias = new ArrayList<>();
    public Programa() {}
    public Programa(List<Stmt> sentencias)
    {
        if (sentencias != null) this.sentencias.addAll(sentencias);
    }
    
    public List<Stmt> getSentencias() 
    { 
        return sentencias; 
    }
    
    public void agregarSentencia(Stmt s) 
    { 
        if (s != null) 
        sentencias.add(s); 
    }
    
     @Override public String toString() 
     { 
         return "Programa" + sentencias; 
     }
}

abstract class Stmt {}

class StmtAsignar extends Stmt // asignacion nombre = valor
{
    private final String nombre;
    private final Expr valor;

    public StmtAsignar(String nombre, Expr valor) 
    {
        this.nombre = nombre;
        this.valor = valor;
    }
    public String getNombre() 
    { 
        return nombre; 
    }
    public Expr getValor() 
    { 
        return valor; 
    }

    @Override public String toString() 
    { 
        return "Asign(" + nombre + " = " + valor + ")"; 
    }
}

class StmtEscribir extends Stmt // Escribir(expr)
{
    private final Expr valor;

    public StmtEscribir(Expr valor) 
    {
        this.valor = valor;
    }
    public Expr getValor()
    { 
        return valor; 
    }

    @Override public String toString() 
    { 
        return "Escribir(" + valor + ")"; 
    }
}

    class StmtExpr extends Stmt 
    {
        private final Expr expr;
        public StmtExpr(Expr expr) 
        {
            this.expr = expr; 
        }
        public Expr getExpr() 
        { 
            return expr;
        }
        @Override public String toString() 
        { 
            return "ExprStmt(" + expr + ")"; 
        }
    }

class StmtBloque extends Stmt 
{
    private final java.util.List<Stmt> sentencias = new java.util.ArrayList<>();
    public StmtBloque() {}
    public void agregar(Stmt s) 
    { 
        if (s != null) sentencias.add(s); 
    }
    public java.util.List<Stmt> getSentencias() 
    { 
        return sentencias; 
    }
    
    @Override public String toString() 
    { 
        return "{ " + sentencias + " }"; 
    }
}

class StmtSi extends Stmt 
{
    private final Expr condicion;
    private final Stmt entonces; // puede ser una sentencia o un bloque
    private final Stmt sino; // puede ser null
    
    public StmtSi(Expr condicion, Stmt entonces) 
    {
        this(condicion, entonces, null);
    }
    
    public StmtSi(Expr condicion, Stmt entonces, Stmt sino) 
    {
        this.condicion = condicion;
        this.entonces = entonces;
        this.sino = sino;
    }
    
    public Expr getCondicion() 
    {
        return condicion;
    }
    
    public Stmt getEntonces() 
    { 
        return entonces; 
    }
    
    public Stmt getSino() 
    { 
        return sino; 
    }
    
    @Override public String toString() 
    { 
        return "Si(" + condicion + ") " + entonces; 
    }
}

class StmtPara extends Stmt
{
    private final Stmt init;
    private final Expr condicion;
    private final Expr post;
    private final Stmt cuerpo;

    public StmtPara(Stmt init, Expr condicion, Expr post, Stmt cuerpo) 
    {
        this.init = init;
        this.condicion = condicion;
        this.post = post;
        this.cuerpo = cuerpo;
    }

    public Stmt getInit()      
    { 
        return init; 
    }
    public Expr getCondicion() 
    { 
        return condicion; 
    }
    public Expr getPost()      
    { 
        return post; 
    }
    public Stmt getCuerpo()    
    { return cuerpo; 
    }

    @Override public String toString() 
    {
        return "Para(init=" + init + ", cond=" + condicion + ", post=" + post + ") " + cuerpo;
    }
}

abstract class Expr {}
// Expresiones
class ExprNumero extends Expr 
{
    private final String lexema; // puede ser entero o decimal

    public ExprNumero(String lexema) 
    { 
        this.lexema = lexema; 
    }
    public String getLexema() 
    { 
        return lexema; 
    }

    @Override public String toString() 
    {
        return lexema; 
    }
}

    class ExprAsign extends Expr 
    {
        private final String nombre;
        private final Expr valor;

        public ExprAsign(String nombre, Expr valor) 
        {
            this.nombre = nombre;
            this.valor = valor;
        }
        public String getNombre() 
        { 
            return nombre; 
        }
        public Expr getValor()
        { 
            return valor; 
        }

        @Override public String toString() 
        {
            return "(" + nombre + " = " + valor + ")";
        }
    }

class ExprCadena extends Expr 
{
    private final String lexema;

    public ExprCadena(String lexema) 
    { 
        this.lexema = lexema; 
    }
    public String getLexema() 
    { 
        return lexema; 
    }

    @Override public String toString() 
    { 
        return "\"" + lexema + "\""; 
    }
}

class ExprIdent extends Expr 
{
    private final String nombre;

    public ExprIdent(String nombre) 
    { 
        this.nombre = nombre; 
    }
    public String getNombre() 
    { 
        return nombre; 
    }

    @Override public String toString() 
    { 
        return nombre; 
    }
}

class ExprUnaria extends Expr 
{
    private final String op;
    private final Expr expr;

    public ExprUnaria(String op, Expr expr) 
    {
        this.op = op;
        this.expr = expr;
    }
    public String getOp() 
    { 
        return op; 
    }
    public Expr getExpr() 
    { 
        return expr; 
    }

    @Override public String toString() 
    { 
        return "(" + op + expr + ")"; 
    }
}

class ExprBinaria extends Expr 
{
    private final Expr izq;
    private final String op;
    private final Expr der;

    public ExprBinaria(Expr izq, String op, Expr der) 
    {
        this.izq = izq;
        this.op = op;
        this.der = der;
    }
    public Expr getIzq() 
    {
        return izq; 
    }
    public String getOp()
    { 
        return op; 
    }
    public Expr getDer() 
    { 
        return der; 
    }

    @Override public String toString() 
    {
        return "(" + izq + " " + op + " " + der + ")"; 
    }
}

class ExprParen extends Expr
{
    private final Expr dentro;

    public ExprParen(Expr dentro)
    { 
        this.dentro = dentro; 
    }
    public Expr getDentro() 
    {
        return dentro; 
    }

    @Override public String toString() 
    {
        return "(" + dentro + ")"; 
    }
}
