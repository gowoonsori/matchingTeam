package it_lab.dev_team_1.domain;

import java.util.*;

public class Team {
    private final List<Member> memberList = new ArrayList<>();
    private final Member leader;
    private final int maxMemberCount;

    public Team(Member leader,int maxMemberCount) {
        this.leader = leader;
        this.maxMemberCount = maxMemberCount;
        this.memberList.add(leader);
    }

    public void selectMemberIn(List<Member> totalMemberList) {
        int vacancy = this.maxMemberCount - 1; //남은 팀원 자리 수 (자기 제외)

        while(0 < vacancy){
            List<Member> minMeetMembers = findMinMeetMembers(totalMemberList);
            vacancy = vacancy - minMeetMembers.size();

            //적게 만난 팀원들이 해당 팀의 남은자리수보다 많다면, 랜덤으로 남은자리수만틈만 뽑기
            if(vacancy < 0){
                Collections.shuffle(minMeetMembers);

                int selectMemberCount = vacancy + minMeetMembers.size();
                minMeetMembers = new ArrayList<>(minMeetMembers.subList(0,selectMemberCount));
            }
            this.addMembers(minMeetMembers);
            minMeetMembers.forEach(Member::select);
        }
    }

    /**
     * targetMember의 가장 적게 만난 Member들 get
     * */
    private List<Member> findMinMeetMembers(List<Member> totalMemberList) {
        assert this.leader != null;

        int tmp, min = Integer.MAX_VALUE;
        List<Member> result = new ArrayList<>();
        for(int i = 0; i < totalMemberList.size(); i++){
            if(this.leader.getId() == i || totalMemberList.get(i).isSelected()) continue;

            tmp = this.leader.getMeetCounts()[i];
            if(min > tmp){
                min = tmp;
                result.clear();
                result.add(totalMemberList.get(i));
            }else if(min == tmp){
                result.add(totalMemberList.get(i));
            }
        }

        return result;
    }

    public void checkMeetCount(int[][] meetGraph) {
        for (Member target : this.memberList) {
            for(Member member: this.memberList) {
                meetGraph[target.getId()][member.getId()]++;
            }
        }
    }

    public void addMembers(List<Member> members) {
        this.memberList.addAll(members);
        members.forEach(Member::select);
    }

    public void addMember(Member member) {
        this.memberList.add(member);
    }

    public Member getLeader() {
        return this.leader;
    }

    public int getTeamSize(){
        return this.memberList.size();
    }

    public List<Member> getMemberList(){
        return Collections.unmodifiableList(this.memberList);
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
