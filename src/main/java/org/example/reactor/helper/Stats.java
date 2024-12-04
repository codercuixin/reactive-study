package org.example.reactor.helper;

import java.util.concurrent.TimeUnit;

public class Stats {
    private long startTime;
    private long endTime;
    private long duration;

    // 开始计时
    public void startTimer() {
        this.startTime = System.nanoTime();
    }

    // 停止计时并记录时间
    public void stopTimerAndRecordTiming() {
        this.endTime = System.nanoTime();
        this.duration = this.endTime - this.startTime;
    }

    // 获取处理持续时间（纳秒）
    public long getDurationNanos() {
        return duration;
    }

    // 将处理持续时间转换为毫秒
    public long getDurationMillis() {
        return TimeUnit.NANOSECONDS.toMillis(duration);
    }

    // 重置统计数据
    public void reset() {
        this.startTime = 0;
        this.endTime = 0;
        this.duration = 0;
    }
}
