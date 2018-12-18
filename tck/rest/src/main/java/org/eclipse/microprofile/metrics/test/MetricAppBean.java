/*
 **********************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICES file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 **********************************************************************/

package org.eclipse.microprofile.metrics.test;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.Histogram;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.Meter;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.Timer;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Metric;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.enterprise.inject.Produces;

@ApplicationScoped
public class MetricAppBean {

    @Inject
    @Metric(description = "red-description", displayName = "red-display-name")
    private Counter redCount;

    @Inject
    @Metric(name = "blue")
    private Counter blueCount;

    @Inject
    @Metric(absolute = true)
    private Counter greenCount;

    @Inject
    @Metric(name = "purple", absolute = true, tags = "app=myShop")
    private Counter purpleCount;

    @Inject
    @Metric(absolute = true, unit = "jellybeans", description = "jellybeans-description", displayName = "jellybeans-displayName")
    private Histogram jellybeanHistogram;

    @Inject
    private MetricRegistry metrics;

    @Produces
    @Metric(name = "coffee_price_produces", unit = "USD", absolute = true,
        description = "getCoffeePriceDescription", displayName = "getCoffeePriceDisplayName")
    protected org.eclipse.microprofile.metrics.Gauge<Long> getCoffeePrice() {
        return () -> 4L;
    }

    @Inject
    @ConfigProperty(name = "MP_METRICS_TAGS")
    private String globalTags;


    public void countMe() {
        Counter counter = metrics.counter("metricTest.test1.count");
        counter.inc();
    }

    @Counted(name = "metricTest.test1.countMeA", monotonic = true, absolute = true,
        description = "count-me-a-description", displayName = "count-me-a-display-name")
    public void countMeA() {

    }
    
    @Counted(name = "metricTest.test1.countMeB", monotonic = true, absolute = true, unit = "jellybean")
    public long countMeB() {
        return 666666;
    }

    public void gaugeMe() {

        @SuppressWarnings("unchecked")
        Gauge<Long> gauge = metrics.getGauges().get("metricTest.test1.gauge");
        if (gauge == null) {
            gauge = () -> {
                return 19L;
            };
            Metadata gaugeMetadata = new Metadata("metricTest.test1.gauge",MetricType.GAUGE, MetricUnits.GIGABYTES);
            metrics.register("metricTest.test1.gauge", gauge, gaugeMetadata);
        }

    }

    @org.eclipse.microprofile.metrics.annotation.Gauge(unit = MetricUnits.KIBIBITS,
        description = "gauge-me-a-description", displayName = "gauge-me-a-displayname")
    public long gaugeMeA() {
        return 1000L;
    }

    @org.eclipse.microprofile.metrics.annotation.Gauge(unit = "hands")
    public long gaugeMeB() {
        return 7777777;
    }

    public void histogramMe() {

        Metadata metadata = new Metadata("metricTest.test1.histogram", MetricType.HISTOGRAM, MetricUnits.BYTES);
        Histogram histogram = metrics.histogram(metadata);

        // Go both ways to minimize error due to decay
        for (int i = 0; i < 500; i++) {
            histogram.update(i);
            histogram.update(999 - i);
        }

        Metadata metadata2 = new Metadata("metricTest.test1.histogram2", MetricType.HISTOGRAM,MetricUnits.NONE);
        Histogram histogram2 = metrics.histogram(metadata2);
        histogram2.update(1);
    }

    public void meterMe() {

        Meter meter = metrics.meter("metricTest.test1.meter");
        meter.mark();

    }

    @Metered(absolute = true, description = "meter-me-a-description", displayName = "meter-me-a-display-name")
    public void meterMeA() {

    }

    public void timeMe() {

        Timer timer = metrics.timer("metricTest.test1.timer");

        Timer.Context context = timer.time();
        try {
            Thread.sleep((long) (Math.random() * 1000));
        }
        catch (InterruptedException e) {
        }
        finally {
            context.stop();
        }

    }

    @Timed
    public void timeMeA() {

    }

    public String getGlobalTags() {
        return globalTags;
    }
}
