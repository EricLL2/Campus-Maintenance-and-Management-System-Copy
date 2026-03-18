-- Group 18 database schema
-- BuildingActivities project SQL schema for Oracle SQL*Plus
-- Creates all tables based on the ERD.

-- ============================================
-- 1. BUILDING
-- ============================================
CREATE TABLE BUILDING (
    Building_ID NUMBER(10) GENERATED ALWAYS AS IDENTITY,
    Building_Name VARCHAR2(30) NOT NULL,
    PRIMARY KEY (Building_ID)
);

-- ============================================
-- 2. AREA
-- Each area is a room on a floor in a building.
-- Closest_Core is unique and used to link to ACTIVITY.
-- ============================================
CREATE TABLE AREA (
    Building_ID NUMBER(10) NOT NULL,
    Floor NUMBER(10) NOT NULL,
    Room_No NUMBER(10) NOT NULL,
    Closest_Core VARCHAR2(15) NOT NULL,
    PRIMARY KEY (Building_ID, Floor, Room_No),
    UNIQUE (Closest_Core),
    CONSTRAINT fk_area_building FOREIGN KEY (Building_ID) REFERENCES BUILDING (Building_ID)
);

-- ============================================
-- 3. EMPLOYEE
-- Self-referencing supervisor via SuperID.
-- ============================================
CREATE TABLE EMPLOYEE (
    Employee_ID NUMBER(10) GENERATED ALWAYS AS IDENTITY,
    First_Name VARCHAR2(15) NOT NULL,
    Middle_Name VARCHAR2(15),
    Last_Name VARCHAR2(15) NOT NULL,
    Rank VARCHAR2(15) NOT NULL,
    Super_ID NUMBER(10),
    PRIMARY KEY (Employee_ID),
    CONSTRAINT fk_employee_supervisor FOREIGN KEY (Super_ID) REFERENCES EMPLOYEE (Employee_ID)
);

-- ============================================
-- 4. CONTRACTOR
-- Contact (phone/email) is the primary key.
-- ============================================
CREATE TABLE CONTRACTOR (
    Contact VARCHAR2(30) NOT NULL,
    Contractor_Name VARCHAR2(25) NOT NULL,
    PRIMARY KEY (Contact)
);

-- ============================================
-- 5. EQUIPMENT
-- ============================================
CREATE TABLE EQUIPMENT (
    Equipment_ID NUMBER(10) GENERATED ALWAYS AS IDENTITY,
    Equipment_Name VARCHAR2(20) NOT NULL,
    Contain_harmful_chemicals CHAR(25) NOT NULL CHECK (Contain_harmful_chemicals IN ('Y','N')),
    PRIMARY KEY (Equipment_ID)
);

-- ============================================
-- 6. ACTIVITY
-- Linked to manager (EMPLOYEE), building (BUILDING),
-- and area via Closest_Core (AREA).
-- ============================================
CREATE TABLE ACTIVITY (
    Activity_No NUMBER(10) GENERATED ALWAYS AS IDENTITY,
    Type VARCHAR2(20) NOT NULL,
    Start_Time TIMESTAMP NOT NULL,
    End_Time TIMESTAMP NOT NULL,
    Manager_ID NUMBER(10) NOT NULL,
    Building_ID NUMBER(10) NOT NULL,
    Closest_Core VARCHAR2(15) NOT NULL,
    PRIMARY KEY (Activity_No),
    CONSTRAINT fk_activity_manager FOREIGN KEY (Manager_ID) REFERENCES EMPLOYEE (Employee_ID),
    CONSTRAINT fk_activity_building FOREIGN KEY (Building_ID) REFERENCES BUILDING (Building_ID),
    CONSTRAINT fk_activity_area_core FOREIGN KEY (Closest_Core) REFERENCES AREA (Closest_Core)
);

-- ============================================
-- 7. WORKS_ON
-- Many-to-many between EMPLOYEE and ACTIVITY.
-- ============================================
CREATE TABLE WORKS_ON (
    Activity_No NUMBER(10) NOT NULL,
    Employee_ID NUMBER(10) NOT NULL,
    PRIMARY KEY (Activity_No, Employee_ID),
    CONSTRAINT fk_workson_activity FOREIGN KEY (Activity_No) REFERENCES ACTIVITY (Activity_No),
    CONSTRAINT fk_workson_employee FOREIGN KEY (Employee_ID) REFERENCES EMPLOYEE (Employee_ID)
);

-- ============================================
-- 8. CONTRACTS
-- Many-to-many between ACTIVITY and CONTRACTOR.
-- ============================================
CREATE TABLE CONTRACTS (
    Activity_No NUMBER(10) NOT NULL,
    Contact VARCHAR2(30) NOT NULL,
    PRIMARY KEY (Activity_No, Contact),
    CONSTRAINT fk_contracts_activity FOREIGN KEY (Activity_No) REFERENCES ACTIVITY (Activity_No),
    CONSTRAINT fk_contracts_contractor FOREIGN KEY (Contact) REFERENCES CONTRACTOR (Contact)
);

-- ============================================
-- 9. USES
-- Many-to-many between ACTIVITY and EQUIPMENT.
-- ============================================
CREATE TABLE USES (
    Activity_No NUMBER(10) NOT NULL,
    Equipment_ID NUMBER(10) NOT NULL,
    PRIMARY KEY (Activity_No, Equipment_ID),
    CONSTRAINT fk_uses_activity FOREIGN KEY (Activity_No) REFERENCES ACTIVITY (Activity_No),
    CONSTRAINT fk_uses_equipment FOREIGN KEY (Equipment_ID) REFERENCES EQUIPMENT (Equipment_ID)
);
