package org.rafael.gobangjsp.common;

import java.io.Serializable;

public record UserSearchResult(
    String username,
    String nationality,
    boolean isLoggedIn,
    String photo,
    String fullName
) implements Serializable {}

