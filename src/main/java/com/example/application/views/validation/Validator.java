package com.example.application.views.validation;

import com.example.application.data.entity.SampleContractor;
import com.example.application.data.entity.SampleReport;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.DateRangeValidator;
import com.vaadin.flow.data.validator.RegexpValidator;

import java.time.LocalDate;

public class Validator {

    private Binder<SampleContractor> binderContractor = new Binder<>();
    private Binder<SampleReport> binderReport = new Binder<>();

    public void setBinderContractor(Binder<SampleContractor> binder) {
        binderContractor = binder;
    }

    public void setBinderReport(Binder<SampleReport> binder) {
        binderReport = binder;
    }

    /*----------------------------------------------------------------------*/
    /* Валидация контрагентов */

    public void validateLimitedNumberFields(TextField textField, int digitNumber) {
        String label = textField.getLabel();
        String message = label + " должен содержать " + digitNumber + " цифр";
        String pattern = "^\\d{" + digitNumber + "}$|^$";

        if (label.equals("ИНН"))
            binderContractor.forField(textField).withValidator(new RegexpValidator(message, pattern))
                    .bind(SampleContractor::getITN, SampleContractor::setITN);
        else if (label.equals("ОГРНИП"))
            binderContractor.forField(textField).withValidator(new RegexpValidator(message, pattern))
                    .bind(SampleContractor::getRegistrationNumber, SampleContractor::setRegistrationNumber);

        textField.addValueChangeListener(e -> binderContractor.validate());
    }

    public void validateStringCyrillic(TextField textField) {
        String label = textField.getLabel();
        String pattern = "^[а-яА-Я \\-]*$";
        String message = label + " может содержать только буквы, пробел и '-'";
        switch (label) {
            case "Фамилия":
                binderContractor.forField(textField).withValidator(new RegexpValidator(message, pattern))
                        .bind(SampleContractor::getLastName, SampleContractor::setLastName);
                break;
            case "Имя":
                binderContractor.forField(textField).withValidator(new RegexpValidator(message, pattern))
                        .bind(SampleContractor::getFirstName, SampleContractor::setFirstName);
                break;
            case "Отчество":
                binderContractor.forField(textField).withValidator(new RegexpValidator(message, pattern))
                        .bind(SampleContractor::getSecondName, SampleContractor::setSecondName);
                break;
            case "Наименование юридического лица":
                binderContractor.forField(textField).withValidator(new RegexpValidator(message, pattern))
                        .bind(SampleContractor::getOOOName, SampleContractor::setOOOName);
                break;
        }

        textField.addValueChangeListener(e -> binderContractor.validate());
    }

    public void validateComboBox(ComboBox<String> comboBox) {
        binderContractor.forField(comboBox).withValidator(new RegexpValidator(
                        "Форма ЮЛ может содержать только буквы, пробел и '-'", "^[а-яА-Яa-zA-Z \\-]*$"))
                .bind(SampleContractor::getOOOForm, SampleContractor::setOOOForm);

        comboBox.addValueChangeListener(e -> binderContractor.validate());
    }


    /*----------------------------------------------------------------------*/
    /* Валидация общая */

    public void validateNumberFields(TextField textField) {
        String label = textField.getLabel();
        String pattern = "^[\\d+]*$";
        String message = label + " может содержать только цифры";

        switch (label) {
            case "Номер доверенности":
                binderContractor.forField(textField).withValidator(new RegexpValidator(message, pattern))
                        .bind(SampleContractor::getProxyNumber, SampleContractor::setProxyNumber);
                textField.addValueChangeListener(e -> binderContractor.validate());
                break;
            case "Номер справки постановки на учет":
                binderContractor.forField(textField).withValidator(new RegexpValidator(message, pattern))
                        .bind(SampleContractor::getRegistrationCertificateNumber,
                                SampleContractor::setRegistrationCertificateNumber);
                textField.addValueChangeListener(e -> binderContractor.validate());
                break;
            case "Номер курса":
                binderReport.forField(textField).withValidator(new RegexpValidator(message, pattern))
                        .bind(SampleReport::getCourseCode, SampleReport::setCourseCode);
                textField.addValueChangeListener(e -> binderReport.validate());
                break;
        }
    }

    public void validateIntegerAndDouble(TextField textField) {
        String label = textField.getLabel();
        String pattern = "^[\\d+$|\\d+.\\d+|\\d+,\\d+]*$";
        String message = label + " может содержать целое или дробное число";

        switch (label) {
            case "Ставка по налогам":
                binderContractor.forField(textField).withValidator(new RegexpValidator(message, pattern))
                        .bind(SampleContractor::getTaxPercentage, SampleContractor::setTaxPercentage);
                textField.addValueChangeListener(e -> binderContractor.validate());
                break;
            case "Ставка по роялти":
                binderReport.forField(textField).withValidator(new RegexpValidator(message, pattern))
                        .bind(SampleReport::getRoyaltyPercentage, SampleReport::setRoyaltyPercentage);
                textField.addValueChangeListener(e -> binderReport.validate());
                break;
            case "К2":
                binderReport.forField(textField).withValidator(new RegexpValidator(message, pattern))
                        .bind(SampleReport::getK2, SampleReport::setK2);
                textField.addValueChangeListener(e -> binderReport.validate());
                break;
        }
    }

    public void validateDates(DatePicker datePicker) {
        String label = datePicker.getLabel();
        String message = "Формат даты: ДД.ММ.ГГГГ";
        DateRangeValidator validator = new DateRangeValidator(message,
                LocalDate.of(1980, 1, 1),
                LocalDate.now().plusMonths(1));

        switch (label) {
            case "Дата договора":
                binderReport.forField(datePicker)
                        .withValidator(validator)
                        .bind(SampleReport::getContractDate, SampleReport::setContractDate);
                datePicker.addValueChangeListener(e -> binderReport.validate());
                break;
            case "Дата передачи РИД":
                binderReport.forField(datePicker)
                        .withValidator(validator)
                        .bind(SampleReport::getTransferDateOfRIA, SampleReport::setTransferDateOfRIA);
                datePicker.addValueChangeListener(e -> binderReport.validate());
                break;
            case "Дата постановки самозанятым":
                binderContractor.forField(datePicker)
                        .withValidator(validator)
                        .bind(SampleContractor::getSelfemployedDate, SampleContractor::setSelfemployedDate);
                datePicker.addValueChangeListener(e -> binderContractor.validate());
                break;
            case "Дата справки постановки на учет":
                binderContractor.forField(datePicker)
                        .withValidator(validator)
                        .bind(SampleContractor::getRegistrationCertificateDate, SampleContractor::setRegistrationCertificateDate);
                datePicker.addValueChangeListener(e -> binderContractor.validate());
                break;
            case "Дата получения доверенности":
                binderContractor.forField(datePicker)
                        .withValidator(validator)
                        .bind(SampleContractor::getProxyDate, SampleContractor::setProxyDate);
                datePicker.addValueChangeListener(e -> binderContractor.validate());
                break;
        }
    }


    /*----------------------------------------------------------------------*/
    /* Уведомление для обязательных полей */

    public void showNotificationForRequiredFields() {
        Notification notification = new Notification();
        notification.setPosition(Notification.Position.MIDDLE);

        Icon icon = VaadinIcon.WARNING.create();
        icon.setColor("var(--lumo-error-color)");

        Div errorMes = new Div(new Text("Все обязательный поля должны быть заполнены!"));

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> notification.close());

        HorizontalLayout layout = new HorizontalLayout(icon, errorMes, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.open();
    }
}
