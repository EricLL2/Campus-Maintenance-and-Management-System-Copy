-- Group 18 - Sample Data

-- By default (as written in the Report), this sample data has been assigned to the database already.
-- However, if you want to use your own Oracle account by using the config.properties file, 
--      you need run this SQL script to populate the tables with this sample data.

-- Activities date range: Last month to Next month
-- UPDATED: No explicit IDs - letting Oracle generate them automatically

-- ============================================
-- 1. BUILDING Data
-- ============================================
INSERT INTO BUILDING (Building_Name) VALUES ('Main Tower');
INSERT INTO BUILDING (Building_Name) VALUES ('Science Center');
INSERT INTO BUILDING (Building_Name) VALUES ('Engineering Hall');
INSERT INTO BUILDING (Building_Name) VALUES ('Library');
INSERT INTO BUILDING (Building_Name) VALUES ('Student Center');

-- ============================================
-- 2. AREA Data
-- ============================================
-- Building 1 - Main Tower
INSERT INTO AREA (Building_ID, Floor, Room_No, Closest_Core) VALUES (1, 1, 101, 'CORE-A1');
INSERT INTO AREA (Building_ID, Floor, Room_No, Closest_Core) VALUES (1, 1, 102, 'CORE-A2');
INSERT INTO AREA (Building_ID, Floor, Room_No, Closest_Core) VALUES (1, 2, 201, 'CORE-B1');
INSERT INTO AREA (Building_ID, Floor, Room_No, Closest_Core) VALUES (1, 2, 202, 'CORE-B2');
INSERT INTO AREA (Building_ID, Floor, Room_No, Closest_Core) VALUES (1, 3, 301, 'CORE-C1');

-- Building 2 - Science Center
INSERT INTO AREA (Building_ID, Floor, Room_No, Closest_Core) VALUES (2, 1, 110, 'CORE-S1');
INSERT INTO AREA (Building_ID, Floor, Room_No, Closest_Core) VALUES (2, 1, 115, 'CORE-S2');
INSERT INTO AREA (Building_ID, Floor, Room_No, Closest_Core) VALUES (2, 2, 210, 'CORE-T1');
INSERT INTO AREA (Building_ID, Floor, Room_No, Closest_Core) VALUES (2, 2, 215, 'CORE-T2');

-- Building 3 - Engineering Hall
INSERT INTO AREA (Building_ID, Floor, Room_No, Closest_Core) VALUES (3, 1, 101, 'CORE-E1');
INSERT INTO AREA (Building_ID, Floor, Room_No, Closest_Core) VALUES (3, 1, 105, 'CORE-E2');
INSERT INTO AREA (Building_ID, Floor, Room_No, Closest_Core) VALUES (3, 2, 201, 'CORE-F1');

-- ============================================
-- 3. EMPLOYEE Data
-- ============================================
-- Top-level managers (no supervisor)
INSERT INTO EMPLOYEE (First_Name, Middle_Name, Last_Name, Rank, Super_ID) 
VALUES ('Robert', 'James', 'Smith', 'Manager', NULL);

INSERT INTO EMPLOYEE (First_Name, Middle_Name, Last_Name, Rank, Super_ID) 
VALUES ('Maria', 'Lynn', 'Johnson', 'Manager', NULL);

-- Middle managers (report to top managers)
-- Note: Super_ID values will be filled after we know the auto-generated IDs
INSERT INTO EMPLOYEE (First_Name, Middle_Name, Last_Name, Rank, Super_ID) 
VALUES ('David', 'Michael', 'Brown', 'Manager', 1);

INSERT INTO EMPLOYEE (First_Name, Middle_Name, Last_Name, Rank, Super_ID) 
VALUES ('Sarah', 'Anne', 'Davis', 'Manager', 2);

INSERT INTO EMPLOYEE (First_Name, Middle_Name, Last_Name, Rank, Super_ID) 
VALUES ('James', 'William', 'Wilson', 'Manager', 1);

