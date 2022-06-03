package com.example.contactsapp.controller;


import com.example.contactsapp.datamodel.Contact;
import com.example.contactsapp.datamodel.ContactSingleton;
import com.example.contactsapp.exceptions.ContactException;
import com.example.contactsapp.exceptions.ErrorCode;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import java.io.IOException;
import java.util.Optional;

public class MainController {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private TableView<Contact> contactsTableView;

    @FXML
    private ContextMenu tableViewContextMenu;

    @FXML
    private ToggleButton toggleButton;

    @FXML
    private TextField searchField;

    @FXML
    private FilteredList<Contact> filteredContactList;

    final ButtonType okButton = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);

    final ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

    final ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);

    final ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);

    final ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);


    @FXML
    public void initialize() throws ContactException{

        filteredContactList = new FilteredList<Contact>(ContactSingleton.getInstance().getContacts(),(contact -> true));


        contactsTableView.setItems(ContactSingleton.getInstance().getContacts());

        tableViewContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(event -> {
            Contact c = contactsTableView.getSelectionModel().getSelectedItem();
            eraseContact(c);
        });
        MenuItem editMenuItem = new MenuItem("Edit");
        editMenuItem.setOnAction(event -> {
            Contact c = contactsTableView.getSelectionModel().getSelectedItem();
            try {
                showDialogEditContact();
            } catch (ContactException e) {
                e.printStackTrace();
            }
        });

        tableViewContextMenu.getItems().add(deleteMenuItem);
        tableViewContextMenu.getItems().add(editMenuItem);

        contactsTableView.setRowFactory(new Callback<TableView<Contact>, TableRow<Contact>>() {
            @Override
            public TableRow<Contact> call(TableView<Contact> contactTableView) {
                final TableRow<Contact> row = new TableRow<>();

                row.emptyProperty().addListener((obs, notEmpty, wasEmpty) -> {
                    if(wasEmpty){
                        row.setContextMenu(null);
                    } else {
                        row.setContextMenu(tableViewContextMenu);
                    }
                });
                return row;
            }
        });

        contactsTableView.getSelectionModel().selectFirst();



    }

    @FXML
    public void showDialogNewContact() throws ContactException {
        Dialog<ButtonType> d = new Dialog<>();
        d.initOwner(mainBorderPane.getScene().getWindow());
        d.setResizable(true);
        d.setTitle("New task");
        d.setHeaderText("Enter a new contact");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/contactsapp/dialog-view.fxml"));
        try {
            d.getDialogPane().setContent(loader.load());
        } catch (IOException exception){
            throw new ContactException(ErrorCode.DIALOG_ERROR, "Can't create new contact dialog");
        }

        DialogController contactController = loader.getController();

        d.getDialogPane().getButtonTypes().add(okButton);
        d.getDialogPane().getButtonTypes().add(cancelButton);

        d.getDialogPane().lookupButton(okButton).disableProperty()
                .bind(Bindings.createBooleanBinding(
                        () -> contactController.getNameField().getText().isEmpty() ||
                                contactController.getSurnameField().getText().isEmpty() ||
                                contactController.getPhoneField().getText().isEmpty(),
                        contactController.getNameField().textProperty(),
                        contactController.getSurnameField().textProperty(),
                        contactController.getPhoneField().textProperty()
                ));


        Optional<ButtonType> response = d.showAndWait();

            if (response.isPresent() && response.get() == okButton) {
                DialogController controller = loader.getController();
                Contact c = controller.processPressOKButton();
                contactsTableView.getSelectionModel().select(c);
            }
    }

    @FXML
    public void showDialogEditContact() throws ContactException{
            Contact c = contactsTableView.getSelectionModel().getSelectedItem();
        if(c == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No contact");
            alert.setHeaderText("");
            alert.setContentText("No contact selected");
            alert.getDialogPane().getStylesheets().add("style.css");
            alert.getButtonTypes().set(0, okButton);
            alert.showAndWait();
            return;
        } else {
            Dialog<ButtonType> d = new Dialog<>();
            d.initOwner(mainBorderPane.getScene().getWindow());
            d.setResizable(true);
            d.setTitle("Edit contact");
            d.setHeaderText("Edit a contact");


            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/contactsapp/dialog-view.fxml"));
            try {
                d.getDialogPane().setContent(loader.load());
            } catch (IOException exception){
                throw new ContactException(ErrorCode.DIALOG_ERROR, "Can't create edit contact dialog");
            }

            DialogController contactController = loader.getController();

            d.getDialogPane().getButtonTypes().add(okButton);
            d.getDialogPane().getButtonTypes().add(cancelButton);

            contactController.loadContactData(c);

            d.getDialogPane().lookupButton(okButton).disableProperty()
                    .bind(Bindings.createBooleanBinding(
                            () -> contactController.getNameField().getText().isEmpty() ||
                                    contactController.getSurnameField().getText().isEmpty() ||
                                    contactController.getPhoneField().getText().isEmpty(),
                            contactController.getNameField().textProperty(),
                            contactController.getSurnameField().textProperty(),
                            contactController.getPhoneField().textProperty()
                    ));

//            \d{9}||\d{3}\-\d{3}\-\d{3}||\d{3} \d{3} \d{3}||\d{3} \d{2} \d{2} \d{2}

            Optional response = d.showAndWait();

            if (response.isPresent() && response.get() == okButton){
                DialogController controller = loader.getController();
                c = controller.editPressOKButton(c);
                contactsTableView.refresh();
                contactsTableView.getSelectionModel().select(c);
            }
        }

    }

    @FXML
    public void showDialogShortcuts() throws ContactException{
//      With fxml file

        Dialog<ButtonType> d = new Dialog<>();
        d.initOwner(mainBorderPane.getScene().getWindow());
        d.setResizable(true);
        d.setTitle("Shortcuts info");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/contactsapp/shortcuts-view.fxml"));
        try {
            d.getDialogPane().setContent(loader.load());
        } catch (IOException exception){
            throw new ContactException(ErrorCode.DIALOG_ERROR, "Can't create shortcut dialog");
        }
        d.getDialogPane().getButtonTypes().add(closeButton);

        d.showAndWait();
    }

    public void onKeyPressedTableView(KeyEvent keyEvent) throws ContactException {
        Contact c = contactsTableView.getSelectionModel().getSelectedItem();
        if(c!=null && keyEvent.getCode() == KeyCode.DELETE) {
            eraseContact(c);
        } else if(c!=null && keyEvent.getCode() == KeyCode.F2) {
            showDialogEditContact();
        }
    }

    public void onKeyPressed(KeyEvent keyEvent) throws ContactException {
        if (keyEvent.getCode() == KeyCode.F1) {
            showDialogNewContact();
        } else if (keyEvent.getCode() == KeyCode.F12){
            exitApp();
        }
    }


    public void eraseContact(Contact c){
        if(c == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No contact");
            alert.setHeaderText("");
            alert.setContentText("No contact selected");
            alert.getDialogPane().getStylesheets().add("style.css");
            alert.getButtonTypes().set(0, okButton);
            alert.showAndWait();
            return;
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Erase a contact");
            alert.setHeaderText("Delete a contact");
            alert.setContentText("Are you sure?");

            alert.getDialogPane().getStylesheets().add("style.css");

            alert.getButtonTypes().set(0, yesButton);
            alert.getButtonTypes().set(1, noButton);
            Optional<ButtonType> response = alert.showAndWait();
            if (response.isPresent() && response.get()==yesButton) {
                ContactSingleton.getInstance().deleteContact(c);
            }
        }
    }

    @FXML
    public void exitApp(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Exiting app");
        alert.setContentText("Are you sure?");

        alert.getDialogPane().getStylesheets().add("style.css");

        alert.getButtonTypes().set(0, yesButton);
        alert.getButtonTypes().set(1, noButton);

        Optional<ButtonType> response = alert.showAndWait();
        if (response.isPresent() && response.get()==yesButton){
            Platform.exit();
        }
    }

    @FXML
    public void deleteContact() {
        Contact c = contactsTableView.getSelectionModel().getSelectedItem();
        eraseContact(c);
    }

    @FXML
    public void handleDisplayContactStartWith(){
        Contact c = contactsTableView.getSelectionModel().getSelectedItem();
        String s = searchField.getText();

        if (toggleButton.isSelected()){
            filteredContactList.setPredicate(contact -> contact.getName().toLowerCase().startsWith(s.toLowerCase()) || contact.getSurname().toLowerCase().startsWith(s.toLowerCase()));
            if (filteredContactList.isEmpty()){
                contactsTableView.setItems(null);
            } else {
                contactsTableView.setItems(filteredContactList);
                contactsTableView.getSelectionModel().getSelectedItem();
            }
        } else {
            if (c == null){
                contactsTableView.getSelectionModel().selectFirst();
            }
                filteredContactList.setPredicate(contact -> true);
                contactsTableView.getSelectionModel().select(c);
        }
    }
}