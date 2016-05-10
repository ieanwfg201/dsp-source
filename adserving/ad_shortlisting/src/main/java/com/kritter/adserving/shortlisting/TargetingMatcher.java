package com.kritter.adserving.shortlisting;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.core.workflow.Context;

import java.util.Set;

/**
 * Interface defining the functionality of any shortlisting or targeting class.
 */
public interface TargetingMatcher {
    /**
     * Returns the name of the targeting matcher
     * @return name of this targeting matcher
     */
    public String getName();

    /**
     * The function defining the base functionality of any targeting matcher. It takes in a set of ads and gives out
     * the set of shortlisted ads
     * @param adIdSet Current eligible ads
     * @param request Request object
     * @param context Context object being maintained by the workflow for this request
     * @return Shortlisted ads
     */
    public Set<Integer> shortlistAds(Set<Integer> adIdSet, Request request, Context context);
}
