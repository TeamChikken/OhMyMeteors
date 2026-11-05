package me.emafire003.dev.ohmymeteors.util.datasaver;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class SimpleData {

    @Expose
    public List<String> enteredDimensions = new ArrayList<>(List.of("minecraft:overworld"));

    @Expose
    public int chanceIncrements = 0;

    @Expose
    public long lastIncreaseTickTime = 0;

    @Expose
    public boolean dimensionTriggered = false;

    @Expose
    public boolean timeTriggered = false;

    public boolean isTimeTriggered() {
        return timeTriggered;
    }

    public void setTimeTriggered(boolean timeTriggered) {
        this.timeTriggered = timeTriggered;
    }

    public boolean isDimensionTriggered() {
        return dimensionTriggered;
    }

    public void setDimensionTriggered(boolean dimensionTriggered) {
        this.dimensionTriggered = dimensionTriggered;
    }

    public long getLastIncreaseTickTime() {
        return lastIncreaseTickTime;
    }

    public void setLastIncreaseTickTime(long lastIncreaseTickTime) {
        this.lastIncreaseTickTime = lastIncreaseTickTime;
    }

    public List<String> getEnteredDimensions() {
        return enteredDimensions;
    }

    public void setEnteredDimensions(List<String> enteredDimensions) {
        this.enteredDimensions = enteredDimensions;
    }

    public int getChanceIncrements() {
        return chanceIncrements;
    }

    public void setChanceIncrements(int chanceIncrements) {
        this.chanceIncrements = chanceIncrements;
    }

    @Override
    public String toString() {
        return "SimpleData{" +
                "enteredDimensions=" + enteredDimensions +
                ", chanceIncrements=" + chanceIncrements +
                ", lastIncreaseTickTime=" + lastIncreaseTickTime +
                ", dimensionTriggered=" + dimensionTriggered +
                ", timeTrigger=" + timeTriggered +
                '}';
    }
}
