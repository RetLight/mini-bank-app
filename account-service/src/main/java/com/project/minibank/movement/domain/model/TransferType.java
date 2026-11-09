package com.project.minibank.movement.domain.model;

public enum TransferType {
    INTERNAL,
    EXTERNAL;

    public static TransferType forDestination(boolean destinationIsInternal) {
        return destinationIsInternal ? INTERNAL : EXTERNAL;
    }
}
