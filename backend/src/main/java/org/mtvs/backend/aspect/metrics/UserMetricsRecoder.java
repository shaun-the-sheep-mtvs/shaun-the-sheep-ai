package org.mtvs.backend.aspect.metrics;


import io.micrometer.core.instrument.Timer;
import org.mtvs.backend.aspect.metrics.model.MetricNames;
import org.mtvs.backend.aspect.metrics.support.TimerRecorder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMetricsRecoder {

    private final TimerRecorder timer;

    @Autowired
    public UserMetricsRecoder(TimerRecorder timer) {
        this.timer = timer;
    }

    public Timer.Sample startUserCreationTimer() {
        return timer.start();
    }

    public void stopUserCreationTimer(Timer.Sample sample, String... tags) {
        timer.stop(sample, MetricNames.USER_CREATE_TIMER, tags);
    }



}
