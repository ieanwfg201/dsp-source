package com.kritter.entity.freqcap_entity;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import com.kritter.constants.FreqEventType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class FreqCap {
	@Getter@Setter
	private Map<FreqEventType, Set<FreqDef>> fDef;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
    }

    public JsonNode toJson(){
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static FreqCap getObject(String str) throws JsonParseException, JsonMappingException, IOException{

        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        return getObject(objectMapper,str);
    }
    public static FreqCap getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	FreqCap entity = objectMapper.readValue(str, FreqCap.class);
        return entity;

    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    /*public static void main(String args[]){
    	FreqCap fc = new FreqCap();
    	System.out.println(fc.toJson().toString());
    	Set<FreqDef> f = new HashSet<FreqDef>();
    	FreqDef  fd = new FreqDef();
    	fd.setDuration(FreqDuration.LIFE);
    	fd.setCount(20);
    	f.add(fd);
    	Map<FreqEventType, Set<FreqDef>> map = new HashMap<FreqEventType, Set<FreqDef>>();
    	map.put(FreqEventType.CLK, f);
    	fc.setFDef(map);
    	System.out.println(fc.toJson().toString());
    	
    }*/
}
