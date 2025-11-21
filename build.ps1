# Build script for CocktailDB Docker deployment (PowerShell version)

$ErrorActionPreference = "Stop"

Write-Host "Building CocktailDB for Docker deployment..." -ForegroundColor Green

# Build backend JAR
Write-Host "`nBuilding backend JAR..." -ForegroundColor Yellow
Set-Location backend
# Set JAVA_HOME for the gradle process
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.6.10-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
# Run gradle with cmd to ensure environment variables are inherited
cmd /c "gradlew.bat clean bootJar --no-daemon"
if ($LASTEXITCODE -ne 0) {
    Write-Host "Backend build failed!" -ForegroundColor Red
    Set-Location ..
    exit 1
}
Set-Location ..

# Build frontend production bundle
Write-Host "`nBuilding frontend production bundle..." -ForegroundColor Yellow
Set-Location frontend
npm ci
if ($LASTEXITCODE -ne 0) {
    Write-Host "npm ci failed!" -ForegroundColor Red
    Set-Location ..
    exit 1
}
npm run build:prod
if ($LASTEXITCODE -ne 0) {
    Write-Host "Frontend build failed!" -ForegroundColor Red
    Set-Location ..
    exit 1
}
Set-Location ..

Write-Host "`nBuild complete! You can now run 'docker compose up -d' to start the application." -ForegroundColor Green

