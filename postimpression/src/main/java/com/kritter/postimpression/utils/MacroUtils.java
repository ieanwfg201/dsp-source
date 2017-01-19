package com.kritter.postimpression.utils;

import com.kritter.entity.postimpression.entity.Request;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import com.kritter.postimpression.macros.Macros;

import java.io.StringWriter;

public class MacroUtils {
    public static String replaceMacrosInLP(Template lpVelocityEngineTemplate, Request request) {
        VelocityContext context = new VelocityContext();

        for(Macros macro : Macros.values()) {
            String macroString = macro.getMacroString();
            String macroValue = macro.getMacroValue(request);
            if(macroValue == null) {
                macroValue = "";
            }
            context.put(macroString, macroValue);
        }

        StringWriter writer = new StringWriter();
        lpVelocityEngineTemplate.merge(context, writer);
        return writer.toString();
    }
}
