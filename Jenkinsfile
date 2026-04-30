pipeline {
    agent any

    environment {
        DOCKER_BUILDKIT = "1"
        DOCKER_USER = "your-dockerhub-username"
    }

    stages {

        stage('Clone Repository') {
            steps {
                git branch: 'main', url: 'https://github.com/itsChandramahesh/microservices-project'
            }
        }

        stage('Build Spring Boot Services') {
            steps {
                sh '''
                cd user-service && mvn clean package -DskipTests
                cd ../product-service && mvn clean package -DskipTests
                cd ../order-service && mvn clean package -DskipTests
                '''
            }
        }

        stage('Docker Compose Test') {
            steps {
                sh '''
                docker-compose down || true
                docker-compose up -d --build
                sleep 20
                docker-compose ps
                '''
            }
        }

        stage('Build Docker Images') {
            steps {
                sh '''
                docker build -t $DOCKER_USER/user-service ./user-service
                docker build -t $DOCKER_USER/product-service ./product-service
                docker build -t $DOCKER_USER/order-service ./order-service

                docker build -t $DOCKER_USER/payment-service ./payment-service
                docker build -t $DOCKER_USER/notification-service ./notification-service
                docker build -t $DOCKER_USER/analytics-service ./analytics-service

                docker build -t $DOCKER_USER/frontend ./order-frontend
                '''
            }
        }

        stage('Push to Docker Hub') {
    steps {
        withCredentials([usernamePassword(
            credentialsId: 'dockerhub-creds',
            usernameVariable: 'DOCKER_USER',
            passwordVariable: 'DOCKER_PASS'
        )]) {
            sh '''
            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

            echo "Tagging images..."

            docker tag user-service $DOCKER_USER/user-service
            docker tag product-service $DOCKER_USER/product-service
            docker tag order-service $DOCKER_USER/order-service

            docker tag payment-service $DOCKER_USER/payment-service
            docker tag notification-service $DOCKER_USER/notification-service
            docker tag analytics-service $DOCKER_USER/analytics-service

            docker tag frontend $DOCKER_USER/frontend

            echo "Pushing images..."

            docker push $DOCKER_USER/user-service
            docker push $DOCKER_USER/product-service
            docker push $DOCKER_USER/order-service

            docker push $DOCKER_USER/payment-service
            docker push $DOCKER_USER/notification-service
            docker push $DOCKER_USER/analytics-service

            docker push $DOCKER_USER/frontend
            '''
        }
    }
}
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh '''
                kubectl apply -f k8s/
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                sh '''
                kubectl get pods
                kubectl get svc
                '''
            }
        }
    }

    post {
        success {
            echo '✅ CI/CD Pipeline Completed Successfully!'
        }
        failure {
            echo '❌ Pipeline Failed. Check logs.'
        }
    }
}