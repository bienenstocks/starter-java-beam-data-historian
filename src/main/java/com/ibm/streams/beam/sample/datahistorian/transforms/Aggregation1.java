package com.ibm.streams.beam.sample.datahistorian.transforms;

import com.ibm.streams.beam.sample.datahistorian.types.AggregatedRecord;
import com.ibm.streams.beam.sample.datahistorian.types.DHMessageRecord;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.transforms.Combine;

public class Aggregation1 extends Combine.CombineFn<DHMessageRecord, Aggregation1.Accum, AggregatedRecord> {
    @DefaultCoder(AvroCoder.class)
    static class Accum {
        DHMessageRecord anInputRecord; // for pass-through valuesf

        int count = 0;

        double minBar = Double.MAX_VALUE;
        double maxHumidity = 0;
        double rainSum = 0;
    }

    @Override
    public Accum createAccumulator() { return new Accum(); }

    @Override
    public Accum addInput(Accum accum, DHMessageRecord input) {
        accum.anInputRecord = input;

        accum.count++;

        accum.minBar = Math.min(accum.minBar, input.baromin);
        accum.maxHumidity = Math.max(accum.maxHumidity, input.humidity);
        accum.rainSum += input.rainin;

        return accum;
    }

    @Override
    public Accum mergeAccumulators(Iterable<Accum> accums) {
        Accum merged = createAccumulator();

        for (Accum accum : accums) {
            if (accum.anInputRecord != null) {
                merged.anInputRecord = accum.anInputRecord;
            }

            merged.count += accum.count;

            merged.minBar = Math.min(merged.minBar, accum.minBar);
            merged.maxHumidity = Math.max(merged.maxHumidity, accum.maxHumidity);
            merged.rainSum += accum.rainSum;
        }
        return merged;
    }

    @Override
    public AggregatedRecord extractOutput(Accum accum) {
        AggregatedRecord output = new AggregatedRecord();

        DHMessageRecord anInputRecord = accum.anInputRecord;
        if (anInputRecord == null) {
            return output;
        }

        output.id = anInputRecord.id;
        output.tz = anInputRecord.tz;
        output.dateutc = anInputRecord.dateutc;
        output.longitude = anInputRecord.longitude;
        output.latitude = anInputRecord.latitude;
        output.temperature = anInputRecord.temperature;

        output.baromin_min1 = accum.minBar;
        output.humidity_max1 = accum.maxHumidity;
        output.rainin_avg1 = accum.rainSum / accum.count;

        return output;
    }
}

