package model;

/*
 * Student.java - Student, UndergraduateStudent, PostgraduateStudent
 * OOP: Encapsulation, Inheritance, Polymorphism
 */
public class Student {
    private String studentId, name, email, phone, address, studentType;

    public Student() {}
    public Student(String studentId, String name, String email, String phone, String address, String studentType) {
        this.studentId = studentId; this.name = name; this.email = email;
        this.phone = phone; this.address = address; this.studentType = studentType;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String v) { this.studentId = v; }
    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }
    public String getAddress() { return address; }
    public void setAddress(String v) { this.address = v; }
    public String getStudentType() { return studentType; }
    public void setStudentType(String v) { this.studentType = v; }

    public double calculateFee() { return 0.0; }
    public String getExtraField1() { return ""; }
    public String getExtraField2() { return ""; }

    public String toFileString() {
        return studentId + "|" + name + "|" + email + "|" + phone + "|" + address + "|" + studentType;
    }

    public String toJson() {
        return "{\"studentId\":\"" + esc(studentId) + "\",\"name\":\"" + esc(name) + "\",\"email\":\"" + esc(email)
             + "\",\"phone\":\"" + esc(phone) + "\",\"address\":\"" + esc(address) + "\",\"studentType\":\"" + esc(studentType)
             + "\",\"fee\":" + calculateFee() + ",\"extraField1\":\"" + esc(getExtraField1())
             + "\",\"extraField2\":\"" + esc(getExtraField2()) + "\"}";
    }

    protected String esc(String s) { return s == null ? "" : s.replace("\"", "\\\""); }

    public static Student fromFileString(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 6) return null;
        if (p[5].trim().equalsIgnoreCase("Undergraduate"))
            return new UndergraduateStudent(p[0],p[1],p[2],p[3],p[4], p.length>6?Integer.parseInt(p[6].trim()):1, p.length>7?p[7]:"N/A");
        if (p[5].trim().equalsIgnoreCase("Postgraduate"))
            return new PostgraduateStudent(p[0],p[1],p[2],p[3],p[4], p.length>6?p[6]:"N/A", p.length>7?p[7]:"N/A");
        return new Student(p[0],p[1],p[2],p[3],p[4],p[5]);
    }
}

class UndergraduateStudent extends Student {
    private int year; private String faculty;
    public UndergraduateStudent(String id,String n,String e,String ph,String a,int year,String faculty) {
        super(id,n,e,ph,a,"Undergraduate"); this.year=year; this.faculty=faculty;
    }
    @Override public double calculateFee() { return 150000.0+(year*25000.0); }
    @Override public String getExtraField1() { return String.valueOf(year); }
    @Override public String getExtraField2() { return faculty; }
    @Override public String toFileString() { return super.toFileString()+"|"+year+"|"+faculty; }
}

class PostgraduateStudent extends Student {
    private String researchArea, supervisor;
    public PostgraduateStudent(String id,String n,String e,String ph,String a,String ra,String sv) {
        super(id,n,e,ph,a,"Postgraduate"); this.researchArea=ra; this.supervisor=sv;
    }
    @Override public double calculateFee() { return 400000.0; }
    @Override public String getExtraField1() { return researchArea; }
    @Override public String getExtraField2() { return supervisor; }
    @Override public String toFileString() { return super.toFileString()+"|"+researchArea+"|"+supervisor; }
}
