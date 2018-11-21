/* begin_generated_IBM_copyright_prolog                             */
/*                                                                  */
/* This is an automatically generated copyright prolog.             */
/* After initializing,  DO NOT MODIFY OR MOVE                       */
/* **************************************************************** */
/* THIS SAMPLE CODE IS PROVIDED ON AN "AS IS" BASIS. IBM MAKES NO   */
/* REPRESENTATIONS OR WARRANTIES, EXPRESS OR IMPLIED, CONCERNING    */
/* USE OF THE SAMPLE CODE, OR THE COMPLETENESS OR ACCURACY OF THE   */
/* SAMPLE CODE. IBM DOES NOT WARRANT UNINTERRUPTED OR ERROR-FREE    */
/* OPERATION OF THIS SAMPLE CODE. IBM IS NOT RESPONSIBLE FOR THE    */
/* RESULTS OBTAINED FROM THE USE OF THE SAMPLE CODE OR ANY PORTION  */
/* OF THIS SAMPLE CODE.                                             */
/*                                                                  */
/* LIMITATION OF LIABILITY. IN NO EVENT WILL IBM BE LIABLE TO ANY   */
/* PARTY FOR ANY DIRECT, INDIRECT, SPECIAL OR OTHER CONSEQUENTIAL   */
/* DAMAGES FOR ANY USE OF THIS SAMPLE CODE, THE USE OF CODE FROM    */
/* THIS [ SAMPLE PACKAGE,] INCLUDING, WITHOUT LIMITATION, ANY LOST  */
/* PROFITS, BUSINESS INTERRUPTION, LOSS OF PROGRAMS OR OTHER DATA   */
/* ON YOUR INFORMATION HANDLING SYSTEM OR OTHERWISE.                */
/*                                                                  */
/* (C) Copyright IBM Corp. 2017, 2018  All Rights reserved.         */
/*                                                                  */
/* end_generated_IBM_copyright_prolog                               */
package com.ibm.streams.beam.sample.datahistorian.io.mh;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


/**
 * This is a utility class that takes the path to a Message Hub credentials
 * file, parses it, and stores configurations as a {@link HashMap} which can
 * be conveniently consumed by {@code KafkaIO}.
 */
public class MessageHubConfig extends HashMap<String, Object> {

    private static final String JAAS_TEMPLATE =
            "org.apache.kafka.common.security.plain.PlainLoginModule required"
                    + " username=\"USERNAME\" password=\"PASSWORD\";";
    private final String bootstrapServers;

    /**
     * @param credFilePath path to the Message Hub credentials file
     */
    public MessageHubConfig(String credFilePath) throws IOException, ParseException {
        super(6);

        // parse credentials file into a JSONObject
        JSONParser parser = new JSONParser();
        JSONObject cred = (JSONObject) parser.parse(new FileReader(credFilePath));
        JSONObject mhcred = (JSONObject) cred.get("messagehub");

        // concatenate servers into a comma-separated string
        bootstrapServers = String.join(",", (JSONArray) mhcred.get("kafka_brokers_sasl"));

        // add properties to the config
        put("bootstrap.servers", bootstrapServers);
        put("security.protocol","SASL_SSL");
        put("ssl.protocol","TLSv1.2");
        put("ssl.enabled.protocols","TLSv1.2");
        put("sasl.mechanism","PLAIN");

        String username = (String) mhcred.get("user");
        String password = (String) mhcred.get("password");
        String jaas = JAAS_TEMPLATE.replace("USERNAME", username)
                .replace("PASSWORD", password);
        put("sasl.jaas.config", jaas);
    }

    /**
     * @return comma-separated bootstrap servers
     */
    public String getBootstrapServers() {
        return bootstrapServers;
    }
}
