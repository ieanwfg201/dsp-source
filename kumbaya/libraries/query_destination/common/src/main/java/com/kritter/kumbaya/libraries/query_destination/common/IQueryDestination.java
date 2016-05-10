package com.kritter.kumbaya.libraries.query_destination.common;

import com.kritter.api.entity.reporting.ReportingEntity;
import com.kritter.kumbaya.libraries.data_structs.common.KumbayaReportingConfiguration;

public interface IQueryDestination {
    void execute(ReportingEntity reportingEntity, KumbayaReportingConfiguration kumbayaReportingConfiguration);
}
