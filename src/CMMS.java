import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import oracle.jdbc.driver.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

public class CMMS {

    public static void main(String[] args) throws IOException, InterruptedException {
        CMMS_System system = new CMMS_System();
        system.run();
    }
}

class CMMS_System {
    Scanner scan = new Scanner(System.in);
    String URL, username, password;
    boolean cannotRun = false;
    OracleConnection conn;
    Statement statement;
    ResultSet resultSet;
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private class CaptionAndHeader {
        private String[] specialTable = {
                "BUILDING BUILDING_ID",
                "EMPLOYEE EMPLOYEE_ID",
                "EQUIPMENT EQUIPMENT_ID",
                "ACTIVITY ACTIVITY_NO"
        };
        private String caption;
        private String[] header;

        public CaptionAndHeader(String caption, String[] header) {
            this.caption = caption;
            this.header = header;
        }

        public String getCaption() {
            return caption;
        }

        public String[] getHeader() {
            System.out.println("Full Header: " + Arrays.toString(header));
            String tableInfo = caption + " " + header[0];
            System.out.println("Table Info: " + tableInfo);
            String[] headerForInsert = new String[header.length - 1];
            for (String table : specialTable) {
                if (tableInfo.equals(table)) {
                    headerForInsert = Arrays.copyOfRange(header, 1, header.length);
                    return headerForInsert;
                }
            }
            return header;
        }
    }

    public CMMS_System() {
        initializeSystem();
    }

    private void loadConnectionDetails() {
        InputStream connectionDetail = getClass().getClassLoader()
                .getResourceAsStream("config.properties");
        if (connectionDetail == null) {
            System.out.println("Connection Error: Oracle JDBC Driver not found");
            cannotRun = true;
            return;
        }
        Properties props = new Properties();
        try {
            props.load(connectionDetail);
        } catch (IOException e) {
            System.out.println("IO Error: Cannot read connection details");
            cannotRun = true;
            return;
        }
        URL = props.getProperty("db.url");
        username = props.getProperty("db.username");
        password = props.getProperty("db.password");
    }

    private void initializeSystem() {
        try {
            loadConnectionDetails();
            if (cannotRun)
                return;
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            conn = (OracleConnection) DriverManager.getConnection(URL, username, password);
            statement = conn.createStatement();
        } catch (SQLException e) {
            System.out.println("Connection Error: Cannot connect to the server");
            System.out.println(e.getMessage());
            cannotRun = true;
        }
    }

    private String getInput(String prompt) {
        if (prompt.equals("Enter your chioce: "))
            showMenu();
        System.out.print(prompt);
        String input = scan.nextLine();
        return input.trim();
    }

    private String getLinesInput(String prompt) {
        System.out.print(prompt);
        String output = "", input = "";
        while (scan.hasNextLine()) {
            input = scan.nextLine();
            input = input.trim();
            output += input + " ";
            if (input.contains(";"))
                break;
        }
        int end = output.indexOf(";");
        output = output.substring(0, end);
        return output.trim();
    }

    public void run() {
        if (cannotRun)
            exit();
        System.out.println("run");
        String input = getInput("Enter your chioce: ");
        while (!input.equals("-1")) {
            switch (input) {
                case "0":
                    showScheduledActivities();
                    break;
                case "1":
                    showOngoingTasks();
                    break;
                case "2":
                    seeTable();
                    break;
                case "3":
                    insertTuple();
                    break;
                case "4":
                    setBasedInsertion();
                    break;
                case "5":
                    deleteTuple();
                    break;
                case "6":
                    runSQL();
                    break;
                case "7":
                    report();
                    break;
                default:
                    System.out.println("Wrong input");
                    break;
            }
            clearScreen();
            input = getInput("Enter your chioce: ");
        }
        exit();
    }

