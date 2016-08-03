package com.kritter.api_caller.core.model;

import com.kritter.api_caller.core.util.DBTableColumnValueWithType;
import lombok.Getter;
import java.util.LinkedList;

/**
 * This class represents request entity that is used to make a database query.
 */
public class DatabaseRequestEntity implements ApiRequestEntity
{
    @Getter
    private String query;
    @Getter
    private LinkedList<DBTableColumnValueWithType> columnsToSet;
    @Getter
    private String[] columnNamesToRead;
    @Getter
    private Boolean isQueryForModification;

    public DatabaseRequestEntity(String query,String[] columnNamesToRead,Boolean isQueryForModification)
    {
        this.query = query;
        this.columnsToSet = new LinkedList<DBTableColumnValueWithType>();
        this.columnNamesToRead = columnNamesToRead;
        this.isQueryForModification = isQueryForModification;
    }

    public void setColumnForPreparedStatement(DBTableColumnValueWithType dbTableColumnValueWithType)
    {
        this.columnsToSet.add(dbTableColumnValueWithType);
    }
}
