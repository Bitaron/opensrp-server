package org.ei.drishti.reporting.controller;

import org.ei.drishti.common.domain.ANMIndicatorSummary;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.reporting.repository.ANMReportsRepository;
import org.ei.drishti.reporting.repository.ServicesProvidedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ReportDataController {
    private ServicesProvidedRepository servicesProvidedRepository;
    private ANMReportsRepository anmReportsRepository;
    private static final Logger logger = LoggerFactory.getLogger(ReportDataController.class);

    @Autowired
    public ReportDataController(ServicesProvidedRepository servicesProvidedRepository, ANMReportsRepository anmReportsRepository) {
        this.servicesProvidedRepository = servicesProvidedRepository;
        this.anmReportsRepository = anmReportsRepository;
    }

    @RequestMapping(value = "/report/submit", method = RequestMethod.POST)
    @ResponseBody
    public String submit(@RequestBody ReportingData reportingData) {
        logger.info("Reporting on: " + reportingData);
        if (reportingData.type().equals("serviceProvided")) {
            servicesProvidedRepository.save(reportingData.get("anmIdentifier"), reportingData.get("externalId"),
                    reportingData.get("indicator"), reportingData.get("date"), reportingData.get("village"), reportingData.get("subCenter"), reportingData.get("phc"));
        } else if (reportingData.type().equals("anmReportData")) {
            anmReportsRepository.save(reportingData.get("anmIdentifier"), reportingData.get("externalId"), reportingData.get("indicator"), reportingData.get("date"));
        }
        return "Success.";
    }

    @RequestMapping(value = "/report/fetch", method = RequestMethod.GET)
    @ResponseBody
    public List<ANMIndicatorSummary> getANMIndicatorSummaries(@RequestParam("anmIdentifier") String anmIdentifier) {
        return anmReportsRepository.fetchANMSummary(anmIdentifier);
    }
}
