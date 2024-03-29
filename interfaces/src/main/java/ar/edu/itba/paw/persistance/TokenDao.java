package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenDao {

    Optional<Token> getByToken(String token);

    Optional<Token> getByUserId(long userId);

    Token create(User user, String token, LocalDateTime expiryDate);

    Token refresh(Token token, String newToken, LocalDateTime newExpiryDate);

    void delete(Token token);

    void deleteStaledTokens();
}
