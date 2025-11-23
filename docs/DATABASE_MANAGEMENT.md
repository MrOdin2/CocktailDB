# Database Management Guide

This guide covers database migrations and backups for the CocktailDB application.

## Table of Contents

1. [Database Migrations with Flyway](#database-migrations-with-flyway)
2. [Database Backups](#database-backups)
3. [Backup Restoration](#backup-restoration)
4. [Troubleshooting](#troubleshooting)

## Database Migrations with Flyway

CocktailDB uses [Flyway](https://flywaydb.org/) for database version control and migrations. This ensures that database schema changes are tracked, versioned, and applied consistently across all environments.

### How It Works

- Flyway migration scripts are located in `backend/src/main/resources/db/migration/`
- Migration files follow the naming pattern: `V{version}__{description}.sql`
- Migrations are automatically applied when the application starts
- Flyway tracks which migrations have been applied in the `flyway_schema_history` table

### Creating a New Migration

1. Create a new SQL file in `backend/src/main/resources/db/migration/`
2. Name it following the pattern: `V{next_version}__{description}.sql`
   - Example: `V2__Add_user_table.sql`
3. Write your SQL DDL statements in the file
4. Restart the application - Flyway will automatically detect and apply the new migration

#### Migration Best Practices

- **Never modify** existing migration files after they've been applied
- Always create a new migration for schema changes
- Test migrations on a dev database before production
- Keep migrations focused - one logical change per migration
- Add comments to explain complex changes

#### Example Migration

```sql
-- V2__Add_cocktail_ratings.sql
-- Add rating functionality to cocktails

ALTER TABLE cocktails ADD COLUMN rating DECIMAL(3,2);
ALTER TABLE cocktails ADD COLUMN rating_count INTEGER DEFAULT 0;

CREATE INDEX idx_cocktails_rating ON cocktails(rating);
```

### Migration Commands

When running the backend application, migrations are handled automatically. However, you can also use the Gradle Flyway plugin for manual control:

```bash
cd backend

# Check migration status
./gradlew flywayInfo

# Validate migrations
./gradlew flywayValidate

# Repair migration history (use with caution)
./gradlew flywayRepair
```

### Disabling Flyway (Development Only)

For local H2 development, Flyway is not needed as the schema is created from scratch each time. The `dev` profile uses `spring.jpa.hibernate.ddl-auto=create-drop`.

For PostgreSQL development (`dev-postgres` profile) and production (`prod` profile), Flyway is enabled and Hibernate DDL is set to `validate` to ensure the schema matches the JPA entities.

## Database Backups

CocktailDB includes automated daily backups for PostgreSQL databases running in Docker.

### Automated Backups

Backups are automatically created daily at 2:00 AM (configurable via `BACKUP_CRON_TIME` environment variable).

#### Backup Configuration

Edit `.env` file (or docker-compose environment):

```env
# Backup schedule in cron format
# Default: Daily at 2 AM
BACKUP_CRON_TIME=0 2 * * *

# Examples:
# Every 6 hours: 0 */6 * * *
# Weekly on Sunday at 3 AM: 0 3 * * 0
# Twice daily (2 AM and 2 PM): 0 2,14 * * *
```

#### Backup Storage

- Backups are stored in the Docker volume `postgres_backups`
- Backups are compressed with gzip
- Backups are automatically cleaned up after 7 days
- File format: `cocktaildb_backup_YYYYMMDD_HHMMSS.sql.gz`

#### Accessing Backup Files

To access backup files from the host system:

```bash
# List all backups
docker exec cocktaildb-postgres ls -lh /var/lib/postgresql/backups/

# Copy a backup to host
docker cp cocktaildb-postgres:/var/lib/postgresql/backups/cocktaildb_backup_20240115_020000.sql.gz ./

# Copy a backup from host to container
docker cp ./my_backup.sql.gz cocktaildb-postgres:/var/lib/postgresql/backups/
```

#### Accessing Backup Volume

```bash
# Find the volume location
docker volume inspect cocktaildb_postgres_backups

# Or use docker-desktop/docker-compose volume browser
```

### Manual Backup

To create a manual backup:

```bash
# Run backup script manually
docker exec cocktaildb-postgres /var/lib/postgresql/scripts/backup.sh

# Or create a custom backup with pg_dump
docker exec cocktaildb-postgres pg_dump -U cocktaildb cocktaildb > cocktaildb_manual_$(date +%Y%m%d).sql
```

### Viewing Backup Logs

```bash
# View backup log
docker exec cocktaildb-postgres tail -f /var/log/postgresql/backup.log

# View cron logs
docker exec cocktaildb-postgres tail -f /var/log/cron
```

## Backup Restoration

### Restore from Automatic Backup

1. List available backups:

```bash
docker exec cocktaildb-postgres ls -lh /var/lib/postgresql/backups/
```

2. Stop the application to prevent data conflicts:

```bash
docker-compose stop backend
```

3. Restore the backup:

```bash
docker exec -it cocktaildb-postgres /var/lib/postgresql/scripts/restore.sh /var/lib/postgresql/backups/cocktaildb_backup_YYYYMMDD_HHMMSS.sql.gz
```

4. Restart the application:

```bash
docker-compose start backend
```

### Restore from Manual Backup

If you have a backup file on your host system:

1. Copy the backup to the container:

```bash
docker cp ./my_backup.sql.gz cocktaildb-postgres:/tmp/
```

2. Stop the backend:

```bash
docker-compose stop backend
```

3. Restore:

```bash
docker exec -it cocktaildb-postgres /var/lib/postgresql/scripts/restore.sh /tmp/my_backup.sql.gz
```

4. Restart:

```bash
docker-compose start backend
```

### Restore to a Different Environment

To restore a production backup to a development environment:

1. Copy the backup file from production
2. Ensure your dev environment is using PostgreSQL (`dev-postgres` profile)
3. Follow the restore steps above

## Troubleshooting

### Migration Fails

**Issue**: Migration fails with validation error

```
FlywayValidateException: Validate failed: Migration checksum mismatch
```

**Solution**: This usually means a migration file was modified after being applied.

1. Check which migration is causing the issue:
   ```bash
   docker-compose logs backend | grep Flyway
   ```

2. If you're in development and can afford to lose data:
   ```bash
   # Drop and recreate the database
   docker-compose down -v
   docker-compose up
   ```

3. For production, you may need to repair the Flyway schema history (use with caution):
   ```bash
   # Access the database
   docker exec -it cocktaildb-postgres psql -U cocktaildb
   
   # Check Flyway history
   SELECT * FROM flyway_schema_history;
   
   # If needed, update the checksum (consult Flyway documentation)
   ```

### Backup Script Not Running

**Issue**: Daily backups are not being created

**Solution**:

1. Check if cron is running:
   ```bash
   docker exec cocktaildb-postgres ps aux | grep cron
   ```

2. Check cron job configuration:
   ```bash
   docker exec cocktaildb-postgres cat /etc/cron.d/cocktaildb-backup
   ```

3. Check backup logs:
   ```bash
   docker exec cocktaildb-postgres cat /var/log/postgresql/backup.log
   ```

4. Test backup script manually:
   ```bash
   docker exec cocktaildb-postgres /var/lib/postgresql/scripts/backup.sh
   ```

### Out of Disk Space

**Issue**: Backups are filling up disk space

**Solution**:

1. Check backup directory size:
   ```bash
   docker exec cocktaildb-postgres du -sh /var/lib/postgresql/backups/
   ```

2. Manually clean old backups:
   ```bash
   # Delete backups older than 7 days
   docker exec cocktaildb-postgres find /var/lib/postgresql/backups/ -name "cocktaildb_backup_*.sql.gz" -type f -mtime +7 -delete
   ```

3. Adjust retention in backup script or reduce backup frequency

### Restore Fails

**Issue**: Restore script fails or database is corrupted

**Solution**:

1. Check backup file integrity:
   ```bash
   docker exec cocktaildb-postgres gunzip -t /var/lib/postgresql/backups/cocktaildb_backup_YYYYMMDD_HHMMSS.sql.gz
   ```

2. Try restoring to a new database first:
   ```bash
   # Create test database
   docker exec -it cocktaildb-postgres psql -U cocktaildb -d postgres -c "CREATE DATABASE cocktaildb_test;"
   
   # Restore to test database
   docker exec cocktaildb-postgres gunzip -c /var/lib/postgresql/backups/cocktaildb_backup_YYYYMMDD_HHMMSS.sql.gz | docker exec -i cocktaildb-postgres psql -U cocktaildb -d cocktaildb_test
   ```

3. If successful, drop the production database and rename:
   ```bash
   docker exec -it cocktaildb-postgres psql -U cocktaildb -d postgres -c "DROP DATABASE cocktaildb;"
   docker exec -it cocktaildb-postgres psql -U cocktaildb -d postgres -c "ALTER DATABASE cocktaildb_test RENAME TO cocktaildb;"
   ```

### Migrating from Old Setup (without Flyway)

If you're upgrading from a version without Flyway:

1. Create a backup of your current database
2. Flyway will detect the existing schema and create a baseline
3. The `baseline-on-migrate` setting ensures Flyway doesn't try to recreate existing tables

## Cloud Backup Integration (Optional)

For production deployments, consider backing up to cloud storage:

### AWS S3 Example

Add to your backup script:

```bash
# Install AWS CLI in Dockerfile
RUN apk add --no-cache aws-cli

# In backup.sh, after creating backup:
aws s3 cp "${BACKUP_DIR}/${BACKUP_FILE}.gz" "s3://your-bucket/cocktaildb-backups/"
```

### Google Cloud Storage Example

```bash
# Install gsutil
RUN apk add --no-cache python3 py3-pip
RUN pip3 install gsutil

# In backup.sh:
gsutil cp "${BACKUP_DIR}/${BACKUP_FILE}.gz" "gs://your-bucket/cocktaildb-backups/"
```

## Best Practices

1. **Regular Testing**: Test restoring from backups regularly (at least quarterly)
2. **Multiple Copies**: Keep backups in multiple locations (local + cloud)
3. **Monitor Backups**: Set up monitoring to alert if backups fail
4. **Document Recovery**: Document your recovery procedures and keep them updated
5. **Version Control**: Keep migration files in version control (Git)
6. **Review Changes**: Review all migration scripts before applying to production
7. **Backup Before Migrations**: Always create a manual backup before running new migrations in production

## Additional Resources

- [Flyway Documentation](https://flywaydb.org/documentation/)
- [PostgreSQL Backup Documentation](https://www.postgresql.org/docs/current/backup.html)
- [Docker Volumes](https://docs.docker.com/storage/volumes/)
