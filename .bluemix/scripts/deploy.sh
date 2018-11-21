 #!/bin/bash

      if ! [ -x "$(command -v bx)" ]; then
         curl -fsSL https://clis.ng.bluemix.net/install/linux | sh
      fi

      # TODO : how to know which env to use ?
      bx login --apikey $PIPELINE_API_KEY -a https://api.ng.bluemix.net
      bx target --cf

      # get Streaming analytics credentials
      bx resource service-key-delete "SA_${APP_NAME}" -f
      SA_KEY=$(bx resource service-key-create "SA_${APP_NAME}" Manager --instance-name "${SA_INSTANCE}")


      echo "generating vcap.json"
      echo "{
         \"streaming-analytics\":[{
         \"name\" : \"streaming-analytics\",
         \"credentials\" : {
             \"apikey\": \"$(echo ${SA_KEY} | awk 'BEGIN{FS="apikey: "} {print $2}' | awk '{ print $1 }')\",
             \"v2_rest_url\": \"$(echo ${SA_KEY} | awk 'BEGIN{FS="v2_rest_url: "} {print $2}' | awk '{ print $1 }')\"
          }
        }]" > vcap.json

      if [ $COS_INSTANCE ]; then
        # get COS credentials
        bx resource service-key-delete "COS_${APP_NAME}" -f
        COS_KEY=$(bx resource service-key-create "COS_${APP_NAME}" Manager --instance-name "${COS_INSTANCE}" --p {\"HMAC\":true})

        bx plugin install cloud-object-storage  -r "Bluemix"
        COS=$(bx resource service-instance "${COS_INSTANCE}")
         COSCRN=$(echo ${COS} | awk 'BEGIN{FS=“crn: "} {print $2}' | awk '{ print $1 }')
         bx cos config —crn $COSCRN
        bx cos create-bucket --bucket ${APP_NAME} --region us-geo

        echo ",
         \"cos\": {
            \"endpoint\": \"s3-api.us-geo.objectstorage.softlayer.net\",
            \"accessKeyId\": \"$(echo ${COS_KEY} | awk 'BEGIN{FS="access_key_id: "} {print $2}' | awk '{ print $1 }')\",
            \"secretKey\": \"$(echo ${COS_KEY} | awk 'BEGIN{FS="secret_access_key: "} {print $2}' | awk '{ print $1 }')\",
            \"bucket\": ${APP_NAME},
            \"filePrefix\": \"prefix\"
         }" >> vcap.json
      fi

      if [ $MH_INSTANCE ]; then
        # get MH credentials
        bx resource service-key-delete "MH_${APP_NAME}" -f
        MH_KEY=$(bx resource service-key-create "MH_${APP_NAME}" Manager --instance-name "${MH_INSTANCE}")

        echo ",
          \"messagehub\": {
            \"user\": \"$(echo ${MH_KEY} | awk 'BEGIN{FS="user: "} {print $2}' | awk '{ print $1 }')\",
            \"password\": \"$(echo ${MH_KEY} | awk 'BEGIN{FS="password: "} {print $2}' | awk '{ print $1 }')\",
            \"kafka_brokers_sasl\": \"$(echo ${COS_KEY} | awk 'BEGIN{FS="kafka_brokers_sasl: "} {print $2}' | awk '{ print $1 }')\"
          }" >>vcap.json
      fi

      echo "}" >> vcap.json
      cat vcap.json

      java -cp ./example-starter-kit-0.1-SNAPSHOT.jar:./dependency/com.ibm.streams.beam-1.2.1/com.ibm.streams.beam/lib/com.ibm.streams.beam.translation.jar \
           com.ibm.streams.beam.sample.datahistorian.DataHistorian \
               --runner=StreamsRunner \
               --contextType=STREAMING_ANALYTICS_SERVICE \
               --vcapServices=./vcap.json \
               --serviceName=streaming-analytics \
               --beamToolkitDir=./dependency/com.ibm.streams.beam-1.2.1 \
               --jarsToStage=./example-starter-kit-0.1-SNAPSHOT.jar \
               --jobName=${APP_NAME}