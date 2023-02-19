#! /bin/bash

REPOSITORY=/home/ec2-user/baba-was/zip
REPOSITORY_DEV=/home/ec2-user/baba-was/dev
REPOSITORY_PRD=/home/ec2-user/baba-was/prd

echo "> 환경변수 불러오기"
source ~/.bashrc

if [ "$DEPLOYMENT_GROUP_NAME" == "baba-was-dev" ]; then
  echo "> 기존의 dev/zip 폴더 삭제"
  rm -r $REPOSITORY_DEV/zip

  echo "> 설치된 프로젝트 폴더 이동"
  mv $REPOSITORY $REPOSITORY_DEV/zip

  echo "> 현재 구동 중인 애플리케이션 pid 확인"
  CURRENT_PID=$(pgrep -fl dev-APP.jar | awk '{print $1}')

  if [ -z "$CURRENT_PID" ]; then
    echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
  else
    echo "> kill -15 $CURRENT_PID"
    sudo kill -15 $CURRENT_PID
    sleep 5
  fi

  OLD_JAR_NAME=$(ls -tr $REPOSITORY_DEV/zip/build/libs/*.jar | tail -n 1)
  JAR_NAME=$REPOSITORY_DEV/zip/build/libs/dev-APP.jar
  mv $OLD_JAR_NAME $JAR_NAME
  echo "> JAR Name 변경: $OLD_JAR_NAME >> $JAR_NAME"

  echo "> $JAR_NAME 에 실행권한 추가"
  chmod +x $JAR_NAME

  echo "> $JAR_NAME 실행"
  nohup java -jar -Dspring.profiles.active=dev $JAR_NAME > $REPOSITORY_DEV/nohup.out 2>&1 &

else
  echo "> 기존의 prd/zip 폴더 삭제"
  rm -r $REPOSITORY_PRD/zip

  echo "> 설치된 프로젝트 폴더 이동"
  mv $REPOSITORY $REPOSITORY_PRD/zip

  echo "> 현재 구동 중인 애플리케이션 pid 확인"
  CURRENT_PID=$(pgrep -경fl prd-APP.jar | awk '{print $1}')

  if [ -z "$CURRENT_PID" ]; then
    echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
  else
    echo "> kill -15 $CURRENT_PID"
    sudo kill -15 $CURRENT_PID
    sleep 5
  fi

  OLD_JAR_NAME=$(ls -tr $REPOSITORY_PRD/zip/build/libs/*.jar | tail -n 1)
  JAR_NAME=$REPOSITORY_PRD/zip/build/libs/prd-APP.jar
  mv $OLD_JAR_NAME $JAR_NAME
  echo "> JAR Name 변경: $OLD_JAR_NAME >> $JAR_NAME"

  echo "> $JAR_NAME 에 실행권한 추가"
  chmod +x $JAR_NAME

  echo "> $JAR_NAME 실행"
  nohup java -jar -Dspring.profiles.active=prd $JAR_NAME > $REPOSITORY_PRD/nohup.out 2>&1 &
fi