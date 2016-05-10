package com.kritter.entity.vast.wrapper.three_dot_zero;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.BeanValidationMode;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;

public class GetJaxBContext {
    private static JAXBContext context;
    private GetJaxBContext(){
    }
    public static synchronized JAXBContext getContext(){
        if(context == null){
            Map<String, BeanValidationMode> props = new HashMap<String, BeanValidationMode>();
            props.put(JAXBContextProperties.BEAN_VALIDATION_MODE, BeanValidationMode.NONE);
            try {
                context= JAXBContextFactory.createContext(new Class[] {VastWrapper.class}, null);
                return context;
            } catch (JAXBException e) {
                // TODO Auto-generated catch block
                return context;
            }
        }else{
            return context;
        }
    }
}
