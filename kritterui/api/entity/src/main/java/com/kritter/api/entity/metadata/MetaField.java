package com.kritter.api.entity.metadata;

public class MetaField {
    private int id = -1;
    private String name = null;
    private String entity_id_set = null;
    private String description = null;
    private String version = null;
    private String country_code = null;

    private String country_name = null;
    private String isp_name = null;
    private String data_source_name = null;
    private String isp_ui_name = null;
    private String parentName = null;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((country_code == null) ? 0 : country_code.hashCode());
        result = prime * result
                + ((country_name == null) ? 0 : country_name.hashCode());
        result = prime
                * result
                + ((data_source_name == null) ? 0 : data_source_name.hashCode());
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result
                + ((entity_id_set == null) ? 0 : entity_id_set.hashCode());
        result = prime * result + id;
        result = prime * result
                + ((isp_name == null) ? 0 : isp_name.hashCode());
        result = prime * result
                + ((isp_ui_name == null) ? 0 : isp_ui_name.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        result = prime * result + ((parentName == null) ? 0 : parentName.hashCode());
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
        MetaField other = (MetaField) obj;
        if (country_code == null) {
            if (other.country_code != null)
                return false;
        } else if (!country_code.equals(other.country_code))
            return false;
        if (country_name == null) {
            if (other.country_name != null)
                return false;
        } else if (!country_name.equals(other.country_name))
            return false;
        if (data_source_name == null) {
            if (other.data_source_name != null)
                return false;
        } else if (!data_source_name.equals(other.data_source_name))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (entity_id_set == null) {
            if (other.entity_id_set != null)
                return false;
        } else if (!entity_id_set.equals(other.entity_id_set))
            return false;
        if (id != other.id)
            return false;
        if (isp_name == null) {
            if (other.isp_name != null)
                return false;
        } else if (!isp_name.equals(other.isp_name))
            return false;
        if (isp_ui_name == null) {
            if (other.isp_ui_name != null)
                return false;
        } else if (!isp_ui_name.equals(other.isp_ui_name))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        if (parentName == null) {
            if (other.parentName != null)
                return false;
        } else if (!parentName.equals(other.parentName))
            return false;
        return true;
    }
    public String getEntity_id_set() {
        return entity_id_set;
    }
    public void setEntity_id_set(String entity_id_set) {
        this.entity_id_set = entity_id_set;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getCountry_code() {
        return country_code;
    }
    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getIsp_name() {
        return isp_name;
    }

    public void setIsp_name(String isp_name) {
        this.isp_name = isp_name;
    }

    public String getData_source_name() {
        return data_source_name;
    }

    public void setData_source_name(String data_source_name) {
        this.data_source_name = data_source_name;
    }

    public String getIsp_ui_name() {
        return isp_ui_name;
    }

    public void setIsp_ui_name(String isp_ui_name) {
        this.isp_ui_name = isp_ui_name;
    }
    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}
