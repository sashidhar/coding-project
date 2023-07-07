package co.harbor.calendly.model;

public enum Interval {
    WEEKLY("weekly", 7);

    private String interval;
    private Integer days;

    Interval(String interval, int days) {
        this.interval = interval;
        this.days = days;
    }

    public String getInterval() {
        return interval;
    }

    public Integer getDays() {
        return days;
    }
}
