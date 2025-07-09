package org.rafael.gobangjsp.common.records;

import java.io.Serializable;

public record ResponseStatus(String status, String operation, String message) implements Serializable {}

