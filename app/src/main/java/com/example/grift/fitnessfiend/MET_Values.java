package com.example.grift.fitnessfiend;

enum MET_Values {
    lighter_calisthenics_training(2.8),
    lighter_cardio_training(2.3),
    lighter_resistance_training(3.5),
    moderate_calisthenics_training(3.8),
    moderate_cardio_training(5.0),
    moderate_resistance_training(5.0),
    vigorous_calisthenics_training(8.0),
    vigorous_cardio_training(9.0),
    vigorous_resistance_training(6.0);

    private double v;

    MET_Values(double v) {
        this.v = v;
    }

    public double getV() {
        return v;
    }
}
