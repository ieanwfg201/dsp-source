package com.kritter.vast.vastversion;

import java.io.StringReader;
import java.util.LinkedList;

import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.Logger;

import com.kritter.constants.VideoBidResponseProtocols;
import com.kritter.entity.vast.normal.threedotzero.CreateVastNormalThreeDotZero;
import com.kritter.entity.vast.normal.twodotzero.CreateVastNormalTwoDotZero;
import com.kritter.entity.vast.wrapper.three_dot_zero.CreateVastWrapper;
import com.kritter.entity.vast.wrapper.two_dot_zero.CreateVastWrapperTwoDotZero;

public class CheckVastVersion {

	public static String enhanceVastResponse(String str,Logger logger,
			LinkedList<String> impressionsTrackers,String id){
		if(impressionsTrackers==null || impressionsTrackers.size()<1){
			return str;
		}
		VideoBidResponseProtocols vrp = checkVersion(str, logger);
		if(vrp==null){
			return null;
		}
		switch (vrp) {
		case VAST_2_0:
			return CreateVastNormalTwoDotZero.addImpressionsVastStr(str, logger, impressionsTrackers, id);
		case VAST_3_0:
			return CreateVastNormalThreeDotZero.addImpressionsVastStr(str, logger, impressionsTrackers, id);
		case VAST_2_0_WRAPPER:
			return CreateVastWrapperTwoDotZero.addImpressionsVastStr(str, logger, impressionsTrackers, id);
		case VAST_3_0_WRAPPER:
			return CreateVastWrapper.addImpressionsVastStr(str, logger, impressionsTrackers, id);
		default:
			break;
		}
		return null;
	}
	public static VideoBidResponseProtocols checkVersion(String str,Logger logger
			){
    	if(str==null || str.isEmpty()){
    		return null;
    	}
    	try{
    		Unmarshaller jaxbUnmarshaller = CheckVersionJaxbContext.getUnMarshallContext().createUnmarshaller();
    		StringReader reader = new StringReader(str);
    		CheckRootVersion vastRoot = (CheckRootVersion)jaxbUnmarshaller.unmarshal(reader);
    		if(vastRoot.getVersion() == null || vastRoot.getAd()==null){
    			return null;
    		}
    		if("2.0".equals(vastRoot.getVersion())){
    			if(vastRoot.getAd().getWrapper()==null){
    				return VideoBidResponseProtocols.VAST_2_0;
    			}
    			return VideoBidResponseProtocols.VAST_2_0_WRAPPER;
    		}else if("3.0".equals(vastRoot.getVersion())){
    			if(vastRoot.getAd().getWrapper()==null){
    				return VideoBidResponseProtocols.VAST_3_0;
    			}
    			return VideoBidResponseProtocols.VAST_3_0_WRAPPER;
    			
    		} 
    		//System.out.println(jaxbUnmarshaller.unmarshal(reader));
    		//return null;
    	}catch(Exception e){
    		logger.error(e.getMessage(),e);
    		//e.printStackTrace();
    		return null;
    	}

		return null;
	}
	
    public static CheckRootVersion unMarshallCheckRootVersionStr(String str,Logger  logger){
    	if(str==null || str.isEmpty()){
    		return null;
    	}
    	try{
    		Unmarshaller jaxbUnmarshaller = CheckVersionJaxbContext.getUnMarshallContext().createUnmarshaller();
    		StringReader reader = new StringReader(str);
    		CheckRootVersion vastRoot = (CheckRootVersion)jaxbUnmarshaller.unmarshal(reader);
    		System.out.println(vastRoot.getVersion());
    		System.out.println(vastRoot.getAd());
    		System.out.println(vastRoot.getAd().getWrapper());
    		//System.out.println(jaxbUnmarshaller.unmarshal(reader));
    		return vastRoot;
    		//return null;
    	}catch(Exception e){
    		logger.error(e.getMessage(),e);
    		//e.printStackTrace();
    		return null;
    	}
    }

