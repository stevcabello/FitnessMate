package unimelb.steven2.fitnessapp.models;

/**
 * Created by pc on 4/14/2016.
 */
public class Accelerometer {
    private String x_axis;
    private String y_axis;
    private String z_axis;


    public Accelerometer(String x_axis, String y_axis, String z_axis) {
        this.x_axis = x_axis;
        this.y_axis = y_axis;
        this.z_axis = z_axis;
    }

    public String getX_axis() {
        return x_axis;
    }

    public void setX_axis(String x_axis) {
        this.x_axis = x_axis;
    }

    public String getY_axis() {
        return y_axis;
    }

    public void setY_axis(String y_axis) {
        this.y_axis = y_axis;
    }

    public String getZ_axis() {
        return z_axis;
    }

    public void setZ_axis(String z_axis) {
        this.z_axis = z_axis;
    }
}

