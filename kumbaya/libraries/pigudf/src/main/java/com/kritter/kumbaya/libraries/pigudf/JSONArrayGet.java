package com.kritter.kumbaya.libraries.pigudf;

import java.io.IOException;

import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class JSONArrayGet extends EvalFunc<String> {
    private static final Logger LOG = LoggerFactory.getLogger(JSONArrayGet.class);
    public String exec(Tuple input) throws IOException {
        String defaultRet = "-1";
        try {
            if(input == null || (input.size()<2 && input.get(0)==null) ){
                return defaultRet;
            }
            defaultRet = input.get(0).toString();
            if(input.size() == 3){
                if(input.get(1) != null && input.get(2) != null){
                    String jsonString = input.get(1).toString();
                    int index = Integer.parseInt(input.get(2).toString());
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readValue(jsonString,JsonNode.class);
                    if(node.isArray() && node.size() > index){   
                        return node.get(index).getValueAsText();
                    }
                }
            }
            return defaultRet;
        }catch (ExecException e) {
            LOG.error(e.getMessage());
            return defaultRet;
        }
    }
}
