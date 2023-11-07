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
                sh 'docker-compose up -d'
            }
        }
        stage('Unit Tests') {
            steps {
                // Add commands to run your tests
                sh '/opt/sbt/bin/sbt clean coverage test coverageReport'
            }
        }

        stage('Acceptance Tests') {
            steps{
                dir('acceptance-tests'){
                    git branch: "main"
                    git url: 'git@github.com:derickdamoah/todo-acceptance-tests.git', credentialsId: 'github_ssh_private_key'
                    sh 'bash run-todo-journey-tests.sh'
                }
            }
        }

        stage('Performance Tests') {
            steps{
                dir('performance-tests'){
                    git branch: "main"
                    git url: 'git@github.com:derickdamoah/todo-performance-tests.git', credentialsId: 'github_ssh_private_key'
                    sh '/opt/sbt/bin/sbt "gatling:test"'
                }
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
            sh 'docker-compose down --volume'
            sh 'docker-compose ps'
        }
    }
}