package model;

/**
 * Lecturer.java - Lecturer (abstract), PermanentLecturer, VisitingLecturer
 * OOP: Abstraction, Inheritance
 */
public abstract class Lecturer {
    private String lecturerId,name,email,phone,department,assignedCourseId,lecturerType;

    public Lecturer(){}
    public Lecturer(String lecturerId,String name,String email,String phone,String department,String assignedCourseId,String lecturerType){
        this.lecturerId=lecturerId;this.name=name;this.email=email;this.phone=phone;
        this.department=department;this.assignedCourseId=assignedCourseId;this.lecturerType=lecturerType;
    }

    public String getLecturerId(){return lecturerId;} public void setLecturerId(String v){lecturerId=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
    public String getDepartment(){return department;} public void setDepartment(String v){department=v;}
    public String getAssignedCourseId(){return assignedCourseId;} public void setAssignedCourseId(String v){assignedCourseId=v;}
    public String getLecturerType(){return lecturerType;} public void setLecturerType(String v){lecturerType=v;}

    public abstract int calculateWorkload();
    public abstract String getExtraField();

    protected String esc(String s){return s==null?"":s.replace("\"","\\\"");}

    public String toFileString(){return lecturerId+"|"+name+"|"+email+"|"+phone+"|"+department+"|"+assignedCourseId+"|"+lecturerType;}

    public String toJson(){
        return "{\"lecturerId\":\""+esc(lecturerId)+"\",\"name\":\""+esc(name)+"\",\"email\":\""+esc(email)
            +"\",\"phone\":\""+esc(phone)+"\",\"department\":\""+esc(department)
            +"\",\"assignedCourseId\":\""+esc(assignedCourseId)+"\",\"lecturerType\":\""+esc(lecturerType)
            +"\",\"workload\":"+calculateWorkload()+",\"extraField\":\""+esc(getExtraField())+"\"}";
    }

    public static Lecturer fromFileString(String line){
        String[]p=line.split("\\|",-1);if(p.length<7)return null;
        if(p[6].trim().equalsIgnoreCase("Permanent"))
            return new PermanentLecturer(p[0],p[1],p[2],p[3],p[4],p[5],p.length>7?Integer.parseInt(p[7].trim()):0);
        return new VisitingLecturer(p[0],p[1],p[2],p[3],p[4],p[5],p.length>7?Integer.parseInt(p[7].trim()):6);
    }
}

class PermanentLecturer extends Lecturer {
    private int yearsExp;
    public PermanentLecturer(String id,String n,String e,String ph,String d,String c,int y){
        super(id,n,e,ph,d,c,"Permanent");yearsExp=y;
    }
    @Override public int calculateWorkload(){return yearsExp>=10?12:16;}
    @Override public String getExtraField(){return yearsExp+" years";}
    @Override public String toFileString(){return super.toFileString()+"|"+yearsExp;}
}

class VisitingLecturer extends Lecturer {
    private int contractMonths;
    public VisitingLecturer(String id,String n,String e,String ph,String d,String c,int m){
        super(id,n,e,ph,d,c,"Visiting");contractMonths=m;
    }
    @Override public int calculateWorkload(){return 8;}
    @Override public String getExtraField(){return contractMonths+" months";}
    @Override public String toFileString(){return super.toFileString()+"|"+contractMonths;}
}
