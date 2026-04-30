pipeline {
    agent any

    environment {
        DOCKER_BUILDKIT = "1"
    }

    stages {

        // 🔹 1. Clone
        stage('Clone Repository') {
            steps {
                git branch: 'main', url: 'https://github.com/itsChandramahesh/microservices-project'
            }
        }

        // 🔹 2. Build Backend
        stage('Build Spring Boot Services') {
            steps {
                sh '''
                cd user-service && mvn clean package -DskipTests
                cd ../product-service && mvn clean package -DskipTests
                cd ../order-service && mvn clean package -DskipTests
                '''
            }
        }

        // 🔹 3. Integration Test
        stage('Docker Compose Test') {
            steps {
                sh '''
                docker-compose down || true
                docker-compose up -d --build
                sleep 20
                docker-compose ps
                docker-compose down
                '''
            }
        }

        // 🔹 4. Build Docker Images
        stage('Build Docker Images') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                    echo "Building images..."

                    docker build -t $DOCKER_USER/user-service:latest ./user-service
                    docker build -t $DOCKER_USER/product-service:latest ./product-service
                    docker build -t $DOCKER_USER/order-service:latest ./order-service

                    docker build -t $DOCKER_USER/payment-service:latest ./payment-service
                    docker build -t $DOCKER_USER/notification-service:latest ./notification-service
                    docker build -t $DOCKER_USER/analytics-service:latest ./analytics-service

                    docker build -t $DOCKER_USER/frontend:latest ./order-frontend
                    '''
                }
            }
        }

        // 🔹 5. Push Images
        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                    echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                    echo "Pushing images..."

                    docker push $DOCKER_USER/user-service:latest
                    docker push $DOCKER_USER/product-service:latest
                    docker push $DOCKER_USER/order-service:latest

                    docker push $DOCKER_USER/payment-service:latest
                    docker push $DOCKER_USER/notification-service:latest
                    docker push $DOCKER_USER/analytics-service:latest

                    docker push $DOCKER_USER/frontend:latest
                    '''
                }
            }
        }

        // 🔹 6. Deploy to Kubernetes
        stage('Deploy to Kubernetes') {
            steps {
                sh '''
                echo "Deploying to Kubernetes..."

                kubectl apply -f k8s/

                echo "Waiting for pods..."
                sleep 15
                '''
            }
        }

        // 🔹 7. Verify
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