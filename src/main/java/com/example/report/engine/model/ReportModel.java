package com.example.report.engine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportModel {
    private byte[] fileToByte;
    private String strFileName;
}
