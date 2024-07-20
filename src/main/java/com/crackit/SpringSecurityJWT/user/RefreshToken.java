package com.crackit.SpringSecurityJWT.user;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Builder
@Data
@Document(collection = "RefreshToken")
public class RefreshToken {
private String id;
private String token;
private Instant expirationTime;
private User user;
}
