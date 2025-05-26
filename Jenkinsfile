pipeline {
    agent any

    environment {
        DB_URL = 'jdbc:postgresql://aws-0-ap-northeast-2.pooler.supabase.com:5432/postgres'
        DB_USERNAME = 'postgres.vmseaaxnzizueahbonmg'
        DB_PASSWORD = credentials('SUPABASE_PASSWORD')
        GEMINI_API_KEY = credentials('GEMINI_API_KEY')
        JWT_SECRET_KEY = credentials('JWT_SECRET_KEY')
    }

    tools {
        jdk 'jdk-17.0.5'
        gradle 'gradle-8.4'
    }

    stages {
        stage('Build JAR') {
            steps {
                dir('backend') {
                    sh './gradlew build -x test'
                }
            }
        }
        stage('Stop Old Container') {
            steps {
                sh 'docker rm -f my-backend-container || true'
            }
        }
        stage('Remove Old Image') {
            steps {
                sh 'docker rmi my-backend:latest || true'
            }
        }
        stage('Build Docker Image') {
            steps {
                dir('backend') {
                    sh 'docker build -t my-backend:latest .'
                }
            }
        }
        stage('Run New Container') {
            steps {
                sh '''
                docker run -d --name my-backend-container \
                    -e DB_URL=$DB_URL \
                    -e DB_USERNAME=$DB_USERNAME \
                    -e DB_PASSWORD=$DB_PASSWORD \
                    -e GEMINI_API_KEY=$GEMINI_API_KEY \
                    -e JWT_SECRET_KEY=$JWT_SECRET_KEY \
                    -p 8081:8080 \
                    my-backend:latest
                '''
            }
        }
        stage('Cleanup Old Images') {
            steps {
                sh 'docker image prune -f'
            }
        }
    }
}