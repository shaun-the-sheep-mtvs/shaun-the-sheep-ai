pipeline {
    agent any

    environment {
        // Set defaults (will be overwritten in script)
        CONTAINER_NAME = 'backend-main'
        HOST_PORT = '8081'
        IMAGE_NAME = 'backend-main-image'
        DB_URL = 'jdbc:postgresql://aws-0-ap-northeast-2.pooler.supabase.com:5432/postgres'
        DB_USERNAME = 'postgres.vmseaaxnzizueahbonmg'
        DB_PASSWORD = credentials('SUPABASE_PASSWORD')
        GEMINI_API_KEY = credentials('GEMINI_API_KEY')
        JWT_SECRET = credentials('JWT_SECRET_KEY')
    }

    tools {
        jdk 'jdk-17.0.5'
        gradle 'gradle-8.4'
    }

    stages {
        stage('Set Environment for Branch') {
            steps {
                script {
                    if (env.BRANCH_NAME == 'dev') {
                        env.CONTAINER_NAME = 'backend-dev'
                        env.HOST_PORT = '8082'
                        env.IMAGE_NAME = 'backend-dev-image'
                    } else {
                        env.CONTAINER_NAME = 'backend-main'
                        env.HOST_PORT = '8081'
                        env.IMAGE_NAME = 'backend-main-image'
                    }
                    echo "Using container: ${env.CONTAINER_NAME}, port: ${env.HOST_PORT}, image: ${env.IMAGE_NAME}"
                }
            }
        }
        stage('Build JAR') {
            steps {
                dir('backend') {
                    sh './gradlew build -x test'
                }
            }
        }
        stage('Stop Old Container') {
            steps {
                sh "docker rm -f $CONTAINER_NAME || true"
            }
        }
        stage('Remove Old Image') {
            steps {
                sh "docker rmi $IMAGE_NAME:latest || true"
            }
        }
        stage('Build Docker Image') {
            steps {
                dir('backend') {
                    sh "docker build -t $IMAGE_NAME:latest ."
                }
            }
        }
        stage('Run New Container') {
            steps {
                sh """
                docker run -d --name $CONTAINER_NAME \
                    -e DB_URL=$DB_URL \
                    -e DB_USERNAME=$DB_USERNAME \
                    -e DB_PASSWORD=$DB_PASSWORD \
                    -e GEMINI_API_KEY=$GEMINI_API_KEY \
                    -e JWT_SECRET=$JWT_SECRET \
                    -p $HOST_PORT:8080 \
                    $IMAGE_NAME:latest
                """
            }
        }
        stage('Cleanup Old Images') {
            steps {
                sh 'docker image prune -f'
            }
        }
    }
}