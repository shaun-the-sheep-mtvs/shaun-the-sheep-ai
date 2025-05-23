pipeline {
    agent any

    environment {
        DB_URL = 'jdbc:postgresql://db.vmseaaxnzizueahbonmg.supabase.co:5432/postgres'
        DB_USERNAME = 'postgres'
        DB_PASSWORD = credentials('SUPABASE_PASSWORD') // Use Jenkins credentials for secrets!
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