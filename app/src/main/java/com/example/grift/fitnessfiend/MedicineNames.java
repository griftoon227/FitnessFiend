package com.example.grift.fitnessfiend;

public class MedicineNames {
    private String mMedicationName;
    //private String mDate;

    MedicineNames(String mMedicationName) {
        this.mMedicationName = mMedicationName;
        //this.mDate = mDate;
    }

   String getmMedicationName() {
        return mMedicationName;
    }

    public void setmMedicationName(String mMedicationName) {
        this.mMedicationName = mMedicationName;
    }
}
