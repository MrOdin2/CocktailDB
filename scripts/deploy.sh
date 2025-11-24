#!/bin/bash
# CocktailDB Server Deployment Script
# This script pulls the latest Docker images and deploys them on the server

set -e

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
REGISTRY="${REGISTRY:-ghcr.io}"
REPO="${REPO:-mrodin2/cocktaildb}"
TAG="${TAG:-latest}"
COMPOSE_FILE="${COMPOSE_FILE:-docker-compose.prod.yml}"

# Functions
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

check_prerequisites() {
    log_info "Checking prerequisites..."
    
    if ! command -v docker &> /dev/null; then
        log_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    
    if ! command -v docker compose &> /dev/null; then
        log_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi
    
    if [ ! -f ".env" ]; then
        log_warn ".env file not found. Creating from .env.example..."
        if [ -f ".env.example" ]; then
            cp .env.example .env
            log_info "Please edit .env file with your configuration before running again."
            exit 0
        else
            log_error ".env.example not found. Cannot create .env file."
            exit 1
        fi
    fi
    
    log_info "Prerequisites check passed."
}

backup_database() {
    log_info "Creating database backup before deployment..."
    
    if docker ps --format '{{.Names}}' | grep -q "cocktaildb-postgres"; then
        docker exec cocktaildb-postgres pg_dump -U cocktaildb cocktaildb > "backup_$(date +%Y%m%d_%H%M%S).sql" || log_warn "Backup failed, continuing anyway..."
        log_info "Database backup completed."
    else
        log_info "Database container not running, skipping backup."
    fi
}

pull_images() {
    log_info "Pulling latest Docker images..."
    
    docker pull "${REGISTRY}/${REPO}/backend:${TAG}"
    docker pull "${REGISTRY}/${REPO}/frontend:${TAG}"
    docker pull "${REGISTRY}/${REPO}/postgres:${TAG}"
    
    log_info "Images pulled successfully."
}

stop_services() {
    log_info "Stopping current services..."
    
    if [ -f "$COMPOSE_FILE" ]; then
        docker compose -f "$COMPOSE_FILE" down || true
    else
        docker compose down || true
    fi
    
    log_info "Services stopped."
}

start_services() {
    log_info "Starting services with new images..."
    
    if [ -f "$COMPOSE_FILE" ]; then
        docker compose -f "$COMPOSE_FILE" up -d
    else
        docker compose up -d
    fi
    
    log_info "Services started."
}

wait_for_health() {
    log_info "Waiting for services to be healthy..."
    
    local max_attempts=30
    local attempt=0
    
    while [ $attempt -lt $max_attempts ]; do
        if docker compose ps | grep -q "healthy"; then
            log_info "Services are healthy."
            return 0
        fi
        
        attempt=$((attempt + 1))
        echo -n "."
        sleep 2
    done
    
    log_warn "Health check timed out. Services may still be starting."
    return 1
}

verify_deployment() {
    log_info "Verifying deployment..."
    
    # Check if containers are running
    if ! docker ps | grep -q "cocktaildb-backend"; then
        log_error "Backend container is not running!"
        return 1
    fi
    
    if ! docker ps | grep -q "cocktaildb-frontend"; then
        log_error "Frontend container is not running!"
        return 1
    fi
    
    if ! docker ps | grep -q "cocktaildb-postgres"; then
        log_error "Postgres container is not running!"
        return 1
    fi
    
    # Check backend health
    if curl -f -s http://localhost:8080/api/auth/status > /dev/null; then
        log_info "Backend is responding correctly."
    else
        log_warn "Backend health check failed. It may still be starting."
    fi
    
    # Check frontend
    if curl -f -s http://localhost:80 > /dev/null; then
        log_info "Frontend is responding correctly."
    else
        log_warn "Frontend health check failed. It may still be starting."
    fi
    
    log_info "Deployment verification completed."
}

cleanup_old_images() {
    log_info "Cleaning up old Docker images..."
    docker image prune -f
    log_info "Cleanup completed."
}

show_status() {
    log_info "Current deployment status:"
    docker compose ps
}

rollback() {
    log_error "Deployment failed! Rolling back..."
    
    # Stop new deployment
    docker compose down
    
    # Restore from backup if available
    local latest_backup=$(ls -t backup_*.sql 2>/dev/null | head -n1)
    if [ -n "$latest_backup" ]; then
        log_info "Restoring from backup: $latest_backup"
        # Start only postgres
        docker compose up -d postgres
        sleep 5
        docker exec -i cocktaildb-postgres psql -U cocktaildb cocktaildb < "$latest_backup"
        log_info "Database restored."
    fi
    
    log_error "Rollback completed. Please check logs and fix issues."
    exit 1
}

# Main deployment flow
main() {
    log_info "Starting CocktailDB deployment..."
    log_info "Registry: ${REGISTRY}"
    log_info "Repository: ${REPO}"
    log_info "Tag: ${TAG}"
    
    check_prerequisites
    backup_database
    
    # Pull new images
    if ! pull_images; then
        log_error "Failed to pull images."
        exit 1
    fi
    
    # Stop current services
    stop_services
    
    # Start new services
    if ! start_services; then
        rollback
    fi
    
    # Wait for services to be healthy
    wait_for_health || true
    
    # Verify deployment
    if ! verify_deployment; then
        log_warn "Deployment verification had warnings. Check the logs."
    fi
    
    # Cleanup
    cleanup_old_images
    
    # Show final status
    show_status
    
    log_info "Deployment completed successfully!"
    log_info "Frontend: http://$(hostname -I | awk '{print $1}')"
    log_info "Backend API: http://$(hostname -I | awk '{print $1}'):8080/api"
}

# Run main function
main "$@"