    private String[] getPrimaryKeys(String table) {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, table);
            String primaryKeysName = "";
            while (primaryKeys.next()) {
                primaryKeysName += primaryKeys.getString("COLUMN_NAME") + " ";
            }
            return primaryKeysName.split(" ");
        } catch (SQLException e) {
            System.out.println("SQL Error: Cannot retrieve primary keys from given table");
            return null;
        }
    }

    private void printUpdateInfo(int updateCount, String originalSql) {
        String sql = originalSql.trim().toUpperCase();
        if (sql.isEmpty()) {
            System.out.println("Command executed successfully");
            return;
        }
        String[][] commands = {
                { "INSERT", "inserted" },
                { "UPDATE", "updated" },
                { "DELETE", "deleted" },
                { "CREATE", "created" },
                { "ALTER", "altered" },
                { "DROP", "dropped" }
        };
        String firstWord = sql.split("\\s+")[0];
        for (String[] command : commands) {
            if (firstWord.equals(command[0])) {
                if (updateCount >= 0)
                    System.out.println(command[0] + " successful - " + updateCount + " row(s) " + command[1]);
                else
                    System.out.println(command[0] + " command executed successfully");
                return;
            }
        }
        if (updateCount >= 0)
            System.out.println("Command executed - " + updateCount + " row(s) affected");
        else
            System.out.println("Command executed successfully");
    }

    private void printData(ResultSet resultSet) throws SQLException {
        try {
            System.out.print("\n");
            ArrayList<String[]> resultStorage = new ArrayList<String[]>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            int[] spaceForEachColumn = new int[columnCount + 1];
            for (int i = 1; i <= columnCount; i++)
                spaceForEachColumn[i] = metaData.getColumnName(i).trim().length();
            while (resultSet.next()) {
                String[] rowData = new String[columnCount + 1];
                for (int i = 1; i <= columnCount; i++) {
                    String data = "";
                    if (resultSet.getObject(i) == null)
                        data = "NULL";
                    else {
                        data = resultSet.getString(i).trim();
                        if (data.contains("-") && data.contains(":") && data.contains("."))
                            data = data.substring(0, data.indexOf('.'));
                    }
                    rowData[i] = data;
                    spaceForEachColumn[i] = Math.max(spaceForEachColumn[i], data.length());
                }
                resultStorage.add(rowData);
            }
            for (int i = 1; i <= columnCount; i++)
                System.out.printf("%" + -spaceForEachColumn[i] + "s   ", metaData.getColumnName(i));
            System.out.print("\n");
            for (int i = 0; i < resultStorage.size(); i++) {
                for (int j = 1; j <= columnCount; j++)
                    System.out.printf("%" + -spaceForEachColumn[j] + "s   ", resultStorage.get(i)[j]);
                System.out.print("\n");
            }
            System.out.print("\n");
        } catch (SQLException e) {
            System.out.println("SQL Error: Cannot print result");
        }
    }

    private void showActivityTime() {
        System.out.println("Time for Ongoing Activities");
        try {
            String query = "SELECT MIN(START_TIME) as min_start, MAX(END_TIME) as max_end FROM ACTIVITY WHERE CURRENT_TIMESTAMP <= END_TIME";
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                Timestamp minStart = resultSet.getTimestamp("min_start");
                Timestamp maxEnd = resultSet.getTimestamp("max_end");

                if (minStart != null && maxEnd != null) {
                    String startTime = minStart.toLocalDateTime().format(TIMESTAMP_FORMATTER);
                    String endTime = maxEnd.toLocalDateTime().format(TIMESTAMP_FORMATTER);
                    System.out.println("Ongoing activities started from\n: " + startTime + " to " + endTime + "\n");
                } else {
                    System.out.println("No ongoing or future activities found");
                }
            }
        } catch (Exception e) {
            System.out.println("SQL Error: Cannot show activity time, " + e.getMessage());
        }
    }

    private void showTable(String table) {
        try {
            String query = "SELECT * FROM " + table;
            resultSet = statement.executeQuery(query);
            printData(resultSet);
        } catch (SQLException e) {
            System.out.println("SQL Error: Cannot show given table");
        }
    }

    private CaptionAndHeader getTableAndHeader() {
        String table = "";
        String[] tableHeader = null;
        try {
            while (tableHeader == null || table.isEmpty()) {
                System.out.println("Available Tables: " +
                        "\n BUILDING" +
                        "\n AREA" +
                        "\n EMPLOYEE" +
                        "\n CONTRACTOR" +
                        "\n EQUIPMENT" +
                        "\n ACTIVITY" +
                        "\n WORKS_ON" +
                        "\n CONTRACTS" +
                        "\n USES" + "\n");
                table = getInput("Enter table name or -1 to exit: ").toUpperCase();
                if (table.equals("-1"))
                    return null;
                resultSet = statement.executeQuery("SELECT * FROM " + table);
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                tableHeader = new String[columnCount];
                for (int i = 0; i < columnCount; i++)
                    tableHeader[i] = metaData.getColumnName(i + 1);
            }
            return new CaptionAndHeader(table, tableHeader);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("NULL Error: Table cannot be NULL");
        }
        return null;
    }

    private void seeTable() {
        String continueSee = "", table = "";
        while (!continueSee.equals("-1")) {
            clearScreen();
            CaptionAndHeader tableAndHeader = getTableAndHeader();
            if (tableAndHeader == null)
                return;
            table = tableAndHeader.getCaption();
            if (table == null)
                return;
            clearScreen();
            System.out.println(table);
            showTable(table);
            continueSee = getInput("Enter anything to continue or -1 to exit: ");
        }
    }

    private void showInsertionPrompt(boolean isSetBased) {
        System.out.println("How to insert data:");
        System.out.println("1. For TIME column, enter in the format of 'YYYY-MM-DD HH:MM:SS'");
        System.out.println("2. For ID/NO/FlOOR column, enter integer value");
        System.out.println("3. For other columns, enter string value");
        System.out.println("4. To insert NULL value, enter -");
        if (isSetBased) {
            System.out.println(
                    "5. For set-based insertion, enter multiple tuples in the format of (val1, val2, ...), (val1, val2, ...); or ");
            System.out.println("   (val1, val2, ...),\n   (val1, val2, ...);");
            System.out.println(
                    "   enclose each tuple with parentheses ,separate each tuple with a comma and a space, and end the input with a semicolon (;)");
            System.out.println(
                    "   Example: (2024-01-01 10:00:00, 1, Equipment A), (2024-01-02 11:00:00, -, Equipment B);");
        }
        System.out.print("\n");
    }

    private void insertTuple() {
        System.out.println("insertTuples");
        clearScreen();
        CaptionAndHeader tableAndHeader = getTableAndHeader();
        if (tableAndHeader == null)
            return;
        String table = tableAndHeader.getCaption();
        String[] tableHeader = tableAndHeader.getHeader();
        String[] valuesList = new String[tableHeader.length];
        Arrays.fill(valuesList, "?");
        String values = "(" + String.join(", ", valuesList) + ")";
        String insertQuery = "INSERT INTO " + table + " (" + String.join(", ", tableHeader) + ") VALUES "
                + values;
        System.out.println(insertQuery);
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);
            String continueInsert = "";
            while (!continueInsert.equals("-1")) {
                clearScreen();
                System.out.println(table);
                showTable(table);
                showInsertionPrompt(false);
                try {
                    for (int i = 0; i < tableHeader.length; i++) {
                        if (tableHeader[i].contains("TIME")) {
                            String input = getInput("Enter " + tableHeader[i] + ": ");
                            java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(input);
                            preparedStatement.setTimestamp(i + 1, timestamp);
                        } else if (tableHeader[i].contains("_ID") || tableHeader[i].contains("_NO")
                                || tableHeader[i].equals("Floor")) {
                            String input = getInput("Enter " + tableHeader[i] + ": ");
                            if (input.equals("-"))
                                preparedStatement.setNull(i + 1, Types.INTEGER);
                            else
                                preparedStatement.setInt(i + 1, Integer.parseInt(input));
                        } else {
                            String input = getInput("Enter " + tableHeader[i] + ": ");
                            if (input.equals("-"))
                                preparedStatement.setNull(i + 1, Types.VARCHAR);
                            else
                                preparedStatement.setString(i + 1, input);
                        }
                    }
                    int updateCount = preparedStatement.executeUpdate();
                    printUpdateInfo(updateCount, insertQuery);
                } catch (SQLException e) {
                    System.out.println("SQL Error: " + e.getMessage());
                } catch (NumberFormatException e) {
                    System.out.println("Number Format Error: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println("Time Format Error: " + e.getMessage());
                }
                continueInsert = getInput("Enter anything to continue or -1 to exit: ");
            }
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return;
        }
    }

    private void setBasedInsertion() {
        System.out.println("setBasedInsertion");
        clearScreen();
        CaptionAndHeader tableAndHeader = getTableAndHeader();
        if (tableAndHeader == null)
            return;
        String table = tableAndHeader.getCaption();
        String[] tableHeader = tableAndHeader.getHeader();
        String[] valuesList = new String[tableHeader.length];
        Arrays.fill(valuesList, "?");
        String values = "(" + String.join(", ", valuesList) + ")";
        String insertQuery = "INSERT INTO " + table + " (" + String.join(", ", tableHeader) + ") VALUES "
                + values;
        System.out.println(insertQuery);
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);
            conn.setAutoCommit(false);
            String continueInsert = "";
            while (!continueInsert.equals("-1")) {
                clearScreen();
                System.out.println(table);
                showTable(table);
                showInsertionPrompt(true);
                String userInput = getLinesInput("Enter your data as (" + String.join(", ", tableHeader) + ")\n: ");
                try {
                    userInput = userInput.replaceAll("\\), \\(", ")<tuple_space>(");
                    String[] userInputList = userInput.split("<tuple_space>");
                    for (String inputTuple : userInputList) {
                        String pureInput = inputTuple.replaceAll("[()]", "");
                        String[] dataList = pureInput.split(", ");
                        if (dataList.length != tableHeader.length) {
                            throw new SQLException("Number of values does not match number of columns");
                        }
                        for (int i = 0; i < tableHeader.length; i++) {
                            String input = dataList[i];
                            if (tableHeader[i].contains("TIME")) {
                                java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(input);
                                preparedStatement.setTimestamp(i + 1, timestamp);

                            } else if (tableHeader[i].contains("_ID") || tableHeader[i].contains("_NO")
                                    || tableHeader[i].equals("Floor")) {
                                if (input.equals("-"))
                                    preparedStatement.setNull(i + 1, Types.INTEGER);
                                else
                                    preparedStatement.setInt(i + 1, Integer.parseInt(input));
                            } else {
                                if (input.equals("-"))
                                    preparedStatement.setNull(i + 1, Types.VARCHAR);
                                else
                                    preparedStatement.setString(i + 1, input);
                            }
                        }
                        preparedStatement.addBatch();
                    }
                    int[] updateCount = preparedStatement.executeBatch();
                    conn.commit();
                    printUpdateInfo(updateCount.length, insertQuery);
                } catch (SQLException e) {
                    System.out.println("SQL Error: " + e.getMessage());
                } catch (NumberFormatException e) {
                    System.out.println("Number Format Error: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println("Time Format Error: " + e.getMessage());
                }
                continueInsert = getInput("Enter anything to continue or -1 to exit: ");
            }
            conn.setAutoCommit(true);
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return;
        }
    }

    private void deleteTuple() {
        System.out.println("deleteTuples");
        clearScreen();
        CaptionAndHeader tableAndHeader = getTableAndHeader();
        if (tableAndHeader == null)
            return;
        String table = tableAndHeader.getCaption();
        String[] primaryKeysName = getPrimaryKeys(table);
        if (primaryKeysName == null)
            return;
        String insertQuery = "DELETE FROM " + table + " WHERE " + String.join(" = ? AND ", primaryKeysName) + " = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);
            String continueInsert = "";
            while (!continueInsert.equals("-1")) {
                clearScreen();
                System.out.println(table);
                showTable(table);
                try {
                    for (int i = 0; i < primaryKeysName.length; i++) {
                        if (primaryKeysName[i].contains("_ID") || primaryKeysName[i].contains("_NO"))
                            preparedStatement.setInt(i + 1, Integer
                                    .parseInt(getInput("Enter " + primaryKeysName[i] + " you want to delete: ")));
                        else
                            preparedStatement.setString(i + 1,
                                    getInput("Enter " + primaryKeysName[i] + " you want to delete: "));
                    }
                    int updateCount = preparedStatement.executeUpdate();
                    printUpdateInfo(updateCount, insertQuery);
                } catch (SQLException e) {
                    System.out.println("SQL Error: " + e.getMessage());
                } catch (NumberFormatException e) {
                    System.out.println("Number Format Error: " + e.getMessage());
                }
                continueInsert = getInput("Enter anything to continue or -1 to exit: ");
            }
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return;
        }
    }

    private void runSQL() {
        System.out.println("runSQL");
        String continueRun = "";
        String sql = "";
        while (!continueRun.equals("-1")) {
            clearScreen();
            sql = getLinesInput("Enter your SQL Query (end the query with ';')\n:");
            try {
                resultSet = statement.executeQuery(sql);
                printData(resultSet);
            } catch (SQLException e) {
                System.out.println("SQL Error: " + e.getMessage());
            }
            continueRun = getInput("Enter anything to continue or -1 to exit: ");
        }
    }

    private String[] getIDAndRank(String firstName, String middleName, String lastName) {
        String[] idAndRank = new String[2];
        try {
            String query = "SELECT EMPLOYEE_ID, RANK FROM EMPLOYEE WHERE First_Name = ? AND Middle_Name = ? AND Last_Name = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, firstName);
            if (middleName.equals("-"))
                preparedStatement.setNull(2, Types.VARCHAR);
            else
                preparedStatement.setString(2, middleName);
            preparedStatement.setString(3, lastName);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                idAndRank[0] = resultSet.getString("EMPLOYEE_ID");
                idAndRank[1] = resultSet.getString("RANK");
                return idAndRank;
            } else {
                System.out.println("No employee found with the given name.");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: Cannot search for your name in the database: " + e.getMessage());
        }
        return null;
    }

    private String[] getOningTasksQuery(String id, String rank) {
        if (rank.equals("Manager")) {
            String[] managerQuery = {
                    "SELECT",
                    "a.TYPE as ACTIVITY_TYPE,",
                    "a.START_TIME,",
                    "a.END_TIME,",
                    "b.BUILDING_NAME,",
                    "a.CLOSEST_CORE",
                    "FROM ACTIVITY a",
                    "JOIN BUILDING b ON b.BUILDING_ID = a.BUILDING_ID",
                    "JOIN AREA ar ON ar.CLOSEST_CORE = a.CLOSEST_CORE AND ar.BUILDING_ID = a.BUILDING_ID",
                    "WHERE CURRENT_TIMESTAMP <= a.END_TIME AND a.MANAGER_ID = " + id,
            };
            return managerQuery;
        } else {
            String[] workerQuery = {
                    "SELECT",
                    "a.TYPE as ACTIVITY_TYPE,",
                    "a.START_TIME,",
                    "a.END_TIME,",
                    "e.FIRST_NAME || ' ' || COALESCE(e.MIDDLE_NAME || ' ', '') || e.LAST_NAME as Manager_Name,",
                    "b.BUILDING_NAME,",
                    "a.Closest_Core,",
                    "eq.Equipment_Name,",
                    "eq.CONTAIN_HARMFUL_CHEMICALS as HARMFUL_CHEMICALS",
                    "FROM ACTIVITY a",
                    "JOIN WORKS_ON w ON w.ACTIVITY_NO = a.ACTIVITY_NO",
                    "JOIN EMPLOYEE e ON e.EMPLOYEE_ID = a.MANAGER_ID",
                    "JOIN BUILDING b ON b.BUILDING_ID = a.BUILDING_ID",
                    "JOIN AREA ar ON ar.CLOSEST_CORE = a.CLOSEST_CORE AND ar.BUILDING_ID = a.BUILDING_ID",
                    "JOIN USES u ON u.ACTIVITY_NO = a.ACTIVITY_NO",
                    "JOIN EQUIPMENT eq ON eq.EQUIPMENT_ID = u.EQUIPMENT_ID",
                    "WHERE CURRENT_TIMESTAMP <= a.END_TIME AND w.EMPLOYEE_ID = " + id
            };
            return workerQuery;
        }
    }

    private void showOngoingTasks() {
        System.out.println("showOngoingTasks");
        String continueTypeName = "", firstName = "", middleName = "", lastName = "", rank = "", ID = "";
        while (!continueTypeName.equals("-1")) {
            clearScreen();
            firstName = getInput("Enter Your First Name: ");
            middleName = getInput("Enter Your Middle Name (- if you don't have): ");
            lastName = getInput("Enter Your Last Name: ");
            String[] idAndRank = getIDAndRank(firstName, middleName, lastName);
            if (idAndRank != null) {
                ID = idAndRank[0];
                rank = idAndRank[1];
            }
            if (!ID.isEmpty() && !rank.isEmpty())
                break;
            continueTypeName = getInput("Enter anything to re-enter your name or -1 to exit: ");
            if (continueTypeName.equals("-1"))
                return;
        }
        try {
            String[] ongoingTasksQuery = getOningTasksQuery(ID, rank);
            resultSet = statement.executeQuery(String.join(" ", ongoingTasksQuery));
            printData(resultSet);
            getInput("Enter anything to exit: ");
        } catch (SQLException e) {
            System.out.println("SQL Error: Cannot search for your name in the database: " + e.getMessage());
        }
    }

    private void showScheduledActivities() {
        System.out.println("showActivities");
        String continueSeeActivities = "";
        while (!continueSeeActivities.equals("-1")) {
            clearScreen();
            showActivityTime();
            showTable("BUILDING");
            try {
                String time = getInput("Enter time as yyyy-MM-dd HH:mm:ss: ");
                String buildingID = getInput("Enter Building_ID: ");
                System.out.print("\n");
                String[] searchQuery = {
                        "SELECT ac.Type, ac.Start_Time, ac.End_Time, b.Building_Name, ar.Floor, ar.Room_No, ar.Closest_Core",
                        "FROM ACTIVITY ac",
                        "INNER JOIN BUILDING b ON ac.Building_ID = b.building_ID",
                        "INNER JOIN AREA ar ON ac.Closest_Core = ar.Closest_Core AND ac.Building_ID = ar.Building_ID",
                        "WHERE TO_TIMESTAMP('" + time
                                + "', 'YYYY-MM-DD HH24:MI:SS') BETWEEN ac.Start_Time AND ac.End_Time",
                        "AND b.Building_ID = " + buildingID,
                };
                resultSet = statement.executeQuery(String.join(" ", searchQuery));
                printData(resultSet);
            } catch (SQLException e) {
                System.out.println("SQL Error: Cannot search for the activities from the given time and building");
                System.out.println(e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Date Format Error: Cannot convert input to date");
                System.out.println(e.getMessage());
            }
            continueSeeActivities = getInput("Enter anything to continue or -1 to exit: ");
        }

    }

    private void report() {
        System.out.println("report");
        clearScreen();
        String[] reportQuery = {
                "SELECT",
                "a.Type AS Activity_Type,",
                "b.Building_Name,",
                "a.Closest_Core AS Location,",
                "COUNT(DISTINCT w.Employee_ID) AS Number_Of_Workers",
                "FROM ACTIVITY a",
                "JOIN WORKS_ON w ON a.Activity_No = w.Activity_No",
                "JOIN EMPLOYEE e ON w.Employee_ID = e.Employee_ID",
                "JOIN BUILDING b ON a.Building_ID = b.Building_ID",
                "WHERE e.Rank = 'Worker'",
                "AND CURRENT_TIMESTAMP BETWEEN a.Start_Time AND a.End_Time",
                "GROUP BY a.Type, b.Building_Name, a.Closest_Core",
                "ORDER BY a.Type, b.Building_Name, a.Closest_Core"
        };
        try {
            resultSet = statement.executeQuery(String.join(" ", reportQuery));
            System.out.println("Worker Activity Analysis Report");
            System.out.println("Generated at: "
                    + java.time.LocalDateTime.now().format(TIMESTAMP_FORMATTER));
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                int space = metaData.getColumnDisplaySize(i);
                for (int j = 0; j < space; j++)
                    System.out.print("=");
                System.out.print("===");
            }
            System.out.print("\n");
            printData(resultSet);
            getInput("Enter anything to exit: ");
        } catch (SQLException e) {
            System.out.println("SQL Error: Cannot generate report");
            System.out.println(e.getMessage());
            return;
        }
    }

    private void showMenu() {
        clearScreen();
        System.out.println("=== CMMS Menu ===");
        System.out.print("\n");
        System.out.println("--- For Users ---");
        System.out.println("0: Show Activities");
        System.out.print("\n");
        System.out.println("--- For Staffs ---");
        System.out.println("1: Show Ongoing Tasks");
        System.out.print("\n");
        System.out.println("--- For Admins ---");
        System.out.println("2: See table");
        System.out.println("3: Insert tuple");
        System.out.println("4: Set-based insertion");
        System.out.println("5: Delete tuple");
        System.out.println("6: Run SQL query");
        System.out.println("7: Report");
        System.out.print("\n");
        System.out.println("-1: exit program");
    }

    private void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                System.out.print("\033[H\033[2J");
        } catch (InterruptedException e) {
            System.out.println("InterruptedException Error: Cannot clear screen");
            return;
        } catch (IOException e) {
            System.out.println("IOException Error: Cannot clear screen");
            return;
        }
    }

    private void exit() {
        System.out.println("Program exit");
        try {
            statement.close();
            resultSet.close();
            conn.close();
        } catch (Exception ignore) {
        }
        scan.close();
        System.exit(0);
    }

}
