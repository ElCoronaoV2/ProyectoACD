package com.restaurant.tec.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConstraintFix implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseConstraintFix(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            System.out.println("🔧 Attempting to fix database constraints...");

            // Drop exisiting check constraint if it exists
            try {
                jdbcTemplate.execute("ALTER TABLE reservas DROP CONSTRAINT IF EXISTS reservas_estado_check");
            } catch (Exception e) {
                System.out.println("Constraint might not exist or verify name: " + e.getMessage());
            }

            // Create new constraint with updated values
            String sql = "ALTER TABLE reservas ADD CONSTRAINT reservas_estado_check " +
                    "CHECK (estado IN ('PENDIENTE', 'CONFIRMADA', 'CANCELADA', 'COMPLETADA', 'ASISTIDO', 'NO_ASISTIDO'))";

            jdbcTemplate.execute(sql);
            System.out.println("✅ Database constraints updated successfully.");

        } catch (Exception e) {
            System.err.println("❌ Error updating database constraints: " + e.getMessage());
        }
    }
}
