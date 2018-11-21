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

import com.ibm.streams.beam.sample.datahistorian.DataHistorianOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.GenerateSequence;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.joda.time.Duration;

import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * This is a sample class that writes data into the IBM Cloud Message Hub using
 * Beam's native {@link KafkaIO}.
 */
public class Producer {

    public static void main(String args[]) throws IOException, ParseException {
        // Create options
        DataHistorianOptions options =
                PipelineOptionsFactory.fromArgs(args)
                        .withValidation()
                        .as(DataHistorianOptions.class);

        // Parse credentials file
        MessageHubConfig config = new MessageHubConfig(options.getCred());

        JSONParser parser = new JSONParser();
        String dataFilePath = "test/data/events.json";
        JSONArray data = (JSONArray) parser.parse(new FileReader(dataFilePath));

        // Run the producer
        options.setAppName("MessageHubWrite");
        Pipeline wp = Pipeline.create(options);

        // Add a source to the pipeline that emits 10 numbers every second.
        wp.apply("Source", GenerateSequence.from(0)
                .withRate(10, Duration.standardSeconds(1)))
          // Convert every number into a key-value pair using "beam" as the key.
          // This is necessary as KafkaIO consumes KV streams.
          .apply("ToKV", MapElements
                .into(TypeDescriptors.kvs(TypeDescriptors.strings(), TypeDescriptors.strings()))
                .via(dataMapper(data)))
          // Add KafkaIO#write and provide configurations.
          .apply("Produce", KafkaIO.<String, String>write()
                .withBootstrapServers(config.getBootstrapServers())
                .withTopic(options.getTopic())
                .updateProducerProperties(config)
                .withKeySerializer(StringSerializer.class)
                .withValueSerializer(StringSerializer.class));

        // Launch the pipeline and block
        wp.run().waitUntilFinish();
    }

    private static SerializableFunction<Long, KV<String, String>> dataMapper(JSONArray data) {
        return (Long x) -> {
            int index = x.intValue() % data.size();
            JSONObject entry = (JSONObject) data.get(index);
            JSONObject msg = convertToMessage(entry);
            addCurrentTimestamp(msg);
            return KV.<String, String>of("beam", msg.toString());
        };
    }

    private static JSONObject convertToMessage(JSONObject entry) {
        JSONObject msg = new JSONObject();
        msg.put("id", entry.get("id"));
        msg.put("tz", entry.get("tz"));
        msg.put("dateutc", entry.get("dateutc"));
        msg.put("latitude", entry.get("lat"));
        msg.put("longitude", entry.get("lon"));
        msg.put("temperature", entry.get("tempf_avg"));
        msg.put("baromin", entry.get("baromin_first"));
        msg.put("humidity", entry.get("humidity_avg"));
        msg.put("rainin", entry.get("rainin"));
        return msg;
    }

    private static void addCurrentTimestamp(JSONObject tuple) {
        String iso = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        tuple.put("timestamp", iso);
    }
}
