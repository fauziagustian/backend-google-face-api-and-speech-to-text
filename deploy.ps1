$PROJECT_ID = "ai-project-487201"
$REGION = "asia-southeast2"
$SERVICE_NAME = "backend-api"
$IMAGE_NAME = "gcr.io/$PROJECT_ID/$SERVICE_NAME"

Write-Host "🚀 Starting Deployment to Google Cloud Platform..."
Write-Host "Project ID: $PROJECT_ID"
Write-Host "Region: $REGION"

# 1. Enable APIs
Write-Host "`n📦 Enabling necessary APIs..."
gcloud services enable run.googleapis.com cloudbuild.googleapis.com containerregistry.googleapis.com speech.googleapis.com vision.googleapis.com storage.googleapis.com

# 2. Build Container
Write-Host "`n🔨 Building Container Image..."
gcloud builds submit --tag $IMAGE_NAME .

# 3. Deploy to Cloud Run
Write-Host "`n🚀 Deploying to Cloud Run..."
# Note: We are deploying with 1GB memory and allowing unauthenticated access for testing.
# We also set the service account to the default compute service account which usually has Editor role, 
# BUT for production you should create a specific service account with limited roles.
gcloud run deploy $SERVICE_NAME `
  --image $IMAGE_NAME `
  --platform managed `
  --region $REGION `
  --allow-unauthenticated `
  --memory 1Gi

Write-Host "`n✅ Deployment Complete!"
Write-Host "Check the URL above to access your API."
