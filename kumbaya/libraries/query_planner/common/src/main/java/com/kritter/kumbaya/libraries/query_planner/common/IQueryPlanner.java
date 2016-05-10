package com.kritter.kumbaya.libraries.query_planner.common;

import com.kritter.api.entity.reporting.ReportingEntity;
import com.kritter.kumbaya.libraries.data_structs.common.KumbayaReportingConfiguration;
import com.kritter.kumbaya.libraries.data_structs.query_planner.KumbayaQueryPlanner;

public interface IQueryPlanner {
    public void convert(KumbayaQueryPlanner kQueryPlanner, KumbayaReportingConfiguration kReportingConfiguration, 
            ReportingEntity reportingEntity, boolean returnId);
    public void plan(KumbayaQueryPlanner kQueryPlanner, KumbayaReportingConfiguration kReportingConfiguration, ReportingEntity reportingEntity);
}