   /*public static void main(String args[]){
    	long a = System.currentTimeMillis();
    	String s ="<?xml version=\"1.0\" encoding=\"UTF-8\"?><VAST version=\"3.0\">   <Ad id=\"011c3e84-ed0b-cb01-5655-a0ea66000010\">      <Wrapper>         <AdSystem version=\"2.0\"><![CDATA[011c3e84-ed0b-cb01-5655-a1b4d7000011]]></AdSystem>         <VASTAdTagURI><![CDATA[https://servedbyadbutler.com/vast.spark?setID=2107&ID=167500&pid=22779]]></VASTAdTagURI>         <Error><![CDATA[http://localhost/tevent/1/5/011c3e84-ed0b-cb01-5659-78bbba000002:3e/115617/10/871/3/108/-1/58/121/-1/aT0wLjAwNSxlPTAuMDA1/-1/-1/2/-1/1/2/3d14ccfb61de6a0d1262feb61d5a257?iid=AAAAAA&ttype=videoerror&tevent=[ERRORCODE]]]></Error>         <Impression id=\"011c3e84-ed0b-cb01-5659-78bbba000002:3e\"><![CDATA[http://localhost/csc/1/5/011c3e84-ed0b-cb01-5659-78bbba000002:3e/115617/10/871/3/108/-1/58/121/-1/aT0wLjAwNSxlPTAuMDA1/-1/-1/2/-1/1/2/3d14ccfb61de6a0d1262feb61d5a257?iid=AAAAAA]]></Impression>         <Creatives>            <Creative id=\"011c3e84-ed0b-cb01-5655-a0ea66000010\">               <Linear>                  <TrackingEvents>                     <Tracking event=\"firstQuartile\"><![CDATA[http://localhost/tevent/1/5/011c3e84-ed0b-cb01-5659-78bbba000002:3e/115617/10/871/3/108/-1/58/121/-1/aT0wLjAwNSxlPTAuMDA1/-1/-1/2/-1/1/2/3d14ccfb61de6a0d1262feb61d5a257?iid=AAAAAA&ttype=video&tevent=firstQuartile]]></Tracking>                     <Tracking event=\"midpoint\"><![CDATA[http://localhost/tevent/1/5/011c3e84-ed0b-cb01-5659-78bbba000002:3e/115617/10/871/3/108/-1/58/121/-1/aT0wLjAwNSxlPTAuMDA1/-1/-1/2/-1/1/2/3d14ccfb61de6a0d1262feb61d5a257?iid=AAAAAA&ttype=video&tevent=midpoint]]></Tracking>                     <Tracking event=\"thirdQuartile\"><![CDATA[http://localhost/tevent/1/5/011c3e84-ed0b-cb01-5659-78bbba000002:3e/115617/10/871/3/108/-1/58/121/-1/aT0wLjAwNSxlPTAuMDA1/-1/-1/2/-1/1/2/3d14ccfb61de6a0d1262feb61d5a257?iid=AAAAAA&ttype=video&tevent=thirdQuartile]]></Tracking>                     <Tracking event=\"complete\"><![CDATA[http://localhost/tevent/1/5/011c3e84-ed0b-cb01-5659-78bbba000002:3e/115617/10/871/3/108/-1/58/121/-1/aT0wLjAwNSxlPTAuMDA1/-1/-1/2/-1/1/2/3d14ccfb61de6a0d1262feb61d5a257?iid=AAAAAA&ttype=video&tevent=complete]]></Tracking>                  </TrackingEvents>               </Linear>            </Creative>         </Creatives>      </Wrapper>   </Ad></VAST>";
    	LinkedList<String> impressionsTrackers = new LinkedList<String>();
    	impressionsTrackers.add("imp1");
    	impressionsTrackers.add("imp23");
    	System.out.println(CheckVastVersion.enhanceVastResponse(s, null, impressionsTrackers, "yahoo"));
    	long b = System.currentTimeMillis();
    	System.out.println(b-a);
    	
    }*/

}
