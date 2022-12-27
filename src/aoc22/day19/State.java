package aoc22.day19;

import java.util.ArrayList;
import java.util.List;

public class State {
    private int oreRobotsCount;
    private int clayRobotsCount;
    private int obsidianRobotsCount;
    private int geodeRobotsCount;
    private int minuteCount;
    private int totalOre;
    private int totalClay;
    private int totalObsidian;
    private int totalGeode;
    private List<State> path;

    public State(int oreRobotsCount, int clayRobotsCount, int obsidianRobotsCount,
                 int geodeRobotsCount, int minuteCount) {
        this.oreRobotsCount = oreRobotsCount;
        this.clayRobotsCount = clayRobotsCount;
        this.obsidianRobotsCount = obsidianRobotsCount;
        this.geodeRobotsCount = geodeRobotsCount;
        this.minuteCount = minuteCount;
        this.path = new ArrayList<>();
    }

    public State(int oreRobotsCount, int clayRobotsCount, int obsidianRobotsCount, int geodeRobotsCount,
                 int minuteCount, int totalOre, int totalClay, int totalObsidian, int totalGeode, List<State> path) {
        this(oreRobotsCount, clayRobotsCount, obsidianRobotsCount, geodeRobotsCount, minuteCount);
        this.totalOre = totalOre;
        this.totalClay = totalClay;
        this.totalObsidian = totalObsidian;
        this.totalGeode = totalGeode;
        this.path = path;
    }

    public int getOreRobotsCount() {
        return oreRobotsCount;
    }
    public int getClayRobotsCount() {
        return clayRobotsCount;
    }
    public int getObsidianRobotsCount() {
        return obsidianRobotsCount;
    }
    public int getGeodeRobotsCount() {
        return geodeRobotsCount;
    }
    public int getMinuteCount() {
        return minuteCount;
    }
    public int getTotalOre() {
        return totalOre;
    }
    public int getTotalClay() {
        return totalClay;
    }
    public int getTotalObsidian() {
        return totalObsidian;
    }
    public int getTotalGeode() {
        return totalGeode;
    }

    public void setOreRobotsCount(int oreRobotsCount) {
        this.oreRobotsCount = oreRobotsCount;
    }
    public void setClayRobotsCount(int clayRobotsCount) {
        this.clayRobotsCount = clayRobotsCount;
    }
    public void setObsidianRobotsCount(int obsidianRobotsCount) {
        this.obsidianRobotsCount = obsidianRobotsCount;
    }
    public void setGeodeRobotsCount(int geodeRobotsCount) {
        this.geodeRobotsCount = geodeRobotsCount;
    }
    public void setTotalOre(int totalOre) {
        this.totalOre = totalOre;
    }
    public void setTotalClay(int totalClay) {
        this.totalClay = totalClay;
    }
    public void setTotalObsidian(int totalObsidian) {
        this.totalObsidian = totalObsidian;
    }

    public void addToPath(State state) {
        this.path.add(state);
    }

    public void harvest() {
        this.minuteCount++;
        this.totalOre+= this.oreRobotsCount;
        this.totalClay+= this.clayRobotsCount;
        this.totalObsidian+= this.obsidianRobotsCount;
        this.totalGeode+= this.geodeRobotsCount;
    }

    @Override
    protected State clone() {
        return new State(this.oreRobotsCount, this.clayRobotsCount, this.obsidianRobotsCount,
                this.geodeRobotsCount, this.minuteCount, this.totalOre, this.totalClay,
                this.totalObsidian, this.totalGeode, new ArrayList<>(this.path));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state = (State) o;

        if (oreRobotsCount != state.oreRobotsCount) return false;
        if (clayRobotsCount != state.clayRobotsCount) return false;
        if (obsidianRobotsCount != state.obsidianRobotsCount) return false;
        if (geodeRobotsCount != state.geodeRobotsCount) return false;
        if (minuteCount != state.minuteCount) return false;
        if (totalOre != state.totalOre) return false;
        if (totalClay != state.totalClay) return false;
        if (totalObsidian != state.totalObsidian) return false;
        return totalGeode == state.totalGeode;
    }
    @Override
    public int hashCode() {
        int result = oreRobotsCount;
        result = 31 * result + clayRobotsCount;
        result = 31 * result + obsidianRobotsCount;
        result = 31 * result + geodeRobotsCount;
        result = 31 * result + minuteCount;
        result = 31 * result + totalOre;
        result = 31 * result + totalClay;
        result = 31 * result + totalObsidian;
        result = 31 * result + totalGeode;
        return result;
    }

    @Override
    public String toString() {
        return "State{" +
                "oreRobotsCount=" + oreRobotsCount +
                ", clayRobotsCount=" + clayRobotsCount +
                ", obsidianRobotsCount=" + obsidianRobotsCount +
                ", geodeRobotsCount=" + geodeRobotsCount +
                ", minuteCount=" + minuteCount +
                ", totalOre=" + totalOre +
                ", totalClay=" + totalClay +
                ", totalObsidian=" + totalObsidian +
                ", totalGeode=" + totalGeode +
                '}';
    }

    public String getSignature() {
        return  this.minuteCount + "|" +         //2-3 chars
                this.oreRobotsCount + "|" +      //2-3 chars
                this.clayRobotsCount + "|" +     //2-3 chars
                this.obsidianRobotsCount + "|" + //2-3 chars
                this.geodeRobotsCount + "|" +    //2-3 chars
                this.totalOre + "|" +            //2-3 chars
                this.totalClay + "|" +           //2-3 chars
                this.totalObsidian + "|" +       //2-3 chars
                this.totalGeode;                 //1-2 chars
                                                 // 17-26 chars total
    }
}
