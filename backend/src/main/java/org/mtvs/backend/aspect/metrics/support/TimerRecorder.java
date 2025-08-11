package org.mtvs.backend.aspect.metrics.support;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;


@Component
public class TimerRecorder {
    private final MeterRegistry registry;
    public TimerRecorder(MeterRegistry registry) {
        this.registry = registry;
    }

    public Timer.Sample start(){
        return Timer.start(registry);
    }

    public void stop(Timer.Sample sample, String name, String... tags) {
        sample.stop(Timer.builder(name)
                .tags(tags)
                .register(registry));
    }
}
