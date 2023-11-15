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
            sh'''
                export AWS_REGION=us-east-1
                /home/linuxbrew/.linuxbrew/bin/copilot app init todo-list
                /home/linuxbrew/.linuxbrew/bin/copilot env init --name test --default-config --profile default
                /home/linuxbrew/.linuxbrew/bin/copilot env deploy --name test
                /home/linuxbrew/.linuxbrew/bin/copilot svc deploy --name todo
                /home/linuxbrew/.linuxbrew/bin/copilot svc init --name todo-list --svc-type "Load Balanced Web Service" --dockerfile "./Dockerfile"
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