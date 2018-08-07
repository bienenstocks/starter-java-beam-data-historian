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
package com.ibm.streams.beam.sample.datahistorian;


import com.amazonaws.auth.AWSCredentialsProvider;
import org.apache.beam.sdk.io.aws.options.AwsOptions;
import org.apache.beam.sdk.options.ApplicationNameOptions;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.PipelineOptions;

/**
 * This is a option class that allows users to provide a Message Hub topic and
 * the path to the credentials file. Both options are required.
 */
public interface DataHistorianOptions extends PipelineOptions, ApplicationNameOptions {
    /**
     * @param topic the Message Hub topic which can be created in the IBM Cloud
     *              Message Hub's manage page.
     */
    void setTopic(String topic);
    @Default.String("DataHistorian")
    String getTopic();

    /**
     * @param credFilePath the path to the file that contains Message Hub JSON
     *                     credentials.
     */
    void setCred(String credFilePath);
    @Default.String("creds.json")
    String getCred();

    void setAwsServiceEndpoint(String ep);
    String getAwsServiceEndpoint();

    void setAwsCredentialsProvider(AWSCredentialsProvider prov);
    @Default.InstanceFactory(AwsOptions.AwsUserCredentialsFactory.class)
    AWSCredentialsProvider getAwsCredentialsProvider();
}
