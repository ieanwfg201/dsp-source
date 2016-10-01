package com.kritter.vast.vastversion;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

public class CheckVersionJaxbContext {
    private static JAXBContext unMarshallcontext;
    private CheckVersionJaxbContext(){
    }
    public static synchronized JAXBContext getUnMarshallContext(){
        if(unMarshallcontext == null){
            try {
            	unMarshallcontext= JAXBContextFactory.createContext(new Class[] {CheckRootVersion.class}, null);
                return unMarshallcontext;
            } catch (JAXBException e) {
                // TODO Auto-generated catch block
                return unMarshallcontext;
            }
        }else{
            return unMarshallcontext;
        }
    }

}
