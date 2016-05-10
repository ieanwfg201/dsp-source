package controllers.reporting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.time.DateUtils;

import com.kritter.api.entity.reporting.ReportingEntity;
import com.kritter.constants.Frequency;
import com.kritter.constants.HygieneCategory;
import com.kritter.constants.SupplySourceTypeEnum;

public class HierarchyPopulator {
    public static void populate(ReportingEntity re, String type){
        if(type == null){
            return;
        }
        if("GLOBAL".equals(type)){
            populateGlobal(re);
        }
    }
    public static void populateFreq(ReportingEntity re, String startDate, String tz){
        if(re == null || startDate == null){
            return;
        }
        TimeZone.setDefault(TimeZone.getTimeZone(tz));
        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
        Date d = new Date();
        d = DateUtils.addDays(d, -1);
        String str =dfm.format(d);
        if(startDate.compareTo(str)<0){
            re.setFrequency(Frequency.DATERANGE);
        }else{
            re.setFrequency(Frequency.TODAY);
        }
    }
    public static void populateSupplySourceType(ReportingEntity re, String supplysourcetype){
        if(re == null || supplysourcetype == null){
            return;
        }
        if("NONE".equals(supplysourcetype)){
            return;
        }
        List<Integer> l = new LinkedList<Integer>();
        if(SupplySourceTypeEnum.WAP.getName().equals(supplysourcetype)){
            l.add(SupplySourceTypeEnum.WAP.getCode());
        }else if(SupplySourceTypeEnum.APP.getName().equals(supplysourcetype)){
            l.add(SupplySourceTypeEnum.APP.getCode());
        }
        re.setSupply_source_type(l);

    }
    public static void populateSiteHygiene(ReportingEntity re, String site_hygiene){
        if(re == null || site_hygiene == null){
            return;
        }
        if("NONE".equals(site_hygiene)){
            return;
        }
        List<String> l = new LinkedList<String>();
        if(HygieneCategory.FAMILY_SAFE.toString().equals(site_hygiene)){
            l.add("'["+HygieneCategory.FAMILY_SAFE.getCode()+"]'");
        }else if(HygieneCategory.MATURE.toString().equals(site_hygiene)){
            l.add("'["+HygieneCategory.MATURE.getCode()+"]'");
        }else if(HygieneCategory.PERFORMANCE.toString().equals(site_hygiene)){
            l.add("'["+HygieneCategory.PERFORMANCE.getCode()+"]'");
        }else if(HygieneCategory.PREMIUM.toString().equals(site_hygiene)){
            l.add("'["+HygieneCategory.PREMIUM.getCode()+"]'");
        }
        re.setSite_hygiene(l);

    }
    private static void populateGlobal(ReportingEntity re){
        if(re.getFrequency() == Frequency.YESTERDAY){
            re.setFrequency(Frequency.DATERANGE);
        }
        re.setDate_as_dimension(true);
        re.setTotal_request(true);
        re.setTotal_impression(true);
        re.setFr(true);
        re.setTotal_csc(true);
        re.setBilledcsc(true);
        re.setTotal_click(true);
        re.setBilledclicks(true);
        re.setCtr(true);
        re.setConversion(true);
        re.setTotal_win(true);
        re.setDemandCharges(true);
        re.setSupplyCost(true);
        re.seteIPM(true);
        re.setBilledEIPM(true);
        re.seteIPC(true);
        re.seteCPC(true);
        re.setBilledECPC(true);
        re.setEcpm(true);
        re.setBilledECPM(true);
    }
    private static void populateDemandMetric(ReportingEntity re){
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
    private static void populateSupplyMetric(ReportingEntity re){
        re.setTotal_request(true);
        populateDemandMetric(re);
    }
    public static void defaultOrderBy(ReportingEntity re, String hierarchy){
        if(re.isTotal_request() && !re.isDate_as_dimension()){
            re.setTotal_request_order_sequence(1);
            re.setOrder_by_desc(true);
        }
    }
    public static void populateOrderBy(ReportingEntity re, String orderBy, boolean orderByReverse, String hierarchy){
        if(orderBy == null){

            return;
        }
        String orderByTrim =  orderBy.trim();
        if("".equals(orderByTrim)){
            defaultOrderBy(re, hierarchy);
            return;
        }
        boolean orderSet = false;
        if("total_request_name".equals(orderBy)){
            re.setTotal_request_order_sequence(1);orderSet = true;
        }else if("total_impression_name".equals(orderBy)){
            re.setTotal_impression_order_sequence(1); orderSet = true;
        }else if("total_bidValue_name".equals(orderBy)){
            re.setTotal_bidValue_order_sequence(1);orderSet = true;
        }else if("total_click_name".equals(orderBy)){
            re.setTotal_click_order_sequence(1);orderSet = true;
        }else if("billedclicks_name".equals(orderBy)){
            re.setBilledclicks_order_sequence(1);orderSet = true;
        }else if("total_win_name".equals(orderBy)){
            re.setTotal_win_order_sequence(1);orderSet = true;
        }else if("total_win_bidValue_name".equals(orderBy)){
            re.setTotal_win_bidValue_order_sequence(1);orderSet = true;
        }else if("total_csc_name".equals(orderBy)){
            re.setTotal_csc_order_sequence(1);orderSet = true;
        }else if("billedcsc_name".equals(orderBy)){
            re.setBilledcsc_order_sequence(1);orderSet = true;
        }else if("conversion_name".equals(orderBy)){
            re.setConversion_order_sequence(1);orderSet = true;
        }else if("demandCharges_name".equals(orderBy)){
            re.setDemandCharges_order_sequence(1);orderSet = true;
        }else if("supplyCost_name".equals(orderBy)){
            re.setSupplyCost_order_sequence(1);orderSet = true;
        }else if("ctr_name".equals(orderBy)){
            re.setCtr_order_sequence(1);orderSet = true;
        }else if("fr_name".equals(orderBy)){
            re.setFr_order_sequence(1);orderSet = true;
        }else if("networkpayout_name".equals(orderBy)){
            re.setNetworkpayout_order_sequence(1);orderSet = true;
        }else if("exchangepayout_name".equals(orderBy)){
            re.setExchangepayout_order_sequence(1);orderSet = true;
        }else if("networkrevenue_name".equals(orderBy)){
            re.setNetworkrevenue_order_sequence(1);orderSet = true;
        }else if("exchangerevenue_name".equals(orderBy)){
            re.setExchangerevenue_order_sequence(1);orderSet = true;
        }
        if(orderSet){
            re.setOrder_by_desc(orderByReverse);
        }else{
            defaultOrderBy(re, hierarchy);
        }
    }
    public static void populate(ReportingEntity re, String type, String hierarchy){
        if("geo".equals(type)){
            GeoHierarchy.populate(re, type, hierarchy);
        }
        if("os".equals(type)){
            DeviceOsHierarchy.populate(re, type, hierarchy);
        }
        if("adv".equals(type)){
            AdvertiserHierarchy.populate(re, type, hierarchy);
        }
        if("pub".equals(type)){
            PublisherHierarchy.populate(re, type, hierarchy);
        }

    }

    public static String getTabs(String type, String hierarchy){
        if("geo".equals(type)){
            if(null == hierarchy || "".equals(hierarchy)){
                return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":1 }]";
            }else{
                String[] hierachySplit = hierarchy.split("/");
                int len = hierachySplit.length;
                if( ((len == 5 && !"all".equals(hierachySplit[4])) || (len == 7 && "all".equals(hierachySplit[6]))) 
                        &&
                        ("deviceOs_id".equals(hierachySplit[3])||
                                "browser_id".equals(hierachySplit[3]) || "deviceManufacturer_id".equals(hierachySplit[3]))){
                    return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":2 }, "
                            + "{\"name\":\"Advertiser\", \"fieldid\": \"adv_id\", \"default\":\"all\", \"depth\":3},"
                            + "{\"name\":\"Publisher\", \"fieldid\": \"pub_id\", \"default\":\"all\", \"depth\":3}]";
                }else if(((len == 5 && !"all".equals(hierachySplit[4])) ||(len == 7 && "all".equals(hierachySplit[6])))
                        && ("isp_id".equals(hierachySplit[3]))){
                    return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":2 }, "
                            + "{\"name\":\"Advertiser\", \"fieldid\": \"adv_id\", \"default\":\"all\", \"depth\":3},"
                            + "{\"name\":\"Publisher\", \"fieldid\": \"pub_id\", \"default\":\"all\", \"depth\":3}," 
                            + "{\"name\":\"Os\", \"fieldid\":\"deviceOs_id\", \"default\":\"all\", \"depth\":3},"
                            + "{\"name\":\"Browser\", \"fieldid\":\"browser_id\", \"default\":\"all\", \"depth\":3},"
                            + "{\"name\":\"Brand\", \"fieldid\":\"deviceManufacturer_id\", \"default\":\"all\", \"depth\":3}]";
                }else if(((len == 5 && !"all".equals(hierachySplit[4])) || (len == 7 && "all".equals(hierachySplit[6])))
                        && ("adv_id".equals(hierachySplit[3]))){
                    return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":2 }, "
                            + "{\"name\":\"Campaign\", \"fieldid\": \"campaign_id\", \"default\":\"all\", \"depth\":3}]";
                }else if(((len == 5 && !"all".equals(hierachySplit[4])) || (len == 7 && "all".equals(hierachySplit[6])))
                        && "pub_id".equals(hierachySplit[3])){
                    return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":2 }, "
                            + "{\"name\":\"Site\", \"fieldid\": \"site_id\", \"default\":\"all\", \"depth\":3}]";
                }else if(len == 7 && "adv_id".equals(hierachySplit[5]) && !"all".equals(hierachySplit[6])){
                    return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":4 }, "
                            + "{\"name\":\"Campaign\", \"fieldid\": \"campaign_id\", \"default\":\"all\", \"depth\":5}]";
                }else if(len == 7 && "pub_id".equals(hierachySplit[5]) && !"all".equals(hierachySplit[6])){
                    return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":4 }, "
                            + "{\"name\":\"Site\", \"fieldid\": \"site_id\", \"default\":\"all\", \"depth\":5}]";
                }else if(len == 7 && "campaign_id".equals(hierachySplit[5]) && !"all".equals(hierachySplit[6])){
                    return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":4 }, "
                            + "{\"name\":\"Ad\", \"fieldid\": \"ad_id\", \"default\":\"all\", \"depth\":5}]";
                }else if(len >= 7 ){
                    return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":6 }]";
                }

                return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":1 }, "
                + "{ \"name\":\"Carrier\", \"fieldid\":\"isp_id\" , \"default\":\"all\", \"depth\":1 },"
                + "{\"name\":\"Os\", \"fieldid\":\"deviceOs_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Browser\", \"fieldid\":\"browser_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Brand\", \"fieldid\":\"deviceManufacturer_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Advertiser\", \"fieldid\": \"adv_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Campaign\", \"fieldid\": \"campaign_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Ad\", \"fieldid\": \"ad_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Publisher\", \"fieldid\": \"pub_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Site\", \"fieldid\": \"site_id\", \"default\":\"all\", \"depth\":1}]";
            }
        }        
        if("adv".equals(type)){
            if(null == hierarchy || "".equals(hierarchy)){
                return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":1 }]";
            }else{
                String[] hierachySplit = hierarchy.split("/");
                int len = hierachySplit.length;
                if(((len == 5 && !"all".equals(hierachySplit[4])) || (len == 7 && "all".equals(hierachySplit[6])))
                        && "pub_id".equals(hierachySplit[3])){
                    return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":2 }, "
                            + "{\"name\":\"Site\", \"fieldid\": \"site_id\", \"default\":\"all\", \"depth\":3}]";
                }else if(((len == 5 && !"all".equals(hierachySplit[4])) || (len == 7 && "all".equals(hierachySplit[6])))
                        && "country_id".equals(hierachySplit[3])){
                    return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":2 }, "
                            + "{\"name\":\"Carrier\", \"fieldid\": \"isp_id\", \"default\":\"all\", \"depth\":3}]";
                }else if(((len == 5 && !"all".equals(hierachySplit[4])) || (len == 7 && "all".equals(hierachySplit[6])))
                        && "campaign_id".equals(hierachySplit[3])){
                    return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":2 }, "
                            + "{\"name\":\"Os\", \"fieldid\":\"deviceOs_id\", \"default\":\"all\", \"depth\":3},"
                            + "{\"name\":\"Browser\", \"fieldid\":\"browser_id\", \"default\":\"all\", \"depth\":3},"
                            + "{\"name\":\"Brand\", \"fieldid\":\"deviceManufacturer_id\", \"default\":\"all\", \"depth\":3},"
                            + "{\"name\":\"Carrier\", \"fieldid\": \"isp_id\", \"default\":\"all\", \"depth\":3},"
                            + "{\"name\":\"Country\", \"fieldid\": \"country_id\", \"default\":\"all\", \"depth\":3},"
                            + "{\"name\":\"Publisher\", \"fieldid\": \"pub_id\", \"default\":\"all\", \"depth\":3},"
                            + "{\"name\":\"Ad\", \"fieldid\": \"ad_id\", \"default\":\"all\", \"depth\":3}]";
                }else if(len == 7 && "pub_id".equals(hierachySplit[5]) && !"all".equals(hierachySplit[6])){
                    return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":4 }, "
                            + "{\"name\":\"Site\", \"fieldid\": \"site_id\", \"default\":\"all\", \"depth\":5}]";
                }else if(len == 7 && "country_id".equals(hierachySplit[5]) && !"all".equals(hierachySplit[6])){
                    return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":4 }, "
                            + "{\"name\":\"Carrier\", \"fieldid\": \"isp_id\", \"default\":\"all\", \"depth\":5}]";
                }else if(len >= 7){
                    return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":6 }]";
                }
                return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":1 }, "
                + "{ \"name\":\"Carrier\", \"fieldid\":\"isp_id\" , \"default\":\"all\", \"depth\":1 },"
                + "{\"name\":\"Os\", \"fieldid\":\"deviceOs_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Browser\", \"fieldid\":\"browser_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Brand\", \"fieldid\":\"deviceManufacturer_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Country\", \"fieldid\": \"country_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Campaign\", \"fieldid\": \"campaign_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Ad\", \"fieldid\": \"ad_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Publisher\", \"fieldid\": \"pub_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Site\", \"fieldid\": \"site_id\", \"default\":\"all\", \"depth\":1}]";
            }
        }
        if("pub".equals(type)){
            if(null == hierarchy || "".equals(hierarchy)){
                return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":1 }]";
            }else{
                String[] hierachySplit = hierarchy.split("/");
                int len = hierachySplit.length;
                if(((len == 5 && !"all".equals(hierachySplit[4]) || (len == 7  && "all".equals(hierachySplit[6]))))
                        && "country_id".equals(hierachySplit[3]) ){
                    return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":2 }, "
                            + "{ \"name\":\"Carrier\", \"fieldid\":\"isp_id\" , \"default\":\"all\", \"depth\":3 },"
                            + "{\"name\":\"Advertiser\", \"fieldid\": \"adv_id\", \"default\":\"all\", \"depth\":3},"
                            + "{\"name\":\"Campaign\", \"fieldid\": \"campaign_id\", \"default\":\"all\", \"depth\":3}]";
                }else if(((len == 5 && !"all".equals(hierachySplit[4])) || (len == 7  && "all".equals(hierachySplit[6])))
                        && "adv_id".equals(hierachySplit[3])){
                    return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":2 }, "
                            + "{\"name\":\"Campaign\", \"fieldid\": \"campaign_id\", \"default\":\"all\", \"depth\":3}]";
                }else if(((len == 5 && !"all".equals(hierachySplit[4])) || (len == 7  && "all".equals(hierachySplit[6])))
                        && "site_id".equals(hierachySplit[3])){
                    return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":2 }, "
                            + "{\"name\":\"Country\", \"fieldid\": \"country_id\", \"default\":\"all\", \"depth\":3},"
                            + "{\"name\":\"Advertiser\", \"fieldid\": \"adv_id\", \"default\":\"all\", \"depth\":3},"
                            + "{\"name\":\"Os\", \"fieldid\":\"deviceOs_id\", \"default\":\"all\", \"depth\":3},"
                            + "{\"name\":\"Browser\", \"fieldid\":\"browser_id\", \"default\":\"all\", \"depth\":3},"
                            + "{\"name\":\"Brand\", \"fieldid\":\"deviceManufacturer_id\", \"default\":\"all\", \"depth\":3},"
                            + "{ \"name\":\"Carrier\", \"fieldid\":\"isp_id\" , \"default\":\"all\", \"depth\":3 }]";
                }else if(len == 7 && "country_id".equals(hierachySplit[5]) && !"all".equals(hierachySplit[6])){
                    return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":4 }, "
                            + "{ \"name\":\"Carrier\", \"fieldid\":\"isp_id\" , \"default\":\"all\", \"depth\":5 },"
                            + "{\"name\":\"Advertiser\", \"fieldid\": \"adv_id\", \"default\":\"all\", \"depth\":5},"
                            + "{\"name\":\"Os\", \"fieldid\":\"deviceOs_id\", \"default\":\"all\", \"depth\":5},"
                            + "{\"name\":\"Browser\", \"fieldid\":\"browser_id\", \"default\":\"all\", \"depth\":5},"
                            + "{\"name\":\"Brand\", \"fieldid\":\"deviceManufacturer_id\", \"default\":\"all\", \"depth\":5}]";

                }else if(len == 7 && "campaign_id".equals(hierachySplit[5]) && !"all".equals(hierachySplit[6])){
                    return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":4 }, "
                            + "{\"name\":\"Ad\", \"fieldid\": \"ad_id\", \"default\":\"all\", \"depth\":5}]";
                }else if(len >= 7){
                    return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":6 }]";
                }
                return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":1 }, "
                + "{ \"name\":\"Carrier\", \"fieldid\":\"isp_id\" , \"default\":\"all\", \"depth\":1 },"
                + "{\"name\":\"Os\", \"fieldid\":\"deviceOs_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Browser\", \"fieldid\":\"browser_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Brand\", \"fieldid\":\"deviceManufacturer_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Advertiser\", \"fieldid\": \"adv_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Country\", \"fieldid\": \"country_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Campaign\", \"fieldid\": \"campaign_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Ad\", \"fieldid\": \"ad_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Site\", \"fieldid\": \"site_id\", \"default\":\"all\", \"depth\":1}]";
            }
        }
        if("os".equals(type)){
            if(null == hierarchy || "".equals(hierarchy)){
                return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":1 }]";
            }else{
                String[] hierachySplit = hierarchy.split("/");
                int len = hierachySplit.length;
                if(len == 5){
                    if("country_id".equals(hierachySplit[3]) && !"all".equals(hierachySplit[4])){
                        return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":2 }, "
                                + "{ \"name\":\"Carrier\", \"fieldid\":\"isp_id\" , \"default\":\"all\", \"depth\":3 }]";
                    }else if("adv_id".equals(hierachySplit[3]) && !"all".equals(hierachySplit[4])){
                        return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":2 }, "
                                + "{ \"name\":\"Campaign\", \"fieldid\":\"campaign_id\" , \"default\":\"all\", \"depth\":3 }]";
                    }else if("pub_id".equals(hierachySplit[3]) && !"all".equals(hierachySplit[4])){
                        return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":2 }, "
                                + "{ \"name\":\"Site\", \"fieldid\":\"site_id\" , \"default\":\"all\", \"depth\":3 }]";
                    }
                }
                if(len >= 5){
                    return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":4 }]";
                }
                return "[{ \"name\":\"Stats\", \"fieldid\":\"stats\" ,\"default\":\"all\", \"depth\":1 }, "
                + "{ \"name\":\"Carrier\", \"fieldid\":\"isp_id\" , \"default\":\"all\", \"depth\":1 },"
                + "{\"name\":\"Country\", \"fieldid\":\"country_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Browser\", \"fieldid\":\"browser_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Brand\", \"fieldid\":\"deviceManufacturer_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Advertiser\", \"fieldid\": \"adv_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Campaign\", \"fieldid\": \"campaign_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Ad\", \"fieldid\": \"ad_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Publisher\", \"fieldid\": \"pub_id\", \"default\":\"all\", \"depth\":1},"
                + "{\"name\":\"Site\", \"fieldid\": \"site_id\", \"default\":\"all\", \"depth\":1}]";            
            }
        }
        return null;
    }
}
