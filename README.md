# Matching Team
팀원들 골고루 만나도록 티타임 조 뽑는 프로그램

Stream의 `toList()` API를 이용하여 개발했기에 java 16이상 필요.
<br>그 이하의 version을 사용하고자 하면, `toList()`를 `collect(Collectors.toList())`로 바꾸어주어야 한다. 

`./members.txt` 파일이 존재하여야 하며 팀원들 이름은 개행(엔터)로 구분하여 작성해주면 된다.
```text
(예시)

홍길동
김진수
박나래
```