package com.example.contactsapp.controller;

import com.example.contactsapp.datamodel.Contact;
import com.example.contactsapp.datamodel.ContactSingleton;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class DialogController {
    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField commentField;


    public TextField getNameField() {
        return nameField;
    }

    public TextField getSurnameField() {
        return surnameField;
    }

    public TextField getPhoneField() {
        return phoneField;
    }


    public Contact processPressOKButton(){
        Contact c;

        if (commentField.getText().isEmpty()) {
            c = new Contact(nameField.getText(), surnameField.getText(), phoneField.getText());
        } else {
            c = new Contact(nameField.getText(), surnameField.getText(), phoneField.getText(), commentField.getText());
        }
        ContactSingleton.getInstance().addContact(c);
        return c;
    }

    public void loadContactData(Contact c) {
            nameField.setText(c.getName());
            surnameField.setText(c.getSurname());
            phoneField.setText(c.getPhone());
            commentField.setText(c.getComment());
    }

    public Contact editPressOKButton(Contact c) {

        c.setName(nameField.getText());
        c.setSurname(surnameField.getText());
        c.setPhone(phoneField.getText());
        if (commentField.getText().isEmpty()) {
            c.setComment(" ");
        } else {
            c.setComment(commentField.getText());
        }

        return c;
    }
}