-- Regular employees
INSERT INTO EMPLOYEE (First_Name, Middle_Name, Last_Name, Rank, Super_ID) 
VALUES ('Jennifer', 'Marie', 'Taylor', 'Worker', 3);

INSERT INTO EMPLOYEE (First_Name, Middle_Name, Last_Name, Rank, Super_ID) 
VALUES ('Thomas', 'Edward', 'Anderson', 'Worker', 4);

INSERT INTO EMPLOYEE (First_Name, Middle_Name, Last_Name, Rank, Super_ID) 
VALUES ('Lisa', 'Grace', 'Martinez', 'Worker', 5);

INSERT INTO EMPLOYEE (First_Name, Middle_Name, Last_Name, Rank, Super_ID) 
VALUES ('Kevin', 'John', 'Garcia', 'Worker', 3);

INSERT INTO EMPLOYEE (First_Name, Middle_Name, Last_Name, Rank, Super_ID) 
VALUES ('Amanda', 'Rose', 'Lee', 'Worker', 4);

-- ============================================
-- 4. CONTRACTOR Data
-- ============================================
INSERT INTO CONTRACTOR (Contact, Contractor_Name) VALUES ('555-0101', 'Elite Construction');
INSERT INTO CONTRACTOR (Contact, Contractor_Name) VALUES ('555-0102', 'Tech Solutions Inc');
INSERT INTO CONTRACTOR (Contact, Contractor_Name) VALUES ('555-0103', 'SafeClean Services');
INSERT INTO CONTRACTOR (Contact, Contractor_Name) VALUES ('info@premierelectric.com', 'Premier Electric');
INSERT INTO CONTRACTOR (Contact, Contractor_Name) VALUES ('contact@modernplumbing.net', 'Modern Plumbing Co');

-- ============================================
-- 5. EQUIPMENT Data
-- ============================================
INSERT INTO EQUIPMENT (Equipment_Name, Contain_harmful_chemicals) VALUES ('Power Drill', 'N');
INSERT INTO EQUIPMENT (Equipment_Name, Contain_harmful_chemicals) VALUES ('Paint Sprayer', 'Y');
INSERT INTO EQUIPMENT (Equipment_Name, Contain_harmful_chemicals) VALUES ('Welding Machine', 'N');
INSERT INTO EQUIPMENT (Equipment_Name, Contain_harmful_chemicals) VALUES ('Cleaning Solution', 'Y');
INSERT INTO EQUIPMENT (Equipment_Name, Contain_harmful_chemicals) VALUES ('HVAC Tester', 'N');
INSERT INTO EQUIPMENT (Equipment_Name, Contain_harmful_chemicals) VALUES ('Safety Harness', 'N');
INSERT INTO EQUIPMENT (Equipment_Name, Contain_harmful_chemicals) VALUES ('Chemical Cleaner', 'Y');
INSERT INTO EQUIPMENT (Equipment_Name, Contain_harmful_chemicals) VALUES ('Electrical Tester', 'N');

-- ============================================
-- 6. ACTIVITY Data
-- ============================================
INSERT INTO ACTIVITY (Type, Start_Time, End_Time, Manager_ID, Building_ID, Closest_Core) 
VALUES ('Renovation', 
        CURRENT_TIMESTAMP - INTERVAL '30' DAY - INTERVAL '2' HOUR, 
        CURRENT_TIMESTAMP - INTERVAL '30' DAY + INTERVAL '6' HOUR, 
        3, 1, 'CORE-A1');

INSERT INTO ACTIVITY (Type, Start_Time, End_Time, Manager_ID, Building_ID, Closest_Core) 
VALUES ('Cleaning', 
        CURRENT_TIMESTAMP - INTERVAL '15' DAY - INTERVAL '1' HOUR, 
        CURRENT_TIMESTAMP - INTERVAL '15' DAY + INTERVAL '4' HOUR, 
        4, 2, 'CORE-S1');

