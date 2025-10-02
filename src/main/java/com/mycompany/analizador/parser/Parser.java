package com.mycompany.analizador.parser;

import com.mycompany.analizador.lexer.TipoToken;
import com.mycompany.analizador.lexer.Token;
import java.util.ArrayList;
import java.util.List;

public class Parser
{
    private final FlujoToken flujo;
    private final List<String> errores = new ArrayList<>();
    
    private String ultimoLexConsumido = null;
    public Parser(List<Token> tokens) 
    {
        this.flujo = new FlujoToken(tokens);
    }
    
    public List<String> getErrores() 
    {
        return errores;
    }
    
    public Programa analizarPrograma() 
    {
        Programa programa = new Programa();

        while (!flujo.esUltimo()) 
        {
            Stmt sentencia = analizarSentencia();
            if (sentencia != null) 
            {
                programa.agregarSentencia(sentencia);
            }

            // Aceptado ; opcional como separador
            if (flujo.revisar() != null && ";".equals(flujo.revisar().getLexema())) 
            {
                flujo.consumir();
            }
            if (flujo.revisar() != null && flujo.revisar().getTipo() == TipoToken.PUNTUACION && "...".equals(flujo.revisar().getLexema())) 
            {
                flujo.consumir(); 
            }
            if (flujo.revisar() == null) break; 
        }

        return programa;
    }
    
    private Stmt analizarSentencia() 
    {
        Token t = flujo.revisar();
        if (t == null) return null;
        // 0) omitir
        if (t.getTipo() == TipoToken.PUNTUACION && "...".equals(t.getLexema()))
        {
            flujo.consumir();
            return null; 
        }
        // 1) Bloque
        if ("{".equals(t.getLexema())) 
        {
            return analizarBloque();
        }

        // 2) PARA 
        if (t.getTipo() == TipoToken.RESERVADA && ("PARA".equals(t.getLexema()) || "para".equals(t.getLexema()))) 
        {
            return analizarSentenciaPara();
        }

        // 3) SI ENTONCES 
        if (t.getTipo() == TipoToken.RESERVADA && ("SI".equals(t.getLexema()) || "si".equals(t.getLexema()))) 
        {
            return analizarSentenciaSi();
        }

        // 4) ESCRIBIR
        if (t.getTipo() == TipoToken.RESERVADA && ("ESCRIBIR".equals(t.getLexema()) || "escribir".equals(t.getLexema()))) 
        {
            flujo.consumir(); // ESCRIBIR
            if (!coincideLexema("(")) 
            {
                error(conMensaje(t, "Se esperaba '(' después de ESCRIBIR"));
                if (")".equals(String.valueOf(flujo.revisar() != null ? flujo.revisar().getLexema() : ""))) 
                {
                    flujo.consumir(); // tolerancia si venía ')'
                }
            }
            Expr valor = analizarExpresion();
            if (!coincideLexema(")")) 
            {
                error(conMensaje(flujo.revisar(), "Se esperaba ')' para cerrar ESCRIBIR"));
                sincronizarHasta(";", ")"); 
            }
            return new StmtEscribir(valor);
        }

        // 5) Asignacion IDENT = expresion
        if (t.getTipo() == TipoToken.IDENTIFICADOR) 
        {
            Token sig = flujo.revisar(1);

            // a) Asignación IDENT = 
            if (sig != null && "=".equals(sig.getLexema()))
            {
                String nombre = t.getLexema();
                flujo.consumir(); // IDENT
                flujo.consumir(); // =
                Expr valor = analizarAsignacion();
                // ; opcional
                if (flujo.revisar()!=null && ";".equals(flujo.revisar().getLexema())) flujo.consumir();
                return new StmtAsignar(nombre, valor);
            }

            Expr e = analizarExpresion(); 
            if (flujo.revisar()!=null && ";".equals(flujo.revisar().getLexema())) flujo.consumir();
            return new StmtExpr(e);
        }

        // 6) Error
        error(conMensaje(t, "Sentencia no reconocida cerca de '" + t.getLexema() + "'"));
        sincronizarHasta(";");
        return null;
    }
    
    private Stmt analizarBloque() 
    {
        flujo.consumir(); // consumir "{"
        StmtBloque bloque = new StmtBloque();
        while (true) 
        {
            Token t = flujo.revisar();
            if (t == null) 
            {
                error("Bloque sin cerrar: falta '}' (EOF)");
                break;
            }
            if ("}".equals(t.getLexema())) 
            {
                flujo.consumir(); // cerrar bloque
                break;
            }
            Stmt s = analizarSentencia();
            if (s != null) bloque.agregar(s);
            // aceptar ; como separador dentro del bloque
            if (flujo.revisar() != null && ";".equals(flujo.revisar().getLexema())) 
            {
                flujo.consumir();
            }
        }
        return bloque;
    }
    
