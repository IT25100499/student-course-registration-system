package model;

/**
 * Admin.java - Represents a system administrator
 * OOP: Encapsulation (secure credentials)
 */
public class Admin {
    private String username;
    private String password;
    private String name;

    public Admin(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String toFileString() {
        return username + "|" + password + "|" + name;
    }

    public static Admin fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length >= 3) {
            return new Admin(parts[0], parts[1], parts[2]);
        }
        return null;
    }

    public String toJson() {
        return "{\"username\":\"" + username + "\",\"name\":\"" + name + "\"}"; // exclude password
    }

    // Student default password
    public static final String STUDENT_PASS = "student123";

    /**
     * Validate login and return role
     */
    public static String authenticate(String username, String password, service.AdminService adminSvc, service.StudentService studentSvc) {
        // Check admin login
        Admin a = adminSvc.getByUsername(username);
        if (a != null && a.getPassword().equals(password)) {
            return "admin";
        }
        
        // Check student login (studentId + default password)
        if (STUDENT_PASS.equals(password) && studentSvc != null) {
            Student student = studentSvc.getById(username);
            if (student != null) {
                return "student";
            }
        }
        return null;
    }
}
