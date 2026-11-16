package com.example.scan_machine_api.asset;

/** One area = (factory, positionAA); tagged = machines that have an RFID tag. */
public record AreaDto(String factory, String positionAA, int total, int tagged) {
}
