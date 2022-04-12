package it_lab.dev_team_1.app;

import it_lab.dev_team_1.domain.Member;
import it_lab.dev_team_1.domain.Team;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class TeamMatchingService {
    private static final String MAP_FILE_PATH = "./meet.txt";
    private final List<Team> teams = new ArrayList<>();
    private final List<Member> members;
    private final int[][] meetGraph;

    private final int memberSizePerTeam;    //팀당 팀원수
    private final int totalMemberCount;     //총 팀원 수
    private final int totalTeamCount;       //팀 수


    private final Random random = new Random();

    public TeamMatchingService(List<Member> members, int memberSizePerTeam) throws IOException {
        this.members = members;
        this.memberSizePerTeam = memberSizePerTeam;
        this.totalMemberCount = members.size();
        this.totalTeamCount = Math.round( totalMemberCount / (float) memberSizePerTeam );
        this.meetGraph = new int[totalMemberCount][totalMemberCount];
        setMeetGraphFromTextFile();
    }

    /**
     * 팀 매칭 로직 수행
     *
     * @return void
    * */
    public List<Team> matchingTeam() throws  IOException {
        this.random.setSeed(System.currentTimeMillis());

        //팀장 선택
        selectTeamLeader();
        //표준편차가 같은 팀원들이 팀장일 경우 항상 앞쪽의 팀원이 먼저 팀원을 뽑기에 랜덤
        Collections.shuffle(this.teams);

        //팀원 매칭
        for(Team team : this.teams){
            team.selectMemberIn(this.members);
        }

        //딱 나누어 떨어지지 않고 팀원이 남았다면 남은 팀으로 묶기
        List<Member> remainMembers = this.members.stream().filter(m -> !m.isSelected()).toList();
        if(!remainMembers.isEmpty()) {
            Team team = new Team(remainMembers.get(0),this.memberSizePerTeam);
            team.addMembers(remainMembers.subList(1,remainMembers.size()));
            this.teams.add(team);
        }

        //만남 표시
        this.teams.forEach(t -> t.checkMeetCount(this.meetGraph));

        //text file write
        writeMapInTextFile();

        return teams;
    }


    private void selectTeamLeader(){
        Map<Member,Double> standardDeviationOfMember = getStandardDeviationOfMembers();

        // 표준 편차 내림차순으로 정렬
        // 표춘편차가 클 수록(안만난 팀원이 많은 경우) 팀장후보
        List<Map.Entry<Member, Double>> sortedMemberByStandardDeviation = new ArrayList<>(standardDeviationOfMember.entrySet());
        sortedMemberByStandardDeviation.sort((e1,e2) -> {
            if(e1 == e2) return this.random.nextInt(2) == 1 ? 1 : 0;      //표준편차가 같으면 랜덤
            else return e2.getValue().compareTo(e1.getValue());
        });
        List<Member> leaders = new ArrayList<>(sortedMemberByStandardDeviation.stream().map(Map.Entry::getKey).toList().subList(0,this.totalTeamCount));

        leaders.forEach(Member::select);
        this.teams.addAll(makeTeams(leaders));
    }

    private List<Team> makeTeams(List<Member> leaders){
        return leaders.stream().map(l -> new Team(l,this.memberSizePerTeam)).toList();
    }

    /**
     * 팀원들의 표준 편차 get
     * */
    private Map<Member, Double> getStandardDeviationOfMembers() {
        return this.members.stream().collect(Collectors.toMap(m -> m, Member::getWeighted));
    }


    private void setMeetGraphFromTextFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(MAP_FILE_PATH))){
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                String[] meetCounts = line.split(" ");
                for (int j = 0; j < this.totalMemberCount; j++) {
                    this.meetGraph[i][j] = Integer.parseInt(meetCounts[j]);
                }
                this.members.get(i).setMeetCounts(Arrays.stream(meetCounts).mapToInt(Integer::parseInt).toArray());
                i++;
            }
        } catch (FileNotFoundException e) {
            initMeetGraph();
            setMeetGraphFromTextFile();
        }
    }

    /**
     * 파일 멤버 수만큼의 이중배열 map file 을 0으로 초기화
     * */
    private void initMeetGraph() throws IOException {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(MAP_FILE_PATH, false))){
            for(int i = 0; i < this.totalMemberCount; i++){
                for(int j = 0; j < this.totalMemberCount; j++){
                    writer.write("0 ");
                }
                writer.write(System.lineSeparator());
            }
        }
    }

    private void writeMapInTextFile() throws IOException {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(MAP_FILE_PATH, false))){
            for (int[] list : this.meetGraph) {
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
}
