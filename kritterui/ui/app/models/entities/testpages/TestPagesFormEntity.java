package models.entities.testpages;

public class TestPagesFormEntity {
    private String site_guid="";
    private String ua="Mozilla/5.0 (SymbianOS/9.4; Series60/5.0 Nokia5230/21.0.004; Profile/MIDP-2.1 Configuration/CLDC-1.1 ) " +
    		"AppleWebKit/525 (KHTML, like Gecko) Version/3.0 BrowserNG/7.2.5.2 3gpp-gba";
    private String ip="";
    private String fmt="xhtml";
    private String ver="s2s_1";
    private String exchange_endpoint="";
    private String exchange_request="{\"id\": \"530c0000e4b0f4f34ba6d060\",\"app\": {\"id\": \"883f6169027dac7494180eff95b1817b368b7183\",\"cat\":[\"IAB9\", \"IAB9­7\"],\"publisher\":{\"id\":\"14066e92840202cf28d34e74a371a662eb0af324\",\"cat\": [\"IAB9\"]}},\"tmax\": 120,\"badv\": [],\"bcat\": [\"IAB23\", \"IAB24\", \"IAB25\", \"IAB26\"], \"imp\": [{\"id\": \"1\",\"bidfloor\": 0.03,\"instl\": 1,\"banner\":{\"topframe\": 1,\"w\": 768,\"battr\": [10],\"h\": 1024,\"pos\": 7}}],\"device\": {\"os\": \"android\",\"model\": \"test\",\"geo\": {\"metro\": \"523\",\"ext\": {\"locale\": \"en_us\"}, \"country\": \"ZA\"},\"osv\": \"6.1\",\"js\": 1,\"dnt\": 0,\"dpidmd5\": \"b6b72250b5bd18d629c96be065fcef63\",\"ip\": \"196.11.239.0\",\"connectiontype\": 2,\"dpidsha1\":\"c8db27379aae8f8677eb8d5aa45a7e23fb47eb3c\",\"ua\": \"Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19\",\"devicetype\": 1,\"make\": \"Apple\",\"ext\": {\"idfa\": \"320369A9­3A4F­4B12­BD87­6B47E09DF3D3\"}},\"at\":2}";
    private String exchange_type="RESPONSE";
    private String click_endpoint="";

    public String getExchange_type() {
        return exchange_type;
    }
    public void setExchange_type(String exchange_type) {
        this.exchange_type = exchange_type;
    }
    public String getSite_guid() {
        return site_guid;
    }
    public void setSite_guid(String site_guid) {
        this.site_guid = site_guid.trim();
    }
    public String getUa() {
        return ua;
    }
    public void setUa(String ua) {
        this.ua = ua.trim();
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip.trim();
    }
    public String getFmt() {
        return fmt;
    }
    public void setFmt(String fmt) {
        this.fmt = fmt;
    }
    public String getVer() {
        return ver;
    }
    public void setVer(String ver) {
        this.ver = ver;
    }
    public String getExchange_endpoint() {
        return exchange_endpoint;
    }
    public void setExchange_endpoint(String exchange_endpoint) {
        this.exchange_endpoint = exchange_endpoint.trim();
    }
    public String getExchange_request() {
        return exchange_request;
    }
    public void setExchange_request(String exchange_request) {
        this.exchange_request = exchange_request.trim();
    }
    public String getClick_endpoint() {
        return click_endpoint;
    }
    public void setClick_endpoint(String click_endpoint) {
        this.click_endpoint = click_endpoint;
    }

}
