pipeline {
    agent any
    environment {
        ACCESS_KEY_ID=credentials('aws_access_key_id')
        AWS_SECRET_KEY=credentials('aws_secret_access_key')
        AWS_CREDS = credentials('aws_access_keys_credentials')
        AWS_DEFAULT_REGION    = 'us-east-1'
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
            sh 'aws configure list'
            sh '''
                export AWS_REGION=AWS_DEFAULT_REGION
                export AWS_ACCESS_KEY_ID=ACCESS_KEY_ID
                export AWS_SECRET_ACCESS_KEY=AWS_SECRET_KEY
                /home/linuxbrew/.linuxbrew/bin/copilot env init --name test --profile default --default-config
                /home/linuxbrew/.linuxbrew/bin/copilot init --app todo-list --name todo-list --type "Load Balanced Web Service" --dockerfile "./Dockerfile" --deploy
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