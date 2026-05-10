package service;

import model.Student;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StudentService {
    private static final String FILE = "data/students.txt";
    public StudentService() { try{new File("data").mkdirs();new File(FILE).createNewFile();}catch(IOException e){} }

    public List<Student> getAll() {
        List<Student> list = new ArrayList<>();
        try(BufferedReader r = new BufferedReader(new FileReader(FILE))) {
            String line; while((line=r.readLine())!=null) { if(!line.trim().isEmpty()){Student s=Student.fromFileString(line);if(s!=null)list.add(s);} }
        }catch(IOException e){}
        return list;
    }

    public Student getById(String id) { for(Student s:getAll()) if(s.getStudentId().equalsIgnoreCase(id))return s; return null; }

    public boolean add(Student s) { if(getById(s.getStudentId())!=null)return false; List<Student>l=getAll();l.add(s);save(l);return true; }

    public boolean update(String id,String name,String email,String phone,String address) {
        List<Student>l=getAll();
        for(int i=0;i<l.size();i++){if(l.get(i).getStudentId().equalsIgnoreCase(id)){
            Student s=l.get(i);
            if(name!=null&&!name.isEmpty())s.setName(name);if(email!=null&&!email.isEmpty())s.setEmail(email);
            if(phone!=null&&!phone.isEmpty())s.setPhone(phone);if(address!=null&&!address.isEmpty())s.setAddress(address);
            l.set(i,s);save(l);return true;
        }} return false;
    }

    public boolean delete(String id) { List<Student>l=getAll();for(int i=0;i<l.size();i++){if(l.get(i).getStudentId().equalsIgnoreCase(id)){l.remove(i);save(l);return true;}} return false; }

    private void save(List<Student>l){try(BufferedWriter w=new BufferedWriter(new FileWriter(FILE))){for(Student s:l){w.write(s.toFileString());w.newLine();}}catch(IOException e){}}
}
