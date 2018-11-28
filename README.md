# Creating and Deploying a Data Historian Application using Java Beam
A generated IBM Cloud application

### Continuously deliver an app with IBM hosted repos and issue tracking

This sample Java Beam application runs the DataHistorian pipeline.
Data historian is an efficient way to collect and store time series data. The data might come from production lines,
transportation routes, network devices, satellites, and other devices. The data is stored with a time stamp and other
identifying information such as device ID and location.

This sample application will deploy a Java Beam runner to ingest the Data historian data from an Event Streams topic.

The data is then flowed through an aggregator operator. The aggregator operator will calculate the average barometric pressure,
humidity, indoor temperature, and rainfall today for each weather station.
The aggregated tuple data will then flow to a cloud object storage instance to be stored.

![](./media/DH_flow.png)

## Prereqs
* Create an <a href="https://console.bluemix.net/dashboard/apps" target="_blank">IBM Cloud account</a> if you don't already have one.
* Need to provision the following in IBM Cloud:
  * Streaming Analytics Instance
  * Event Streams Instance  - Standard Plan
  * Cloud Object Storage Instance
  * Must have Editor permissions on a Toolchain Service in IBM Cloud
* Be sure to [populate Event Streams ](#populateES) with a topic and Data Historian data before running the application.

## To get started, click **Deploy to IBM Cloud**.

<a href="https://bluemix.net/deploy?repository=https://github.com/bienenstocks/starter-java-beam-data-historian" target="_blank">![Deploy to IBM Cloud](https://bluemix.net/deploy/button.png)</a>

This will open up the toolchain create page. Follow the instructions for selecting the
name and region for the toolchain creation, as well as the resource group.

Note: The Resource group selected must contain the Streaming Analytics instance and Cloud Object Storage instance
that are being used.

Then, click 'Create'.

<img src="./media/toolchaincreatepage.jpg" alt="drawing" width="500" height="500"/>


On the next page, you will need to insert the names of each service.



<img src="./media/toolchaincreatepage2.jpg" alt="drawing" width="500" height="500"/>



Once the toolchain is created, you will see the Git repo where the code has been copied to
and the Delivery pipeline. By clicking on Deliver pipeline, the toolchain will
run through the stages of build and and then deploy of the code.



<img src="./media/toolchaincreatepage3.png" alt="drawing" width="500" height="500"/>


If the deploy flow stage succeeds, then you have successfully deployed your pipeline
and can follow the steps to [monitor](#monitor) the flow.

IF the deploy flow stage fails, click on "view logs and history" to find the failure.
To debug, the deploy script can by found in the git repo under .bluemix/scripts.

## Populating Event Streams topic with Data Historian data<a name="populateES"></a>
The application that is running will subscribe to a specific topic in your Event Streams Instance to ingest the Data
Historian data. This application provides an Event Streams Producer that can be run to create and
populate this topic in Event Streams.
Before deploying the flow, run the Producer script to ensure the data is ready for ingest.

##### 1. clone this repo:
Clone the starter-java-beam-data-historian code locally. In a terminal, run:

$ git clone https://github.com/IBM/starter-java-beam-data-historian.git

##### 2. run the producer script

## Deploying locally
Alternatively to deploying the application to cloud, the application can be run locally once it's been cloned.

##### 1. clone this repo:
Clone the starter-java-beam-data-historian code locally. In a terminal, run:

$ git clone https://github.com/IBM/starter-java-beam-data-historian.git

##### 2. copy vcap-template.json to vcap.json

Fill in the relevant service credential information in the vcap.json.
To get the service credentials for a service, go to the service instance in IBM cloud.
On the left panel click on "service credentials". Click "view credentials" from one of the
listed credentials.

##### 3. run the run-remote.sh script

$ ./run_remote.sh

## Monitoring your application<a name="monitor"></a>
Once the application has been successfully deployed, either by running locally or by deploying to cloud,
go to the Streaming Analytics instance that you are using to run the flow and click "LAUNCH".
This will open the Streaming Analytics console. From the console, you can view and monitor the running flow.