INSERT INTO ACTIVITY (Type, Start_Time, End_Time, Manager_ID, Building_ID, Closest_Core) 
VALUES ('Electrical Work', 
        CURRENT_TIMESTAMP - INTERVAL '2' DAY, 
        CURRENT_TIMESTAMP + INTERVAL '2' DAY, 
        5, 3, 'CORE-E1');

INSERT INTO ACTIVITY (Type, Start_Time, End_Time, Manager_ID, Building_ID, Closest_Core) 
VALUES ('Painting', 
        CURRENT_TIMESTAMP - INTERVAL '1' DAY, 
        CURRENT_TIMESTAMP + INTERVAL '3' DAY, 
        3, 1, 'CORE-B1');

INSERT INTO ACTIVITY (Type, Start_Time, End_Time, Manager_ID, Building_ID, Closest_Core) 
VALUES ('HVAC Maintenance', 
        CURRENT_TIMESTAMP + INTERVAL '15' DAY, 
        CURRENT_TIMESTAMP + INTERVAL '17' DAY, 
        4, 2, 'CORE-T1');

INSERT INTO ACTIVITY (Type, Start_Time, End_Time, Manager_ID, Building_ID, Closest_Core) 
VALUES ('Plumbing Repair', 
        CURRENT_TIMESTAMP + INTERVAL '25' DAY, 
        CURRENT_TIMESTAMP + INTERVAL '26' DAY, 
        5, 3, 'CORE-E2');

INSERT INTO ACTIVITY (Type, Start_Time, End_Time, Manager_ID, Building_ID, Closest_Core) 
VALUES ('Safety Inspection', 
        CURRENT_TIMESTAMP + INTERVAL '20' DAY, 
        CURRENT_TIMESTAMP + INTERVAL '20' DAY + INTERVAL '5' HOUR, 
        3, 1, 'CORE-C1');

INSERT INTO ACTIVITY (Type, Start_Time, End_Time, Manager_ID, Building_ID, Closest_Core) 
VALUES ('Book Installation', 
        CURRENT_TIMESTAMP - INTERVAL '5' DAY, 
        CURRENT_TIMESTAMP + INTERVAL '2' DAY, 
        3, 4, 'CORE-A1');

INSERT INTO ACTIVITY (Type, Start_Time, End_Time, Manager_ID, Building_ID, Closest_Core) 
VALUES ('Carpet Cleaning', 
        CURRENT_TIMESTAMP + INTERVAL '10' DAY, 
        CURRENT_TIMESTAMP + INTERVAL '11' DAY, 
        4, 5, 'CORE-S1');

INSERT INTO ACTIVITY (Type, Start_Time, End_Time, Manager_ID, Building_ID, Closest_Core) 
VALUES ('Lighting Upgrade', 
        CURRENT_TIMESTAMP + INTERVAL '12' DAY, 
        CURRENT_TIMESTAMP + INTERVAL '14' DAY, 
        5, 4, 'CORE-B1');

INSERT INTO ACTIVITY (Type, Start_Time, End_Time, Manager_ID, Building_ID, Closest_Core) 
VALUES ('Furniture Assembly', 
        CURRENT_TIMESTAMP - INTERVAL '3' DAY, 
        CURRENT_TIMESTAMP + INTERVAL '1' DAY, 
        3, 5, 'CORE-C1');

INSERT INTO ACTIVITY (Type, Start_Time, End_Time, Manager_ID, Building_ID, Closest_Core) 
VALUES ('Window Cleaning', 
        CURRENT_TIMESTAMP + INTERVAL '8' DAY, 
        CURRENT_TIMESTAMP + INTERVAL '8' DAY + INTERVAL '6' HOUR, 
        4, 1, 'CORE-A2');

INSERT INTO ACTIVITY (Type, Start_Time, End_Time, Manager_ID, Building_ID, Closest_Core) 
VALUES ('Lab Maintenance', 
        CURRENT_TIMESTAMP + INTERVAL '18' DAY, 
        CURRENT_TIMESTAMP + INTERVAL '19' DAY, 
        5, 2, 'CORE-T2');

