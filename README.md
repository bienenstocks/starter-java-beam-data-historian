# Java Beam Samples
A generated IBM Cloud application

[![](https://img.shields.io/badge/IBM%20Cloud-powered-blue.svg)](https://bluemix.net)

## Setup
- Copy the com.ibm.streams.beam Beam toolkit v1.1.1 to the same directory as the starter project
- Fill in the vcap.json to point to the service
- `source ./streams-runner-env.sh`

## Setting up config value
- Copy the file config-template.json to config.json
- In the config.json, fill in all of the values as follows:

##### messagehub
Open up the message hub service and click on _Service Credentials_ on the left.
Click on _View Credentials_ and copy over the relevent values into the config.json
messagehub section

##### aws
Open up the Cloud Object Storage service. Click on _Service Credentials_ on the left.
You will need to use credentials that have write access, as well as HMAC keys.
To generate HMAC credentials, specify
    ```
    { "HMAC"=true }
    ```
inside the _Add Inline Configuration Parameters_ section when adding new credentials.
To set the config.json credential values for aws, you will need to set:

**awsAccessKeyId** =  _cos_hmac_keys.access_key_id value_.

**awsSecretKey** = _cos_hmac_keys.secret_access_key_

A bucket needs to be created in the region of choice. To get the endpoint for that region, click
on _endpoints_ on the left. choose the public endpoint for the region matching the bucket.
Set the config.json values:

**awsServiceEndpoint** = public endpoint for the region matching the bucket.

**bucket** = bucket created in the region of choice.

**filePrefix** = optional config variable for prefixing each file.

## Run locally using the Beam DirectRunner

```bash
./run-dev
```

## Deploy to the Streaming Analytics instance

```bash
./run-remote
```
