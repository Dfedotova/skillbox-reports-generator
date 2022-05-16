package com.example.application.data.service;

import com.example.application.data.entity.SampleReport;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleReportRepository extends JpaRepository<SampleReport, Integer> {

}