    private Stmt analizarSentenciaSi() 
    {
        Token tSi = flujo.revisar(); // SI
        flujo.consumir();

        // ( condicion )
        if (!coincideLexema("(")) 
        {
            error(conMensaje(flujo.revisar(), "Se esperaba '(' después de SI"));
        }
        Expr condicion = analizarExpresion();
        if (!coincideLexema(")")) 
        {
            error(conMensaje(flujo.revisar(), "Se esperaba ')' para cerrar la condición de SI"));
            sincronizarHasta(";", "ENTONCES", "entonces", "{", ")");
        }

        // ENTONCES
        Token despues = flujo.revisar();
        if (!(despues != null && despues.getTipo() == TipoToken.RESERVADA && ("ENTONCES".equals(despues.getLexema()) || "entonces".equals(despues.getLexema())))) 
        {
            error(conMensaje(despues, "Se esperaba 'ENTONCES' después de la condición"));
        } 
        else 
        {
            flujo.consumir();
        }
        // Cuerpo del ENTONCES bloque o sentencia
        Stmt cuerpoEntonces;
        Token prox = flujo.revisar();
        if (prox != null && "{".equals(prox.getLexema()))
        {
            cuerpoEntonces = analizarBloque();
        } 
        else 
        {
            cuerpoEntonces = analizarSentencia();
            if (flujo.revisar() != null && ";".equals(flujo.revisar().getLexema())) 
            {
                flujo.consumir();
            }
        }
        // Viene SINO???
        Token tokSino = flujo.revisar();
        if (tokSino != null && tokSino.getTipo() == TipoToken.RESERVADA && ("SINO".equals(tokSino.getLexema()) || "sino".equals(tokSino.getLexema()))) 
        {
            flujo.consumir(); // SINO
            // Soporte para SINO SI  ENTONCES 
            Token talVezSi = flujo.revisar();
            if (talVezSi != null && talVezSi.getTipo() == TipoToken.RESERVADA && ("SI".equals(talVezSi.getLexema()) || "si".equals(talVezSi.getLexema()))) 
            {
                // Parsear un SI completo como el cuerpo del SINO
                Stmt cuerpoSino = analizarSentenciaSi();
                return new StmtSi(condicion, cuerpoEntonces, cuerpoSino);
            }
            // SINO con bloque o sentencia simple
            Stmt cuerpoSino;
            Token proxSino = flujo.revisar();
            if (proxSino != null && "{".equals(proxSino.getLexema()))
            {
                cuerpoSino = analizarBloque();
            } 
            else 
            {
                cuerpoSino = analizarSentencia();
                if (flujo.revisar() != null && ";".equals(flujo.revisar().getLexema())) 
                {
                    flujo.consumir();
                }
            }
            if (cuerpoSino == null) {
                error(conMensaje(flujo.revisar(), "Se esperaba una sentencia o bloque después de SINO"));
                cuerpoSino = new StmtBloque();
            }
            return new StmtSi(condicion, cuerpoEntonces, cuerpoSino);
        }
        // Sin SINO
        return new StmtSi(condicion, cuerpoEntonces);
    }
    
    
    private Stmt analizarSentenciaPara() 
    {
        Token tPara = flujo.revisar(); // PARA
        flujo.consumir();
        if (!coincideLexema("(")) 
        {
            error(conMensaje(flujo.revisar(), "Se esperaba '(' después de PARA"));
        }
        Stmt init = null;
        Token t = flujo.revisar();
        if (t != null && t.getTipo() == TipoToken.IDENTIFICADOR) 
        {
            // Intentar parsear una asignación nombre = expr
            String nombre = t.getLexema();
            flujo.consumir(); // IDENT
            if (!coincideLexema("=")) 
            {
                error(conMensaje(flujo.revisar(), "Se esperaba '=' en la inicialización de PARA"));
                sincronizarHasta(";", ")"); // saltar a fin de init
            } 
            else 
            {
                Expr eInit = analizarExpresion();
                init = new StmtAsignar(nombre, eInit);
            }
        }
        if (!coincideLexema(";")) 
        {
            error(conMensaje(flujo.revisar(), "Se esperaba ';' tras la inicialización de PARA"));
            sincronizarHasta(";", ")"); // intentar ubicar el siguiente campo
            if (coincideLexema(";")) 
            {
                // ok
            }
        }
        Expr condicion = null;
        t = flujo.revisar();
        if (t != null && !";".equals(t.getLexema())) 
        {
            condicion = analizarExpresion();
        }
        if (!coincideLexema(";")) 
        {
            error(conMensaje(flujo.revisar(), "Se esperaba ';' tras la condición de PARA"));
            sincronizarHasta(";", ")"); // intentar ubicar el siguiente campo
            if (coincideLexema(";")) 
            {
                // ok
            }
        }
        Expr post = null;
        t = flujo.revisar();
        if (t != null && !")".equals(t.getLexema()))
        {
            post = analizarExpresion();
        }
        if (!coincideLexema(")")) 
        {
            error(conMensaje(flujo.revisar(), "Se esperaba ')' para cerrar el encabezado de PARA"));
            sincronizarHasta(")", "{", ";");
            if (coincideLexema(")")) 
            {
                // ok
            }
        }
        Stmt cuerpo;
        Token prox = flujo.revisar();
        if (prox != null && "{".equals(prox.getLexema())) 
        {
            cuerpo = analizarBloque();
        } 
        else 
        {
            cuerpo = analizarSentencia();
            if (flujo.revisar() != null && ";".equals(flujo.revisar().getLexema())) 
            {
                flujo.consumir();
            }
        }
        return new StmtPara(init, condicion, post, cuerpo);
    }
    
