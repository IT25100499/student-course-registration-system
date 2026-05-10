package model;

/**
 * Schedule.java - Represents a scheduled lecture session
 */
public class Schedule {
    private String scheduleId;
    private String courseId;
    private String lecturerId;
    private String dateTime; // e.g., "2025-06-01 10:00 AM"
    private String type; // "Online" or "Physical"
    private String location; // Link or Room number

    public Schedule(String scheduleId, String courseId, String lecturerId, String dateTime, String type, String location) {
        this.scheduleId = scheduleId;
        this.courseId = courseId;
        this.lecturerId = lecturerId;
        this.dateTime = dateTime;
        this.type = type;
        this.location = location;
    }

    public String getScheduleId() { return scheduleId; }
    public void setScheduleId(String scheduleId) { this.scheduleId = scheduleId; }
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public String getLecturerId() { return lecturerId; }
    public void setLecturerId(String lecturerId) { this.lecturerId = lecturerId; }
    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String toFileString() {
        return scheduleId + "|" + courseId + "|" + lecturerId + "|" + dateTime + "|" + type + "|" + location;
    }

    public static Schedule fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length >= 6) {
            return new Schedule(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
        }
        return null;
    }

    public String toJson() {
        return "{" +
                "\"scheduleId\":\"" + scheduleId + "\"," +
                "\"courseId\":\"" + courseId + "\"," +
                "\"lecturerId\":\"" + lecturerId + "\"," +
                "\"dateTime\":\"" + dateTime + "\"," +
                "\"type\":\"" + type + "\"," +
                "\"location\":\"" + location + "\"" +
                "}";
    }
}
