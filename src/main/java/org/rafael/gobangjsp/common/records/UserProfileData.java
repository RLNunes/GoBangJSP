package org.rafael.gobangjsp.common.records;

import java.io.Serializable;

public record UserProfileData(
        String username,
        int age,
        String nationality,
        int wins,
        int losses,
        long timePlayed,
        String photoBase64
) implements Serializable {}
