package com.ibm.streams.beam.sample.datahistorian.io.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;

/**
 * Created by sarahb on 07/08/2018.
 */
public class CustomAWSCredentialsProvider implements AWSCredentialsProvider{
        public CustomAWSCredentialsProvider(String mAccessKey, String mSecretKey) {
            super();
            this.mSecretKey = mSecretKey;
            this.mAccessKey = mAccessKey;
        }

        private String mSecretKey;
        private String mAccessKey;

        @Override
        public AWSCredentials getCredentials() {
            AWSCredentials awsCredentials = new AWSCredentials() {

                @Override
                public String getAWSSecretKey() {
                    // TODO Auto-generated method stub
                    return mSecretKey;
                }

                @Override
                public String getAWSAccessKeyId() {
                    // TODO Auto-generated method stub
                    return mAccessKey;
                };
            };
            return awsCredentials;
        }

        @Override
        public void refresh() {
            // TODO Auto-generated method stub

        }
}
