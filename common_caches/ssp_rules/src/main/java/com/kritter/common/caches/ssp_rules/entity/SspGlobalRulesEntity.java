package com.kritter.common.caches.ssp_rules.entity;

import com.kritter.abstraction.cache.interfaces.IUpdatableEntity;
import com.kritter.constants.SSPEnum;
import com.kritter.entity.ssp_rules.Rule;
import com.kritter.entity.ssp_rules.SSPGlobalRuleDef;
import lombok.Getter;
import lombok.ToString;
import org.codehaus.jackson.map.ObjectMapper;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class keeps global ssp rules used in context of ssp.
 */
@ToString
public class SspGlobalRulesEntity implements IUpdatableEntity<Integer>
{


    @Getter
    private int id;
    private String rule_def;

    private final Timestamp updateTime;


    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Getter
    private SSPGlobalRuleDef sspGlobalRuleDef = null;

    public SspGlobalRulesEntity(int id,
                                        String rule_def,
                                        Timestamp updateTime) throws IOException
    {
        this.id = id;
        this.rule_def = rule_def;
        this.updateTime = updateTime;

        if(null != this.rule_def){
            sspGlobalRuleDef = SSPGlobalRuleDef.getObject(objectMapper, this.rule_def);
        }
    }
    
    private static class SSPComparator implements Comparator<Map.Entry<String, Double>>
    {
        @Override
        public int compare(Map.Entry<String, Double> first, Map.Entry<String, Double> second){
            double firstValue = first.getValue();
            double secondtValue = second.getValue();
            if(firstValue > secondtValue)
                return -1;
            else if(firstValue < secondtValue)
                return 1;
            else return 0;
        }
    }
    
    private class SSPEntry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;

        public SSPEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }

    public List<Map.Entry<String, Double>> getAvertiserInOrder(){
        List<Map.Entry<String, Double>> ll = new LinkedList<Map.Entry<String,Double>>();
        if(this.sspGlobalRuleDef != null){
           Map<String, Rule> dpaMap = sspGlobalRuleDef.getCountryMap().get(SSPEnum.COUNTRY_ALL.getCode()).getDpaMap();
           for(String str:dpaMap.keySet()){
               ll.add(new SSPEntry<String, Double>(str,dpaMap.get(str).getEcpm()));
           }
           SSPComparator sspComparator = new SSPComparator();
           Collections.sort(ll, sspComparator);        
        }
        return ll;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result
                + ((rule_def == null) ? 0 : rule_def.hashCode());
        result = prime * result
                + ((updateTime == null) ? 0 : updateTime.hashCode());
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
        SspGlobalRulesEntity other = (SspGlobalRulesEntity) obj;
        if (id != other.id)
            return false;
        if (rule_def == null) {
            if (other.rule_def != null)
                return false;
        } else if (!rule_def.equals(other.rule_def))
            return false;
        if (updateTime == null) {
            if (other.updateTime != null)
                return false;
        } else if (!updateTime.equals(other.updateTime))
            return false;
        return true;
    }
    @Override
    public Long getModificationTime()
    {
        return this.updateTime.getTime();
    }

    @Override
    public boolean isMarkedForDeletion()
    {
        return false;
    }

    @Override
    public Integer getId()
    {
        return this.id;
    }
    
}
