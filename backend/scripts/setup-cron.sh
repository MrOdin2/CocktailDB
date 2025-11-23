#!/bin/bash

# CocktailDB Cron Setup Script
# This script sets up the daily backup cron job

# Configuration
BACKUP_SCRIPT="/var/lib/postgresql/scripts/backup.sh"
CRON_TIME="${BACKUP_CRON_TIME:-0 2 * * *}"  # Default: 2 AM daily

echo "Setting up daily database backup cron job..."
echo "Schedule: ${CRON_TIME}"

# Create cron job
echo "${CRON_TIME} ${BACKUP_SCRIPT} >> /var/log/postgresql/backup.log 2>&1" > /etc/cron.d/cocktaildb-backup

# Set proper permissions
chmod 0644 /etc/cron.d/cocktaildb-backup

# Create log file if it doesn't exist
touch /var/log/postgresql/backup.log
chown postgres:postgres /var/log/postgresql/backup.log

echo "Cron job installed successfully"
echo "Backup logs will be written to: /var/log/postgresql/backup.log"
