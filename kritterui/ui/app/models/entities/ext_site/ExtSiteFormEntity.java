package models.entities.ext_site;



public class ExtSiteFormEntity {
    private String exchange = null;
    private String osId = "ALL";
    
    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }
    public String getOsId() {
        return osId;
    }

    public void setOsId(String osId) {
        this.osId = osId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((exchange == null) ? 0 : exchange.hashCode());
        result = prime * result + ((osId == null) ? 0 : osId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ExtSiteFormEntity other = (ExtSiteFormEntity) obj;
        if (exchange == null) {
            if (other.exchange != null)
                return false;
        } else if (!exchange.equals(other.exchange))
            return false;
        if (osId == null) {
            if (other.osId != null)
                return false;
        } else if (!osId.equals(other.osId))
            return false;
        return true;
    }
    
}
