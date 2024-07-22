pipeline {
    agent any

    tools {
        gradle 'Gradle'  
    }

    environment {
        AWS_REGION = 'us-east-1'
        ECR_REPOSITORY = '393035689023.dkr.ecr.us-east-1.amazonaws.com'
        IMAGE_REPO_NAME = "weasel-backend"
        IMAGE_TAG = "latest"
        REPOSITORY_URI = "393035689023.dkr.ecr.us-east-1.amazonaws.com/weasel-backend"
        AWS_ACCOUNT_ID = "393035689023"
        AWS_CREDENTIAL = "weasel-AWS-Credential"
        GIT_URL = 'https://github.com/Team-S5T1/weasel-backend.git'
        SLACK_CHANNEL = '#alarm-jenkins'
        SLACK_CREDENTIALS_ID = 'weasel-slack-alarm'
    }

    stages {
       stage('Login to ECR') {
            steps {
                script {
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: "${AWS_CREDENTIAL}"]]) {
                       sh """
                            aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPOSITORY}
                          """

                    }
                }
            }
        }
        
        stage('Cloning Git') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: '*/main']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '', url: "${GIT_URL}"]]])
            }
        }

        stage('Build') {
            steps {
                script {
                sh """
                    chmod +x ./gradlew
                    ./gradlew clean build --no-daemon
                """
                }
            }
        }
        
        stage('Building image') {
          steps{
                script {
                        sh "docker build -t ${IMAGE_REPO_NAME}:${IMAGE_TAG} ."
                }
          }
        }


        stage('Pushing to ECR') {
            steps {
                script {
                    sh """docker tag ${IMAGE_REPO_NAME}:${IMAGE_TAG} ${REPOSITORY_URI}:${IMAGE_TAG}"""
                    sh """docker push ${REPOSITORY_URI}:${IMAGE_TAG}"""
                }
            }
        }
        
        stage('Delete Docker images') {
            steps {
                script {
                    sh """docker rmi ${IMAGE_REPO_NAME}:${IMAGE_TAG}"""
                }
            }
        }
    }
    
    post {
        success {
            slackSend(channel: SLACK_CHANNEL, message: "Build succeeded: ${env.JOB_NAME} #${env.BUILD_NUMBER} - ${env.BUILD_URL}")
        }
        failure {
            slackSend(channel: SLACK_CHANNEL, message: "Build failed: ${env.JOB_NAME} #${env.BUILD_NUMBER} - ${env.BUILD_URL}")
        }
        unstable {
            slackSend(channel: SLACK_CHANNEL, message: "Build unstable: ${env.JOB_NAME} #${env.BUILD_NUMBER} - ${env.BUILD_URL}")
        }
    }
}