package service;

import model.Course;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CourseService {
    private static final String FILE = "data/courses.txt";
    public CourseService() { try{new File("data").mkdirs();new File(FILE).createNewFile();}catch(IOException e){} }

    public List<Course> getAll() {
        List<Course> list = new ArrayList<>();
        try(BufferedReader r = new BufferedReader(new FileReader(FILE))) {
            String line; while((line=r.readLine())!=null) { if(!line.trim().isEmpty()){Course c=Course.fromFileString(line);if(c!=null)list.add(c);} }
        }catch(IOException e){}
        return list;
    }

    public Course getById(String id) { for(Course c:getAll()) if(c.getCourseId().equalsIgnoreCase(id))return c; return null; }

    public boolean add(Course c) { if(getById(c.getCourseId())!=null)return false; List<Course>l=getAll();l.add(c);save(l);return true; }

    public boolean update(String id,String name,String credits,String dept,String max,String fee) {
        List<Course>l=getAll();
        for(int i=0;i<l.size();i++){if(l.get(i).getCourseId().equalsIgnoreCase(id)){
            Course c=l.get(i);
            if(name!=null&&!name.isEmpty())c.setCourseName(name);
            if(credits!=null&&!credits.isEmpty())c.setCredits(Integer.parseInt(credits));
            if(dept!=null&&!dept.isEmpty())c.setDepartment(dept);
            if(max!=null&&!max.isEmpty())c.setMaxStudents(Integer.parseInt(max));
            if(fee!=null&&!fee.isEmpty())c.setFee(Double.parseDouble(fee));
            l.set(i,c);save(l);return true;
        }} return false;
    }

    public boolean delete(String id) { List<Course>l=getAll();for(int i=0;i<l.size();i++){if(l.get(i).getCourseId().equalsIgnoreCase(id)){l.remove(i);save(l);return true;}} return false; }

    private void save(List<Course>l){try(BufferedWriter w=new BufferedWriter(new FileWriter(FILE))){for(Course c:l){w.write(c.toFileString());w.newLine();}}catch(IOException e){}}
}
