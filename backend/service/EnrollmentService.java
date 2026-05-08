package service;

import model.Enrollment;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentService {
    private static final String FILE = "data/enrollments.txt";
    private StudentService ss; private CourseService cs;

    public EnrollmentService(StudentService ss,CourseService cs) {
        this.ss=ss;this.cs=cs;
        try{new File("data").mkdirs();new File(FILE).createNewFile();}catch(IOException e){}
    }

    public List<Enrollment> getAll() {
        List<Enrollment> list = new ArrayList<>();
        try(BufferedReader r = new BufferedReader(new FileReader(FILE))) {
            String line; while((line=r.readLine())!=null) { if(!line.trim().isEmpty()){Enrollment e=Enrollment.fromFileString(line);if(e!=null)list.add(e);} }
        }catch(IOException e){}
        return list;
    }

    public String register(String sid,String cid,String type) {
        if(ss.getById(sid)==null)return "Student not found";
        if(cs.getById(cid)==null)return "Course not found";
        List<Enrollment>l=getAll();
        for(Enrollment e:l) if(e.getStudentId().equalsIgnoreCase(sid)&&e.getCourseId().equalsIgnoreCase(cid)&&e.getStatus().equalsIgnoreCase("Active")) return "Already enrolled";
        int max=type.equalsIgnoreCase("Full-Time")?Enrollment.FT_MAX:Enrollment.PT_MAX;
        long active=l.stream().filter(e->e.getStudentId().equalsIgnoreCase(sid)&&e.getStatus().equalsIgnoreCase("Active")).count();
        if(active>=max)return "Max limit reached for "+type;
        int nid=1;for(Enrollment e:l){try{int n=Integer.parseInt(e.getEnrollmentId().replace("E",""));if(n>=nid)nid=n+1;}catch(Exception ex){}}
        l.add(new Enrollment("E"+String.format("%03d",nid),sid,cid,LocalDate.now().toString(),"Pending",type));
        save(l);return "success";
    }

    public boolean updateStatus(String eid, String status) {
        List<Enrollment>l=getAll();
        for(int i=0;i<l.size();i++){if(l.get(i).getEnrollmentId().equalsIgnoreCase(eid)){l.get(i).setStatus(status);save(l);return true;}}
        return false;
    }

    public boolean drop(String eid) {
        List<Enrollment>l=getAll();
        for(int i=0;i<l.size();i++){if(l.get(i).getEnrollmentId().equalsIgnoreCase(eid)){l.get(i).setStatus("Dropped");save(l);return true;}}
        return false;
    }

    public List<Enrollment> getByStudent(String sid) {
        List<Enrollment>r=new ArrayList<>();for(Enrollment e:getAll())if(e.getStudentId().equalsIgnoreCase(sid))r.add(e);return r;
    }

    private void save(List<Enrollment>l){try(BufferedWriter w=new BufferedWriter(new FileWriter(FILE))){for(Enrollment e:l){w.write(e.toFileString());w.newLine();}}catch(IOException e){}}
}
