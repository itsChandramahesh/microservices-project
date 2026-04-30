pipeline {
    agent any

    tools {
        jdk 'jdk-21'          // 🔥 IMPORTANT: Configure this in Jenkins
        maven 'maven-3'       // Ensure Maven is configured
    }

    environment {
        DOCKER_BUILDKIT = "1"
        JAVA_HOME = tool name: 'jdk-21', type: 'jdk'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }

    stages {

        // 🔹 0. Debug Environment (VERY IMPORTANT)
        stage('Check Environment') {
            steps {
                sh '''
                echo "Java Version:"
                java -version
                javac -version

                echo "Maven Version:"
                mvn -version

                echo "Docker Version:"
                docker --version
                '''
            }
        }

        // 🔹 1. Clone Repository
        stage('Clone Repository') {
            steps {
                git branch: 'main', url: 'https://github.com/itsChandramahesh/microservices-project'
            }
        }

        // 🔹 2. Build Spring Boot (Maven)
        stage('Build Spring Boot Services') {
            steps {
                sh '''
                echo "Building Spring Boot services..."

                cd user-service
                mvn clean package -DskipTests

                cd ../product-service
                mvn clean package -DskipTests

                cd ../order-service
                mvn clean package -DskipTests
                '''
            }
        }

        // 🔹 3. Test with Docker Compose
        stage('Docker Compose Test') {
            steps {
                sh '''
                echo "Starting docker-compose for testing..."

                docker-compose down || true
                docker-compose up -d --build

                echo "Waiting for services..."
                sleep 30

                docker-compose ps
                '''
            }
        }

        // 🔹 4. Build Docker Images
        stage('Build Docker Images') {
            steps {
                sh '''
                echo "Building Docker images..."

                docker build -t user-service ./user-service
                docker build -t product-service ./product-service
                docker build -t order-service ./order-service

                docker build -t payment-service ./payment-service
                docker build -t notification-service ./notification-service
                docker build -t analytics-service ./analytics-service

                docker build -t frontend ./order-frontend
                '''
            }
        }

        // 🔹 5. Stop Docker Compose
        stage('Stop Compose') {
            steps {
                sh 'docker-compose down'
            }
        }

        // 🔹 6. Load Images into Minikube
        stage('Load Images to Minikube') {
            steps {
                sh '''
                echo "Loading images into Minikube..."

                minikube image load user-service
                minikube image load product-service
                minikube image load order-service

                minikube image load payment-service
                minikube image load notification-service
                minikube image load analytics-service

                minikube image load frontend
                '''
            }
        }

        // 🔹 7. Deploy to Kubernetes
        stage('Deploy to Kubernetes') {
            steps {
                sh '''
                echo "Deploying to Kubernetes..."

                kubectl apply -f k8s/
                '''
            }
        }

        // 🔹 8. Verify Deployment
        stage('Verify Deployment') {
            steps {
                sh '''
                echo "Checking deployment..."

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