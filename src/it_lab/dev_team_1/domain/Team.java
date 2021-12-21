package it_lab.dev_team_1.domain;

import it_lab.dev_team_1.domain.Member;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Team {
    List<Member> memberList = new ArrayList<>();
    Member leader;

    public Team(Member leader) {
        this.leader = leader;
        memberList.add(leader);
    }

    public void addMembers(List<Member> members) {
        memberList.addAll(members);
    }

    public Member getLeader() {
        return leader;
    }

    public int getTeamSize(){
        return memberList.size();
    }

    public List<Member> getMemberList(){
        return Collections.unmodifiableList(memberList);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Member member : memberList) {
            sb.append(member.getName())
                .append(" ");
        }
        return sb.toString();
    }
}