    private Expr analizarExpresion() 
    {
        return analizarAsignacion();
    }
    
    private Expr analizarExprAd()  // exprAd := exprMul ( (+ | -) exprMul )*
    {
        Expr izquierda = analizarExprMul();
        while (coincideLexema("+", "-")) 
        {
            String op = ultimoLexemaConsumido();
            Expr derecha = analizarExprMul();
            izquierda = new ExprBinaria(izquierda, op, derecha);
        }
        return izquierda;
    }
    
    // exprMul := exprUn ( (* | / | %) exprUn )*
    private Expr analizarExprMul() 
    {
        Expr izquierda = analizarExprUn();
        String op;
        while ((op = leerOperadorMul()) != null) 
        {
            Expr derecha = analizarExprUn();
            izquierda = new ExprBinaria(izquierda, op, derecha);
        }
        return izquierda;
    }
    
    // exprUn := (+ | -) exprUn | primaria
    private Expr analizarExprUn() 
    {
        if (coincideLexema("!", "+", "-")) 
        {
            String op = ultimoLexemaConsumido();
            Expr expr = analizarExprUn();
            return new ExprUnaria(op, expr);
        }
        return analizarPrimaria();
    }
    
    // asignacion := IDENT = asignacion | exprOr
    private Expr analizarAsignacion() 
    {
        Token a = flujo.revisar();
        Token b = flujo.revisar(1);
        Token c = flujo.revisar(2);
        if (a != null && a.getTipo() == TipoToken.IDENTIFICADOR && b != null && "=".equals(b.getLexema()) && !(c != null && "=".equals(c.getLexema()))) 
        {
            String nombre = a.getLexema();
            flujo.consumir(); // IDENT
            flujo.consumir(); // '='
            Expr valor = analizarAsignacion(); // right-associative
            return new ExprAsign(nombre, valor);
        }
        return analizarExprOr();
    }
    
    // OR := AND ( '||' AND )*
    private Expr analizarExprOr()
    {
        Expr izq = analizarExprAnd();
        String op;
        while ((op = leerOperadorOr()) != null) 
        {
            Expr der = analizarExprAnd();
            izq = new ExprBinaria(izq, op, der);
        }
        return izq;
    }

    // AND := IGUALDAD ( '&&' IGUALDAD )*
    private Expr analizarExprAnd() 
    {
        Expr izq = analizarExprIgualdad();
        String op;
        while ((op = leerOperadorAnd()) != null) 
        {
            Expr der = analizarExprIgualdad();
            izq = new ExprBinaria(izq, op, der);
        }
        return izq;
    }

    // IGUALDAD := COMPARACION ( ('==' | '!=') COMPARACION )*
    private Expr analizarExprIgualdad()
    {
        Expr izq = analizarExprComparacion();
        String op;
        while ((op = leerOperadorIgualdad()) != null) 
        {
            Expr der = analizarExprComparacion();
            izq = new ExprBinaria(izq, op, der);
        }
        return izq;
    }

    // COMPARACION := AD ( (< | <= | > | >=) AD )*
    private Expr analizarExprComparacion() 
    {
        Expr izq = analizarExprAd();
        String op;
        while ((op = leerOperadorComparacion()) != null) 
        {
            Expr der = analizarExprAd();
            izq = new ExprBinaria(izq, op, der);
        }
        return izq;
    }
    
