pipeline {
    agent any

    stages {
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