package com.example.report.engine.controller;

import com.example.report.engine.model.ReportModel;
import com.example.report.engine.service.BirtReportService;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/report")
public class BirtReportController {
    private static final Logger log = Logger.getLogger(BirtReportController.class);

    @Autowired
    private BirtReportService reportService;



    @RequestMapping(method = RequestMethod.POST, value = "/{name}")
    @ResponseBody
    @ApiOperation("Generate report")
    public void generateReport(HttpServletResponse response,
                               @PathVariable("name") String name,
                               @RequestParam("format") String format,
                               @RequestBody Map<String, Object> param) {
        log.info("Generate report: " + name + "; Format: " + format);
        reportService.exportReportBuffer(format,param,response,name);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/invoice")
    @ApiOperation("get external report")
    public void getExchangeInvoiceFile(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> param){
        log.info("Get report from +" + request.getRequestURL());
        reportService.getReportFromAPI(request, response, param);
    }





}
