pipeline {
    agent any
    environment {
        SONAR_PROJECT_KEY='AppointmentService'
        PROJECT_ID='ardent-depth-474623-t8'
        LOCALIZATION='europe-west1'
        ARTIFACT_REPOSITORY='vitalis-ar'
        IMAGE_NAME='appointment-service'
        LOCALIZATION_KUBE='europe-west1-b'
        GCP_SA_KEY = credentials('gcloud-creds')
        GCR_HOSTNAME = 'europe-west1-docker.pkg.dev'
        GKE_CLUSTER_NAME = "vitalis-gke"
        GCR_REGION = 'europe-west1'
        KUBECONFIG = '/root/.kube/config'
        PATH="$PATH:/root/google-cloud-sdk/bin"
    }
    stages {

        stage('Compile project') {
            steps {
                dir('/home/vitalis_clinic_group/appointmentService') {
                    sh 'mvn clean compile'
                }
            }
        }

        stage('Test') {
            steps {
                dir('/home/vitalis_clinic_group/appointmentService') {
                    sh 'mvn test'
                }
            }
        }

        stage('Docker Build') {
            steps {
                dir('/home/vitalis_clinic_group/appointmentService') {
                    sh '''
                        docker images -af reference="$IMAGE_NAME" -q || true
                        docker build -t "$IMAGE_NAME":latest .
                        docker image prune -f
                    '''
                }
            }
        }

        stage('Push to Artifact Registry') {
            steps {
                withCredentials([file(credentialsId: 'gcloud-creds', variable: 'GC_CLOUD_CREDS')]) {
                    dir('/home/vitalis_clinic_group/appointmentService') {
                        sh '''
                            gcloud auth activate-service-account --key-file="$GC_CLOUD_CREDS"
                            gcloud auth configure-docker "$LOCALIZATION"-docker.pkg.dev
                            docker tag "$IMAGE_NAME" "$LOCALIZATION"-docker.pkg.dev/"$PROJECT_ID"/"$ARTIFACT_REPOSITORY"/"$IMAGE_NAME"
                            docker push "$LOCALIZATION"-docker.pkg.dev/"$PROJECT_ID"/"$ARTIFACT_REPOSITORY"/"$IMAGE_NAME"
                        '''
                    }
                }
            }
        }

        /*stage('Deploy Kubernetes') {
            steps {
                withCredentials([file(credentialsId: 'gcloud-creds', variable: 'GC_CLOUD_CREDS')]) {
                    dir('/home/vitalis_clinic_group/apiGateway') {
                        sh '''
                            gcloud auth activate-service-account --key-file="$GC_CLOUD_CREDS"
                            gcloud container clusters get-credentials "$GKE_CLUSTER_NAME" --region "$LOCALIZATION_KUBE"
                            kubectl apply -f dep.yml
                            kubectl set image deployment/api-gateway-deployment api-gateway=$GCR_HOSTNAME/$PROJECT_ID/$ARTIFACT_REPOSITORY/$IMAGE_NAME:latest --record
                            kubectl delete pods -l app=api-gateway
                        '''
                    }
                }
            }
        }*/
    }
}
