package com.ibm.streams.beam.sample.datahistorian;

import com.ibm.streams.beam.sample.datahistorian.io.WriteOneFilePerWindow;
import com.ibm.streams.beam.sample.datahistorian.io.mh.MessageHubConfig;
import com.ibm.streams.beam.sample.datahistorian.io.aws.AWSConfig;
import com.ibm.streams.beam.sample.datahistorian.transforms.Aggregation1;
import com.ibm.streams.beam.sample.datahistorian.types.AggregatedRecord;
import com.ibm.streams.beam.sample.datahistorian.types.DHMessageRecord;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.CoderRegistry;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.extensions.jackson.AsJsons;
import org.apache.beam.sdk.extensions.jackson.ParseJsons;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Combine;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.PCollection;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.joda.time.Duration;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class DataHistorian {

    public static void main(String args[]) throws IOException, ParseException {

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
        .apply("ConsumeFromMessageHub", KafkaIO.<String, String>read()
                .withBootstrapServers(config.getBootstrapServers())
                .withTopic(options.getTopic())
                .updateConsumerProperties(config)
                .withKeyDeserializer(StringDeserializer.class)
                .withValueDeserializer(StringDeserializer.class)
                // This example does not need meta data
                .withoutMetadata()
        )
        // Drop the dummy key "beam", turning ["beam", [record]] into: [record]
        .apply(Values.create())

        .apply("ParseJSON", ParseJsons.of(DHMessageRecord.class))
        .setCoder(SerializableCoder.of(DHMessageRecord.class))

        .apply("WindowOneSecond", Window.into(FixedWindows.of(Duration.millis(1000))))

        .apply("CombineEvents", Combine.globally(new Aggregation1()).withoutDefaults())

        .apply("SerializeAsJSON", AsJsons.of(AggregatedRecord.class));

        // Print messages to System.out, for debugging
        pc.apply("PrintToStdoutForDebug", ParDo.of(new PrintDoFn()));

        // Print to temp files, for debugging
        pc.apply("WriteToTempFileForDebug",
                new WriteOneFilePerWindow("/tmp/out", 1));

        // Production output to COS
        pc.apply("WriteToCloudObjectStore",
                new WriteOneFilePerWindow("s3://" + bucket + "/" + filePrefix, 1));

        // Launch the pipeline and block.
        rp.run();
    }

    /**
     * A simple DoFn that prints message to System.out
     */
    private static class PrintDoFn extends DoFn<String, Void> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            System.out.println(c.element());
        }
    }
}
