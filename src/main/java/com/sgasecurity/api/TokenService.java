package com.sgasecurity.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenService {
    @Autowired
    TokenRepository tokenRepository;
    public long getTokensCount() {
        return tokenRepository.count();
    }

    public Token getById(long id) {
        return tokenRepository.findById(id);
    }

    public Token getByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public Token saveToken(Token token)
    {
        return tokenRepository.save(token);
    }

    public List<Token> getAllTokens() {
        return tokenRepository.findAll();
    }
}
