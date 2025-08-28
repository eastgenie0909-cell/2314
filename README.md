# TQQQWidgetAPI (No wrapper)

안드로이드 홈 화면 위젯으로 TQQQ의 **실시간 가격**과 **200일 이동평균(200MA)** 을 표시합니다.  
야후 파이낸스의 공개 엔드포인트를 직접 호출합니다.

- Quote: `https://query1.finance.yahoo.com/v7/finance/quote?symbols=TQQQ`
- 1y Daily Chart: `https://query1.finance.yahoo.com/v8/finance/chart/TQQQ?range=1y&interval=1d`

## 위젯 동작
- 설치/업데이트 시 즉시 한 번 새로고침하고, 이후 30분마다 WorkManager가 백그라운드에서 갱신합니다.
- 저장은 SharedPreferences를 사용합니다.

## 빌드 (GitHub Actions)
이 저장소에는 Gradle Wrapper가 없습니다. 대신 워크플로우가 Gradle **8.7**을 설치하고 `gradle :app:assembleDebug` 를 실행합니다.

출력 APK는 워크플로우의 **Artifacts**에서 `app-debug.apk`로 받을 수 있습니다.

## 로컬 빌드 (선택)
로컬에서 빌드하려면 Gradle 8.7+ 과 JDK 17이 필요합니다.
```
gradle :app:assembleDebug
```
