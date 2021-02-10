package info.ryandorman.simplescheduler.controller;

import info.ryandorman.simplescheduler.common.AlertUtil;
import info.ryandorman.simplescheduler.dao.*;
import info.ryandorman.simplescheduler.model.Country;
import info.ryandorman.simplescheduler.model.Customer;
import info.ryandorman.simplescheduler.model.FirstLevelDivision;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomerViewController implements Initializable {

    private CustomerDao customerDao = new CustomerDaoImpl();
    private CountryDao countryDao = new CountryDaoImpl();
    private FirstLevelDivisionDao divisionDao = new FirstLevelDivisionDaoImpl();
    private Customer currentCustomer = new Customer();
    private boolean isUpdating = false;

    // Modal Header
    @FXML
    private Label header;

    // Customer Fields
    @FXML
    private TextField idTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField phoneTextField;

    @FXML
    private TextField addressTextField;

    @FXML
    private TextField postalCodeTextField;

    @FXML
    private ComboBox<Country> countryComboBox;

    @FXML
    private ComboBox<FirstLevelDivision> divisionComboBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load Countries
        ObservableList<Country> countries = FXCollections.observableArrayList(countryDao.getAll());

        // Configure how ComboBox displays a Country
        countryComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Country co) {
                return co.getName();
            }

            @Override
            public Country fromString(String string) {
                return countries.stream().filter(co ->
                        co.getName().equals(string)).findFirst().orElse(null);
            }
        });

        // Listener to configure divisions ComboBox based on country selected
        countryComboBox.valueProperty().addListener((obs, oldVale, newValue) -> {
            if (newValue != null) {
                setupDivisionComboBox(newValue.getId());
            }
        });
        countryComboBox.setItems(countries);
    }

    public void initData(Stage currentStage, int selectedCustomerId) {
        // Setup modal for editing
        isUpdating = true;
        header.setText("Edit Customer");

        // Get latest version of customer
        currentCustomer = customerDao.getById(selectedCustomerId);

        if (currentCustomer != null) {
            // Set up form with selected customer for updating
            // populate text fields
            idTextField.setText(String.valueOf(currentCustomer.getId()));
            nameTextField.setText(currentCustomer.getName());
            phoneTextField.setText(currentCustomer.getPhone());
            addressTextField.setText(currentCustomer.getAddress());
            postalCodeTextField.setText(currentCustomer.getPostalCode());

            // set combo values based on country and fld
            countryComboBox.valueProperty().setValue(currentCustomer.getDivision().getCountry());
            divisionComboBox.valueProperty().setValue(currentCustomer.getDivision());
        } else {
            // Display warning and close
            AlertUtil.warning("Not Found", "Invalid Id", "Customer specified no longer exists.");
            currentStage.close();
        }
    }

    @FXML
    public void onSave(ActionEvent actionEvent) {
        // Get customer fields updated in form
        String name = nameTextField.getText().trim();
        String phone = phoneTextField.getText().trim();
        String address = addressTextField.getText().trim();
        String postalCode = postalCodeTextField.getText().trim();
        FirstLevelDivision division = divisionComboBox.valueProperty().getValue();

        // Update customer object
        currentCustomer.setName(name);
        currentCustomer.setPhone(phone);
        currentCustomer.setAddress(address);
        currentCustomer.setPostalCode(postalCode);
        currentCustomer.setDivision(division);
        currentCustomer.setUpdatedBy(MainViewController.currentUser.getName());

        if (isUpdating) {
            customerDao.update(currentCustomer);
        } else {
            currentCustomer.setCreatedBy(MainViewController.currentUser.getName());
            customerDao.create(currentCustomer);
        }

        // Close the Modal
        Stage partStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        partStage.close();
    }

    @FXML
    public void onCancel(ActionEvent actionEvent) {
        // Confirm cancel before closing the associated Modal
        boolean userConfirmed = AlertUtil.confirmation("Cancel", "Cancel Changes",
                "Are you sure you want to return to the Customers?");

        if (userConfirmed) {
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            currentStage.close();
        }
    }

    private void setupDivisionComboBox(int countryId) {
        ObservableList<FirstLevelDivision> divisions = FXCollections.observableArrayList(divisionDao.getByCountryId(countryId));
        divisionComboBox.setConverter(new StringConverter<>() {

            @Override
            public String toString(FirstLevelDivision division) {
                return division.getName();
            }

            @Override
            public FirstLevelDivision fromString(String string) {
                return divisions.stream().filter(fld ->
                        fld.getName().equals(string)).findFirst().orElse(null);
            }
        });
        divisionComboBox.setItems(divisions);
    }

}
