package com.nexora.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Configuration class for scheduling PostgreSQL database backups.
 * Creates backup dump files at specified intervals using pg_dump utility.
 * Note: Backups are scheduled to run at 1 AM daily.
 */
@Slf4j
@Configuration
@EnableScheduling
public class DatabaseBackupConfig {

    @Value("${spring.datasource.write.url}")
    private String dbUrl;

    @Value("${spring.datasource.write.username}")
    private String dbUsername;

    @Value("${spring.datasource.write.password}")
    private String dbPassword;

    @Value("${backup.directory:./resources}")
    private String backupDirectory;

    @Value("${backup.retention-days:7}")
    private int retentionDays;

    /**
     * Scheduled task to create database backup at 1 AM daily.
     * Creates a dump file with timestamp in the specified backup directory.
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void scheduleDatabaseBackup() {
        log.info("Starting database backup scheduling...");
        try {
            createBackupDirectoryIfNeeded();
            String backupFile = createBackupFile();
            if (backupFile != null) {
                cleanupOldBackups();
            }
        } catch (IOException e) {
            log.error("IO error during database backup: {}", e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Database backup was interrupted: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during database backup: {}", e.getMessage(), e);
        }
    }

    /**
     * Creates the backup directory if it doesn't exist.
     *
     * @throws IOException if directory creation fails
     */
    private void createBackupDirectoryIfNeeded() throws IOException {
        File directory = new File(backupDirectory);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Failed to create backup directory: " + backupDirectory);
        }
    }

    /**
     * Creates a database backup file using pg_dump.
     *
     * @return the path to the created backup file, or null if backup failed
     * @throws IOException if there's an error during backup creation
     * @throws InterruptedException if the process is interrupted
     */
    private String createBackupFile() throws IOException, InterruptedException {
        String dbName = extractDbNameFromUrl();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
        String backupFile = String.format("%s/megacity_backup_%s.sql", backupDirectory, timestamp);

        log.debug("Attempting to create backup file: {}", backupFile);

        ProcessBuilder pb = new ProcessBuilder("pg_dump",
                "--host", getHostFromUrl(),
                "--port", getPortFromUrl(),
                "--username", dbUsername,
                "--format", "plain",
                "--verbose",
                "--file", backupFile,
                dbName);

        pb.environment().put("PGPASSWORD", dbPassword);
        pb.redirectErrorStream(true);

        Process process = pb.start();
        try {
            int exitCode = process.waitFor();
            String output = new String(process.getInputStream().readAllBytes());

            if (exitCode == 0) {
                log.info("Database backup completed successfully: {}", backupFile);
                return backupFile;
            } else {
                log.error("Database backup failed with exit code: {}. Output: {}", exitCode, output);
                return null;
            }
        } finally {
            if (process.isAlive()) {
                process.destroy();
            }
        }
    }

    /**
     * Extracts database name from the JDBC URL.
     *
     * @return the database name
     */
    private String extractDbNameFromUrl() {
        return dbUrl.substring(dbUrl.lastIndexOf("/") + 1);
    }

    /**
     * Removes backup files older than the retention period.
     */
    private void cleanupOldBackups() {
        try {
            File directory = new File(backupDirectory);
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".sql"));

            if (files != null) {
                long cutoff = System.currentTimeMillis() - (retentionDays * 24 * 60 * 60 * 1000L);

                for (File file : files) {
                    if (file.lastModified() < cutoff) {
                        deleteOldBackupFile(file);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error during backup cleanup: {}", e.getMessage(), e);
        }
    }

    /**
     * Deletes a single backup file.
     *
     * @param file the file to delete
     */
    private void deleteOldBackupFile(File file) {
        Path filePath = file.toPath();
        try {
            Files.delete(filePath);
            log.info("Deleted old backup: {}", file.getName());
        } catch (IOException e) {
            log.warn("Failed to delete old backup {}: {}", file.getName(), e.getMessage());
        }
    }

    /**
     * Extracts host from database URL.
     *
     * @return host name from the JDBC URL
     */
    private String getHostFromUrl() {
        String withoutPrefix = dbUrl.substring("jdbc:postgresql://".length());
        return withoutPrefix.substring(0, withoutPrefix.indexOf(":"));
    }

    /**
     * Extracts port from database URL.
     *
     * @return port number from the JDBC URL
     */
    private String getPortFromUrl() {
        String withoutPrefix = dbUrl.substring("jdbc:postgresql://".length());
        String afterHost = withoutPrefix.substring(withoutPrefix.indexOf(":") + 1);
        return afterHost.substring(0, afterHost.indexOf("/"));
    }
}