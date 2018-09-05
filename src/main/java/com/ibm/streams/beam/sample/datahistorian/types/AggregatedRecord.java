package com.ibm.streams.beam.sample.datahistorian.types;

import java.io.Serializable;

public class AggregatedRecord implements Serializable {
    public String id = null;
    public String tz = null;
    public String timestamp = null;
    public String dateutc = null;
    public Double latitude = null;
    public Double longitude = null;
    public Double temperature = null;
    public Double baromin_min1 = null;
    public Double humidity_max1 = null;
    public Double rainin_avg1 = null;
}
