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
package com.ibm.streams.beam.sample.datahistorian.io.aws;

import com.ibm.streams.beam.sample.datahistorian.DataHistorianOptions;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class AWSConfig {

    public static void setObjectStorageConfigParams(DataHistorianOptions options) throws IOException, ParseException {

        // parse credentials file into a JSONObject
        JSONParser parser = new JSONParser();
        JSONObject cred = (JSONObject) parser.parse(new FileReader(options.getCred()));
        JSONObject coscred = (JSONObject) cred.get("cos");
        options.setAwsServiceEndpoint((String) coscred.get("endpoint"));
        options.setAwsCredentialsProvider(new AWSStaticCredentialsProvider(
                new BasicAWSCredentials((String) coscred.get("accessKeyId"),
                                    (String) coscred.get("secretKey"))));
    }

    public static String getBucket(DataHistorianOptions options) throws IOException, ParseException{
        // parse credentials file into a JSONObject
        JSONParser parser = new JSONParser();
        JSONObject cred = (JSONObject) parser.parse(new FileReader(options.getCred()));
        JSONObject coscred = (JSONObject) cred.get("cos");
        return (String) coscred.get("bucket");
    }

    public static String getFilePrefix(DataHistorianOptions options) throws IOException, ParseException {
        // parse credentials file into a JSONObject
        JSONParser parser = new JSONParser();
        JSONObject cred = (JSONObject) parser.parse(new FileReader(options.getCred()));
        JSONObject coscred = (JSONObject) cred.get("cos");
        return (String) coscred.get("filePrefix");
    }
}

