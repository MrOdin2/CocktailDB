#!/bin/bash

# CocktailDB PostgreSQL Restore Script
# This script restores a backup of the CocktailDB PostgreSQL database

set -e

# Parse command line arguments
FORCE=false
while [[ $# -gt 0 ]]; do
    case $1 in
        -f|--force)
            FORCE=true
            shift
            ;;
        *)
            BACKUP_FILE="$1"
            shift
            ;;
    esac
done

# Check if backup file is provided
if [ -z "$BACKUP_FILE" ]; then
    echo "Usage: $0 [-f|--force] <backup_file.sql.gz>"
    echo ""
    echo "Options:"
    echo "  -f, --force    Skip confirmation prompt (for automated restores)"
    echo ""
    echo "Available backups:"
    ls -lh /var/lib/postgresql/backups/ | grep "cocktaildb_backup"
    exit 1
fi

DB_NAME="${POSTGRES_DB:-cocktaildb}"
DB_USER="${POSTGRES_USER:-cocktaildb}"

# Check if backup file exists
if [ ! -f "${BACKUP_FILE}" ]; then
    echo "Error: Backup file not found: ${BACKUP_FILE}"
    exit 1
fi

echo "WARNING: This will restore the database from backup and overwrite existing data!"
echo "Backup file: ${BACKUP_FILE}"
echo "Database: ${DB_NAME}"
echo ""

# Skip confirmation if force flag is set
if [ "$FORCE" = false ]; then
    read -p "Are you sure you want to continue? (yes/no): " CONFIRM
    if [ "${CONFIRM}" != "yes" ]; then
        echo "Restore cancelled"
        exit 0
    fi
fi

# Decompress if needed
if [[ "${BACKUP_FILE}" == *.gz ]]; then
    echo "Decompressing backup file..."
    TEMP_FILE="/tmp/restore_$(date +%Y%m%d_%H%M%S).sql"
    gunzip -c "${BACKUP_FILE}" > "${TEMP_FILE}"
    RESTORE_FILE="${TEMP_FILE}"
else
    RESTORE_FILE="${BACKUP_FILE}"
fi

echo "Starting restore..."

# Drop existing database and recreate (WARNING: This will delete all data!)
echo "Dropping and recreating database..."
psql -U "${DB_USER}" -d postgres -c "DROP DATABASE IF EXISTS ${DB_NAME};"
psql -U "${DB_USER}" -d postgres -c "CREATE DATABASE ${DB_NAME};"

# Restore the backup
echo "Restoring from backup..."
psql -U "${DB_USER}" -d "${DB_NAME}" < "${RESTORE_FILE}"

# Clean up temp file if created
if [ -n "${TEMP_FILE}" ] && [ -f "${TEMP_FILE}" ]; then
    rm -f "${TEMP_FILE}"
fi

echo "Database restore completed successfully!"