package it_lab.dev_team_1.event;

import it_lab.dev_team_1.domain.Member;
import it_lab.dev_team_1.domain.Team;

import java.io.*;
import java.util.*;

public class MeetService {
    private final List<Member> members;
    private final List<Team> teams = new ArrayList<>();

    private static final String MAP_FILE_PATH = "./meet.txt";
    private final int numberOfMember;
    private final int numberOfTeam;
    private final int memberSizePerTeam;

    private final int[][] map;

    private final Random random = new Random();
    private List<Member> membersCopy;

    public MeetService(List<Member> members,int numberOfTeam) throws IOException {
        this.members = members;
        this.numberOfMember = members.size();
        this.numberOfTeam = numberOfTeam;
        this.memberSizePerTeam = numberOfMember / numberOfTeam;
        map = new int[numberOfMember][numberOfMember];
        setMapFromTextFile();
    }

    public List<Team> matchingTeam() throws  IOException {
        random.setSeed(System.currentTimeMillis());
        membersCopy = deepCopy(members);

        selectTeamLeader();

        for(Team team : teams){
            selectTeamMember(team);

            //팀원들끼리 서로 만남 표시
            for(Member memberA : team.getMemberList()){
                for(Member memberB : team.getMemberList()){
                    map[memberA.getId()][memberB.getId()]++;
                }
            }
        }

        writeMapInTextFile();

        return teams;
    }

    /**
     * Team(Leader만 존재하는 team) 에 팀원들 매칭
     * */
    private void selectTeamMember(Team team){
        Member leader = team.getLeader();
        int vacancy = memberSizePerTeam - 1; //남은 팀원 자리 수

        while(team.getTeamSize() < 4){
            List<Member> minMeetMembers = findMinMeetMembers(leader);
            vacancy = vacancy - minMeetMembers.size();
            if(vacancy < 0){
                Collections.shuffle(minMeetMembers);

                int selectMemberCount = vacancy + minMeetMembers.size();
                minMeetMembers = new ArrayList<>(minMeetMembers.subList(0,selectMemberCount));
            }

            team.addMembers(minMeetMembers);
            membersCopy.removeAll(minMeetMembers);
        }
    }

    private void selectTeamLeader(){
        Map<Member,Double> standardDeviationOfMember = new HashMap<>();

        for(Member member : members){
            List<Double> deviation = getDeviation(member);
            double dispersion = getDispersion(deviation);
            standardDeviationOfMember.put(member,Math.sqrt(dispersion));    //표준편차
        }

        // 내림차순으로 정렬
        // 표춘편차가 클 수록 팀장후보
        List<Map.Entry<Member, Double>> sortedMemberByStandardDeviation = new ArrayList<>(standardDeviationOfMember.entrySet());
        sortedMemberByStandardDeviation.sort((entry1,entry2) -> {
            if(entry1 == entry2) {
                return random.nextInt(2) == 1 ? 1 : 0;      //표준편차가 같으면 랜덤
            }
            else return entry2.getValue().compareTo(entry1.getValue());
        });

        List<Member> leaders = new ArrayList<>(sortedMemberByStandardDeviation.stream().map(Map.Entry::getKey).toList().subList(0,numberOfTeam));
        teams.addAll(makeTeams(leaders));
        membersCopy.removeAll(leaders);
    }

    private List<Team> makeTeams(List<Member> leaders){
        return leaders.stream().map(Team::new).toList();
    }

    /**
     * 분산 get
    * */
    private double getDispersion(List<Double> deviation){
        double sum = 0;
        for(Double number : deviation){
            sum += Math.pow(number,2);
        }

        return sum / numberOfMember;
    }

    /**
     * 편차 get
    * */
    private List<Double> getDeviation(Member member){
        int memberId = member.getId();
        double avarage = Arrays.stream(map[memberId]).average().orElse(0);

        List<Double> deviation = new ArrayList<>();
        for(int i=0; i < numberOfMember; i++){
            deviation.add(avarage - map[memberId][i]);
        }

        return deviation;
    }

    /**
     * targetMember의 가장 적게 만난 Member들 get
     * */
    private List<Member> findMinMeetMembers(Member targetMember) {
        if(targetMember == null) throw new NullPointerException("targetMember 는 null일 수 없습니다.");

        int min = Integer.MAX_VALUE;
        int tmp;
        List<Member> result = new ArrayList<>();

        for(int i=0; i < map.length; i++){
            if(targetMember.getId() == i || !membersCopy.contains(members.get(i))) continue;

            tmp = map[targetMember.getId()][i];
            if(min > tmp){
                min = tmp;
                result.clear();
                result.add(members.get(i));
            }else if(min == tmp){
                result.add(members.get(i));
            }
        }

        return result;
    }

    private static List<Member> deepCopy(List<Member> members) {
        if(members == null) return Collections.emptyList();

        List<Member> copy = new ArrayList<>();
        for(Member member : members) {
            copy.add(member.clone());
        }

        return copy;
    }

    private void setMapFromTextFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(MAP_FILE_PATH))){
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                String[] val = line.split(" ");
                for (int j = 0; j < this.numberOfMember; j++) {
                    map[i][j] = Integer.parseInt(val[j]);
                }
                i++;
            }
        } catch (FileNotFoundException e) {
            initMap();
        }
    }

    /**
     *
     * */
    private void writeMapInTextFile() throws IOException {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(MAP_FILE_PATH, false))){
            for (int[] list : map) {
                for (int i = 0; i < list.length; i++) {
                    writer.write(String.valueOf(list[i]));
                    if (i != list.length - 1) {
                        writer.write(" ");
                    }
                }
                writer.newLine();
            }
        }
    }

    /**
     * 파일 멤버 수만큼의 이중배열 map file 을 0으로 초기화
    * */
    private void initMap() throws IOException {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(MAP_FILE_PATH, false))){
            for(int i=0; i < this.numberOfMember; i++){
                for(int j=0; j < this.numberOfMember; j++){
                    writer.write("0 ");
                }
                writer.write(System.lineSeparator());
            }
        }
    }
}
