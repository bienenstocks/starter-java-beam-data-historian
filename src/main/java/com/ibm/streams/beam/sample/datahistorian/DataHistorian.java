package com.ibm.streams.beam.sample.datahistorian;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.ibm.streams.beam.sample.datahistorian.io.mh.MessageHubConfig;
import com.ibm.streams.beam.sample.datahistorian.io.aws.AWSConfig;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.CoderRegistry;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.extensions.jackson.AsJsons;
import org.apache.beam.sdk.extensions.jackson.ParseJsons;
import org.apache.beam.sdk.io.FileBasedSink;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.fs.ResourceId;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.joda.time.Duration;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Serializable;

import static org.apache.beam.sdk.repackaged.com.google.common.base.Preconditions.checkArgument;

public class DataHistorian {

    /**
     * A simple DoFn that prints message to System.out
     */
    private static class PrintDoFn extends DoFn<String, Void> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            System.out.println(c.element());
        }
    }

    private final static CoderRegistry coderRegistry = CoderRegistry.createDefault();

    public static class DHMessageRecord implements Serializable {
        public String id = null;
        public String tz = null;
        public String timestamp = null;
        public String dateutc = null;
        public Double latitude = null;
        public Double longitude = null;
        public Double temperature = null;
        public Double baromin = null;
        public Double humidity = null;
        public String rainin = null;
    }

    public static void main(String args[]) throws IOException, ParseException {
//        coderRegistry.registerCoderForClass(DHMessageRecord.class, new DefaultCoder());

        // Create options
        DataHistorianOptions options =
                PipelineOptionsFactory.fromArgs(args)
                        .withValidation()
                        .as(DataHistorianOptions.class);

        // Parse credentials file
        MessageHubConfig config = new MessageHubConfig(options.getCred());

        // Run the consumer
        options.setAppName("MessageHubRead");

        // Set up the Object Storage options
        AWSConfig.setObjectStorageConfigParams(options);
        String bucket = AWSConfig.getBucket(options);
        String filePrefix = AWSConfig.getFilePrefix(options);

        Pipeline rp = Pipeline.create(options);

        PCollection pc = rp
        // Add KafkaIO#read and provide configurations.
        .apply(KafkaIO.<String, String>read()
                .withBootstrapServers(config.getBootstrapServers())
                .withTopic(options.getTopic())
                .updateConsumerProperties(config)
                .withKeyDeserializer(StringDeserializer.class)
                .withValueDeserializer(StringDeserializer.class)
                // This example does not need meta data
                .withoutMetadata()
        )
        // Drop the dummy key "beam"
        .apply(Values.create())

        // Parse JSON
        .apply(ParseJsons.of(DHMessageRecord.class))
        .setCoder(SerializableCoder.of(DHMessageRecord.class))

        // First windowing
        .apply(Window.into(FixedWindows.of(Duration.millis(1000))))

        // TODO: transforms: mean, stddev, min, max

        // Second windowing
        .apply(Window.into(FixedWindows.of(Duration.millis(10000))))

        // TODO: transforms: mean, stddev, min, max

        // Serialize to JSON, in preparation for output to sink(s)
        .apply(AsJsons.of(DHMessageRecord.class));

        // Print messages to System.out, for debugging
        pc.apply(ParDo.of(new PrintDoFn()));

        // Print to temp files, for debugging
        pc.apply(new WriteOneFilePerWindow("/tmp/out", 1));

        // Production output to COS
        pc.apply(new WriteOneFilePerWindow("s3://" + bucket + "/" + filePrefix, 1));

        // Launch the pipeline and block.
        rp.run().waitUntilFinish();
    }

}
