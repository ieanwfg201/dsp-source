package com.kritter.kritterui.api.payout_threshold;

import java.sql.ResultSet;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

public class PayoutCrudHelper {
    public static void fillGlobalValues(ObjectMapper mapper, ArrayNode dataNode, ResultSet rset) throws Exception{
                ObjectNode dataObjNode = mapper.createObjectNode();
                dataObjNode.put("keyname", rset.getString("name"));
                dataObjNode.put("value", rset.getFloat("value"));
                dataNode.add(dataObjNode);
    }
    public static ArrayNode createGlobalColumnNode(ObjectMapper mapper){
        ArrayNode columnNode = mapper.createArrayNode();
        ObjectNode demandpartnerObjNode = mapper.createObjectNode();
        demandpartnerObjNode.put("title", "keyname");
        demandpartnerObjNode.put("field", "keyname");
        demandpartnerObjNode.put("visible", true);
        columnNode.add(demandpartnerObjNode);
        ObjectNode ecpmObjNode = mapper.createObjectNode();
        ecpmObjNode.put("title", "value");
        ecpmObjNode.put("field", "value");
        ecpmObjNode.put("visible", true);
        columnNode.add(ecpmObjNode);
        return columnNode;
    }
}
