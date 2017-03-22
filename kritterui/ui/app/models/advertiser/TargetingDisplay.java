package models.advertiser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.accounts.displays.AdvertiserDisplay;
import models.uiutils.Path;
import services.DataAPI;

import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.targeting_profile.Targeting_profile;
import com.kritter.constants.StatusIdEnum;

import controllers.advertiser.routes;


public class TargetingDisplay extends EntityDisplay {

    public Targeting_profile tp = null;
    private Account account;


    public TargetingDisplay(Targeting_profile tp) {
        this.tp = tp;
        this.account = DataAPI.getAccountByGuid(tp.getAccount_guid());
    }


    public String getName() {
        if (tp.getName() != null)
            return tp.getName();
        else
            return "Untitled Targeting Profile";
    }

    public String getStatus() {
        return tp.getStatus_id().getName();
    }

    public String getCreatedOn() {
        Date date = new Date(tp.getCreated_on());
        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        return dt.format(date);
    }

    public String getBrand_list() {

        return tp.getBrand_list();
    }

    public String getModel_list() {
        return tp.getModel_list();
    }

    public String getOs_json() {
        return tp.getOs_json();
    }

    public String getBrowser_json() {
        return tp.getBrowser_json();
    }

    public String getCountry_list() {
        return tp.getCountry_json();
    }

    public String getCarrier_list() {
        return tp.getCarrier_json();
    }

    public String getState_list() {
        return tp.getState_json();
    }

    public String getCity_list() {
        return tp.getCity_json();
    }

    public String getZipcode_file_id_set() {
        return tp.getZipcode_file_id_set();
    }

    public String getSite_list() {
        return tp.getSite_list();
    }


    public String getCategory_list() {
        return tp.getSite_list();
    }


    public String getCustom_ip_file_id_set() {
        return tp.getCustom_ip_file_id_set();
    }


    public long getCreated_on() {
        return tp.getCreated_on();
    }

    public List<Path> getBreadCrumbPaths() {
        List<Path> paths = new AdvertiserDisplay(account).getBreadCrumbPaths();
        paths.add(new Path(getName(), "#"));
        return paths;
    }


    public String getAccountName() {
        return account.getName();
    }

    public String getEditUrl() {
        return routes.TargetingProfileController.edit(tp.getGuid(), tp.getAccount_guid()).url();
    }

    public String getViewUrl() {
        return routes.TargetingProfileController.view(tp.getGuid(), tp.getAccount_guid()).url();
    }

    public String getAccountGuid() {
        return account.getGuid();
    }

    public String getAudienceInc() {
        return tp.getAudience_inc();
    }

    public String getAudienceEnc() {
        return tp.getAudience_exc();
    }

    public String getAudiencePackage() {
        return tp.getAudience_package();
    }
}
