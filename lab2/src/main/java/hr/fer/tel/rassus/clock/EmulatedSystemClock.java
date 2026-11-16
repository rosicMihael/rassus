package hr.fer.tel.rassus.clock;

import java.util.Random;

/**
 *
 * @author Aleksandar
 */
public class EmulatedSystemClock {

    private long startTime;
    private double jitter; //jitter per second,  percentage of deviation per 1 second

    public EmulatedSystemClock() {
        startTime = System.currentTimeMillis();
        Random r = new Random();
        jitter = (r.nextInt(20 )) / 100d; //divide by 10 to get the interval between [0, 20], and then divide by 100 to get percentage
    }

    public long currentTimeMillis() {
        long current = System.currentTimeMillis();
        long diff =current - startTime;
        double coef = diff / 1000;
        return startTime + Math.round(diff * Math.pow((1+jitter), coef));
    }


}
