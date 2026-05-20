package service;

import model.Payment;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentService {
    private static final String FILE = "data/payments.txt";
    public PaymentService() { try{new File("data").mkdirs();new File(FILE).createNewFile();}catch(IOException e){} }

    public List<Payment> getAll() {
        List<Payment> list = new ArrayList<>();
        try(BufferedReader r = new BufferedReader(new FileReader(FILE))) {
            String line; while((line=r.readLine())!=null) { if(!line.trim().isEmpty()){Payment p=Payment.fromFileString(line);if(p!=null)list.add(p);} }
        }catch(IOException e){}
        return list;
    }

    public boolean add(Payment p) {
        List<Payment>l=getAll();int nid=1;
        for(Payment px:l){try{int n=Integer.parseInt(px.getPaymentId().replace("P",""));if(n>=nid)nid=n+1;}catch(Exception ex){}}
        p.setPaymentId("P"+String.format("%03d",nid));
        if(p.getPaymentDate()==null||p.getPaymentDate().isEmpty())p.setPaymentDate(LocalDate.now().toString());
        l.add(p);save(l);return true;
    }

    public boolean updateStatus(String id,String status) {
        List<Payment>l=getAll();for(int i=0;i<l.size();i++){if(l.get(i).getPaymentId().equalsIgnoreCase(id)){l.get(i).setStatus(status);save(l);return true;}} return false;
    }

    public boolean delete(String id) { List<Payment>l=getAll();for(int i=0;i<l.size();i++){if(l.get(i).getPaymentId().equalsIgnoreCase(id)){l.remove(i);save(l);return true;}} return false; }

    public List<Payment> getByStudent(String sid) { List<Payment>r=new ArrayList<>();for(Payment p:getAll())if(p.getStudentId().equalsIgnoreCase(sid))r.add(p);return r; }

    public double getTotalRevenue() { double t=0;for(Payment p:getAll())if("Completed".equalsIgnoreCase(p.getStatus()))t+=p.getAmount();return t; }

    private void save(List<Payment>l){try(BufferedWriter w=new BufferedWriter(new FileWriter(FILE))){for(Payment p:l){w.write(p.toFileString());w.newLine();}}catch(IOException e){}}
}
