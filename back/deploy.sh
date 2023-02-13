#! /bin/bash

REPOSITORY=/home/ec2-user/baba-was

echo "> Build 파일 복사"
cp $REPOSITORY/zip/build/libs/*.jar $REPOSITORY

echo "> 현재 구동 중인 애플리케이션 pid 확인"

CURRENT_PID=$(pgrep -fl back-0.0.1-SNAPSHOT.jar | grep jar | awk '{print $1}')

if [ -z "$CURRENT_PID" ]; then
  echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 새 애플리케이션 배포"

JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"

chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"

nohup java -jar -DKAKAO_GRANT_TYPE=$KAKAO_GRANT_TYPE -DKAKAO_CLIENT_ID=$KAKAO_CLIENT_ID -DKAKAO_REDIRECT_URI=$KAKAO_REDIRECT_URI -DJWT_SECRET_KEY=$JWT_SECRET_KEY -DJWT_EXPIRE_LENGTH=$JWT_EXPIRE_LENGTH -DAWS_ACCESS_KEY=$AWS_ACCESS_KEY -DAWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY -DAWS_BUCKET_NAME=$AWS_BUCKET_NAME $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &
