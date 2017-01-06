package com.kritter.postimpression.urlreader.impl;

import com.kritter.constants.UserConstant;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Workflow;
import com.kritter.entity.postimpression.entity.Request;
import com.kritter.postimpression.urlreader.PostImpressionEventUrlReader;

import javax.servlet.http.HttpServletRequest;

/**
 * This class reads a Usr URL and extracts segment information from url.
 */
public class UsrUrlReader implements PostImpressionEventUrlReader {

    private String name;
    private String usrInfoParameterName;

    public UsrUrlReader(String name,String usrInfoParameterName) {
        this.name = name;
        this.usrInfoParameterName = usrInfoParameterName;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void decipherPostImpressionUrl(Request postImpressionRequest,
                                          String requestURI,
                                          Context context) throws Exception {

        if(null == postImpressionRequest || null== requestURI)
            throw new Exception("The supplied request object is null inside decipherPostImpressionUrl " +
                                "of UsrUrlReader");

        HttpServletRequest httpServletRequest = (HttpServletRequest)context.getValue(Workflow.CONTEXT_REQUEST_KEY);
        String retargetingSegment = httpServletRequest.getParameter(this.usrInfoParameterName);
        if(null == retargetingSegment) {
            throw new Exception("retargetingSegment is null inside UsrUrlReader");
        }
        String tmpretargetingSegment = retargetingSegment.trim();
        if("".equals(tmpretargetingSegment)) {
            throw new Exception("retargetingSegment is empty inside UsrUrlReader");
        }
        try{
            int seg = UserConstant.retargeting_segment_default;
            seg = Integer.parseInt(tmpretargetingSegment);
            postImpressionRequest.setRetargetingSegment(seg);
        }catch(NumberFormatException nfe){
            throw new Exception(nfe);
        }
    }
}
