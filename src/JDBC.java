import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBC {
    public static void main(String[] args) {
        // Datenbankverbindung herstellen
        String url = "jdbc:mariadb://localhost:3306/Aufgabe4";
        String user = "root";
        String password = "bitnami";
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            while (true) {
                System.out.println("Was wollen Sie?");
                System.out.println("(0) Programm beenden");
                System.out.println("(1) neuen Studierenden hinzufuegen");
                System.out.println("(2) alle Studierenden zeigen");
                System.out.println("(3) Namen eines Studierenden aendern");
                System.out.println("(4) alle Studierenden loeschen");

                int choice = Input.getInt(); //Input-Klasse vewrenden

                switch (choice) {
                    case 0:
                        // Programm beenden
                        return;
                    case 1:
                        // neuen Studierenden hinzufuegen
                       addStudent(conn);
                        break;
                    case 2:
                        // alle Studierenden zeigen
                       showStudents(conn);
                        break;
                    case 3:
                        // Namen eines Studierenden aendern
                       updateStudentName(conn);
                        break;
                    case 4:
                        // alle Studierenden loeschen
                       deleteAllStudents(conn);
                        break;
                    default:
                        System.out.println("Ungueltige Eingabe");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addStudent(Connection conn) throws SQLException {
        System.out.print("Matrikelnummer: ");
        int matnr = Input.getInt();
        Input.getString(); // consume newline

        System.out.print("Name: ");
        String name = Input.getString();

        System.out.print("Semester: ");
        String semester = Input.getString();

        // Überprüfen, ob die Matrikelnummer bereits existiert
        String checkQuery = "SELECT Matnr FROM Student WHERE Matnr = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, matnr);
            ResultSet resultSet = checkStmt.executeQuery();
            if (resultSet.next()) {
                System.out.println("Matrikelnummer bereits vorhanden. Einfügen fehlgeschlagen.");
            } else {
                // Matrikelnummer existiert nicht, neuen Studierenden hinzufügen
                String insertQuery = "INSERT INTO Student(Matnr, Name, Semester) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, matnr);
                    insertStmt.setString(2, name);
                    insertStmt.setString(3, semester);
                    insertStmt.executeUpdate();
                    System.out.println("Studierender erfolgreich hinzugefügt!");
                }
            }
        }
    }

    private static void showStudents(Connection conn) throws SQLException {
        String query = "SELECT * FROM Student";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                int matnr = resultSet.getInt("Matnr");
                String name = resultSet.getString("Name");
                String semester = resultSet.getString("Semester");
                System.out.println("Matrikelnummer: " + matnr + ", Name: " + name + ", Semester: " + semester);
            }
        }
    }

    private static void updateStudentName(Connection conn) throws SQLException {
        System.out.print("Matrikelnummer: ");
        int matnr = Input.getInt();
        Input.getString(); // consume newline

        System.out.print("Neuer Name: ");
        String newName = Input.getString();

        String updateQuery = "UPDATE Student SET Name = ? WHERE Matnr = ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
            updateStmt.setString(1, newName);
            updateStmt.setInt(2, matnr);
            int rowsUpdated = updateStmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Erfolgreich geändert!");
            } else {
                System.out.println("Matrikelnummer nicht gefunden. Änderung fehlgeschlagen.");
            }
        }
    }

    private static void deleteAllStudents(Connection conn) throws SQLException {
        String deleteQuery = "DELETE FROM Student";
        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
            int rowsDeleted = deleteStmt.executeUpdate();
            System.out.println(rowsDeleted + " Studierende gelöscht.");
        }
    }
}