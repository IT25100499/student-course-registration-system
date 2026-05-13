package service;

import model.Schedule;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleService {
    private static final String FILE = "data/schedules.txt";
    
    public ScheduleService() { 
        try {
            new File("data").mkdirs();
            new File(FILE).createNewFile();
        } catch(IOException e) {} 
    }

    public List<Schedule> getAll() {
        List<Schedule> list = new ArrayList<>();
        try(BufferedReader r = new BufferedReader(new FileReader(FILE))) {
            String line; 
            while((line=r.readLine())!=null) { 
                if(!line.trim().isEmpty()){
                    Schedule s = Schedule.fromFileString(line);
                    if(s != null) list.add(s);
                } 
            }
        }catch(IOException e){}
        return list;
    }

    public Schedule getById(String id) { 
        for(Schedule s : getAll()) 
            if(s.getScheduleId().equalsIgnoreCase(id)) return s; 
        return null; 
    }

    public boolean add(Schedule s) { 
        if(getById(s.getScheduleId()) != null) return false; 
        List<Schedule> l = getAll();
        l.add(s);
        save(l);
        return true; 
    }

    public boolean update(String id, String courseId, String lecturerId, String dateTime, String type, String location) {
        List<Schedule> l = getAll();
        for(int i=0; i<l.size(); i++){
            if(l.get(i).getScheduleId().equalsIgnoreCase(id)){
                Schedule s = l.get(i);
                if(courseId != null && !courseId.isEmpty()) s.setCourseId(courseId);
                if(lecturerId != null && !lecturerId.isEmpty()) s.setLecturerId(lecturerId);
                if(dateTime != null && !dateTime.isEmpty()) s.setDateTime(dateTime);
                if(type != null && !type.isEmpty()) s.setType(type);
                if(location != null && !location.isEmpty()) s.setLocation(location);
                l.set(i, s);
                save(l);
                return true;
            }
        } 
        return false;
    }

    public boolean delete(String id) { 
        List<Schedule> l = getAll();
        for(int i=0; i<l.size(); i++){
            if(l.get(i).getScheduleId().equalsIgnoreCase(id)){
                l.remove(i);
                save(l);
                return true;
            }
        } 
        return false; 
    }

    private void save(List<Schedule> l){
        try(BufferedWriter w = new BufferedWriter(new FileWriter(FILE))){
            for(Schedule s : l){
                w.write(s.toFileString());
                w.newLine();
            }
        }catch(IOException e){}
    }
}
