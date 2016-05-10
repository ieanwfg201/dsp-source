package com.kritter.abstraction.cache.abstractions;

import com.kritter.abstraction.cache.interfaces.ISecondaryIndex;
import com.kritter.abstraction.cache.utils.exceptions.InitializationException;
import com.kritter.abstraction.cache.utils.exceptions.UnSupportedOperationException;
import lombok.Getter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Date: 9/6/13
 * Class:
 */
//@RunWith(JUnit4.class)
public class TestQueryableCache
{
    @Getter
    private class DummySecondaryIndex implements ISecondaryIndex
    {
        private boolean allTargeted = false;
    }

    private static Logger logger = LoggerFactory.getLogger(TestQueryableCache.class);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test(expected = InitializationException.class)
    public void testConstructionNullLogger() throws InitializationException
    {
        new SampleQueryableCache(null, null);
    }

    @Test
    public void testInvalidSecIndexQuery() throws InitializationException
    {
        exception.expect(InitializationException.class);
        List<Class> secIndexList = new ArrayList<Class>();
        secIndexList.add(SampleQueryableCache.class);
        new SampleQueryableCache(secIndexList, logger);
    }

    /**
     * No secondary index but queryEntities called on secondary index
     * @throws UnSupportedOperationException
     * @throws InitializationException
     */
    @Test(expected = UnSupportedOperationException.class)
    public void testUnSuppSecIndex() throws UnSupportedOperationException, InitializationException
    {
        SampleQueryableCache cache = new SampleQueryableCache(null, logger);
        assertEquals(true, cache.getConstructionTime() != null);
        assertEquals(true, cache.getEntityCount() == 0);
        assertEquals(true, cache.getFailedEntityCount() == 0);
        cache.query(new SampleSecondaryIndex(10));
    }

    /**
     * Secondary index but query called on un-supported secondary index
     * @throws UnSupportedOperationException
     * @throws InitializationException
     */
    @Test(expected = UnSupportedOperationException.class)
    public void testUnSuppIndex2() throws UnSupportedOperationException, InitializationException
    {
        List<Class> secIndexList = new ArrayList<Class>();
        secIndexList.add(SampleSecondaryIndex.class);
        SampleQueryableCache cache = new SampleQueryableCache(secIndexList, logger);
        assertEquals(true, cache.getConstructionTime() != null);
        assertEquals(true, cache.getEntityCount() == 0);
        assertEquals(true, cache.getFailedEntityCount() == 0);

        cache.query(new DummySecondaryIndex());
    }

    /**
     * After querying on invalid index, whether things work fine
     * @throws UnSupportedOperationException
     * @throws InitializationException
     */
    @Test(expected = UnSupportedOperationException.class)
    public void testUnSuppQueryIncorrectQuerySecIndex() throws UnSupportedOperationException, InitializationException
    {
        List<Class> secIndexList = new ArrayList<Class>();
        secIndexList.add(SampleSecondaryIndex.class);
        SampleQueryableCache cache = new SampleQueryableCache(secIndexList, logger);

        cache.query(new DummySecondaryIndex());
        cache.query(1);
        assertEquals(true, cache.getConstructionTime() != null);
        assertEquals(true, cache.getEntityCount() == 0);
        assertEquals(true, cache.getFailedEntityCount() == 0);
    }

    /**
     * Job cannot query on a null secondary index
     * @throws UnSupportedOperationException
     * @throws InitializationException
     */
    @Test(expected = NullPointerException.class)
    public void testQueryNullSecIndex() throws UnSupportedOperationException, InitializationException
    {
        List<Class> secIndexList = new ArrayList<Class>();
        secIndexList.add(SampleSecondaryIndex.class);
        SampleQueryableCache cache = new SampleQueryableCache(secIndexList, logger);
        assertEquals(true, cache.getConstructionTime() != null);
        assertEquals(true, cache.getEntityCount() == 0);
        assertEquals(true, cache.getFailedEntityCount() == 0);
        DummySecondaryIndex dummySecondaryIndex = null;
        cache.query(dummySecondaryIndex);
    }

    /**
     * Job cannot query on a null primary index
     * @throws InitializationException
     */
    @Test(expected = NullPointerException.class)
    public void testQueryNullPrimaryIndex() throws InitializationException
    {
        List<Class> secIndexList = new ArrayList<Class>();
        secIndexList.add(SampleSecondaryIndex.class);
        SampleQueryableCache cache = new SampleQueryableCache(secIndexList, logger);
        assertEquals(true, cache.getConstructionTime() != null);
        assertEquals(true, cache.getEntityCount() == 0);
        assertEquals(true, cache.getFailedEntityCount() == 0);
        Integer id = null;
        cache.query(id);
    }

