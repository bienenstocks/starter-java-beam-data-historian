package com.ibm.streams.beam.sample.datahistorian.types;

import java.io.Serializable;

public class DHMessageRecord implements Serializable {
    public String id = null;
    public String tz = null;
    public String timestamp = null;
    public String dateutc = null;
    public Double latitude = null;
    public Double longitude = null;
    public Double temperature = null;
    public Double baromin = null;
    public Double humidity = null;
    public Double rainin = null;
}
