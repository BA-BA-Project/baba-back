#!/bin/bash

REPOSITORY=/home/ec2-user/baba-was
APP_NAME=back-0.0.1-SNAPSHOT.jar
LOG_DIRECTORY=$REPOSITORY/logs
NOHUP_OUT=$LOG_DIRECTORY/nohup.out

echo "> Create log directory"
sudo mkdir -p $LOG_DIRECTORY

echo "> Change the ownership of the log directory to ec2-user"
sudo chown -R ec2-user:ec2-user $LOG_DIRECTORY

echo "> Build 파일 복사"
sudo cp $REPOSITORY/build/libs/*.jar $REPOSITORY/

echo "> 현재 구동 중인 애플리케이션 pid 확인"
CURRENT_PID=$(pgrep -fl $APP_NAME | grep jar | awk '{print $1}')

echo ">현재 구동 중인 애플리케이션 pid: $CURRENT_PID"

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
sudo chmod +x $JAR_NAME

echo "> jar 실행"
nohup java -jar -DKAKAO_GRANT_TYPE=$KAKAO_GRANT_TYPE -DKAKAO_CLIENT_ID=$KAKAO_CLIENT_ID -DKAKAO_REDIRECT_URI=$KAKAO_REDIRECT_URI -DJWT_SECRET_KEY=$JWT_SECRET_KEY -DJWT_EXPIRE_LENGTH=$JWT_EXPIRE_LENGTH -DAWS_ACCESS_KEY=$AWS_ACCESS_KEY -DAWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY -DAWS_BUCKET_NAME=$AWS_BUCKET_NAME -DDATABASE_HOST=$DATABASE_HOST -DDATABASE_NAME=$DATABASE_NAME -DDATABASE_USERNAME=$DATABASE_USERNAME -DDATABASE_PASSWORD=$DATABASE_PASSWORD $JAR_NAME > $NOHUP_OUT 2>&1 &
Foot
