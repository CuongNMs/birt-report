package com.example.report.engine.service;


import com.example.report.engine.model.ReportModel;
import com.fis.ftu.dynamic.report.DynamicReportTask;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@Service
public class BirtReportService{

    private static final String EXTENSION_REPORT_FILE_NAME = ".rptdesign";

    @Autowired
    @Qualifier("oracleSource")
    private DataSource dataSource;

    @Value("${reports.relative.path}")
    private String reportsPath;
    @Value("${images.relative.path}")
    private String imagesPath;

    @Autowired
    private RestTemplate restTemplate;


    public void getReportFromAPI(HttpServletRequest request, HttpServletResponse response, Map<String, Object> param){
        StringBuilder requestUrl = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();
        if(queryString != null){
            requestUrl.append("?").append(queryString);
        }
        ResponseEntity<ReportModel> reportModel = restTemplate.postForEntity(requestUrl.toString(), param, ReportModel.class);
        byte[] data = reportModel.getBody().getFileToByte();
        ByteArrayOutputStream dataFile = null;
        try{
            dataFile.write(reportModel.getBody().getFileToByte(), 0, data.length);
            dataFile.writeTo(response.getOutputStream());
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void exportReportBuffer(String docType, Map birtParams, HttpServletResponse response, String reportName) {
        ByteArrayOutputStream dataFile = null;
        DynamicReportTask reportTask = null;
        Connection connection = null;
        InputStream templateDesignStream = null;
        try {
            String templateReportFilePath = reportsPath + reportName + EXTENSION_REPORT_FILE_NAME;
            templateDesignStream = new ByteArrayInputStream(Files.readAllBytes(Paths.get(templateReportFilePath)));
            reportTask = new DynamicReportTask(templateDesignStream);
            connection = dataSource.getConnection();
            reportTask.setConnection(connection, true);
            switch (docType.toUpperCase()) {
                case "PDF":
                    dataFile = reportTask.pdfExport(birtParams);
                    break;
                case "HTML":
                    dataFile = reportTask.htmlExport(birtParams);
                    break;
            }
            if(dataFile != null){
                dataFile.writeTo(response.getOutputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (templateDesignStream != null) {
                try {
                    templateDesignStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (reportTask != null) {
                try {
                    reportTask.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
