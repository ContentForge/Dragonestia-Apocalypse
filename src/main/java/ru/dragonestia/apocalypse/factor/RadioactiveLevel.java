package ru.dragonestia.apocalypse.factor;

import java.util.Random;

public enum RadioactiveLevel {

    ZERO(0),
    LOW(30),
    NORMAL(300),
    HIGH(6000),
    CRITICAL(12000);

    public final int dose;
    private static final Random random = new Random();

    RadioactiveLevel(int dose){
        this.dose = dose;
    }

    public double getGroundDose(){
        int rad = random.nextInt(dose / 5) - (dose / 10);
        double result = (dose + rad) / 10.0 + (random.nextFloat() < 0.1? 1 : 0);
        return result < 0? 0 : result;
    }

}
