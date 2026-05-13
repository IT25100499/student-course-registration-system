package service;

import model.Admin;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AdminService {
    private static final String FILE = "data/admins.txt";
    
    public AdminService() { 
        try {
            new File("data").mkdirs();
            File f = new File(FILE);
            if(f.createNewFile()) {
                // Create default admin on first run
                add(new Admin("admin", "admin123", "Super Admin"));
            }
        } catch(IOException e) {} 
    }

    public List<Admin> getAll() {
        List<Admin> list = new ArrayList<>();
        try(BufferedReader r = new BufferedReader(new FileReader(FILE))) {
            String line; 
            while((line=r.readLine())!=null) { 
                if(!line.trim().isEmpty()){
                    Admin a=Admin.fromFileString(line);
                    if(a!=null)list.add(a);
                } 
            }
        }catch(IOException e){}
        return list;
    }

    public Admin getByUsername(String username) { 
        for(Admin a:getAll()) 
            if(a.getUsername().equalsIgnoreCase(username))return a; 
        return null; 
    }

    public boolean add(Admin a) { 
        if(getByUsername(a.getUsername())!=null) return false; 
        List<Admin>l=getAll();
        l.add(a);
        save(l);
        return true; 
    }

    public boolean update(String username, String password, String name) {
        List<Admin>l=getAll();
        for(int i=0;i<l.size();i++){
            if(l.get(i).getUsername().equalsIgnoreCase(username)){
                Admin a=l.get(i);
                if(password!=null&&!password.isEmpty()) a.setPassword(password);
                if(name!=null&&!name.isEmpty()) a.setName(name);
                l.set(i,a);
                save(l);
                return true;
            }
        } 
        return false;
    }

    public boolean delete(String username) { 
        List<Admin>l=getAll();
        for(int i=0;i<l.size();i++){
            if(l.get(i).getUsername().equalsIgnoreCase(username)){
                l.remove(i);
                save(l);
                return true;
            }
        } 
        return false; 
    }

    private void save(List<Admin>l){
        try(BufferedWriter w=new BufferedWriter(new FileWriter(FILE))){
            for(Admin a:l){
                w.write(a.toFileString());
                w.newLine();
            }
        }catch(IOException e){}
    }
}
