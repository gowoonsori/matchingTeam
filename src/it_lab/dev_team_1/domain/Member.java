package it_lab.dev_team_1.domain;

import java.util.Objects;

public class Member implements Cloneable{
    private final Integer id;
    private final String name;

    public Member(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

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
