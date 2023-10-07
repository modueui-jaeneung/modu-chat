pipeline {
    agent any

    environment {
        gitEmail = 'hgk9872@gmail.com'
        gitName = 'hgk9872'
        dockerImage = ''
        dockerImageRegistry = ''
        ECR_PATH = credentials('ecr-path')
        ECR_REPOSITORY = 'modu-chat'
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
                    dockerImageRegistry = "${ECR_PATH}/${ECR_REPOSITORY}"
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
                    docker.withRegistry("https://" + ECR_PATH, "ecr:ap-northeast-2:" + AWS_CREDENTIAL_ID) {
                        dockerImage.push()
                    }
                }
            }
            post {
                success {
                    sh "docker rmi $dockerImageRegistry:1.$BUILD_NUMBER"
                }
                failure {
                    error 'Docker Image Push Fail'
                }
            }
        }

        stage('Update manifest registry') {
            steps {
                script {
                    // Clone the private manifest repo
                    git branch: 'main',
                        credentialsId: 'github_access_token_hugo',
                        url: 'https://github.com/modueui-jaeneung/kubernetes-manifest.git'

                    // Update the Docker image tag in the deployment yaml file
                    sh """
                        git config --global user.email ${gitEmail}
                        git config --global user.name ${gitName}
                        sed -i 's/$ECR_REPOSITORY:1.*\$/$ECR_REPOSITORY:1.$BUILD_NUMBER/g' chat-deployment.yaml
                        git add chat-deployment.yaml
                        git commit -m "Update Docker image tag for 1.$BUILD_NUMBER version"
                    """
                }
            }
            post {
                success {
                    withCredentials([gitUsernamePassword(credentialsId: 'github_access_token_hugo', gitToolName: 'Default')]) {
                        sh "git push -u origin main"
                    }
                }
                failure {
                    sh 'echo "Fail update manifest"'
                }
            }
        }


    }
}
