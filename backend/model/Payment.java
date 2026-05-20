package model;

/**
 * Payment.java - Payment, OnlinePayment, CashPayment
 * OOP: Encapsulation, Inheritance, Polymorphism
 */
public class Payment {
    private String paymentId,studentId,courseId,paymentDate,status,paymentType;
    private double amount;

    public Payment(){}
    public Payment(String paymentId,String studentId,String courseId,double amount,String paymentDate,String status,String paymentType){
        this.paymentId=paymentId;this.studentId=studentId;this.courseId=courseId;this.amount=amount;
        this.paymentDate=paymentDate;this.status=status;this.paymentType=paymentType;
    }

    public String getPaymentId(){return paymentId;} public void setPaymentId(String v){paymentId=v;}
    public String getStudentId(){return studentId;} public void setStudentId(String v){studentId=v;}
    public String getCourseId(){return courseId;} public void setCourseId(String v){courseId=v;}
    public double getAmount(){return amount;} public void setAmount(double v){amount=v;}
    public String getPaymentDate(){return paymentDate;} public void setPaymentDate(String v){paymentDate=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public String getPaymentType(){return paymentType;} public void setPaymentType(String v){paymentType=v;}

    public String processPayment(){return "Processing...";}
    public String getExtraField(){return "";}

    protected String esc(String s){return s==null?"":s.replace("\"","\\\"");}

    public String toFileString(){return paymentId+"|"+studentId+"|"+courseId+"|"+amount+"|"+paymentDate+"|"+status+"|"+paymentType;}

    public String toJson(){
        return "{\"paymentId\":\""+esc(paymentId)+"\",\"studentId\":\""+esc(studentId)+"\",\"courseId\":\""+esc(courseId)
            +"\",\"amount\":"+amount+",\"paymentDate\":\""+esc(paymentDate)
            +"\",\"status\":\""+esc(status)+"\",\"paymentType\":\""+esc(paymentType)
            +"\",\"extraField\":\""+esc(getExtraField())+"\"}";
    }

    public static Payment fromFileString(String line){
        String[]p=line.split("\\|",-1);if(p.length<7)return null;
        double amt=Double.parseDouble(p[3].trim());
        if(p[6].trim().equalsIgnoreCase("Online"))
            return new OnlinePayment(p[0],p[1],p[2],amt,p[4],p[5],p.length>7?p[7]:"N/A");
        if(p[6].trim().equalsIgnoreCase("Cash"))
            return new CashPayment(p[0],p[1],p[2],amt,p[4],p[5],p.length>7?p[7]:"Main Office");
        return new Payment(p[0],p[1],p[2],amt,p[4],p[5],p[6]);
    }
}

class OnlinePayment extends Payment {
    private String transactionRef;
    public OnlinePayment(String id,String sid,String cid,double amt,String dt,String st,String ref){
        super(id,sid,cid,amt,dt,st,"Online");transactionRef=ref;
    }
    @Override public String processPayment(){return "Online payment via "+transactionRef;}
    @Override public String getExtraField(){return transactionRef;}
    @Override public String toFileString(){return super.toFileString()+"|"+transactionRef;}
}

class CashPayment extends Payment {
    private String location;
    public CashPayment(String id,String sid,String cid,double amt,String dt,String st,String loc){
        super(id,sid,cid,amt,dt,st,"Cash");location=loc;
    }
    @Override public String processPayment(){return "Cash at "+location;}
    @Override public String getExtraField(){return location;}
    @Override public String toFileString(){return super.toFileString()+"|"+location;}
}
