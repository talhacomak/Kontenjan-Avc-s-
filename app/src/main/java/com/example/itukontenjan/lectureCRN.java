package com.example.itukontenjan;

public class lectureCRN {
    int id;
    String crn;
    String lecture;

    public lectureCRN(){

    }

    public lectureCRN(String crn, String lecture){
        this.crn = crn;
        this.lecture = lecture;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getCrn(){
        return crn;
    }

    public void setCrn(String crn){
        this.crn = crn;
    }

    public String getLecture(){
        return lecture;
    }

    public void setLecture(String lecture){
        this.lecture = lecture;
    }
}
