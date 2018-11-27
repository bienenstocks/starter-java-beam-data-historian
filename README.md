# Creating and Deploying a Data Historian Application using Java Beam
A generated IBM Cloud application

### Continuously deliver an app with IBM hosted repos and issue tracking

This sample Java Beam application runs the DataHistorian pipeline.
Data historian is an efficient way to collect and store time series data. The data might come from production lines,
transportation routes, network devices, satellites, and other devices. The data is stored with a time stamp and other
identifying information such as device ID and location.

This sample application will deploy a Java Beam runner to ingest the Data historian data from an Event Streams topic.

**Instructions are provided below on how to populate an Event Streams topic with the relevant data.

The data is then flowed through an aggregator operator. The aggregator operator will calculate the average barometric pressure,
humidity, indoor temperature, and rainfall today for each weather station.
The aggregated tuple data will then flow to a cloud object storage instance to be stored.


### Prereqs
Need to provision the following in IBM Cloud:

- Streaming Analytics Instance
- Event Streams Instance  - Standard Plan
- Cloud Object Storage Instance
- Editor permissions on a Toolchain Service in IBM Cloud

### To get started, click **Deploy to IBM Cloud**.
<a href="https://bluemix.net/deploy?repository=https://github.com/bienenstocks/starter-java-beam-data-historian" target="_blank">![Deploy to IBM Cloud](https://bluemix.net/deploy/button.png)</a>

This will open up the toolchain create page. Follow the instructions for selecting the
toolchain name, region, Git repo, and then click 'Create'.

On the next page, you will need to create an API key or use an existing one.

Enter in the name of each service that is required.

