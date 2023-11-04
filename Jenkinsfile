pipeline {
    agent any
    options {
        skipStagesAfterUnstable()
        ansiColor('xterm')
    }
    stages {
        stage('Build') {
            steps {
                // Add commands to build your application
                sh '/opt/sbt/bin/sbt clean compile'
            }
        }
        stage('Dockerize') {
            steps {
                // Add commands to dockerize your application
                sh '/opt/sbt/bin/sbt assembly'
                sh 'colima start'
                sh 'docker compose up -d'
            }
        }
        stage('Test') {
            steps {
                // Add commands to run your tests
                sh '/opt/sbt/bin/sbt clean coverage test coverageReport'
            }
        }

        stage('Deploy') {
            steps {
            sh 'echo "Deploying"'
            }
        }
    }
    post {
        always {
            sh 'docker compose down --remove-orphans -v'
            sh 'docker compose ps'
        }
    }
}