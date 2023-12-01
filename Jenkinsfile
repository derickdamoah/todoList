pipeline {
    agent any
    environment {
        MONGO_DB_USERNAME = credentials('MONGO_DB_USERNAME')
        MONGO_DB_PASSWORD = credentials('MONGO_DB_PASSWORD')
    }
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
                dir('acceptance-tests') {
                    git url: 'git@github.com:derickdamoah/todo-acceptance-tests.git', branch: 'main', credentialsId: 'github_ssh_private_key'
                    sh '/opt/sbt/bin/sbt -Daccessibility.audit=true -Daccessibility.htmlvalidator=false -Dbrowser=chrome -Denvironment=local \'testOnly acceptance.suites.RunTodoListSuite\''
                }
            }
        }

        stage('Performance Tests') {
            steps{
                dir('performance-tests'){
                    git url: 'git@github.com:derickdamoah/todo-performance-tests.git', branch: 'main', credentialsId: 'github_ssh_private_key'
                    sh '/opt/sbt/bin/sbt "gatling:test"'
                }
            }
        }


        stage('Deploy') {
            steps {
                sh '''
                    docker-compose exec app bash -c 'echo $MONGO_DB_USERNAME $MONGO_DB_PASSWORD'
                    printenv
                    export AWS_REGION='us-east-1'
                    aws configure list
                    /home/linuxbrew/.linuxbrew/bin/copilot svc package
                '''
            }
        }
    }
    post {
        always {
            sh 'docker-compose down --volumes'
            sh 'docker-compose ps'
        }
    }
}