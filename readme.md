# Simple Scheduler v1.0.1

## Features:
- Dashboard view that generates data reports on the following for a specified window of time:
    * Appointment data by type, month, type and month
    * Each Contact's Appointment schedule
    * The Appointment workload for each User over time
- Customers view that displays all Customers and supports Customer searching, creating, updating, and deleting
- Appointments view that displays all Appointments and supports Appointment filtering, creating, updating, and deleting

## How to Run:
### Database
- Create a connection.properties file in /resources that includes a MySQL database: url, name, user, pass
- Init MySQL database with provided initDB.sql file in /resources
### IntelliJ
- In Settings set Path Variable 'PATH_TO_FX' to the location of the JavaFx SDK on your machine
- In Build Config set VM options: --module-path ${PATH_TO_FX} --add-modules=javafx.controls,javafx.fxml,javafx.graphics

## Built using:
- Java SE 11.0.10
- JavaFx SDK 11.0.2
- MySql Connector Java 8.0.22
- IntelliJ IDEA Community Edition 2020.3.2
- [FontAwesome Free Icons](https://fontawesome.com/license/free)

_Last update: 2/27/2021_