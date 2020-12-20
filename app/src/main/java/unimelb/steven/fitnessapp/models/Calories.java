package unimelb.steven.fitnessapp.models;

/**
 * Created by pc on 12/21/2015.
 */
public class Calories {
    private String activity;
    private String date;
    private String calories;
    private String duration;
    private String distance;
    private String speed;


    public Calories(String activity, String date, String calories, String duration, String distance, String speed) {
        this.activity = activity;
        this.date = date;
        this.calories = calories;
        this.duration = duration;
        this.distance = distance;
        this.speed = speed;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }
}