    // primaria := NUMERO | DECIMAL | CADENA | IDENTIFICADOR | "(" expresion ")"
    private Expr analizarPrimaria() 
    {
        Token t = flujo.revisar();
        if (t == null) 
        {
            error("Expresión incompleta (fin de archivo)");
            return new ExprNumero("0");
        }
        // "(" expresion ")"
        if ("(".equals(t.getLexema())) 
        {
            flujo.consumir();
            Expr e = analizarExpresion();
            if (!coincideLexema(")")) 
            {
                error(conMensaje(flujo.revisar(), "Se esperaba ')'"));
            }
            return new ExprParen(e);
        }
        // NUMERO o DECIMAL
        if (t.getTipo() == TipoToken.NUMERO || t.getTipo() == TipoToken.DECIMAL) 
        {
            flujo.consumir();
            return new ExprNumero(t.getLexema());
        }
        // CADENA
        if (t.getTipo() == TipoToken.CADENA) 
        {
            flujo.consumir();
            return new ExprCadena(t.getLexema());
        }
        // IDENTIFICADOR
        if (t.getTipo() == TipoToken.IDENTIFICADOR) 
        {
            flujo.consumir();
            return new ExprIdent(t.getLexema());
        }
        // Nada aplica entonces consumir y reportar para no ciclar
        error(conMensaje(t, "Token inesperado en expresión: '" + t.getLexema() + "'"));
        flujo.consumir();
        return new ExprNumero("0");
    }
    
    private boolean coincideLexema(String... lexemas) 
    {
        Token t = flujo.revisar();
        if (t == null) return false;
        for (String s : lexemas) 
        {
            if (s.equals(t.getLexema())) 
            {
                ultimoLexConsumido = t.getLexema();
                flujo.consumir();
                return true;
            }
        }
        return false;
    }
    
    private String ultimoLexemaConsumido() 
    {
        return ultimoLexConsumido;
    }
    
    private void error(String mensaje)
    {
        errores.add(mensaje);
    }
    
    private void sincronizarHasta(String... separadoresLexema) 
    {
        externo:
        while (flujo.revisar() != null) 
        {
            Token t = flujo.revisar();
            for (String s : separadoresLexema) 
            {
                if (s.equals(t.getLexema())) 
                {
                    break externo; // detenerse en el separador
                }
            }
            flujo.consumir(); // descartar hasta encontrar separador 
        }
    }
    
    private String conMensaje(Token t, String base) 
    {
        if (t == null) return base + " (EOF)";
        return String.format("%s @%d:%d", base, t.getFila(), t.getColumna());
    }
    
    private String leerOperadorOr() 
    {
        Token a = flujo.revisar(), b = flujo.revisar(1);
        if (a != null && b != null && "|".equals(a.getLexema()) && "|".equals(b.getLexema())) 
        {
            flujo.consumir(); flujo.consumir();
            return "||";
        }
        return null;
    }

    private String leerOperadorAnd() 
    {
        Token a = flujo.revisar(), b = flujo.revisar(1);
        if (a != null && b != null && "&".equals(a.getLexema()) && "&".equals(b.getLexema())) 
        {
            flujo.consumir(); flujo.consumir();
            return "&&";
        }
        return null;
    }
    
    private String leerOperadorMul() 
    {
        Token t = flujo.revisar();
        if (t == null) return null;
        String lx = t.getLexema();
        if ("*".equals(lx) || "/".equals(lx) || "%".equals(lx)) 
        {
            flujo.consumir();
            return lx;
        }
        return null;
    }

    private String leerOperadorIgualdad() 
    {
        Token a = flujo.revisar(), b = flujo.revisar(1);
        if (a != null && b != null) 
        {
            if ("=".equals(a.getLexema()) && "=".equals(b.getLexema())) 
            { 
                flujo.consumir(); flujo.consumir(); return "=="; 
            }
            if ("!".equals(a.getLexema()) && "=".equals(b.getLexema())) 
            { 
                flujo.consumir(); flujo.consumir(); return "!="; 
            }
        }
        return null;
    }

    private String leerOperadorComparacion() 
    {
        Token a = flujo.revisar(), b = flujo.revisar(1);
        if (a == null) return null;
        String la = a.getLexema();

        // <= , >=
        if (b != null) 
        {
            String lb = b.getLexema();
            if ("<".equals(la) && "=".equals(lb)) 
            { 
                flujo.consumir(); flujo.consumir(); return "<="; 
            }
            if (">".equals(la) && "=".equals(lb)) 
            { 
                flujo.consumir(); flujo.consumir(); return ">="; 
            }
        }
        // < , >
        if ("<".equals(la) || ">".equals(la)) 
        {
            flujo.consumir();
            return la;
        }
        return null;
    }
}
