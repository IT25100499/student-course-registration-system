package service;

import model.Lecturer;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LecturerService {
    private static final String FILE = "data/lecturers.txt";
    public LecturerService() { try{new File("data").mkdirs();new File(FILE).createNewFile();}catch(IOException e){} }

    public List<Lecturer> getAll() {
        List<Lecturer> list = new ArrayList<>();
        try(BufferedReader r = new BufferedReader(new FileReader(FILE))) {
            String line; while((line=r.readLine())!=null) { if(!line.trim().isEmpty()){Lecturer l=Lecturer.fromFileString(line);if(l!=null)list.add(l);} }
        }catch(IOException e){}
        return list;
    }

    public Lecturer getById(String id) { for(Lecturer l:getAll()) if(l.getLecturerId().equalsIgnoreCase(id))return l; return null; }

    public boolean add(Lecturer lec) { if(getById(lec.getLecturerId())!=null)return false; List<Lecturer>l=getAll();l.add(lec);save(l);return true; }

    public boolean update(String id,String name,String email,String phone,String dept,String courseId) {
        List<Lecturer>l=getAll();
        for(int i=0;i<l.size();i++){if(l.get(i).getLecturerId().equalsIgnoreCase(id)){
            Lecturer lec=l.get(i);
            if(name!=null&&!name.isEmpty())lec.setName(name);if(email!=null&&!email.isEmpty())lec.setEmail(email);
            if(phone!=null&&!phone.isEmpty())lec.setPhone(phone);if(dept!=null&&!dept.isEmpty())lec.setDepartment(dept);
            if(courseId!=null&&!courseId.isEmpty())lec.setAssignedCourseId(courseId);
            l.set(i,lec);save(l);return true;
        }} return false;
    }

    public boolean delete(String id) { List<Lecturer>l=getAll();for(int i=0;i<l.size();i++){if(l.get(i).getLecturerId().equalsIgnoreCase(id)){l.remove(i);save(l);return true;}} return false; }

    private void save(List<Lecturer>l){try(BufferedWriter w=new BufferedWriter(new FileWriter(FILE))){for(Lecturer lec:l){w.write(lec.toFileString());w.newLine();}}catch(IOException e){}}
}
