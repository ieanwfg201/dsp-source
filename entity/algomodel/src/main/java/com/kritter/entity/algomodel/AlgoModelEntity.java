package com.kritter.entity.algomodel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import com.kritter.constants.AlgoModel;
import com.kritter.constants.AlgoModelType;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class AlgoModelEntity
{

    @Getter@Setter
    private int algomodel = AlgoModel.BIDDER.getCode();
    @Getter@Setter
    private int algomodelType = AlgoModelType.GETALPHA.getCode();
    @Getter@Setter
    private LinkedList<Integer> advId = null;
    @Getter@Setter
    private LinkedList<Integer> campaignId = null;
    @Getter@Setter
    private List<Integer> adId = null; 
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode toJson(){
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static AlgoModelEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        return getObject(objectMapper,str);
    }
    public static AlgoModelEntity getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        AlgoModelEntity entity = objectMapper.readValue(str, AlgoModelEntity.class);
        return entity;

    }
}
