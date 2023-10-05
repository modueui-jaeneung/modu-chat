pipeline {
    agent any

    environment {
        dockerImage = ''
        dockerImageRegistry = ''
        ECR_PATH = credentials('ecr-path')
        ECR_REPOSITORY = 'modu-chat'
        REGION = 'ap-northeast-2'
        AWS_CREDENTIAL_ID = 'build-test_ecr'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    credentialsId: 'github_access_token_hugo',
                    url: 'https://github.com/modueui-jaeneung/modu-chat.git'
            }
            post {
                success {
                     sh 'echo "Successfully Cloned Repository"'
                 }
                 failure {
                     sh 'echo "Fail Cloned Repository"'
                 }
            }
        }

        stage('Build') {
            steps {
                dir('.') {
                    sh """
                    ./gradlew clean build -x test
                    """
                }
            }
            post {
                success {
                    sh 'echo "gradle build success"'
                }
                failure {
                    sh 'echo "gradle build fail"'
                }
            }
        }

        stage('Build docker image') {
            steps {
                script {
                    dockerImageRegistry = '$ECR_PATH/$ECR_REPOSITORY'
                    dockerImage = docker.build dockerImageRegistry + ":1.$BUILD_NUMBER"
                }
            }
            post {
                failure {
                    sh 'echo "Bulid Docker Fail"'
                }
            }
        }

        stage('Push to ECR') {
            steps {
                script {
                    docker.withRegistry("https://" + ECR_PATH , "ecr:ap-northeast-2:" + AWS_CREDENTIAL_ID) {
                        dockerImage.push()
                    }
                }
            }
        }

        // stage('Push docker image') {
        //     steps {
        //         script {
        //             docker.withRegistry( '', AWS_CREDENTIAL_ID ) {
        //                 dockerImage.push()
        //             }
        //         }
        //     }
        //     post {
        //         success {
        //             sh "docker rmi $dockerHubRegistry:1.$BUILD_NUMBER"
        //         }
        //         failure {
        //             error 'Docker Image Push Fail'
        //         }
        //     }
        // }

    }
}
