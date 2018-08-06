package com.ibm.streams.beam.sample.datahistorian;

import com.ibm.streams.beam.sample.datahistorian.io.mh.Consumer;
import com.ibm.streams.beam.sample.datahistorian.io.mh.MessageHubConfig;
import com.ibm.streams.beam.sample.datahistorian.io.mh.MessageHubOptions;
import org.apache.beam.sdk.Pipeline;
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

    public static void main(String args[]) throws IOException, ParseException {
        // Create options
        MessageHubOptions options =
                PipelineOptionsFactory.fromArgs(args)
                        .withValidation()
                        .as(MessageHubOptions.class);

        // Parse credentials file
        MessageHubConfig config = new MessageHubConfig(options.getCred());

        // Run the consumer
        options.setAppName("MessageHubRead");
        Pipeline rp = Pipeline.create(options);

        PCollection pc =
        // Add KafkaIO#write and provide configurations.
        rp.apply(KafkaIO.<String, String>read()
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

        .apply(Window.into(FixedWindows.of(Duration.millis(1000))));

        // Print messages to System.out
        pc.apply(ParDo.of(new PrintDoFn()));

        pc.apply(new WriteOneFilePerWindow("/tmp/out", 1));

        pc.apply(new WriteOneFilePerWindow("s3://starter-kits/out", 1));

        // Launch the pipeline and block.
        rp.run().waitUntilFinish();
    }

//    @Override
    public PDone expand(PCollection<String> input) {
        // Verify that the input has a compatible window type.
        checkArgument(
                input.getWindowingStrategy().getWindowFn().windowCoder() == IntervalWindow.getCoder());

        ResourceId resource = FileBasedSink.convertToFileResourceIfPossible("out");

        return input.apply(
                TextIO.write()
//                        .to(new PerWindowFiles(resource))
                        .withTempDirectory(resource.getCurrentDirectory())
                        .withWindowedWrites()
                        .withNumShards(3));
    }
}
