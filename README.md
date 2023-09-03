# Project 'SeeYourSound' Frontend

### 1. 역할 및 임무 

#### 앱의 전반적인 UI 디자인 및 구성 , 그리고 오디오 데이터를 추출하여 중앙 서버로 전송하는 일을 담당한다.

---

### 2. 개발환경 및 SDK 버전  
* 개발환경 : 안드로이드 스튜디오 PHONE(**API 9.0**) / EMULATOR(**32.1.114**)  
* 사용 언어 - **JAVA**
* ~~~
  defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 28
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
  ~~~
---

### 3. 앱 실행 방법 및 설명
#### 3.1 앱 실행방법 
* 1. 아래의 주소를 git-clone 하여 모든 파일들을 저장한다. 
~~~
https://github.com/KOBOT-BOARD12/seeyoursound-frontend.git
~~~

* 2. 우측 상단에 Running Device를 설정한다. PHONE과 유선연결 하여 그에 맞는 기기 정보 또는 EMULATOR를 선택하고 오른쪽의 재생 버튼을 누른다.

<div align="center">
<img width="560" src="https://i.esdrop.com/d/f/MQDIWwBKU7/63NVRdRTes.png">  
</div>

#### 3.2 앱 설명
* 1. 시작 화면이며 구글과 이메일로 회원가입이 가능하다.
<div align="center">
<img width="200" src="https://i.esdrop.com/d/f/MQDIWwBKU7/1RQtfjACrM.jpg">  
</div>

* 2. 이메일로 회원가입 시의 화면이다. 
<div align="center">
<img width="200" src="https://i.esdrop.com/d/f/MQDIWwBKU7/cGucS9GUeX.jpg">  
</div>

* 3. 이메일로 회원가입 했을 때 로그인 창이다.
<div align="center">
<img width="200" src="https://i.esdrop.com/d/f/MQDIWwBKU7/YpDGLQjsBn.jpg">  
</div>

* 4. 메인화면이며 마이크 버튼을 눌렀을 때 색깔이 녹색으로 활성화 되며 음성과 방향 탐색이 가능해진다. 데이터가 인식되면 시각적 이미지와 텍스트를 통해 알려준다. 
<div align="center">
<img width="200" src="https://i.esdrop.com/d/f/MQDIWwBKU7/wOgX3KZGnE.jpg">  
</div>

<div align="center">
<img width="200" src="https://i.esdrop.com/d/f/MQDIWwBKU7/Jn1Lq2UNfp.jpg">  
</div>

* 5. 메인 화면을 설명할 수 있는 도움말이다 메인화면 오른쪽 상단의 물음표 버튼을 클릭하면 설명을 제공받을 수 있다.
<div align="center">
<img width="200" src="https://i.esdrop.com/d/f/MQDIWwBKU7/P89uefO7S5.jpg">  
</div>

* 6. 사용자가 원하는 예약어를 최대 3개까지 추가하여 인식하게 할 수 있다. 키워드는 3글자에서 5글자 사이로 추가가 가능하다.
<div align="center">
<img width="200" src="https://i.esdrop.com/d/f/MQDIWwBKU7/9LHdBe0iLQ.jpg">  
</div>

<div align="center">
<img width="200" src="https://i.esdrop.com/d/f/MQDIWwBKU7/jkUHFL353g.jpg">  
</div>

* 7. 예약어를 제외한 다른 소리들 중에서 사용자가 듣고 싶은 소리만을 설정할 수 있다. 체크박스를 풀었을 시에 그 소리와 방향은 인식하지 않는다.
<div align="center">
<img width="200" src="https://i.esdrop.com/d/f/MQDIWwBKU7/xe3ZgFtDqI.jpg">  
</div>








  
