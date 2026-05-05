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

        // 🔹 3. Docker Compose Test
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

        // 🔥 4. Setup KIND Cluster
        stage('Setup KIND Cluster') {
            steps {
                sh '''
                echo "🔧 Setting up KIND cluster..."

                # Delete old cluster if exists
                kind delete cluster || true

                # Create new cluster
                kind create cluster --name dev-cluster

                echo "📌 Verifying cluster..."
                kubectl cluster-info
                kubectl get nodes
                '''
            }
        }

        // 🔥 5. Build Docker Images (LOCAL)
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

                docker images
                '''
            }
        }

        // 🔥 6. Load Images into KIND
        stage('Load Images to KIND') {
            steps {
                sh '''
                echo "Loading images into KIND..."

                kind load docker-image user-service --name dev-cluster
                kind load docker-image product-service --name dev-cluster
                kind load docker-image order-service --name dev-cluster

                kind load docker-image payment-service --name dev-cluster
                kind load docker-image notification-service --name dev-cluster
                kind load docker-image analytics-service --name dev-cluster

                kind load docker-image frontend --name dev-cluster
                '''
            }
        }

        // 🔹 7. Deploy to Kubernetes
        stage('Deploy to Kubernetes') {
            steps {
                sh '''
                echo "Deploying to Kubernetes..."

                kubectl apply -f k8s/

                echo "⏳ Waiting for pods..."
                kubectl wait --for=condition=Ready pods --all --timeout=180s
                '''
            }
        }

        // 🔹 8. Verify
        stage('Verify Deployment') {
            steps {
                sh '''
                echo "📊 Deployment status..."

                kubectl get pods -o wide
                kubectl get svc
                '''
            }
        }
    }

    post {
        success {
            echo '✅ CI/CD Pipeline Completed Successfully with KIND!'
        }
        failure {
            echo '❌ Pipeline Failed. Check logs.'pipeline {
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

        // 🔹 3. Docker Compose Test
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

        // 🔥 4. Start Minikube
        stage('Start Minikube') {
    steps {
        sh '''
        echo "🔧 Setting up Minikube..."

        # Clean broken cluster if exists
        minikube delete || true

        echo "🚀 Starting Minikube..."

        minikube start \
          --driver=docker \
          --memory=4096 \
          --cpus=2 \
          --kubernetes-version=v1.28.3 \
          --force

        echo "⏳ Waiting for Kubernetes API..."

        # Wait until node is ready
        kubectl wait --for=condition=Ready node/minikube --timeout=120s

        echo "📌 Setting context..."
        kubectl config use-context minikube

        echo "📊 Cluster status:"
        kubectl get nodes
        kubectl get pods -A
        '''
    }
}

        // 🔥 5. Build Images INSIDE Minikube
        stage('Build Images in Minikube') {
            steps {
                sh '''
                echo "Switching to Minikube Docker..."

                eval $(minikube docker-env)

                docker build -t user-service ./user-service
                docker build -t product-service ./product-service
                docker build -t order-service ./order-service

                docker build -t payment-service ./payment-service
                docker build -t notification-service ./notification-service
                docker build -t analytics-service ./analytics-service

                docker build -t frontend ./order-frontend

                docker images
                '''
            }
        }

        // 🔹 6. Deploy to Kubernetes
        stage('Deploy to Kubernetes') {
            steps {
                sh '''
                echo "Deploying to Kubernetes..."

                kubectl apply -f k8s/

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
        }
    }
}