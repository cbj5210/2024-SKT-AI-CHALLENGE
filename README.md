# 🌱 2024-SKT-AI-Challenge
  * 긴급상황 도와줘 : 백그라운드에서 동작하는 모바일 AI 어플리케이션
  * 음성 인식으로 긴급 상황을 판별하고, 상황에 맞는 문자 메세지 발송

<br /><br />

# 🖥️ Demo 시연 및 소개 자료
  * 시연
    * [위급 상황이 아닌 경우](https://drive.google.com/file/d/1JWIQb86BzcF6kZUhVT8VVoBKNMfjX_it/view?usp=sharing)
    * [위급 상황인 경우](https://drive.google.com/file/d/1hH5E4p4pYmPaKZm_BObcrA2Rbgzfsoyg/view?usp=sharing)
    * [백그라운드에서 동작하는 경우](https://drive.google.com/file/d/1RmF_PRyiKNCCDMF5Gad266Ors3Y9RzmU/view?usp=sharing)
    * [인터넷 연결 안되는 경우](https://drive.google.com/file/d/15R9qhYXWYA8sGYCZVTKRxdfgftIi-yVm/view?usp=sharing)
  * 소개 자료
    * [링크](https://docs.google.com/presentation/d/1gS9bmYXNGlp5W4XJQWgg5G4mfJQigB28/edit?usp=sharing&ouid=105829050231473623633&rtpof=true&sd=true)

<br /><br />

# !!! 주의 사항 !!!
  * ChatGPT, 네이버 Map API Key는 git에 업로드 하지 않습니다.
  * 로컬에서 테스트 실행이 필요한 경우에는 본 문서 최하단 Maintainer로 문의 부탁 드립니다.

<br /><br />

# 📌 Architecture

| 컴포넌트      |           외부 API 및 라이브러리      | 용도                                         |                              공식 URL                               |
|-------------|-------------------------------------------------------------------------------------------------------------|--------------------------------------------|--------------------------------------|
|   Service   |                                  | Android Application                        |                                                                    | 
|             |     google SpeechRecognizer      | 안드로이드 내장 음성 인식 모듈                          |       [링크](https://developer.android.com/reference/android/speech/SpeechRecognizer)                    |
|             |        OpenAI ChatGPT API        | 인식된 음성으로 긴급 상황 여부를 판단하고 후속 조치를 강구하기 위한 API |       [링크](https://platform.openai.com/docs/api-reference/introduction)       |
|             |         Naver Map API            | 모바일 기기의 위도, 경도를 도로명 주소로 변환하기 위한 API        |       [링크](https://api.ncloud-docs.com/docs/ai-naver-mapsgeocoding-geocode)     |
|             |         내장 소형 AI 모델            | 응급상황 판단에 특화된 경량화 로컬 AI 모델            |       [링크]()     |



* pseudo code
```

1. 사용자로부터 {긴급 상황 시작 단어}, {특이 사항}을 입력 받음
2. {긴급 상황 시작 단어}가 음성 인식되면 이후 대화를 포함하여 ChatGPT로 긴급 상황 파악 및 후속 대처 방안 수립 (ex: 불이 났어요 -> 화재 상황, 119 신고 요망, 사용자의 현재 위치 서울시 을지로 2가 등)
3. GPT가 판단한 내용 + 사용자의 특이 사항을 고려하여 문자 메세지 발송 (To.119 을지로 2가에 화재가 발생하여 출동이 필요합니다)

```

* 주요 서비스 코드
```

* MainActivity.java
  * 어플리케이션 화면 구성 및 동작
* ForeGround.java
  * 백그라운드에서 Google SpeechRecognizer 음성을 인식하고, {긴급 상황 시작 단어}가 인지되면 ChatGPT API를 호출하여 상황을 판단하고 후속 대응 (문자 메세지 발송 등)

```

<br /><br />

# ⚙️ 개발 환경
  * Android SDK 34 (min 26)
  * Java JDK 17

<br /><br />



# 💬 Maintainers
  * API 사용을 위한 데이터는 암호화 되어 있습니다. Local 환경에서의 실행 등은 아래 메일로 문의해주세요.
  * byungjun.choi@sk.com
  * jusang.jung@sk.com
  * hahyuk.choi@sk.com
  * kelee@sk.com
