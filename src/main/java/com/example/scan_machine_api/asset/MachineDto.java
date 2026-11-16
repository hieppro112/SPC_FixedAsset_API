package com.example.scan_machine_api.asset;

/** codeRFID is null when the machine has no tag yet. */
public record MachineDto(String machineCode, String faName, String floor, String codeRFID) {
}
