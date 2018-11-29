#!/bin/bash

# Simple shell script to run producer to generate data to send to Event Streams topic
mvn exec:java -Dexec.classpathScope="runtime" -Dexec.mainClass=com.ibm.streams.beam.sample.datahistorian.io.mh.Producer -Dexec.args="\
--runner=DirectRunner "