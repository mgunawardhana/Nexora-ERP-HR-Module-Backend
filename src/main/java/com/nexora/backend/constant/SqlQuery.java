package com.nexora.backend.constant;

public class SqlQuery {

    private SqlQuery() {
    }
    /**
     * This holds all the select queries
     */
    public static class SelectQuery {

        public static final String FIND_ID_BY_EMAIL = "SELECT id FROM users WHERE email = ?";

        private SelectQuery() {
        }
    }

    /**
     * This holds all the insert queries
     */
    public static class InsertQuery {


        public static final String INSERT_TOKEN = "INSERT INTO token (token, token_type, revoked, expired, user_id)\n" + "VALUES (?, ?, ?, ?, ?);\n";

        public static final String INSERT_ARTICLE = """
                INSERT INTO _article ( discount, title, description, author, media, is_active ) VALUES (?, ?, ?, ?, ?, ?);""";

        public static final String ADD_NEW_GUIDELINE = """
                INSERT INTO guideline (title, description, category, priority, related_to) VALUES (?, ?, ?, ?, ?)""";

        public static final String ADD_NEW_VEHICLE = """
                    INSERT INTO vehicle (
                        registration_number, vehicle_image, make, model, year_of_manufacture, 
                        color, fuel_type, engine_capacity, chassis_number, vehicle_type, 
                        owner_name, owner_contact, owner_address, insurance_provider, 
                        insurance_policy_number, insurance_expiry_date, seating_capacity, 
                        license_plate_number, permit_type, air_conditioning, additional_features, status
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;

        public static final String ADD_NEW_DRIVER = """
                    INSERT INTO driver (root_user_id, driver_nic, phone_number, 
                                        license_number, license_expiry_date, driver_address, 
                                        vehicle_assigned, driver_status, emergency_contact, 
                                        date_of_birth, date_of_joining, license_image_front, license_image_back) 
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;


        public static final String ADD_NEW_CUSTOMER = """
                INSERT INTO customer (root_user_id, address, nic, phone_number) VALUES (?, ?, ?, ?)""";

        public static final String ADD_NEW_MANAGER = """
                INSERT INTO manager (root_user_id, address, nic, phone_number) VALUES (?, ?, ?, ?)""";

        public static final String ADD_NEW_BOOKING = """
                INSERT INTO booking (booking_date, pickup_location, drop_off_location, 
                car_number, taxes, distance, estimatedTime, tax_without_cost, total_amount, 
                customer_registration_number, driver_id, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""";

        public static final String UPDATE_VEHICLE_STATUS = "UPDATE vehicle SET status = 'UNAVAILABLE' WHERE id = CAST(? AS INTEGER)";

        public static final String VALIDATE_BOOKING = """
                SELECT COUNT(*)
                FROM booking
                WHERE car_number = ?
                AND ? < (booking_date + (estimatedTime * INTERVAL '1 minute'))
                """;

        private InsertQuery() {
        }
    }

    /**
     * This holds all the update queries
     */
    public static class UpdateQuery {

        public static final String UPDATE_DRIVER_AVAILABILITY = "UPDATE driver SET driver_status = ? WHERE root_user_id = ?";

        public static final String UPDATE_BOOKING_STATUS_FROM_DRIVER_SIDE = "WITH updated_booking AS (UPDATE booking SET status = ?, updated_date = CURRENT_TIMESTAMP WHERE booking_number = ? RETURNING booking_number, status, updated_date, driver_id), updated_driver AS ( UPDATE driver SET driver_status = CASE WHEN (SELECT status FROM updated_booking) = 'ACCEPTED' THEN 'BUSY' ELSE 'AVAILABLE' END WHERE driver_registration_number = (SELECT CAST(driver_id AS INTEGER) FROM updated_booking) RETURNING driver_registration_number) SELECT booking_number, status, updated_date, driver_id FROM updated_booking";

        public static final String REVOKE_ALL_USER_TOKENS = "UPDATE token SET revoked = ?, expired = ? WHERE user_id = ? AND (revoked = false OR expired = false)";

        public static final String UPDATE_ARTICLE = """
                UPDATE _article SET discount = ?, title = ?, description = ?, author = ?, media = ?, is_active = ? WHERE article_id = ?""";

        public static final String UPDATE_GUIDELINE = """
                UPDATE guideline SET title = ?, description = ?, category = ?, priority = ?, related_to = ? WHERE guidance_id = ?;""";

        public static final String UPDATE_VEHICLE = """
                    UPDATE vehicle SET 
                        registration_number = ?,
                        make = ?,
                        model = ?,
                        year_of_manufacture = ?,
                        color = ?,
                        fuel_type = ?,
                        engine_capacity = ?,
                        chassis_number = ?,
                        vehicle_type = ?,
                        owner_name = ?,
                        owner_contact = ?,
                        owner_address = ?,
                        insurance_provider = ?,
                        insurance_policy_number = ?,
                        insurance_expiry_date = ?,
                        seating_capacity = ?,
                        license_plate_number = ?,
                        permit_type = ?,
                        air_conditioning = ?,
                        vehicle_image = ?,
                        additional_features = ?,
                        status = ?
                    WHERE id = ?
                """;


        public static final String UPDATE_DRIVER = """
                UPDATE driver SET driver_first_name = ?, driver_last_name = ?, driver_nic = ?, phone_number = ?, email_address = ?, license_number = ?, license_expiry_date = ?, driver_address = ?, vehicle_assigned = ?, driver_status = ?, emergency_contact = ?, date_of_birth = ?, date_of_joining = ?, license_images = ? WHERE driver_registration_number = ?""";

        public static final String UPDATE_CUSTOMER = """
                UPDATE customer SET root_user_id = ?, address = ?, nic = ?, phone_number = ? WHERE registration_number = ?""";

        public static final String UPDATE_MANAGER = """
                UPDATE manager SET root_user_id = ?, address = ?, nic = ?, phone_number = ? WHERE registration_number = ?;
                """;

        public static final String UPDATE_BOOKING = """
                    UPDATE booking 
                    SET booking_date = ?, 
                        pickup_location = ?, 
                        drop_off_location = ?, 
                        car_number = ?, 
                        taxes = ?, 
                        distance = ?, 
                        estimatedTime = ?, 
                        tax_without_cost = ?, 
                        total_amount = ?, 
                        customer_registration_number = ?, 
                        driver_id = ?, 
                        status = ?, 
                        updated_date = ? 
                    WHERE booking_number = ?
                """;

        private UpdateQuery() {
        }
    }

    /**
     * This holds all  delete queries
     */
    public static class DeleteQuery {

        public static final String DELETE_ARTICLE = """
                DELETE FROM _article WHERE article_id = ?""";

        public static final String DELETE_GUIDELINE = """
                DELETE FROM guideline WHERE guidance_id = ?""";

        public static final String DELETE_VEHICLE = """
                DELETE FROM vehicle WHERE id = ?""";

        public static final String DELETE_DRIVER_BY_NIC = """
                DELETE FROM driver WHERE driver_nic = ?""";

        public static final String DELETE_CUSTOMER_BY_ID = """
                DELETE FROM customer WHERE registration_number = ?""";

        public static final String DELETE_MANAGER = """
                DELETE FROM manager WHERE registration_number = ?""";

        public static final String DELETE_BOOKING_BY_ID = """
                DELETE FROM booking WHERE booking_number = ?""";

        private DeleteQuery() {
        }
    }
}
