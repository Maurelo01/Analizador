package com.mycompany.analizador.parser;

import com.mycompany.analizador.lexer.Token;
import java.util.List;

public class FlujoToken 
{
    private final List<Token> tokens;
    private int pos = 0;
    
    public FlujoToken(List<Token> tokens) 
    {
        this.tokens = tokens != null ? tokens : List.of();
    }
    
    public boolean esUltimo() 
    {
        return pos >= tokens.size();
    }
    
    public Token revisar() 
    {
        return esUltimo() ? null : tokens.get(pos);
    }
    
    public Token revisar(int k) 
    {
        int idx = pos + k;
        return (idx >= 0 && idx < tokens.size()) ? tokens.get(idx) : null;
    }
    
    public Token consumir() 
    {
        return esUltimo() ? null : tokens.get(pos++);
    }
    
    public boolean juntarLex(String... lexemas) 
    {
        Token t = revisar();
        if (t == null) return false;
        for (String lx : lexemas)
        {
            if (lx.equals(t.getLexema())) 
            { 
                consumir(); 
                return true; 
            }
        }
        return false;
    }
}
