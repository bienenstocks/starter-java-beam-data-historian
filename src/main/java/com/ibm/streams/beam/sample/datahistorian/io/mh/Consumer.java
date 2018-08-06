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

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Values;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.simple.parser.ParseException;

import java.io.IOException;


/**
 * This is a sample class that reads data from the IBM Cloud Message Hub using
 * Beam's native {@link KafkaIO}.
 */
public class Consumer {

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

        // Add KafkaIO#write and provide configurations.
        rp.apply(KafkaIO.<String, String>read()
                .withBootstrapServers(config.getBootstrapServers())
                .withTopic(options.getTopic())
                .updateConsumerProperties(config)
                .withKeyDeserializer(StringDeserializer.class)
                .withValueDeserializer(StringDeserializer.class)
                // This example does not need meta data
                .withoutMetadata())
          // Drop the dummy key "beam"
          .apply(Values.create())
          // Print messages to System.out
          .apply(ParDo.of(new PrintDoFn()));

        // Launch the pipeline and block.
        rp.run().waitUntilFinish();
    }
}
