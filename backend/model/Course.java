package model;

/**
 * Course.java - Course, CoreCourse, ElectiveCourse
 * OOP: Encapsulation, Inheritance, Method Overriding
 */
public class Course {
    private String courseId, courseName, department, courseType;
    private int credits, maxStudents;
    private double fee;

    public Course() {}
    public Course(String courseId,String courseName,int credits,String department,int maxStudents,double fee,String courseType) {
        this.courseId=courseId;this.courseName=courseName;this.credits=credits;
        this.department=department;this.maxStudents=maxStudents;this.fee=fee;this.courseType=courseType;
    }

    public String getCourseId(){return courseId;} public void setCourseId(String v){courseId=v;}
    public String getCourseName(){return courseName;} public void setCourseName(String v){courseName=v;}
    public int getCredits(){return credits;} public void setCredits(int v){credits=v;}
    public String getDepartment(){return department;} public void setDepartment(String v){department=v;}
    public int getMaxStudents(){return maxStudents;} public void setMaxStudents(int v){maxStudents=v;}
    public double getFee(){return fee;} public void setFee(double v){fee=v;}
    public String getCourseType(){return courseType;} public void setCourseType(String v){courseType=v;}
    public String getExtraField(){return "";}

    protected String esc(String s){return s==null?"":s.replace("\"","\\\"");}

    public String toFileString(){return courseId+"|"+courseName+"|"+credits+"|"+department+"|"+maxStudents+"|"+fee+"|"+courseType;}

    public String toJson(){
        return "{\"courseId\":\""+esc(courseId)+"\",\"courseName\":\""+esc(courseName)+"\",\"credits\":"+credits
            +",\"department\":\""+esc(department)+"\",\"maxStudents\":"+maxStudents+",\"fee\":"+fee
            +",\"courseType\":\""+esc(courseType)+"\",\"extraField\":\""+esc(getExtraField())+"\"}";
    }

    public static Course fromFileString(String line){
        String[]p=line.split("\\|",-1);if(p.length<7)return null;
        int cr=Integer.parseInt(p[2].trim()),mx=Integer.parseInt(p[4].trim());
        double f=Double.parseDouble(p[5].trim());
        if(p[6].trim().equalsIgnoreCase("Core"))
            return new CoreCourse(p[0],p[1],cr,p[3],mx,f,p.length>7?Boolean.parseBoolean(p[7]):true);
        if(p[6].trim().equalsIgnoreCase("Elective"))
            return new ElectiveCourse(p[0],p[1],cr,p[3],mx,f,p.length>7?p[7]:"None");
        return new Course(p[0],p[1],cr,p[3],mx,f,p[6]);
    }
}

class CoreCourse extends Course {
    private boolean isMandatory;
    public CoreCourse(String id,String n,int c,String d,int m,double f,boolean mandatory){
        super(id,n,c,d,m,f,"Core");isMandatory=mandatory;
    }
    @Override public String getExtraField(){return isMandatory?"Mandatory":"Optional";}
    @Override public String toFileString(){return super.toFileString()+"|"+isMandatory;}
}

class ElectiveCourse extends Course {
    private String prerequisite;
    public ElectiveCourse(String id,String n,int c,String d,int m,double f,String prereq){
        super(id,n,c,d,m,f,"Elective");prerequisite=prereq;
    }
    @Override public String getExtraField(){return prerequisite;}
    @Override public String toFileString(){return super.toFileString()+"|"+prerequisite;}
}
