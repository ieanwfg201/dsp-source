package models.pmp.display;

import models.uiutils.Path;
import scala.Option;
import com.kritter.api.entity.deal.PrivateMarketPlaceApiEntity;

import controllers.deal.routes;
import java.util.ArrayList;


public class PMPDisplay
{
    protected PrivateMarketPlaceApiEntity privateMarketPlaceApiEntity;

    public PMPDisplay(PrivateMarketPlaceApiEntity privateMarketPlaceApiEntity)
    {
        this.privateMarketPlaceApiEntity = privateMarketPlaceApiEntity;
    }

    public String getViewUrl() {
        return routes.DealController.info(privateMarketPlaceApiEntity.getDealId()).url();
    }

    public String getEditUrl() {
        return routes.DealController.edit(privateMarketPlaceApiEntity.getDealId()).url();
    }

    public String addPMPUrl(){
        return  routes.DealController.add().url();
    }

    public ArrayList<Path> getBreadCrumbPaths() {
        ArrayList<Path> trails= new ArrayList<Path>();
        trails.add(new Path("All Deals",routes.DealController.PMPListView().url()));
        trails.add(new Path(privateMarketPlaceApiEntity.getDealName(), getViewUrl()));

        return trails;
    }


    protected String destinationUrl = null;

    public String getDealName()
    {
        return this.privateMarketPlaceApiEntity.getDealName();
    }

    public String getDealId()
    {
        return this.privateMarketPlaceApiEntity.getDealId();
    }

    public String getAdIdList()
    {
        return this.privateMarketPlaceApiEntity.getAdIdList();
    }


    public void setDestination(String destination){
        this.destinationUrl = destination;
    }

}