-- ============================================
-- 7. WORKS_ON Data
-- ============================================
INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (1, 6);
INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (1, 9);
INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (1, 8);

INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (2, 7);
INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (2, 10);

INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (3, 6);
INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (3, 8);
INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (3, 10);

INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (4, 7);
INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (4, 9);

INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (5, 6);
INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (5, 8);

INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (6, 7);
INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (6, 9);
INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (6, 10);

INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (7, 6);
INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (7, 7);

INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (8, 6);
INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (8, 7);
INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (8, 9);

INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (9, 8);
INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (9, 10);

INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (10, 6);
INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (10, 10);

INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (11, 7);
INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (11, 8);
INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (11, 9);

INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (12, 6);
INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (12, 10);

INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (13, 7);
INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (13, 8);
INSERT INTO WORKS_ON (Activity_No, Employee_ID) VALUES (13, 9);

-- ============================================
-- 8. CONTRACTS Data
-- ============================================
INSERT INTO CONTRACTS (Activity_No, Contact) VALUES (1, '555-0101');
INSERT INTO CONTRACTS (Activity_No, Contact) VALUES (1, 'info@premierelectric.com');
INSERT INTO CONTRACTS (Activity_No, Contact) VALUES (2, '555-0103');
INSERT INTO CONTRACTS (Activity_No, Contact) VALUES (3, 'info@premierelectric.com');
INSERT INTO CONTRACTS (Activity_No, Contact) VALUES (4, '555-0101');
INSERT INTO CONTRACTS (Activity_No, Contact) VALUES (5, '555-0102');
INSERT INTO CONTRACTS (Activity_No, Contact) VALUES (6, 'contact@modernplumbing.net');
INSERT INTO CONTRACTS (Activity_No, Contact) VALUES (7, '555-0102');
INSERT INTO CONTRACTS (Activity_No, Contact) VALUES (8, '555-0101');
INSERT INTO CONTRACTS (Activity_No, Contact) VALUES (9, '555-0103');
INSERT INTO CONTRACTS (Activity_No, Contact) VALUES (10, 'info@premierelectric.com');
INSERT INTO CONTRACTS (Activity_No, Contact) VALUES (11, '555-0101');
INSERT INTO CONTRACTS (Activity_No, Contact) VALUES (12, '555-0103');
INSERT INTO CONTRACTS (Activity_No, Contact) VALUES (13, '555-0102');

-- ============================================
-- 9. USES Data
-- ============================================
INSERT INTO USES (Activity_No, Equipment_ID) VALUES (1, 1);
INSERT INTO USES (Activity_No, Equipment_ID) VALUES (1, 3);
INSERT INTO USES (Activity_No, Equipment_ID) VALUES (2, 4);
INSERT INTO USES (Activity_No, Equipment_ID) VALUES (2, 7);
INSERT INTO USES (Activity_No, Equipment_ID) VALUES (3, 8);
INSERT INTO USES (Activity_No, Equipment_ID) VALUES (4, 2);
INSERT INTO USES (Activity_No, Equipment_ID) VALUES (5, 5);
INSERT INTO USES (Activity_No, Equipment_ID) VALUES (6, 1);
INSERT INTO USES (Activity_No, Equipment_ID) VALUES (7, 6);
INSERT INTO USES (Activity_No, Equipment_ID) VALUES (7, 8);
INSERT INTO USES (Activity_No, Equipment_ID) VALUES (8, 1);
INSERT INTO USES (Activity_No, Equipment_ID) VALUES (8, 6);
INSERT INTO USES (Activity_No, Equipment_ID) VALUES (9, 4);
INSERT INTO USES (Activity_No, Equipment_ID) VALUES (10, 8);
INSERT INTO USES (Activity_No, Equipment_ID) VALUES (11, 1);
INSERT INTO USES (Activity_No, Equipment_ID) VALUES (12, 4);
INSERT INTO USES (Activity_No, Equipment_ID) VALUES (13, 5);

COMMIT;