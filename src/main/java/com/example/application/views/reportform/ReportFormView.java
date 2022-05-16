package com.example.application.views.reportform;

import com.example.application.database.DBManager;
import com.example.application.data.entity.SampleContractor;
import com.example.application.data.entity.SampleReport;
import com.example.application.data.service.SampleReportService;
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
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@PageTitle("Регистрация отчёта")
@Route(value = "report-form", layout = MainLayout.class)
@RolesAllowed("admin")
@Uses(Icon.class)
public class ReportFormView extends Div {

    private final ComboBox<String> reportModel = new ComboBox<>("Модель отчёта");
    private final MultiselectComboBox<String> contractorsRegistered = new MultiselectComboBox<>("Контрагенты");
    private final TextField courseCode = new TextField("Номер курса");
    private final TextField courseName = new TextField("Название курса");
    private final TextField courseDirection = new TextField("Направление курса");
    private final MultiselectComboBox<String> courseObjects = new MultiselectComboBox<>("Предмет договора");
    private final TextField royaltyPercentage = new TextField("Ставка по роялти");
    private final TextField contractNumber = new TextField("Номер договора");
    private final DatePicker contractDate = new DatePicker("Дата договора");
    private final DatePicker transferDateOfRIA = new DatePicker("Дата передачи РИД");
    private final TextField k2 = new TextField("К2");

    private final Button cancel = new Button("Отмена");
    private final Button save = new Button("Сохранить");

    private final Binder<SampleReport> binder = new Binder(SampleReport.class);
    private final Validator validator = new Validator();

    private final Locale russianLocale = new Locale("ru", "RU");

    private static boolean tableCreated = false;

    public ReportFormView(SampleReportService reportService) {
        addClassName("report-form-view");

        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        setLocaleToDatePickers();

        binder.bindInstanceFields(this);
        clearForm();

        validateFields();
        setRequiredFields();
        addValueChangeListeners(reportService);
    }

    private void addValueChangeListeners(SampleReportService reportService) {
        if (!tableCreated) {
            DBManager.createReportsTable();
            tableCreated = true;
        }

        reportModel.addValueChangeListener(e -> {
            if (e.getValue() != null)
                k2.setEnabled(e.getValue().equals("Модель К2"));
        });

        contractorsRegistered.addValueChangeListener(e -> {
            if (!e.getValue().isEmpty())
                binder.getBean().setContractor(createListOfObjects(e.getValue()));
        });

        courseObjects.addSelectionListener(e -> {
            if (!e.getValue().isEmpty())
                binder.getBean().setCourseObject(createListOfObjects(e.getValue()));
        });

        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> {
            if (!checkRequiredFields())
                validator.showNotificationForRequiredFields();
            else {
                SampleReport report = binder.getBean();
                reportService.update(report);

                DBManager.insertRowIntoReports(binder);
                Notification.show("Информация об отчёте сохранена");
                clearForm();
            }
        });
    }

    private void validateFields() {
        validator.setBinderReport(binder);

        validator.validateNumberFields(courseCode);

        validator.validateIntegerAndDouble(royaltyPercentage);
        validator.validateIntegerAndDouble(k2);

        validator.validateDates(contractDate);
        validator.validateDates(transferDateOfRIA);
    }

    private String createListOfObjects(Set<String> values) {
        StringBuilder objects = new StringBuilder();
        for (String obj : values)
            objects.append(obj).append(", ");
        return objects.substring(0, objects.length() - 2);
    }

    private void setRequiredFields() {
        contractorsRegistered.setRequired(true);
        courseCode.setRequired(true);
        courseName.setRequired(true);
        courseDirection.setRequired(true);
        courseObjects.setRequired(true);
        royaltyPercentage.setRequired(true);
        contractNumber.setRequired(true);
        contractDate.setRequired(true);
        transferDateOfRIA.setRequired(true);
        if (k2.isEnabled())
            k2.setRequired(true);
    }

    private boolean checkRequiredFields() {
        return !reportModel.getValue().equals("") &&
                !contractorsRegistered.getValue().isEmpty() &&
                !courseCode.getValue().equals("") &&
                !courseName.getValue().equals("") &&
                !courseDirection.getValue().equals("") &&
                !courseObjects.isEmpty() &&
                !royaltyPercentage.getValue().equals("") &&
                !contractNumber.getValue().equals("") &&
                !contractDate.isEmpty() &&
                !transferDateOfRIA.isEmpty() &&
                !(k2.isEnabled() && k2.getValue().equals(""));
    }

    private void setLocaleToDatePickers() {
        contractDate.setLocale(russianLocale);
        transferDateOfRIA.setLocale(russianLocale);
    }

    private void clearForm() {
        binder.setBean(new SampleReport());
        k2.setEnabled(false);
        contractorsRegistered.deselectAll();
        courseObjects.deselectAll();
    }

    private Component createTitle() {
        return new H3("Данные об отчёте");
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        reportModel.setItems("Чистая выручка", "Модель К2");
        courseObjects.setItems("Сценарий", "Материалы", "Программа", "Исполнение");

        List<SampleContractor> contractors = DBManager.getRowsFromContractorsTable();
        List<String> names = new ArrayList<>();
        for (SampleContractor c : contractors)
            names.add(c.getLastName() + " " + c.getFirstName() + " " + c.getSecondName());
        contractorsRegistered.setItems(names);

        contractDate.setPlaceholder("ДД.ММ.ГГГГ");
        transferDateOfRIA.setPlaceholder("ДД.ММ.ГГГГ");

        k2.setEnabled(false);

        formLayout.add(reportModel, courseCode, courseName, courseDirection, contractorsRegistered,
                royaltyPercentage, courseObjects, contractNumber, contractDate, transferDateOfRIA, k2);
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
}
