#!/bin/bash
# Build script for CocktailDB Docker deployment

set -e

echo "Building CocktailDB for Docker deployment..."

# Build backend JAR
echo "Building backend JAR..."
cd backend
./gradlew clean bootJar --no-daemon
cd ..

# Build frontend production bundle
echo "Building frontend production bundle..."
cd frontend
npm ci
npm run build:prod
cd ..

echo "Build complete! You can now run 'docker compose up -d' to start the application."
