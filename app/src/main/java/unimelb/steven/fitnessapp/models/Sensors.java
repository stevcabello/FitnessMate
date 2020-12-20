package unimelb.steven.fitnessapp.models;

/**
 * Created by pc on 4/14/2016.
 */
public class Sensors {
    private double ax;
    private double ay;
    private double az;
    private double gx;
    private double gy;
    private double gz;
    private double bp;
    private String id;
    private double seconds;
    private String activity;

    public Sensors(String id, double ax, double ay, double az, double gx, double gy, double gz, double bp, double seconds, String activity) {
        this.bp = bp;
        this.gz = gz;
        this.gy = gy;
        this.gx = gx;
        this.az = az;
        this.ay = ay;
        this.ax = ax;
        this.id = id;
        this.seconds = seconds;
        this.activity = activity;
    }


    public String getActivity() {return activity;}
    public double getSeconds() {return seconds;}

    public String getId() {return  id;}

    public double getAx() {
        return ax;
    }

    public void setAx(double ax) {
        this.ax = ax;
    }

    public double getAy() {
        return ay;
    }

    public void setAy(double ay) {
        this.ay = ay;
    }

    public double getAz() {
        return az;
    }

    public void setAz(double az) {
        this.az = az;
    }

    public double getGx() {
        return gx;
    }

    public void setGx(double gx) {
        this.gx = gx;
    }

    public double getGy() {
        return gy;
    }

    public void setGy(double gy) {
        this.gy = gy;
    }

    public double getGz() {
        return gz;
    }

    public void setGz(double gz) {
        this.gz = gz;
    }

    public double getBp() {
        return bp;
    }

    public void setBp(double bp) {
        this.bp = bp;
    }


}

