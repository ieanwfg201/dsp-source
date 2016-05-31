package com.kritter.kritterui.api.algo_models;

import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.kritter.constants.AlgoModel;
import com.kritter.constants.AlgoModelType;
import com.kritter.entity.algomodel.AlgoModelEntity;

public class AlgoModelHelper {
    private static final String CONTROL_A = String.valueOf((char) 1);
    private static final String CONTROL_B = String.valueOf((char) 2);

    public String createQueryString(AlgoModelEntity algoModelEntity){
        if(algoModelEntity != null){
            if(algoModelEntity.getAlgomodel() == AlgoModel.BIDDER.getCode() &&
                    algoModelEntity.getAlgomodelType() == AlgoModelType.GETALPHA.getCode()){
                return "select data as data from bidder_models";
            }
        }
        return null;
    }

    public void createData(String data, AlgoModelEntity algoModelEntity, 
            ArrayNode dataNode, ObjectMapper mapper){
        if(data != null && algoModelEntity != null){
            List<Integer> list = algoModelEntity.getAdId();
            if(list != null){
                HashMap<String, String> alphaMap = new HashMap<String, String>();
                String split[] = data.split("\n");
                String firstDelimiter = "a"+CONTROL_A;
                for(String str:split){
                    String[] firstSplit= str.split(firstDelimiter);
                    if(firstSplit.length ==2){
                        String secondSplit[] = firstSplit[1].split(CONTROL_B);
                        if(secondSplit.length == 2){
                            alphaMap.put(secondSplit[0], secondSplit[1]);
                        }
                    }
                }
                for(Integer i :algoModelEntity.getAdId()){
                    String iStr = String.valueOf(i);
                    if(alphaMap.get(iStr) != null){
                        ObjectNode dataObjNode = mapper.createObjectNode();
                        dataObjNode.put("adId",iStr);
                        dataObjNode.put("value",alphaMap.get(iStr));
                        dataNode.add(dataObjNode);
                    }
                }
            }
        }
    }

    public ArrayNode createColumnNode(ObjectMapper mapper){
        ArrayNode columnNode = mapper.createArrayNode();
        ObjectNode columnObjNode = mapper.createObjectNode();
        columnObjNode.put("title", "AdId");
        columnObjNode.put("field", "adId");
        columnObjNode.put("visible", true);
        columnNode.add(columnObjNode);
        ObjectNode columnObjNode1 = mapper.createObjectNode();
        columnObjNode1.put("title", "value");
        columnObjNode1.put("field", "value");
        columnObjNode1.put("visible", true);
        columnNode.add(columnObjNode1);
        return columnNode;
    }

}
