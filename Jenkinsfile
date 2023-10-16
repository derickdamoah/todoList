pipeline {
    agent any
    options {
        skipStagesAfterUnstable()
    }
    stages {
        stage('Build') {
            steps {
                // Add commands to build your application
                sh 'sbt clean compile'
            }
        }
        stage('Test') {
            steps {
                // Add commands to run your tests
                sh 'sbt clean coverage test coverageReport'
            }
        }
        stage('Dockerize') {
            steps {
                // Add commands to dockerize your application
                sh 'sbt assembly'
                sh 'docker compose up -d'
            }
        }
        stage('Deploy') {
            steps {
                // Add commands to deploy to AWS instance
            }
        }
    }
}
