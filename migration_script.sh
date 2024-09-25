#!/bin/bash

# 변수 설정
S3_BUCKET="s3://nemo-mysql-s3"  # S3 버킷 이름
PROD_HOST="oceanacademydb.cx0u8okkguoh.ap-northeast-2.rds.amazonaws.com"
USER="root"
PASSWORD="Nemo0325!!"
DEV_CONTAINER="nemo-mysql"
DEV_DATABASE="OceanAcademyDevDB"
BACKUP_FILE="/tmp/rds_backup.sql"

# 1. RDS에서 MySQL 데이터베이스 백업 가져오기
echo "RDS에서 데이터베이스 백업 중..."
mysqldump -h $PROD_HOST -u $USER -p$PASSWORD --single-transaction --set-gtid-purged=OFF OceanAcademyDB > $BACKUP_FILE

if [ $? -ne 0 ]; then
  echo "RDS에서 백업을 가져오는 중 오류가 발생했습니다."
  exit 1
fi

# 2. S3에 백업 파일 업로드
echo "S3에 백업 파일 업로드 중..."
aws s3 cp $BACKUP_FILE $S3_BUCKET/rds_backup.sql

if [ $? -ne 0 ]; then
  echo "S3에 백업 파일 업로드 중 오류가 발생했습니다."
  exit 1
fi

# 3. S3에서 백업 파일을 Docker 컨테이너로 복사
echo "S3에서 백업 파일을 다운로드 중..."
aws s3 cp $S3_BUCKET/rds_backup.sql $BACKUP_FILE

if [ $? -ne 0 ]; then
  echo "S3에서 백업 파일을 다운로드하는 중 오류가 발생했습니다."
  exit 1
fi

# 4. 백업 파일을 DEV 컨테이너의 /tmp 디렉토리로 복사
echo "백업 파일을 Docker 컨테이너로 복사 중..."
docker cp $BACKUP_FILE $DEV_CONTAINER:/tmp/rds_backup.sql

if [ $? -ne 0 ]; then
  echo "백업 파일을 컨테이너로 복사하는 중 오류가 발생했습니다."
  exit 1
fi

# 5. DEV MySQL에서 백업을 복원 (MySQL 'source' 명령어 사용)
echo "Docker 컨테이너에서 데이터베이스 복원 중..."
docker exec -i $DEV_CONTAINER mysql -u $USER -p$PASSWORD -e "source /tmp/rds_backup.sql" $DEV_DATABASE

if [ $? -ne 0 ]; then
  echo "DEV MySQL에서 백업 복원 중 오류가 발생했습니다."
  exit 1
fi

# 6. 완료 메시지
echo "PROD에서 DEV로 데이터 마이그레이션이 완료되었습니다."