    @Test
    public void testQueryPrimaryIndex()
    {
        try
        {
            SampleQueryableCache cache = new SampleQueryableCache(null, logger);
            SampleQueryableEntity entity = new SampleQueryableEntity(1, 0L, null, 10);
            assertEquals(true, cache.getConstructionTime() != null);
            assertEquals(true, cache.getEntityCount() == 0);
            assertEquals(true, cache.getFailedEntityCount() == 0);

            // Add to cache and check entity count
            cache.add(entity);
            assertEquals(true, cache.getEntityCount() == 1);

            // Add the same entity. should not increase the entity count
            cache.add(entity);
            assertEquals(true, cache.getEntityCount() == 1);

            // remove entity id that does not exist
            cache.remove(11112);
            assertEquals(true, cache.getEntityCount() == 1);

            // fetch and check equality of entity
            SampleQueryableEntity fetchedEntity = cache.query(1);
            assertEquals(entity, fetchedEntity);

            // Remove from cache and check entity count
            cache.remove(entity.getId());
            assertEquals(true, cache.getEntityCount() == 0);

            // Check return on entity not being present
            fetchedEntity = cache.query(1);
            assertEquals(true, fetchedEntity == null);
        }
        catch (InitializationException initExcp)
        {
            assertTrue("Initialization exception should not be thrown here", false);
        }
    }

    @Test
    public void testQuerySecIndex() throws InitializationException, UnSupportedOperationException
    {
        List<Class> secIndexList = new ArrayList<Class>();
        secIndexList.add(SampleSecondaryIndex.class);
        SampleQueryableCache cache = new SampleQueryableCache(secIndexList, logger);
        SampleQueryableEntity entity = new SampleQueryableEntity(1, 0L, null, 10);

        // Add to cache
        cache.add(entity);
        assertEquals(true, cache.getEntityCount() == 1);

        // Add the same entity. should not increase the entity count
        cache.add(entity);
        assertEquals(true, cache.getEntityCount() == 1);

        // remove entity id that does not exist
        cache.remove(11112);
        assertEquals(true, cache.getEntityCount() == 1);

        SampleQueryableEntity fetchedEntity = cache.query(1);
        assertEquals(entity, fetchedEntity);

        // Remove from cache
        cache.remove(entity.getId());
        assertEquals(true, cache.getEntityCount() == 0);

        // Check return on entity not being present while querying on secondary index
        Set<Integer> fetchedEntitySet = cache.query(new SampleSecondaryIndex(10));
        assertEquals(0, fetchedEntitySet.size());

        fetchedEntitySet = cache.query(new SampleSecondaryIndex(11112));
        assertEquals(true, (fetchedEntitySet == null || fetchedEntitySet.size() == 0));

        // Second Remove from cache
        cache.remove(entity.getId());
        assertEquals(true, cache.getEntityCount() == 0);

        // Check return on entity not being present
        fetchedEntity = cache.query(1);
        assertEquals(true, fetchedEntity == null);
        cache.destroy();
    }

    @Test
    public void testQuerySecIndexAllTargeted() throws InitializationException, UnSupportedOperationException
    {
        List<Class> secIndexList = new ArrayList<Class>();
        secIndexList.add(SampleSecondaryIndex.class);
        secIndexList.add(SampleSecondaryIndex2.class);
        SampleQueryableCache cache = new SampleQueryableCache(secIndexList, logger);

        // broad targeted entry
        SampleQueryableEntity entity = new SampleQueryableEntity(1, 0L, null, 10);

        List<Integer> tempList = new ArrayList<Integer>();
        tempList.add(10);
        tempList.add(20);
        tempList.add(30);
        SampleQueryableEntity entity2 = new SampleQueryableEntity(2, 0L, tempList, 20);

        cache.add(entity);
        cache.add(entity2);
        assertEquals(2, cache.getEntityCount());

        // query on secondary index should return broad targeted also
        Set<Integer> entitySet = cache.query(new SampleSecondaryIndex2(10));
        assertEquals(2, entitySet.size());

        // remove the broad targeted entity
        cache.remove(1);

        // query on secondary index should return only the specific one
        entitySet = cache.query(new SampleSecondaryIndex2(10));
        assertEquals(1, entitySet.size());
    }
}
