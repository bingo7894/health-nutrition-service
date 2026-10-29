pipeline {
    agent any

    environment {
        AWS_REGION     = 'ap-southeast-1'
        AWS_ACCOUNT_ID = '123456789012'
        ECR_REGISTRY   = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
        IMAGE_TAG      = "${BUILD_NUMBER}"
        SERVICE_NAME   = "health-nutrition-service"
    }

    stages {
        stage('Test & Build JAR') {
            steps {
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${ECR_REGISTRY}/${SERVICE_NAME}:${IMAGE_TAG} ."
                sh "docker tag ${ECR_REGISTRY}/${SERVICE_NAME}:${IMAGE_TAG} ${ECR_REGISTRY}/${SERVICE_NAME}:latest"
            }
        }

        stage('Push to AWS ECR') {
            steps {
                withAWS(region: "${AWS_REGION}", credentials: 'aws-jenkins-credentials') {
                    sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}"
                    sh "docker push ${ECR_REGISTRY}/${SERVICE_NAME}:${IMAGE_TAG}"
                    sh "docker push ${ECR_REGISTRY}/${SERVICE_NAME}:latest"
                }
            }
        }

        stage('Deploy to AWS ECS') {
            steps {
                withAWS(region: "${AWS_REGION}", credentials: 'aws-jenkins-credentials') {
                    sh """
                    aws ecs update-service --cluster health-app-cluster \
                                           --service ${SERVICE_NAME} \
                                           --force-new-deployment
                    """
                }
            }
        }
    }

    post {
        always {
            sh 'docker system prune -f'
        }
    }
}

