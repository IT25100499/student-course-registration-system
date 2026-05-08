package model;

/**
 * Enrollment.java - Student-Course association
 * OOP: Abstraction, Association, Polymorphism
 */
public class Enrollment {
    private String enrollmentId, studentId, courseId, enrollmentDate, status, enrollmentType;
    public static final int FT_MAX=6, PT_MAX=3;

    public Enrollment(){}
    public Enrollment(String enrollmentId,String studentId,String courseId,String enrollmentDate,String status,String enrollmentType){
        this.enrollmentId=enrollmentId;this.studentId=studentId;this.courseId=courseId;
        this.enrollmentDate=enrollmentDate;this.status=status;this.enrollmentType=enrollmentType;
    }

    public String getEnrollmentId(){return enrollmentId;} public void setEnrollmentId(String v){enrollmentId=v;}
    public String getStudentId(){return studentId;} public void setStudentId(String v){studentId=v;}
    public String getCourseId(){return courseId;} public void setCourseId(String v){courseId=v;}
    public String getEnrollmentDate(){return enrollmentDate;} public void setEnrollmentDate(String v){enrollmentDate=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public String getEnrollmentType(){return enrollmentType;} public void setEnrollmentType(String v){enrollmentType=v;}

    public int getMaxCourses(){return "Full-Time".equalsIgnoreCase(enrollmentType)?FT_MAX:PT_MAX;}

    protected String esc(String s){return s==null?"":s.replace("\"","\\\"");}

    public String toFileString(){return enrollmentId+"|"+studentId+"|"+courseId+"|"+enrollmentDate+"|"+status+"|"+enrollmentType;}

    public String toJson(){
        return "{\"enrollmentId\":\""+esc(enrollmentId)+"\",\"studentId\":\""+esc(studentId)
            +"\",\"courseId\":\""+esc(courseId)+"\",\"enrollmentDate\":\""+esc(enrollmentDate)
            +"\",\"status\":\""+esc(status)+"\",\"enrollmentType\":\""+esc(enrollmentType)+"\"}";
    }

    public static Enrollment fromFileString(String line){
        String[]p=line.split("\\|",-1);
        if(p.length>=6)return new Enrollment(p[0],p[1],p[2],p[3],p[4],p[5]);
        return null;
    }
}
