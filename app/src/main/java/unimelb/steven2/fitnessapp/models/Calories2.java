package unimelb.steven2.fitnessapp.models;

/**
 * Created by pc on 12/21/2015.
 */
public class Calories2 {
    private String activity;
    private String date;
    private String calories;
    private String steps;
    private String distance;


    public Calories2(String activity, String date, String calories, String distance, String steps) {
        this.activity = activity;
        this.date = date;
        this.calories = calories;
        this.distance = distance;
        this.steps = steps;
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


    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String speed) {
        this.steps = steps;
    }
}
