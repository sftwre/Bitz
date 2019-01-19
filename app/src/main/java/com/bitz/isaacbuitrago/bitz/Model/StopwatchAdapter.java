package com.bitz.isaacbuitrago.bitz.Model;

import com.google.common.base.Stopwatch;
import java.util.concurrent.TimeUnit;

/**
 * Adapter for Guava Stopwatch.
 * Manages the use of a StopWatch to include the
 * elapsed times that occur when seeking through a track.
 *
 * @author isaacbuitrago
 */
public class StopwatchAdapter
{
    private long time;              // total time StopWatch has been running in nanoseconds

    private Stopwatch stopwatch;    // stopwatch to manage

    private boolean interrupted;    // flags if underlying timer has been reset

    public StopwatchAdapter()
    {
        time = 0;

        interrupted = false;
    }

    /**
     * stops the underlying stopwatch
     */
    public void stop()
    {
        stopwatch.stop();
    }

    /**
     * starts the underlying stopwatch
     */
    public void start()
    {
        stopwatch = Stopwatch.createStarted();
    }

    /**
     * Set stopwatch time to position.
     * Marks the StopWatch as being interrupted
     *
     * @param position
     */
    public void setTime(long position)
    {
        interrupted = true;

        stopwatch.stop();

        time = position;

        stopwatch.reset();

        stopwatch.start();
    }

    /**
     *
     * @return time the underlying StopWatch has been active in MILLISECONDS
     */
    public long getTime()
    {
        if(interrupted)
            time += stopwatch.elapsed(TimeUnit.MILLISECONDS);
        else
            time = stopwatch.elapsed(TimeUnit.MILLISECONDS);

        return time;
    }
}
