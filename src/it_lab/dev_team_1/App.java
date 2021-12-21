package it_lab.dev_team_1;

import it_lab.dev_team_1.domain.Member;
import it_lab.dev_team_1.domain.Team;
import it_lab.dev_team_1.event.MeetService;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class App {
    private static final String MEMBER_FILE_PATH = "./members.txt";

    public static void main(String[] args) throws Exception {
        int numberOfTeam = 4;
        List<Member> members = initMemberList();

        MeetService meetService = new MeetService(members,numberOfTeam);
        List<Team> teams = meetService.matchingTeam();

        print(teams);
    }

    private static List<Member> initMemberList() throws FileNotFoundException {
        List<Member> members = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(MEMBER_FILE_PATH))){
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                members.add(new Member(i,line.trim()));
                i++;
            }
        } catch (IOException e) {
            throw new FileNotFoundException("members.txt 파일이 존재하지 않습니다.");
        }

        return members;
    }

    public static void print(List<Team> teams) {
        int i = 1;
        for (Team team : teams) {
            System.out.print(i + "팀 : ");
            System.out.println(team.toString());
            i++;
        }
    }
}
