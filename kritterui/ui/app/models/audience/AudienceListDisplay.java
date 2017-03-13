package models.audience;

import com.kritter.api.entity.account.Account;
import controllers.audience.routes;
import models.accounts.displays.AccountDisplay;
import models.accounts.displays.AdvertiserDisplay;
import models.advertiser.ListDisplay;
import models.uiutils.Path;
import scala.Option;

import java.util.List;

/**
 * Created by zhangyan on 3/3/17.
 */
public class AudienceListDisplay extends ListDisplay {
    public String getName(){
        return "Audience List";
    }

    public String getPath(){
        return routes.AudienceController.list(account.getGuid()).url();
    }
    public AudienceListDisplay(Account account) {
        super(account);
    }

    public List<Path> getBreadCrumbPaths() {
        List<Path> paths = new AdvertiserDisplay(account).getBreadCrumbPaths();
        paths.add(new Path(getName(), getPath()));
        return paths;
    }

    public String addAudienceUrl(){
        return routes.AudienceController.add(account.getGuid()).url();
    }
}
