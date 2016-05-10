package controllers.reporting;

import java.util.LinkedList;

import com.kritter.api.entity.reporting.ReportingEntity;

public class AdvertiserHierarchy {
    public static void populate_size_gt_9(ReportingEntity re, String[] hierarchySplit){
        boolean isnotall = true;
        boolean clickable=false;
        if ("all".equals(hierarchySplit[8])) {
            isnotall =false;
            clickable=true;
        }else{
            isnotall = true;
            re.setDate_as_dimension(true);
        }
        if ("pub_id".equals(hierarchySplit[5])) {
            if ("site_id".equals(hierarchySplit[7])) {
                LinkedList<Integer> l = new LinkedList<Integer>();
                if(isnotall){   l.add(Integer.parseInt(hierarchySplit[8])); }
                re.setSiteId(l);
                re.setSiteId_clickable(clickable);
                Metric.populateSupplyMetric(re); 
                return;
            }
        }else if ("country_id".equals(hierarchySplit[5])) {
            if ("isp_id".equals(hierarchySplit[7])) {
                LinkedList<Integer> l = new LinkedList<Integer>();
                if(isnotall){   l.add(Integer.parseInt(hierarchySplit[8])); }
                re.setCountryCarrierId(l);
                re.setCountryCarrierId_clickable(clickable);
                Metric.populateDemandMetric(re); return;
            }
        }
    }
    public static void populate_size_gt_7(ReportingEntity re, String[] hierarchySplit){
        boolean isnotall = true;
        boolean clickable=false;
        if ("all".equals(hierarchySplit[6])) {
            isnotall =false;
            clickable=true;
        }else{
            isnotall = true;
            re.setDate_as_dimension(true);
        }
        if ("pub_id".equals(hierarchySplit[3])) {
            if ("site_id".equals(hierarchySplit[5])) {
                LinkedList<Integer> l = new LinkedList<Integer>();
                if(isnotall){   l.add(Integer.parseInt(hierarchySplit[6])); }
                re.setSiteId(l);
                re.setSiteId_clickable(clickable);
                Metric.populateSupplyMetric(re); 
                return;
            }
        }else if ("country_id".equals(hierarchySplit[3])) {
            if ("isp_id".equals(hierarchySplit[5])) {
                LinkedList<Integer> l = new LinkedList<Integer>();
                if(isnotall){   l.add(Integer.parseInt(hierarchySplit[6])); }
                re.setCountryCarrierId(l);
                re.setCountryCarrierId_clickable(clickable);
                Metric.populateDemandMetric(re); return;
            }
        }else if ("campaign_id".equals(hierarchySplit[3])) {
            if ("deviceOs_id".equals(hierarchySplit[5])) {
                LinkedList<Integer> l = new LinkedList<Integer>();
                if(isnotall){   l.add(Integer.parseInt(hierarchySplit[6])); }
                re.setDeviceOsId(l);
                re.setDeviceOsId_clickable(clickable);
                Metric.populateDemandMetric(re); return;
            }else  if ("browser_id".equals(hierarchySplit[5])) {
                LinkedList<Integer> l = new LinkedList<Integer>();
                if(isnotall){   l.add(Integer.parseInt(hierarchySplit[6])); }
                re.setBrowserId(l);
                re.setBrowserId_clickable(clickable);
                Metric.populateDemandMetric(re); return;
            }else if ("deviceManufacturer_id".equals(hierarchySplit[5])) {
                LinkedList<Integer> l = new LinkedList<Integer>();
                if(isnotall){   l.add(Integer.parseInt(hierarchySplit[6])); }
                re.setDeviceManufacturerId(l);
                re.setDeviceManufacturerId_clickable(clickable);
                Metric.populateSupplyMetric(re); return;
            }else if ("pub_id".equals(hierarchySplit[5])) {
                LinkedList<Integer> l = new LinkedList<Integer>();
                if(isnotall){   l.add(Integer.parseInt(hierarchySplit[6])); }
                re.setPubId(l);
                re.setPubId_clickable(clickable);
                Metric.populateDemandMetric(re); return;
            }else if ("country_id".equals(hierarchySplit[5])) {
                LinkedList<Integer> l = new LinkedList<Integer>();
                if(isnotall){   l.add(Integer.parseInt(hierarchySplit[6])); }
                re.setCountryId(l);
                re.setCountryId_clickable(clickable);
                Metric.populateDemandMetric(re);
            }else if ("ad_id".equals(hierarchySplit[5])) {
                LinkedList<Integer> l = new LinkedList<Integer>();
                if(isnotall){   l.add(Integer.parseInt(hierarchySplit[6])); }
                re.setAdId(l);
                re.setAdId_clickable(clickable);
                Metric.populateDemandMetric(re); 
                return;
            }
        }
    }

