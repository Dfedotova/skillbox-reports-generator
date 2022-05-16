package com.example.application.data.service;

import com.example.application.data.entity.SampleContractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class SampleContractorService extends CrudService<SampleContractor, Integer> {
    private SampleContractorRepository repository;

    public SampleContractorService(@Autowired SampleContractorRepository repository) {
        this.repository = repository;
    }

    @Override
    protected SampleContractorRepository getRepository() {
        return repository;
    }
}
