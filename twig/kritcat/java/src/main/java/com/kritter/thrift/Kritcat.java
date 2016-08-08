package com.kritter.thrift;

import com.kritter.adserving.thrift.struct.AdservingRequestResponse;
import com.kritter.adserving.thrift.struct.Impression;
import com.kritter.postimpression.thrift.struct.PostImpressionRequestResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TSimpleJSONProtocol;
import org.apache.commons.cli.*;

import java.io.*;

public class Kritcat {
    private static TDeserializer deserializer = new TDeserializer();
    private static Base64 decoder = new Base64(0);

    private static void decodeAndPrintLog(String line, int type) throws TException {
        line.trim();

        // Base 64 decode the line
        byte[] decoded = decoder.decode(line.getBytes());

        TBase tBase = null;
        if(type == 0) {
            tBase = new AdservingRequestResponse();
        } else if(type == 1) {
            tBase = new PostImpressionRequestResponse();
        }
        deserializer.deserialize(tBase, decoded);

        TSerializer serializer = new TSerializer(new TSimpleJSONProtocol.Factory());
        byte[] serialized = serializer.serialize(tBase);

        System.out.println(new String(serialized));
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("h", false, "Print the help for this application");
        options.addOption("p", false, "Read the log line as post impression log");
        options.addOption("r", false, "Read the log line as ad serving request response log");
        OptionBuilder.withArgName("f")
                .withLongOpt("f")
                .hasOptionalArgs()
                .hasArgs()
                .withDescription("Get the files to read from. Leave out the option in case of stdin");
        options.addOption(OptionBuilder.create());

        return options;
    }

    public static void printUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("kritcat", options);
    }

    public static void main(String[] args) throws Exception {
        Options options = getOptions();

        BasicParser parser = new BasicParser();
        CommandLine commandLine = parser.parse(options, args);

        if(commandLine.hasOption("h")) {
            printUsage(options);
            return;
        }

        int type = -1;
        if(commandLine.hasOption("p") && commandLine.hasOption("r")) {
            System.out.println("Cannot have both p and r options. Choose exactly one.");
            printUsage(options);
            return;
        } else if(commandLine.hasOption("r")) {
            type = 0;
        } else if(commandLine.hasOption("p")) {
            type = 1;
        } else {
            System.out.println("Must have exactly one of p or r options.");
            printUsage(options);
            return;
        }

        if(!commandLine.hasOption("f")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while((line = reader.readLine()) != null) {
                decodeAndPrintLog(line, type);
            }
        } else {
            String[] fileNames = commandLine.getOptionValues("f");
            for(int i = 0; i < fileNames.length; ++i) {
                BufferedReader reader = new BufferedReader(new FileReader(fileNames[i]));
                String line;
                while((line = reader.readLine()) != null) {
                    decodeAndPrintLog(line, type);
                }
            }
        }
    }
}
