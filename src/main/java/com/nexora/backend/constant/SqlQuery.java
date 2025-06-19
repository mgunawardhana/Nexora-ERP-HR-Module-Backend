package com.nexora.backend.constant;

public class SqlQuery {

    private SqlQuery() {
    }
    /**
     * This holds all the select queries
     */
    public static class SelectQuery {

        public static final String FIND_ID_BY_EMAIL = "SELECT id FROM users WHERE email = ?";

        public static final String SELECT_ARTICLES = """
                SELECT article_id, discount, title, description, author, media, is_active, created_at, updated_at FROM _article LIMIT ? OFFSET ?""";

        public static final String SELECT_ARTICLE_BY_ID = """
                SELECT article_id, discount, title, description, author, media, is_active, created_at, updated_at FROM _article WHERE article_id = ?""";

        public static final String FETCH_ALL_GUIDELINE = """
                SELECT * FROM guideline LIMIT ? OFFSET ?;""";

        public static final String FETCH_GUIDELINE_BY_ID = """
                SELECT * FROM guideline WHERE guidance_id = ?""";

        public static final String FETCH_VEHICLE_BY_ID = """
                SELECT * FROM vehicle WHERE id = ?""";

        public static final String FETCH_ALL_VEHICLE = """
                SELECT * FROM vehicle LIMIT ? OFFSET ?;""";

        public static final String GET_DRIVER_BY_NIC = """
                SELECT * FROM driver WHERE driver_nic = ?;""";

        public static final String FETCH_ALL_DRIVERS = """
                SELECT 
                    d.driver_registration_number,
                    d.root_user_id,
                    d.driver_nic,
                    d.phone_number,
                    d.license_number,
                    d.license_expiry_date,
                    d.driver_address,
                    d.vehicle_assigned,
                    d.driver_status,
                    d.emergency_contact,
                    d.date_of_birth,
                    d.date_of_joining,
                    d.license_image_front,
                    d.license_image_back,
                    u.user_profile_pic
                FROM driver d
                LEFT JOIN users u ON d.root_user_id = u.id""";

        public static final String GET_ALL_CUSTOMERS = """
                SELECT * FROM customer""";

        public static final String GET_CUSTOMER_BY_ID = """
                SELECT * FROM customer WHERE registration_number = ?""";

        public static final String GET_CUSTOMER_BY_NIC = """
                SELECT * FROM customer WHERE nic = ?""";

        public static final String GET_MANAGER_BY_ID = """
                SELECT * FROM manager WHERE registration_number = ?""";

        public static final String GET_ALL_MANAGERS = """
                SELECT * FROM manager""";

        public static final String GET_ALL_BOOKINGS = """
                SELECT * FROM booking LIMIT ? OFFSET ?;""";

        public static final String GET_ALL_BOOKINGS_WITHOUT_PAGINATION = """
                    SELECT 
                    booking_number, 
                    booking_date, 
                    pickup_location, 
                    drop_off_location, 
                    car_number, 
                    taxes, 
                    distance, 
                    estimatedTime, 
                    tax_without_cost, 
                    total_amount, 
                    customer_registration_number, 
                    driver_id, 
                    status, 
                    created_date, 
                    updated_date 
                    FROM booking
                """;

        public static final String GET_TOTAL_REVENUE_BY_STATUS_ORDERED = """
                    SELECT order_status, 
                           SUM(taxes) AS total_taxes, 
                           SUM(tax_without_cost) AS total_tax_without_cost, 
                           SUM(total_amount) AS total_amount 
                    FROM booking
                    WHERE order_status IN ('CLOSED', 'CANCELLED', 'COMPLETED', 'PENDING')
                    GROUP BY order_status
                    ORDER BY CASE 
                                WHEN order_status = 'CLOSED' THEN 1
                                WHEN order_status = 'CANCELLED' THEN 2
                                WHEN order_status = 'COMPLETED' THEN 3
                                WHEN order_status = 'PENDING' THEN 4
                             END
                """;

        public static final String GET_TAX_DETAILS_BY_STATUS_WISE = """
                SELECT status, COUNT(*) AS row_count, SUM(taxes) AS total_taxes, SUM(tax_without_cost) AS total_tax_without_cost, SUM(total_amount) AS total_amount FROM booking GROUP BY status""";

        public static final String GET_BOOKING_BY_ID = """
                SELECT * FROM booking WHERE booking_number = ?""";

        public static final String FIND_BOOKING_BY_DRIVER_ID = """
                SELECT 
                    booking_number,
                    booking_date,
                    pickup_location,
                    drop_off_location,
                    distance,
                    estimatedTime,
                    total_amount,
                    customer_registration_number,
                    driver_id,
                    status
                FROM booking 
                WHERE driver_id = ?
                """;

        public static final String FIND_CUSTOMER_BY_ROOT_USER_ID = """
                SELECT registration_number, root_user_id, address, nic, phone_number FROM customer WHERE root_user_id = ?""";

        public static final String FIND_MANAGER_BY_ROOT_USER_ID = """
                SELECT registration_number, root_user_id, address, nic, phone_number FROM manager WHERE root_user_id = ?""";

        public static final String FIND_DRIVER_BY_ROOT_USER_ID = """
                SELECT driver_registration_number, root_user_id, driver_profile_picture, driver_nic, phone_number,
                       license_number, license_expiry_date, driver_address, vehicle_assigned, driver_status,
                       emergency_contact, date_of_birth, date_of_joining, license_image_front, license_image_back
                FROM driver
                WHERE root_user_id = ?
                """;


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
