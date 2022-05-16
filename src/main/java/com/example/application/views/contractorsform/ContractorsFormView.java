package com.example.application.views.contractorsform;

import com.example.application.database.DBManager;
import com.example.application.data.entity.SampleContractor;
import com.example.application.data.service.SampleContractorService;
import com.example.application.views.validation.Validator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.example.application.views.MainLayout;

import javax.annotation.security.RolesAllowed;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;

import java.util.Locale;

@PageTitle("Регистрация контрагента")
@Route(value = "contractors-form", layout = MainLayout.class)
@RolesAllowed("admin")
@Uses(Icon.class)
public class ContractorsFormView extends Div {

    private final ComboBox<String> contractorTypes = new ComboBox<>("Тип контрагента");
    private final TextField lastName = new TextField("Фамилия");
    private final TextField firstName = new TextField("Имя");
    private final TextField secondName = new TextField("Отчество");
    private final ComboBox<String> OOOForm = new ComboBox<>("Форма юридического лица");
    private final TextField OOOName = new TextField("Наименование юридического лица");
    private final TextField taxPercentage = new TextField("Ставка по налогам");
    private final ComboBox<String> signatoryPosition = new ComboBox<>("Должность подписанта");
    private final DatePicker selfemployedDate = new DatePicker("Дата постановки самозанятым");
    private final TextField registrationCertificateNumber = new TextField("Номер справки постановки на учет");
    private final DatePicker registrationCertificateDate = new DatePicker("Дата справки постановки на учет");
    private final TextField registrationNumber = new TextField("ОГРНИП");
    private final TextField ITN = new TextField("ИНН");
    private final TextField proxyNumber = new TextField("Номер доверенности");
    private final DatePicker proxyDate = new DatePicker("Дата получения доверенности");

    private final Button cancel = new Button("Отмена");
    private final Button save = new Button("Сохранить");

    private final Binder<SampleContractor> binder = new Binder(SampleContractor.class);
    private final Validator validator = new Validator();

    private final FormLayout formLayout = new FormLayout();

    private final Locale russianLocale = new Locale("ru", "RU");

    private static boolean tableCreated = false;

    public ContractorsFormView(SampleContractorService contractorService) {
        addClassName("contractors-form-view");

        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        setLocaleToDatePickers();

        binder.bindInstanceFields(this);
        clearForm();

        validateFields();
        setRequiredFields();
        addValueChangeListeners(contractorService);
    }

