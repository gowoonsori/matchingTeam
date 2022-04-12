package it_lab.dev_team_1.domain;

import java.util.*;

public class Member implements Cloneable{
    private final Integer id;
    private final String name;
    private boolean isSelected = false;
    private int[] meetCounts;

    public Member(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * 팀원의 선택 순위 가중치 get
     * 해당 프로그램에서는 표준편차 이용
     * */
    public Double getWeighted() {
        assert meetCounts != null;
        List<Double> deviation = getDeviation();
        double dispersion = getDispersion(deviation);
        return Math.sqrt(dispersion);
    }

    /**
     * 편차 get
     * */
    private List<Double> getDeviation(){
        double avarage = Arrays.stream(this.meetCounts).average().orElse(0);

        List<Double> deviation = new ArrayList<>();
        for (int meetCount : this.meetCounts) {
            deviation.add(avarage - meetCount);
        }

        return deviation;
    }

    /**
     * 팀원의 분산 get
     * */
    private double getDispersion(List<Double> deviation){
        double sum = 0;
        for(Double number : deviation){
            sum += Math.pow(number,2);
        }

        return sum / this.meetCounts.length;
    }

    public void select() {this.isSelected = true;}

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int[] getMeetCounts() { return this.meetCounts; }

    public boolean isSelected() { return this.isSelected; }

    public void setMeetCounts(int[] meetCounts) { this.meetCounts = meetCounts; }

    @Override
    public Member clone() {
        return new Member(this.id,this.name);
    }

    @Override
    public boolean equals(Object obj) {
        Member target = (Member)obj;
        if(target == null) return false;
        return id.equals(target.getId()) && name.equals(target.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
