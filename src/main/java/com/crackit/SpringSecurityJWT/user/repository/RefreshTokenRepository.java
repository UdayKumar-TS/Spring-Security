package com.crackit.SpringSecurityJWT.user.repository;


import com.crackit.SpringSecurityJWT.user.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken,String> {
Optional<RefreshToken> findByToken(String token);
}
