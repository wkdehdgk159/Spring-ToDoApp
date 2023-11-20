# Spring-ToDoApp Documentation
<br><br>

ERD Diagram(by erdcloud)
-------------
![erd](https://github.com/wkdehdgk159/Spring-ToDoApp/assets/70753659/e600b352-6419-4f19-9878-121f2e6ffd2d)
<br><br>

API Spec
-------------
![api spec](https://github.com/wkdehdgk159/Spring-ToDoApp/assets/70753659/315cd5f5-c6b2-4e74-ad6d-6c558b8b0773)
<br><br>

Function
-------------
1. 회원가입 API
POST - (/api/user/signup)

x-www-form-urlencoded 데이터로
username - dongha
password - 11111111

2. 로그인 API
POST - (/api/user/login)
로그인은 body raw-json으로
{
    "username":"dongha",
    "password":"11111111"
}
{
    "username":"dongha2",
    "password":"22222222"
}
토큰은 Headers에 authorization - 받은 토큰값(bearer 포함)으로 보내면된다.

3. 할일카드 작성 기능 API
POST - (/api/todocards)
todocard create
{
    "title":"donghas title",
    "contents":"Good Day!"
}

4. 선택한 할일카드 조회 기능 API
GET - (/api/todocards/{id}) Path variable 방식

5. 할일카드 목록 조회 기능 API
GET - (/api/todocards)
작성일 기준 내림차순

6. 선택한 할일카드 수정 기능 API
PUT - (/api/todocards/{id})

7. 할일카드 완료 기능 API
PUT - (/api/todocards/{id}/{complete})
complete 자리에 true, false 넣기

8. 댓글 작성 API
POST - (/api/todocards/{id}/comments)
{
    "contents":"Faker"
}

9. 댓글 수정 API
PUT - (/api/todocards/comments/{id})

10. 댓글 삭제 API
DELETE (/api/todocards/comments/{id})

11. 예외 처리 (ResponseEntity 사용)
