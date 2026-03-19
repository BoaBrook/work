package cn.stylefeng.guns.modular.industrialTV.schedule;

import java.util.concurrent.ScheduledFuture;

public final class VideoInspectionScheduledTask {

    volatile ScheduledFuture<?> future;

    public void cancel() {
        ScheduledFuture<?> future = this.future;
        if (future != null) {
            future.cancel(false);
        }
    }
}