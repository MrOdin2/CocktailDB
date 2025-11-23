#!/bin/bash

# CocktailDB PostgreSQL Backup Script
# This script creates a backup of the CocktailDB PostgreSQL database

set -e

# Configuration
BACKUP_DIR="/var/lib/postgresql/backups"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="cocktaildb_backup_${TIMESTAMP}.sql"
DAYS_TO_KEEP=7

# Database credentials (from environment variables)
DB_NAME="${POSTGRES_DB:-cocktaildb}"
DB_USER="${POSTGRES_USER:-cocktaildb}"

# Create backup directory if it doesn't exist
mkdir -p "${BACKUP_DIR}"

echo "Starting backup of database: ${DB_NAME}"
echo "Backup file: ${BACKUP_FILE}"

# Create the backup
pg_dump -U "${DB_USER}" -d "${DB_NAME}" -F p -f "${BACKUP_DIR}/${BACKUP_FILE}"

# Compress the backup
gzip "${BACKUP_DIR}/${BACKUP_FILE}"

echo "Backup completed: ${BACKUP_DIR}/${BACKUP_FILE}.gz"

# Clean up old backups (keep only last DAYS_TO_KEEP days)
echo "Cleaning up backups older than ${DAYS_TO_KEEP} days..."
find "${BACKUP_DIR}" -name "cocktaildb_backup_*.sql.gz" -type f -mtime +${DAYS_TO_KEEP} -delete

# List current backups
echo "Current backups:"
ls -lh "${BACKUP_DIR}" | grep "cocktaildb_backup"

echo "Backup process completed successfully"
