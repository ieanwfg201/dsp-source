package com.kritter.constants;

import java.util.HashMap;
import java.util.Map;

public enum POSTIMPRESSION_EVENT_URL_PREFIX {

        TRACKING_URL_FROM_THIRD_PARTY("/trk/"),
        THIRD_PARTY_CLICK_ALIAS_URL("/tclk/"),
        CLICK("/clk/"),
        CSC("/csc/"),
        WIN_NOTIFICATION("/win/"),
        WIN_API_NOTIFICATION("/winapi/"),
        CONVERSION_FEEDBACK("/conv/"),
        INT_EXCHANGE_WIN("/excwin/"),
        COOKIE_BASED_CONV_JS("/cnv_cky"),
        MACRO_CLICK("/macroclk"),
        TEVENT("/tevent"),
        BEVENT("/bevent"),
        USR("/usr"),
        NOFRDP("/nofrdp"),
        USERSYNC("/usersync");

        private String urlIdentifierPrefix;

        private POSTIMPRESSION_EVENT_URL_PREFIX(String urlIdentifierPrefix){
            this.urlIdentifierPrefix = urlIdentifierPrefix;
        }

        public String getUrlIdentifierPrefix(){
            return this.urlIdentifierPrefix;
        }

}