    public static void populate_size_gt_5(ReportingEntity re, String[] hierarchySplit,LinkedList<String> advList){
        re.setDate_as_dimension(false);
        advList.add("'"+hierarchySplit[2]+"'");
        re.setAdvertiserId(advList);
        boolean isnotall = true;
        boolean clickable=false;
        if ("all".equals(hierarchySplit[4])) {
            isnotall =false;
            clickable=true;
        }else{
            isnotall = true;
            re.setDate_as_dimension(true);
        }

        if ("stats".equals(hierarchySplit[3])) {
            re.setDate_as_dimension(true);
            Metric.populateDemandMetric(re); return;
        }else if ("deviceOs_id".equals(hierarchySplit[3])) {
            LinkedList<Integer> l = new LinkedList<Integer>();
            if(isnotall){   l.add(Integer.parseInt(hierarchySplit[4])); }
            re.setDeviceOsId(l);
            re.setDeviceOsId_clickable(clickable);
            Metric.populateDemandMetric(re); return;
        }else if ("isp_id".equals(hierarchySplit[3])) {
            LinkedList<Integer> l = new LinkedList<Integer>();
            if(isnotall){   l.add(Integer.parseInt(hierarchySplit[4])); }
            re.setCountryCarrierId(l);
            re.setCountryCarrierId_clickable(clickable);
            Metric.populateDemandMetric(re); return;
        }else if ("browser_id".equals(hierarchySplit[3])) {
            LinkedList<Integer> l = new LinkedList<Integer>();
            if(isnotall){   l.add(Integer.parseInt(hierarchySplit[4])); }
            re.setBrowserId(l);
            re.setBrowserId_clickable(clickable);
            Metric.populateDemandMetric(re); return;
        }else if ("deviceManufacturer_id".equals(hierarchySplit[3])) {
            LinkedList<Integer> l = new LinkedList<Integer>();
            if(isnotall){   l.add(Integer.parseInt(hierarchySplit[4])); }
            re.setDeviceManufacturerId(l);
            re.setDeviceManufacturerId_clickable(clickable);
            Metric.populateSupplyMetric(re); return;
        }else if ("country_id".equals(hierarchySplit[3])) {
            LinkedList<Integer> l = new LinkedList<Integer>();
            if(isnotall){   l.add(Integer.parseInt(hierarchySplit[4])); }
            re.setCountryId(l);
            re.setCountryId_clickable(clickable);
            Metric.populateDemandMetric(re);
        }else if ("pub_id".equals(hierarchySplit[3])) {
            LinkedList<Integer> l = new LinkedList<Integer>();
            if(isnotall){   l.add(Integer.parseInt(hierarchySplit[4])); }
            re.setPubId(l);
            re.setPubId_clickable(clickable);
            Metric.populateDemandMetric(re); return;
        }else if ("campaign_id".equals(hierarchySplit[3])) {
            LinkedList<Integer> l = new LinkedList<Integer>();
            if(isnotall){   l.add(Integer.parseInt(hierarchySplit[4])); }
            re.setCampaignId(l);
            re.setCampaignId_clickable(clickable);
            Metric.populateDemandMetric(re); 
            return;
        }else if ("ad_id".equals(hierarchySplit[3])) {
            LinkedList<Integer> l = new LinkedList<Integer>();
            if(isnotall){   l.add(Integer.parseInt(hierarchySplit[4])); }
            re.setAdId(l);
            re.setAdId_clickable(clickable);
            Metric.populateDemandMetric(re); 
            return;
        }else if ("site_id".equals(hierarchySplit[3])) {
            re.setPubId(new LinkedList<Integer>());
            LinkedList<Integer> l = new LinkedList<Integer>();
            if(isnotall){   l.add(Integer.parseInt(hierarchySplit[4])); }
            re.setSiteId(l);
            re.setSiteId_clickable(clickable);
            Metric.populateSupplyMetric(re); 
            return;
        }
    }

    public static void populate(ReportingEntity re, String type, String hierarchy){

        LinkedList<String> advList = new LinkedList<String>();
        if(null == hierarchy || "".equals(hierarchy)){
            re.setDate_as_dimension(false);
            re.setAdvertiserId(advList);
            re.setAdvertiserId_clickable(true);
            Metric.populateDemandMetric(re);
            return;
        }else{
            String[] hierarchySplit = hierarchy.split("/");
            int size = hierarchySplit.length;
            if(size >= 5){
                populate_size_gt_5(re, hierarchySplit, advList);
            }
            if(size >= 7){
                populate_size_gt_7(re, hierarchySplit);
            }
            if(size >= 9){
                populate_size_gt_9(re, hierarchySplit);
            }
            if(size == 3){
                re.setDate_as_dimension(true);
                advList.add("'"+hierarchySplit[2]+"'");
                re.setAdvertiserId(advList);
                Metric.populateDemandMetric(re); return;
            }
        }

    }

}
