package com.kritter.abstraction.cache.abstractions;

import com.kritter.abstraction.cache.entities.SecondaryIndexMap;
import com.kritter.abstraction.cache.interfaces.IEntity;
import com.kritter.abstraction.cache.interfaces.IQueryable;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import com.kritter.abstraction.cache.interfaces.ISecondaryIndexWrapper;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.ProcessingException;
import com.kritter.abstraction.cache.utils.exceptions.UnSupportedOperationException;
import lombok.Getter;
import org.json.simple.JSONObject;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Date: 6-June-2013<br></br>
 * Class: Abstract Queryable cache is an entity based queryable cache which provides:
 * <ul>
 *     <li>Addition and removal of entities</li>
 *     <li>Querying on primary and secondary indices</li>
 *     <li>Stats are published on the number of entities and the indices</li>
 * </ul>
 *  <ul><b>Expectations from the concrete implementation:</b>
 *     <li>The secondary index key list provided should contain the Secondary index class names and not any wrappers. Same would be used while querying</li>
 *  </ul>
 */
public abstract class AbstractQueryableCache<I, E extends IEntity<I>> implements IQueryable<I, E>
{
    protected Map<I, E> primaryIndex = new ConcurrentHashMap<I, E>();
    protected Map<Class, SecondaryIndexMap<I>> secIndexMapping;
    private Set<Class> supportedIndexTypes;
    private boolean usesSecIndices = false;
    @Getter private Date constructionTime;
    private static Logger logger;

    /**
     * Constructor for a Query-able class. Derived class is expected to call
     * this at construction time (obviously)
     * Verifies if the provided class are of the permissible secondaryIndex class type
     * Constructs and initializes the data structures
     * @param secIndexKeyClassList List of supported 'Class's for the secondary index keys
     * @param log Initialized logger
     */
    public AbstractQueryableCache(List<Class> secIndexKeyClassList, Logger log) throws InitializationException
    {
        if(log == null)
            throw new InitializationException("Logger cannot be null");

        logger = log;
        // Construct the secondary index structures only if necessary
        if(secIndexKeyClassList != null && secIndexKeyClassList.size() > 0)
        {
            secIndexMapping = new ConcurrentHashMap<Class, SecondaryIndexMap<I>>();
            usesSecIndices = true;
            supportedIndexTypes = new HashSet<Class>();
            SecondaryIndexMap<I> secondaryIndexMap;
            for(Class secIndexClass : secIndexKeyClassList)
            {
                // Verify if ISecondaryIndex is a class(superclass or super interface) of the provided class
                if(!(ISecondaryIndex.class.isAssignableFrom(secIndexClass)))
                {
                    logger.error("Provided class: {} is not of class type ISecondaryIndex",secIndexClass.getName());
                    throw new InitializationException("Provided class: " + secIndexClass.getName() + " is not of class type ISecondaryIndex");
                }

                secondaryIndexMap = new SecondaryIndexMap<I>();
                secIndexMapping.put(secIndexClass, secondaryIndexMap);
                supportedIndexTypes.add(secIndexClass);
            }
        }
        constructionTime = new Date();
    }

    /**
     * Query on secondary index and return the Set of Primary Key entity ids
     * Note: We are making an assumption that the caller is only interested in the Set of ids at this moment
     * and is comfortable with the fact that the entities against these ids might be altered independently
     * @param indexKey Secondary index key
     * @return Set of primary index keys
     * @throws UnSupportedOperationException Thrown if secondary indexing or the secondary index type is not supported
     */
    @Override
    public Set<I> query(ISecondaryIndex indexKey) throws UnSupportedOperationException
    {
        Class indexKeyClassName = indexKey.getClass();
        if(!usesSecIndices)
            throw new UnSupportedOperationException("No secondary indices supported by ICache: " + this.getName());

        if(!this.supportsIndexKeyType(indexKeyClassName))
            throw new UnSupportedOperationException("Key type: " + indexKeyClassName.getName() + " is not supported by ICache: " + this.getName());

        Set<I> returnSet = (secIndexMapping.get(indexKeyClassName)).getPrimaryIndexSet(indexKey);
        if(returnSet == null)
            return null;
        return Collections.unmodifiableSet(returnSet);
    }

    /**
     * Querying on the primary index
     * @param primaryIndexKey Primary index key for the entity
     * @return Entity matching the index key if there is a match and null otherwise
     */
    @Override
    public E query(I primaryIndexKey)
    {
        // Querying on Primary Index
        return primaryIndex.get(primaryIndexKey);
    }

