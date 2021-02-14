package info.ryandorman.simplescheduler.common;

/*
 *   Ryan Dorman
 *   ID: 001002824
 */

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Optional;

/**
 * A utility class that allow easy creation and manipulation of various JavaFx components such as ComboBoxes and Alerts.
 */
public class JavaFXUtil {

    public static StringConverter getComboBoxConverter(List<ComboBoxOption> options) {
        return new StringConverter<ComboBoxOption>() {
            @Override
            public String toString(ComboBoxOption option) {
                return option.getLabel();
            }

            @Override
            public ComboBoxOption fromString(String string) {
                return options.stream().filter(co ->
                        co.getLabel().equals(string)).findFirst().orElse(null);
            }
        };
    }

    /**
     * Create a confirmation window that requires user feedback to determine which path the application takes.
     *
     * @param title Title of the window
     * @param header Header in the window
     * @param content Content in main body of window
     * @return A boolean that indicates the user's choice
     */
    public static boolean confirmation(String title, String header, String content) {
        return alert(Alert.AlertType.CONFIRMATION, title, header, content);
    }

    /**
     * Create a warning window that notifies the user and requires they accept to continue.
     *
     * @param title Title of the window
     * @param header Header in the window
     * @param content Content in main body of window
     * @return A boolean that indicates the user has accepted the warning
     */
    public static boolean warning(String title, String header, String content) {
        return alert(Alert.AlertType.WARNING, title, header, content);
    }

    /**
     * Create a warning window that informs the user and requires they accept to continue.
     *
     * @param title Title of the window
     * @param header Header in the window
     * @param content Content in main body of window
     * @return A boolean that indicates the user has accepted the information
     */
    public static boolean inform(String title, String header, String content) {
        return alert(Alert.AlertType.INFORMATION, title, header, content);
    }

    /**
     * A wrapper function that allows easy population of a JavaFx alert window that is displayed until the user selects
     * a confirmation/choice.
     *
     * @param type Type of JavaFx alert to display
     * @param title Title of the window
     * @param header Header in the window
     * @param content Content in main body of window
     * @return A boolean that indicates the user has accepted the alert
     */
    private static boolean alert(Alert.AlertType type, String title, String header, String content) {
        // Create Confirmation Alert and set the stylesheet on the pane
        Alert alert = new Alert(type);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(JavaFXUtil.class.getResource("/view/theme.css").toExternalForm());

        // Set the Alert's type content
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Show the Alert and wait for a response, returning it to the calling method
        Optional<ButtonType> option = alert.showAndWait();

        return option.filter(ButtonType.OK::equals).isPresent();
    }


}