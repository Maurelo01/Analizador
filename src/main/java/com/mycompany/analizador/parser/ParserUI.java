package com.mycompany.analizador.parser;
public class ParserUI 
{
    private final StringBuilder sb = new StringBuilder();
    private int nivel = 0;
    
    public String imprimir(Programa prog) 
    {
        sb.setLength(0);
        nivel = 0;
        linea("Programa");
        nivel++;
        for (Stmt s : prog.getSentencias()) 
        {
            imprimirStmt(s);
        }
        nivel--;
        return sb.toString();
    }
    
    private void imprimirStmt(Stmt s) 
    {
        if (s == null) 
        { 
            linea("(sentencia nula)"); return; 
        }

        if (s instanceof StmtAsignar) 
        {
            StmtAsignar a = (StmtAsignar) s;
            linea("Asignar");
            nivel++;
            linea("nombre = " + a.getNombre());
            linea("valor:");
            nivel++;
            imprimirExpr(a.getValor());
            nivel -= 2;
            return;
        }
        if (s instanceof StmtEscribir) 
        {
            StmtEscribir e = (StmtEscribir) s;
            linea("Escribir");
            nivel++;
            imprimirExpr(e.getValor());
            nivel--;
            return;
        }
        if (s instanceof StmtBloque) 
        {
            StmtBloque b = (StmtBloque) s;
            linea("Bloque {");
            nivel++;
            for (Stmt h : b.getSentencias()) imprimirStmt(h);
            nivel--;
            linea("}");
            return;
        }
        if (s instanceof StmtSi) 
        {
            StmtSi si = (StmtSi) s;
            linea("Si");
            nivel++;
            linea("condicion:");
            nivel++;
            imprimirExpr(si.getCondicion());
            nivel--;
            linea("entonces:");
            nivel++;
            imprimirStmt(si.getEntonces());
            nivel--;
            if (si.getSino() != null) {
                linea("sino:");
                nivel++;
                imprimirStmt(si.getSino());
                nivel--;
            }
            nivel--;
            return;
        }
        if (s instanceof StmtPara) 
        {
            StmtPara p = (StmtPara) s;
            linea("Para");
            nivel++;
            linea("init:");
            nivel++;
            if (p.getInit() != null) imprimirStmt(p.getInit()); else linea("(vacío)");
            nivel--;
            linea("condicion:");
            nivel++;
            if (p.getCondicion() != null) imprimirExpr(p.getCondicion()); else linea("(vacía)");
            nivel--;
            linea("post:");
            nivel++;
            if (p.getPost() != null) imprimirExpr(p.getPost()); else linea("(vacío)");
            nivel--;
            linea("cuerpo:");
            nivel++;
            imprimirStmt(p.getCuerpo());
            nivel--;
            nivel--;
            return;
        }
        linea(s.toString());
    }
    
    private void imprimirExpr(Expr e) 
    {
        if (e == null) 
        { 
            linea("(expresión nula)"); 
            return; 
        }

        if (e instanceof ExprNumero) 
        {
            linea("Numero: " + ((ExprNumero) e).getLexema());
            return;
        }
        if (e instanceof ExprCadena) 
        {
            linea("Cadena: \"" + ((ExprCadena) e).getLexema() + "\"");
            return;
        }
        if (e instanceof ExprIdent) 
        {
            linea("Ident: " + ((ExprIdent) e).getNombre());
            return;
        }
        if (e instanceof ExprUnaria) 
        {
            ExprUnaria u = (ExprUnaria) e;
            linea("Unaria (" + u.getOp() + ")");
            nivel++;
            imprimirExpr(u.getExpr());
            nivel--;
            return;
        }
        if (e instanceof ExprBinaria) 
        {
            ExprBinaria b = (ExprBinaria) e;
            linea("Binaria (" + b.getOp() + ")");
            nivel++;
            linea("izq:");
            nivel++;
            imprimirExpr(b.getIzq());
            nivel--;
            linea("der:");
            nivel++;
            imprimirExpr(b.getDer());
            nivel -= 2;
            return;
        }
        if (e instanceof ExprAsign) 
        {
            ExprAsign a = (ExprAsign) e;
            linea("Asign (expr)");
            nivel++;
            linea("nombre = " + a.getNombre());
            linea("valor:");
            nivel++;
            imprimirExpr(a.getValor());
            nivel -= 2;
            return;
        }
        if (e instanceof ExprParen) 
        {
            linea("Paren");
            nivel++;
            imprimirExpr(((ExprParen) e).getDentro());
            nivel--;
            return;
        }
        linea(e.toString());
    }
    
    private void linea(String s) 
    {
        for (int i = 0; i < nivel; i++) sb.append("  ");
        sb.append(s).append('\n');
    }
}
