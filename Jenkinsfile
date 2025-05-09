pipeline {
  agent any

  environment {
    DOCKER_IMAGE = "kimsik01/ankim:latest"
    DOCKER_CREDENTIALS = credentials('dockerhub')
    EC2_HOST = "3.34.4.183"
  }

  stages {
    stage('🔍 현재 앱 역할 확인') {
      steps {
        script {
          def result = sh(script: "curl -s http://$EC2_HOST/health/ping", returnStdout: true).trim()
          if (result.contains("app1")) {
            env.TARGET = "app2"
            env.PORT = "8082"
          } else {
            env.TARGET = "app1"
            env.PORT = "8081"
          }
          echo "✅ 이번에 배포할 대상: ${env.TARGET} (${env.PORT})"
        }
      }
    }

    stage('📦 Gradle Build') {
      steps {
        sh './gradlew build -x test'
      }
    }

    stage('🐳 Docker Build & Push') {
      steps {
        sh '''
          docker build -t $DOCKER_IMAGE .
          echo "$DOCKER_CREDENTIALS_PSW" | docker login -u "$DOCKER_CREDENTIALS_USR" --password-stdin
          docker push $DOCKER_IMAGE
        '''
      }
    }

    stage('🚀 Ansible 배포') {
                steps {
                    echo "✅ Ansible로 EC2-2 배포 시작"
                    sh '''
                        docker exec ansible ansible-playbook \
                            -i /ansible/inventory.ini \
                            /ansible/playbook.yml
                    '''
                }
    }

    stage('🔎 Health Check 확인') {
      steps {
        script {
          sh """
            echo ">> Health Check for ${TARGET} (port ${PORT})"
            URL="http://${EC2_HOST}:${PORT}/health/ping"
            TRY_COUNT=0
            MAX_TRIES=5
            RETRY_DELAY=5

            while [ \$TRY_COUNT -lt \$MAX_TRIES ]; do
                HTTP_CODE=\$(curl -s -o /dev/null -w "%{http_code}" \$URL)

                if [ "\$HTTP_CODE" -eq 200 ]; then
                    echo "✅ Health check 성공 (응답코드: \$HTTP_CODE)"
                    exit 0
                fi

                echo "⏳ Health check 재시도: \$TRY_COUNT회 (응답코드: \$HTTP_CODE)"
                TRY_COUNT=\$((TRY_COUNT + 1))
                sleep \$RETRY_DELAY
            done

            echo "❌ Health check 실패"
            exit 1
          """
        }
      }
    }

    stage('🔀 Nginx 전환') {
      steps {
        sh """
          docker exec ansible ansible-playbook \
            -i /ansible/inventory.ini \
            /ansible/switch-nginx-${TARGET}.yml
        """
      }
    }

    stage('🧹 이전 앱 정리') {
      steps {
        script {
          def old = (env.TARGET == 'app1') ? 'ankim-app2' : 'ankim-app1'
          sh "docker exec ansible docker rm -f ${old} || true"
        }
      }
    }
  }

  post {
    success {
      echo "🎉 무중단 배포 성공 — 현재 운영 앱: ${env.TARGET}"
    }
    failure {
      echo "❌ 배포 실패 — 기존 앱 유지 중"
    }
  }
}