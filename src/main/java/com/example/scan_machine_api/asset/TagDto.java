package com.example.scan_machine_api.asset;

public record TagDto(String codeRFID, String machineCode, String factory, String positionAA) {
}
