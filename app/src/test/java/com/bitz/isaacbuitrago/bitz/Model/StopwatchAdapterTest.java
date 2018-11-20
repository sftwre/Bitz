package com.bitz.isaacbuitrago.bitz.Model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class StopwatchAdapterTest
{
    StopwatchAdapter stopwatchAdapter;

    public static final long P = 10000;

    public static final long P1 = 13000;

    public static final long P2 = 17000;

    public static final long P3 = 27000;

    public static final long WAIT_TIME = 1000;

    public static final long DELAY1 = 1000;

    public static final long DELAY2 = 100000;

    public static final long DELTA = 10;


    @Before
    public void init() {
        stopwatchAdapter = new StopwatchAdapter();

        stopwatchAdapter.start();
    }

    @Test
    public void setTime() {
        stopwatchAdapter.setTime(P);

        assertEquals(P, stopwatchAdapter.getTime());
    }

    @Test
    public void getTime()
    {
        checkModifiedStopWatch(stopwatchAdapter, P);
        checkModifiedStopWatch(stopwatchAdapter, P3);
        checkModifiedStopWatch(stopwatchAdapter, P1);
        checkModifiedStopWatch(stopwatchAdapter, P2);

        stopwatchAdapter = new StopwatchAdapter();

        stopwatchAdapter.start();

        checkUnModifiedStopWatch(stopwatchAdapter, DELAY1);

        checkUnModifiedStopWatch(stopwatchAdapter, DELAY2, DELAY1);

    }

    private void checkModifiedStopWatch(StopwatchAdapter stopwatchAdapter, long p)
    {
        stopwatchAdapter.setTime(p);

        // wait 1 sec
        try {
            Thread.sleep(WAIT_TIME);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(p + WAIT_TIME, stopwatchAdapter.getTime(), DELTA);
    }

    private void checkUnModifiedStopWatch(StopwatchAdapter stopwatchAdapter, long delay, long ... other)
    {
        // wait 1 sec
        try
        {
            Thread.sleep(delay);

        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        if (other.length > 0)
        {
            long totalTime = delay;

            for (long o : other)
            {
                totalTime += o;
            }

            assertEquals(totalTime, stopwatchAdapter.getTime(), DELTA);
        }
        else
        {
            assertEquals(delay, stopwatchAdapter.getTime(), DELTA);
        }
    }

}