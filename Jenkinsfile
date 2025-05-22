pipeline {
    agent any

    environment {
        // Set any environment variables here if needed
    }

    stages {
        stage('Checkout') {
            steps {
                // Use your SSH credentials ID here
                git credentialsId: 'ec2-ssh-key', url: 'git@github.com:shaun-the-sheep-mtvs/shaun-the-sheep-ai.git'
            }
        }
        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh './gradlew build'
                }
            }
        }
        stage('Run Backend (optional)') {
            steps {
                dir('backend') {
                    // Stop any running backend, then start new one in background
                    sh 'pkill -f "java -jar" || true'
                    sh 'nohup java -jar build/libs/*.jar > backend.log 2>&1 &'
                }
            }
        }
    }
}