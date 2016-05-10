package controllers.reporting;

import com.kritter.api.entity.reporting.ReportingEntity;

public class Metric {

    public static void populateDemandMetric(ReportingEntity re){
            re.setTotal_impression(true);
            re.setFr(true);
            re.setTotal_win(true);
            re.setWtr(true); /**/
            re.setTotal_bidValue(true);
            re.setTotal_win_bidValue(true);
            re.seteCPW(true);/**/
            re.seteIPW(true); /**/
            re.setTotal_csc(true);
            re.setBilledcsc(true);
            re.setRtr(true); /**/
            re.setTotal_click(true);
            re.setBilledclicks(true);
            re.setCtr(true);
            re.setProfitmargin(true); /**/
            re.setConversion(true);
            re.setClicksr(true); /**/
            re.setDemandCharges(true); /**/
            re.setNetworkrevenue(true);
            re.setExchangerevenue(true);
            re.setCpa_goal(true);
            re.seteCPC(true);/**/
            re.setBilledECPC(true);
            re.setEcpm(true); /**/
            re.setBilledECPM(true); /**/
            re.seteCPA(true); /**/
            re.setSupplyCost(true);
            re.setNetworkpayout(true);
            re.setExchangepayout(true);
            re.seteIPC(true);
            re.seteIPM(true); /**/
            re.setBilledEIPM(true);
            re.seteIPA(true);/**/
        }
        public static void populateSupplyMetric(ReportingEntity re){
            re.setTotal_request(true);
            populateDemandMetric(re);
        }
}
