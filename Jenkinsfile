pipeline {
    agent any
    environment {
        // Read the content of the .env file
        ENV_FILE_CONTENT = readFile('.env').trim()

        // Define regular expressions for extracting variables
        def usernameRegex = /MONGO_DB_USERNAME=([^\s]+)/
        def passwordRegex = /MONGO_DB_PASSWORD=([^\s]+)/

        // Match the regular expressions against the content
        def usernameMatch = ENV_FILE_CONTENT =~ usernameRegex
        def passwordMatch = ENV_FILE_CONTENT =~ passwordRegex

        // Extract the values
        def mongoUsername = usernameMatch ? usernameMatch[0][1] : null
        def mongoPassword = passwordMatch ? passwordMatch[0][1] : null

        MONGO_DB_USERNAME = ${mongoUsername}
        MONGO_DB_PASSWORD = ${mongoPassword}

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
                    echo "MONGO_DB_USERNAME: ${MONGO_DB_USERNAME}"
                    echo "MONGO_DB_PASSWORD: ${MONGO_DB_PASSWORD}"
                    /home/linuxbrew/.linuxbrew/bin/copilot env init --name test --profile default --default-config
                    /home/linuxbrew/.linuxbrew/bin/copilot env deploy --force
                    /home/linuxbrew/.linuxbrew/bin/copilot svc deploy
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