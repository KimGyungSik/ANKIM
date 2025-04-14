pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "kimsik01/ankim:latest"
        DOCKER_CREDENTIALS = credentials('dockerhub')
    }

    stages {
        stage('📦 Build with Gradle') {
                steps {
                    echo "✅ Gradle로 빌드 시작"
                    sh '''
                        mkdir -p build/generated-snippets
                        ./gradlew bootJar -x test
                    '''
                }
            }

        stage('🐳 Docker Build & Push') {
            steps {
                echo "✅ Docker 이미지 빌드 및 푸시"
                sh '''
                  docker build --no-cache -t $DOCKER_IMAGE .
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
    }

    post {
        success {
            echo "🎉 배포 완료!"
        }
        failure {
            echo "❌ 빌드 또는 배포 실패"
        }
    }
}
