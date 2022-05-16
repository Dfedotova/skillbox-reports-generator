package com.example.application.data.service;

import com.example.application.data.entity.SampleReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class SampleReportService extends CrudService<SampleReport, Integer> {

    private SampleReportRepository repository;

    public SampleReportService(@Autowired SampleReportRepository repository) {
        this.repository = repository;
    }

    @Override
    protected SampleReportRepository getRepository() {
        return repository;
    }
}