    private void addValueChangeListeners(SampleContractorService contractorService) {
        if (!tableCreated) {
            DBManager.createContractorsTable();
            tableCreated = true;
        }

        contractorTypes.addValueChangeListener(e -> {
            binder.getBean().setContractorType(e.getValue());
            if (e.getValue() != null && !e.getValue().equals(""))
                registerContractor();
            if (e.getValue().equals("Юридическое лицо"))
                validator.validateLimitedNumberFields(ITN, 10);
            else validator.validateLimitedNumberFields(ITN, 12);
        });

        valueChangingWithCustomValues(OOOForm);
        valueChangingWithCustomValues(signatoryPosition);

        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> {
            if (!checkRequiredFields())
                validator.showNotificationForRequiredFields();
            else {
                SampleContractor contractor = binder.getBean();
                contractorService.update(contractor);

                DBManager.insertRowIntoContractors(binder);
                Notification.show("Информация о контрагенте сохранена");
                clearForm();
            }
        });
    }

    private void validateFields() {
        validator.setBinderContractor(binder);

        validator.validateLimitedNumberFields(registrationNumber, 15);

        validator.validateNumberFields(proxyNumber);

        validator.validateStringCyrillic(lastName);
        validator.validateStringCyrillic(firstName);
        validator.validateStringCyrillic(secondName);
        validator.validateStringCyrillic(OOOName);

        validator.validateIntegerAndDouble(taxPercentage);

        validator.validateComboBox(OOOForm);

        validator.validateDates(selfemployedDate);
        validator.validateDates(registrationCertificateDate);
        validator.validateDates(proxyDate);
    }

    private void setRequiredFields() {
        lastName.setRequired(true);
        firstName.setRequired(true);
        secondName.setRequired(true);
        OOOForm.setRequired(true);
        OOOName.setRequired(true);
        taxPercentage.setRequired(true);
        signatoryPosition.setRequired(true);
        selfemployedDate.setRequired(true);
        registrationCertificateNumber.setRequired(true);
        registrationCertificateDate.setRequired(true);
        registrationNumber.setRequired(true);
        proxyNumber.setRequired(true);
        proxyDate.setRequired(true);
    }

    private boolean checkRequiredFields() {
        String status = contractorTypes.getValue();
        switch (status) {
            case "Физическое лицо":
                if (lastName.getValue().equals("") ||
                        firstName.getValue().equals("") ||
                        secondName.getValue().equals("")) {
                    return false;
                }
                break;
            case "Юридическое лицо":
                if (OOOForm.getValue().equals("") ||
                        OOOName.getValue().equals("") ||
                        taxPercentage.getValue().equals("") ||
                        signatoryPosition.getValue().equals("") ||
                        lastName.getValue().equals("") ||
                        firstName.getValue().equals("") ||
                        secondName.getValue().equals("") ||
                        (proxyNumber.isEnabled() && proxyNumber.getValue().equals("")) ||
                        (proxyDate.isEnabled() && proxyDate.isEmpty())) {
                    return false;
                }
                break;
            case "Самозанятый":
                if (lastName.getValue().equals("") ||
                        firstName.getValue().equals("") ||
                        secondName.getValue().equals("") ||
                        selfemployedDate.isEmpty() ||
                        registrationCertificateNumber.getValue().equals("") ||
                        registrationCertificateDate.isEmpty()) {
                    return false;
                }
                break;
            case "Индивидуальный предприниматель":
                if (lastName.getValue().equals("") ||
                        firstName.getValue().equals("") ||
                        secondName.getValue().equals("") ||
                        registrationNumber.getValue().equals("")) {
                    return false;
                }
                break;
        }
        return true;
    }

    private void setLocaleToDatePickers() {
        selfemployedDate.setLocale(russianLocale);
        registrationCertificateDate.setLocale(russianLocale);
        proxyDate.setLocale(russianLocale);
    }

    private void registerContractor() {
        String status = contractorTypes.getValue();
        contractorTypes.setEnabled(false);
        switch (status) {
            case "Физическое лицо":
                formLayout.add(lastName, firstName, secondName, ITN);
                break;
            case "Юридическое лицо":
                lastName.setLabel("Фамилия подписанта");
                firstName.setLabel("Имя подписанта");
                secondName.setLabel("Отчество подписанта");

                OOOForm.setItems("Общество с ограниченной ответственностью", "Акционерное общество",
                        "Публичное акционерное общество", "Иное");
                OOOForm.setAllowCustomValue(true);

                signatoryPosition.setItems("Генеральный директор", "Иное");
                signatoryPosition.setAllowCustomValue(true);

                proxyNumber.setEnabled(false);
                proxyDate.setEnabled(false);
                proxyDate.setPlaceholder("ДД.ММ.ГГГГ");

                formLayout.add(OOOForm, OOOName, taxPercentage, signatoryPosition, ITN,
                        lastName, firstName, secondName, proxyNumber, proxyDate);
                break;
            case "Самозанятый":
                selfemployedDate.setPlaceholder("ДД.ММ.ГГГГ");
                registrationCertificateDate.setPlaceholder("ДД.ММ.ГГГГ");

                formLayout.add(lastName, firstName, secondName, selfemployedDate,
                        registrationCertificateNumber, registrationCertificateDate, ITN);
                break;
            case "Индивидуальный предприниматель":
                formLayout.add(lastName, firstName, secondName, registrationNumber, ITN);
                break;
        }
    }

    private void clearForm() {
        binder.setBean(new SampleContractor());
        contractorTypes.setEnabled(true);
        contractorTypes.setValue("");
        lastName.setLabel("Фамилия");
        firstName.setLabel("Имя");
        secondName.setLabel("Отчество");
        formLayout.removeAll();
        formLayout.add(contractorTypes);
    }

    private Component createTitle() {
        return new H3("Данные о контрагенте");
    }

    private Component createFormLayout() {
        contractorTypes.setItems("Физическое лицо", "Юридическое лицо",
                "Индивидуальный предприниматель", "Самозанятый");
        contractorTypes.setAllowCustomValue(false);
        formLayout.add(contractorTypes);
        return formLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save);
        buttonLayout.add(cancel);
        return buttonLayout;
    }

    private void valueChangingWithCustomValues(ComboBox<String> comboBox) {
        comboBox.addValueChangeListener(e -> {
            if (e.getValue() != null && e.getValue().equals("Иное")) {
                comboBox.setPlaceholder("Иное");
                comboBox.setValue("");
            } else if (e.getValue() != null && e.getValue().equals("Генеральный директор")) {
                proxyNumber.setEnabled(false);
                proxyDate.setEnabled(false);
            }
        });

        comboBox.addCustomValueSetListener(e -> {
            comboBox.setValue(e.getDetail());
            if (comboBox.getLabel().equals("Форма юридического лица"))
                binder.getBean().setOOOForm(e.getDetail());
            else {
                proxyNumber.setEnabled(true);
                proxyDate.setEnabled(true);
                binder.getBean().setSignatoryPosition(e.getDetail());
            }
        });
    }
}