    /**
     * Add the entity to the cache if it is a newer version. Update the secondary indices to remove the older
     * entries and then update the secondary index to reflect the new value. Then add to the primary index
     * @param entity Entity to be added
     */
    @Override
    public void add(E entity)
    {
        E oldEntity = primaryIndex.get(entity.getId());

        // Additional to secondary indices
        if(usesSecIndices)
        {
            for(Class className : supportedIndexTypes)
            {
                SecondaryIndexMap<I> secIndexMap = secIndexMapping.get(className);
                // remove entries for the old entity state
                // Note that we could have used remove for doing this without any harm
                // except for the fact that we would not be able to warn against false deletes
                // which is done in remove method
                // and we do not want to iterate through the supportedIndexTypes twice
                if(oldEntity != null)
                {
                    ISecondaryIndexWrapper wrapper = getSecondaryIndexKey(className, oldEntity);
                    Set<ISecondaryIndex> oldSecIndexSet = wrapper.getSecondaryIndexSet();
                    boolean isAllTargeted = wrapper.isAllTargeted();
                    secIndexMap.remove(oldSecIndexSet, oldEntity.getId(), isAllTargeted);
                }

                ISecondaryIndexWrapper wrapper = getSecondaryIndexKey(className, entity);
                Set<ISecondaryIndex> secIndexSet = wrapper.getSecondaryIndexSet();
                boolean isAllTargeted = wrapper.isAllTargeted();
                secIndexMap.add(secIndexSet, entity.getId(), isAllTargeted);
            }
        }
        // Add to primary index
        primaryIndex.put(entity.getId(), entity);
    }

    /**
     * Remove the entity from the primary index and corresponding mappings in the secondary index
     * @param entityId Entity Id to be removed from the indices
     */
    @Override
    public void remove(I entityId)
    {
        // Get the existing entity object and remove on the basis of the existing entity object
        E oldEntity = primaryIndex.get(entityId);

        // addition to primary index is done as the last step during index building
        // So, if the entity does not exist in the primary index, it does not exist in the cache
        if(oldEntity == null)
        {
            // Example: in cases where some application updated 'update_time' of the entity more than once
            // after the entity was de-activated
            logger.warn("Entity id: {} does not exist in ICache: {}. Hence cannot delete",entityId, this.getName());
            return;
        }

        primaryIndex.remove(oldEntity.getId());

        if(usesSecIndices)
        {
            for(Class className : supportedIndexTypes)
            {
                SecondaryIndexMap<I> secIndexMap = secIndexMapping.get(className);
                ISecondaryIndexWrapper wrapper = getSecondaryIndexKey(className, oldEntity);
                Set<ISecondaryIndex> secIndexSet = wrapper.getSecondaryIndexSet();
                boolean isAllTargeted = wrapper.isAllTargeted();
                secIndexMap.remove(secIndexSet, oldEntity.getId(), isAllTargeted);
            }
        }
    }

    /**
     * Clean up the held up resources
     */
    @Override
    public void destroy()
    {
        primaryIndex.clear();
        primaryIndex = null;
        if(usesSecIndices)
        {
            for(Class className : supportedIndexTypes)
            {
                secIndexMapping.get(className).clear();
            }
            secIndexMapping.clear();
            secIndexMapping = null;
        }

        try
        {
            this.cleanUp();
        }
        catch(ProcessingException procExcp)
        {
            throw new RuntimeException("Cleaning up resources failed in ICache: " + this.getName(), procExcp);
        }
    }

    private boolean supportsIndexKeyType(Class objClass) throws UnSupportedOperationException
    {
        return this.supportedIndexTypes.contains(objClass);
    }

    @Override
    public int getEntityCount()
    {
        return primaryIndex.size();
    }

    @Override
    public String getSecondaryIndexCount()
    {
        if(!usesSecIndices)
            return "{}";
        JSONObject jsonObj = new JSONObject();
        for(Class className: supportedIndexTypes)
            jsonObj.put(className, secIndexMapping.get(className).toString());
        return jsonObj.toString();
    }

    // Give a chance to child class to clean up in case it holds additional data structures
    protected abstract void cleanUp() throws ProcessingException;
    public abstract ISecondaryIndexWrapper getSecondaryIndexKey(Class className, E entity);

    public Collection<E> getAllEntities() {
        return primaryIndex.values();
    }
}
