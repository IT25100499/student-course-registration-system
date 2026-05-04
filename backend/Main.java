import com.sun.net.httpserver.*;
import model.*;
import service.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main.java - HTTP Server + REST API + Frontend Serving
 * Backend runs on port 8080, serves frontend from ../frontend
 */
public class Main {
    static StudentService studentSvc = new StudentService();
    static CourseService courseSvc = new CourseService();
    static EnrollmentService enrollSvc = new EnrollmentService(studentSvc, courseSvc);
    static LecturerService lecturerSvc = new LecturerService();
    
    static AdminService adminSvc = new AdminService();
    static ScheduleService scheduleSvc = new ScheduleService();
    static String FRONTEND = "../frontend";

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/login", Main::handleLogin);
        server.createContext("/api/students", Main::handleStudents);
        server.createContext("/api/courses", Main::handleCourses);
        server.createContext("/api/enrollments", Main::handleEnrollments);
        server.createContext("/api/lecturers", Main::handleLecturers);
        
        server.createContext("/api/admins", Main::handleAdmins);
        server.createContext("/api/schedules", Main::handleSchedules);
        server.createContext("/api/dashboard", Main::handleDashboard);
        server.createContext("/", Main::serveFrontend);
        server.setExecutor(null);
        server.start();
        System.out.println("========================================");
        System.out.println("  Student Course Registration System");
        System.out.println("  http://localhost:8080");
        System.out.println("  Login: admin / admin123");
        System.out.println("========================================");
    }

    // --- Utility ---
    static void cors(HttpExchange x){x.getResponseHeaders().set("Access-Control-Allow-Origin","*");x.getResponseHeaders().set("Access-Control-Allow-Methods","GET,POST,PUT,DELETE,OPTIONS");x.getResponseHeaders().set("Access-Control-Allow-Headers","Content-Type");}
    static void json(HttpExchange x,int c,String j)throws IOException{cors(x);x.getResponseHeaders().set("Content-Type","application/json");byte[]b=j.getBytes("UTF-8");x.sendResponseHeaders(c,b.length);x.getResponseBody().write(b);x.getResponseBody().close();}
    static String body(HttpExchange x)throws IOException{return new String(x.getRequestBody().readAllBytes(),"UTF-8");}
    static String jval(String j,String k){String s="\""+k+"\"";int i=j.indexOf(s);if(i==-1)return"";i=j.indexOf(":",i)+1;while(i<j.length()&&j.charAt(i)==' ')i++;if(i>=j.length())return"";if(j.charAt(i)=='"'){int e=j.indexOf("\"",i+1);return e==-1?"":j.substring(i+1,e);}int e=i;while(e<j.length()&&j.charAt(e)!=','&&j.charAt(e)!='}')e++;return j.substring(i,e).trim();}

    // --- Frontend ---
    static void serveFrontend(HttpExchange x) throws IOException {
        String path = x.getRequestURI().getPath();
        if(path.equals("/")) path = "/index.html";
        File f = new File(FRONTEND + path);
        if(!f.exists()||f.isDirectory()) f = new File(FRONTEND+"/index.html");
        String ct="text/html";
        if(path.endsWith(".css"))ct="text/css";else if(path.endsWith(".js"))ct="application/javascript";
        else if(path.endsWith(".png"))ct="image/png";else if(path.endsWith(".jpg")||path.endsWith(".jpeg"))ct="image/jpeg";
        else if(path.endsWith(".svg"))ct="image/svg+xml";else if(path.endsWith(".ico"))ct="image/x-icon";
        x.getResponseHeaders().set("Content-Type",ct);
        byte[]b=Files.readAllBytes(f.toPath());x.sendResponseHeaders(200,b.length);x.getResponseBody().write(b);x.getResponseBody().close();
    }

    // --- Login ---
    static void handleLogin(HttpExchange x) throws IOException {
        cors(x);if(x.getRequestMethod().equals("OPTIONS")){json(x,200,"{}");return;}
        String b=body(x);
        String username = jval(b,"username");
        String password = jval(b,"password");
        String role = Admin.authenticate(username, password, adminSvc, studentSvc);
        if(role != null) {
            json(x,200,"{\"success\":true,\"role\":\"" + role + "\",\"username\":\"" + username + "\"}");
        } else {
            json(x,401,"{\"success\":false,\"message\":\"Invalid credentials\"}");
        }
    }

    // --- Admins ---
    static void handleAdmins(HttpExchange x) throws IOException {
        cors(x);if(x.getRequestMethod().equals("OPTIONS")){json(x,200,"{}");return;}
        String p=x.getRequestURI().getPath(),m=x.getRequestMethod();String[]pts=p.split("/");
        if(m.equals("GET")){
            if(pts.length>3){Admin a=adminSvc.getByUsername(pts[3]);json(x,a!=null?200:404,a!=null?a.toJson():"{\"error\":\"Not found\"}");}
            else json(x,200,"["+adminSvc.getAll().stream().map(Admin::toJson).collect(Collectors.joining(","))+"]");
        }else if(m.equals("POST")){
            String b=body(x);
            Admin a=new Admin(jval(b,"username"),jval(b,"password"),jval(b,"name"));
            json(x,adminSvc.add(a)?201:409,adminSvc.getByUsername(jval(b,"username"))!=null?"{\"success\":true}":"{\"error\":\"Username exists\"}");
        }else if(m.equals("PUT")){
            String b=body(x),id=pts.length>3?pts[3]:jval(b,"username");
            json(x,adminSvc.update(id,jval(b,"password"),jval(b,"name"))?200:404,"{\"success\":true}");
        }else if(m.equals("DELETE")){json(x,adminSvc.delete(pts.length>3?pts[3]:"")?200:404,"{\"success\":true}");}
    }

    // --- Students ---
    static void handleStudents(HttpExchange x) throws IOException {
        cors(x);if(x.getRequestMethod().equals("OPTIONS")){json(x,200,"{}");return;}
        String p=x.getRequestURI().getPath(),m=x.getRequestMethod();String[]pts=p.split("/");
        if(m.equals("GET")){
            if(pts.length>3){Student s=studentSvc.getById(pts[3]);json(x,s!=null?200:404,s!=null?s.toJson():"{\"error\":\"Not found\"}");}
            else json(x,200,"["+studentSvc.getAll().stream().map(Student::toJson).collect(Collectors.joining(","))+"]");
        }else if(m.equals("POST")){
            String b=body(x),t=jval(b,"studentType");Student s;
            if(t.equalsIgnoreCase("Undergraduate"))s=Student.fromFileString(jval(b,"studentId")+"|"+jval(b,"name")+"|"+jval(b,"email")+"|"+jval(b,"phone")+"|"+jval(b,"address")+"|Undergraduate|"+jval(b,"extraField1")+"|"+jval(b,"extraField2"));
            else if(t.equalsIgnoreCase("Postgraduate"))s=Student.fromFileString(jval(b,"studentId")+"|"+jval(b,"name")+"|"+jval(b,"email")+"|"+jval(b,"phone")+"|"+jval(b,"address")+"|Postgraduate|"+jval(b,"extraField1")+"|"+jval(b,"extraField2"));
            else s=new Student(jval(b,"studentId"),jval(b,"name"),jval(b,"email"),jval(b,"phone"),jval(b,"address"),t);
            json(x,studentSvc.add(s)?201:409,studentSvc.getById(jval(b,"studentId"))!=null?"{\"success\":true}":"{\"error\":\"ID exists\"}");
        }else if(m.equals("PUT")){
            String b=body(x),id=pts.length>3?pts[3]:jval(b,"studentId");
            json(x,studentSvc.update(id,jval(b,"name"),jval(b,"email"),jval(b,"phone"),jval(b,"address"))?200:404,"{\"success\":true}");
        }else if(m.equals("DELETE")){json(x,studentSvc.delete(pts.length>3?pts[3]:"")?200:404,"{\"success\":true}");}
    }

    // --- Courses ---
    static void handleCourses(HttpExchange x) throws IOException {
        cors(x);if(x.getRequestMethod().equals("OPTIONS")){json(x,200,"{}");return;}
        String p=x.getRequestURI().getPath(),m=x.getRequestMethod();String[]pts=p.split("/");
        if(m.equals("GET")){
            if(pts.length>3){Course c=courseSvc.getById(pts[3]);json(x,c!=null?200:404,c!=null?c.toJson():"{\"error\":\"Not found\"}");}
            else json(x,200,"["+courseSvc.getAll().stream().map(Course::toJson).collect(Collectors.joining(","))+"]");
        }else if(m.equals("POST")){
            String b=body(x),t=jval(b,"courseType");int cr=3,mx=30;double f=0.0;
            try{cr=Integer.parseInt(jval(b,"credits"));}catch(Exception e){}
            try{mx=Integer.parseInt(jval(b,"maxStudents"));}catch(Exception e){}
            try{f=Double.parseDouble(jval(b,"fee"));}catch(Exception e){}
            Course c;
            if(t.equalsIgnoreCase("Core"))c=Course.fromFileString(jval(b,"courseId")+"|"+jval(b,"courseName")+"|"+cr+"|"+jval(b,"department")+"|"+mx+"|"+f+"|Core|"+jval(b,"extraField"));
            else if(t.equalsIgnoreCase("Elective"))c=Course.fromFileString(jval(b,"courseId")+"|"+jval(b,"courseName")+"|"+cr+"|"+jval(b,"department")+"|"+mx+"|"+f+"|Elective|"+jval(b,"extraField"));
            else c=new Course(jval(b,"courseId"),jval(b,"courseName"),cr,jval(b,"department"),mx,f,t);
            json(x,courseSvc.add(c)?201:409,"{\"success\":true}");
        }else if(m.equals("PUT")){
            String b=body(x),id=pts.length>3?pts[3]:jval(b,"courseId");
            json(x,courseSvc.update(id,jval(b,"courseName"),jval(b,"credits"),jval(b,"department"),jval(b,"maxStudents"),jval(b,"fee"))?200:404,"{\"success\":true}");
        }else if(m.equals("DELETE")){json(x,courseSvc.delete(pts.length>3?pts[3]:"")?200:404,"{\"success\":true}");}
    }

    // --- Enrollments ---
    static void handleEnrollments(HttpExchange x) throws IOException {
        cors(x);if(x.getRequestMethod().equals("OPTIONS")){json(x,200,"{}");return;}
        String m=x.getRequestMethod();
        if(m.equals("GET")){
            String q=x.getRequestURI().getQuery();List<Enrollment>l;
            if(q!=null&&q.contains("studentId="))l=enrollSvc.getByStudent(q.split("studentId=")[1].split("&")[0]);else l=enrollSvc.getAll();
            json(x,200,"["+l.stream().map(Enrollment::toJson).collect(Collectors.joining(","))+"]");
        }else if(m.equals("POST")){
            String b=body(x),r=enrollSvc.register(jval(b,"studentId"),jval(b,"courseId"),jval(b,"enrollmentType"));
            json(x,r.equals("success")?201:400,r.equals("success")?"{\"success\":true}":"{\"error\":\""+r+"\"}");
        }else if(m.equals("DELETE")){
            String[]pts=x.getRequestURI().getPath().split("/");
            json(x,enrollSvc.drop(pts.length>3?pts[3]:"")?200:404,"{\"success\":true}");
        }
    }

    // --- Lecturers ---
    static void handleLecturers(HttpExchange x) throws IOException {
        cors(x);if(x.getRequestMethod().equals("OPTIONS")){json(x,200,"{}");return;}
        String p=x.getRequestURI().getPath(),m=x.getRequestMethod();String[]pts=p.split("/");
        if(m.equals("GET")){
            if(pts.length>3){Lecturer l=lecturerSvc.getById(pts[3]);json(x,l!=null?200:404,l!=null?l.toJson():"{\"error\":\"Not found\"}");}
            else json(x,200,"["+lecturerSvc.getAll().stream().map(Lecturer::toJson).collect(Collectors.joining(","))+"]");
        }else if(m.equals("POST")){
            String b=body(x);Lecturer l=Lecturer.fromFileString(jval(b,"lecturerId")+"|"+jval(b,"name")+"|"+jval(b,"email")+"|"+jval(b,"phone")+"|"+jval(b,"department")+"|"+jval(b,"assignedCourseId")+"|"+jval(b,"lecturerType")+"|"+jval(b,"extraField"));
            json(x,l!=null&&lecturerSvc.add(l)?201:409,"{\"success\":true}");
        }else if(m.equals("PUT")){
            String b=body(x),id=pts.length>3?pts[3]:jval(b,"lecturerId");
            json(x,lecturerSvc.update(id,jval(b,"name"),jval(b,"email"),jval(b,"phone"),jval(b,"department"),jval(b,"assignedCourseId"))?200:404,"{\"success\":true}");
        }else if(m.equals("DELETE")){json(x,lecturerSvc.delete(pts.length>3?pts[3]:"")?200:404,"{\"success\":true}");}
    }

    // --- Schedules ---
    static void handleSchedules(HttpExchange x) throws IOException {
        cors(x);if(x.getRequestMethod().equals("OPTIONS")){json(x,200,"{}");return;}
        String p=x.getRequestURI().getPath(),m=x.getRequestMethod();String[]pts=p.split("/");
        if(m.equals("GET")){
            if(pts.length>3){Schedule s=scheduleSvc.getById(pts[3]);json(x,s!=null?200:404,s!=null?s.toJson():"{\"error\":\"Not found\"}");}
            else json(x,200,"["+scheduleSvc.getAll().stream().map(Schedule::toJson).collect(Collectors.joining(","))+"]");
        }else if(m.equals("POST")){
            String b=body(x);
            Schedule s=new Schedule(jval(b,"scheduleId"),jval(b,"courseId"),jval(b,"lecturerId"),jval(b,"dateTime"),jval(b,"type"),jval(b,"location"));
            json(x,scheduleSvc.add(s)?201:409,scheduleSvc.getById(jval(b,"scheduleId"))!=null?"{\"success\":true}":"{\"error\":\"ID exists\"}");
        }else if(m.equals("PUT")){
            String b=body(x),id=pts.length>3?pts[3]:jval(b,"scheduleId");
            json(x,scheduleSvc.update(id,jval(b,"courseId"),jval(b,"lecturerId"),jval(b,"dateTime"),jval(b,"type"),jval(b,"location"))?200:404,"{\"success\":true}");
        }else if(m.equals("DELETE")){json(x,scheduleSvc.delete(pts.length>3?pts[3]:"")?200:404,"{\"success\":true}");}
    }

    // --- Dashboard ---
    static void handleDashboard(HttpExchange x) throws IOException {
        cors(x);if(x.getRequestMethod().equals("OPTIONS")){json(x,200,"{}");return;}
        json(x,200,"{\"students\":"+studentSvc.getAll().size()+",\"courses\":"+courseSvc.getAll().size()
            +",\"enrollments\":"+enrollSvc.getAll().size()+",\"lecturers\":"+lecturerSvc.getAll().size()
            + "}");
    }
}
