package com.example.eric.application;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Description of the class:
 * user_log information class.
 * This class keeps track of the participant's information.
 * The ID is saved and also the interactions.
 * An instance of this class is passed between the activities.
 */
public class UserInputLog implements Parcelable{

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class variables                                                               //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    private long user_id=0;
    private int versuch;
    private String modalitaet;
    //versuchsteil 1
    private String alarmtyp;
    private String clickedButtonType;
    private String popuptime;
    private String clicktime;
    private String clearing;
    //versuchsteil 2
    private String prozess_id;
    private String processBlendInTime;
    private String confirmationtime;

    //Namen der Modalität
    public final String MONITOR = "Monitor";
    public final String WATCH = "Watch";
    public final String BRILLE = "Brille";

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // constructors, getters, setters                                                //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    UserInputLog(){

    }

    UserInputLog(Parcel in){
        Log.i("UILog Operation","Parcel constructor");
        user_id = in.readLong();
        versuch = in.readInt();
        modalitaet = in.readString();
        //versuchstiel 1
        alarmtyp = in.readString();
        clickedButtonType = in.readString();
        popuptime = in.readString();
        clearing = in.readString();
        //versuchsteil 2
        prozess_id = in.readString();
        processBlendInTime = in.readString();
        confirmationtime = in.readString();
    }

    public String getAlarmtyp() {
        return alarmtyp;
    }

    public String getClearing() {
        if(getAlarmtyp()==getClickedButtonType()) {
            return "ja";
        }else {
            return "fehler";
        }
    }

    public String getClicktime() {
        return clicktime;
    }

    public String getClickedButtonType() {
        return clickedButtonType;
    }

    public String getConfirmationtime() {
        return confirmationtime;
    }

    public String getModalitaet() {
        return modalitaet;
    }

    public String getPopuptime() {
        return popuptime;
    }

    public String getProzess_id() {
        return prozess_id;
    }

    public String getProcessBlendInTime() {
        return processBlendInTime;
    }

    public long getUser_id() {
        return user_id;
    }

    public int getVersuch() {
        return versuch;
    }

    public void setAlarmtyp(String alarmtyp) {
        this.alarmtyp = alarmtyp;
    }

    public void setClicktime(String clicktime) {
        this.clicktime = clicktime;
    }

    public void setClickedButtonType(String korrekturbutton) {
        this.clickedButtonType = korrekturbutton;
    }

    public void setConfirmationtime(String confirmationtime){
        this.confirmationtime = confirmationtime;
    }

    public void setModalitaet(String modalitaet) {
        this.modalitaet = modalitaet;
    }

    public void setPopuptime(String popuptime) {
        this.popuptime = popuptime;
    }

    public void setProzess_id(String prozess_id){
        this.prozess_id = prozess_id;
    }

    public void setProcessBlendInTime(String processBlendInTime){
        this.processBlendInTime = processBlendInTime;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public void setVersuch(int versuch) {
        this.versuch = versuch;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //                                                                               //
    // class functions to provide the essential class functionality                  //
    //                                                                               //
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.i("UILog Operation", "writeToParcel");
        dest.writeLong(user_id);
        dest.writeInt(versuch);
        dest.writeString(modalitaet);
        dest.writeString(alarmtyp);
        dest.writeString(clickedButtonType);
        dest.writeString(popuptime);
        dest.writeString(clicktime);
        dest.writeString(clearing);
        //versuchsteil 2
        dest.writeString(modalitaet);
        dest.writeString(prozess_id);
        dest.writeString(processBlendInTime);
        dest.writeString(confirmationtime);
    }

    //To pass Log Information, make UserInput parcelable
    public static final Parcelable.Creator<UserInputLog> CREATOR =
            new Parcelable.Creator<UserInputLog>(){

                @Override
                public UserInputLog createFromParcel(Parcel source) {
                    Log.i("UILog Operation","Create From Parcel");
                    return new UserInputLog(source);
                }

                @Override
                public UserInputLog[] newArray(int size){
                    Log.i("UILog Operation","newArray");
                    return new UserInputLog[size];
                }
            };
}
