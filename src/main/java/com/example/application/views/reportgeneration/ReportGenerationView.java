package com.example.application.views.reportgeneration;

import com.example.application.data.report.WordsConverter;
import com.example.application.database.DBManager;
import com.example.application.data.entity.SampleContractor;
import com.example.application.data.entity.SampleReport;
import com.example.application.data.report.FileProvider;
import com.example.application.data.report.GenerateReport;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.notification.Notification;

import javax.annotation.security.RolesAllowed;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.annotation.EnableVaadin;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.xmlbeans.XmlException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@PageTitle("Формирование отчёта")
@Route(value = "report-generation", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed({"user", "admin"})
@EnableVaadin({"com.example.application.views"})
@Uses(Icon.class)
public class ReportGenerationView extends Div {

    private final ComboBox<String> name = new ComboBox<>("Контрагент");
    private final ComboBox<String> courseName = new ComboBox<>("Название курса");
    private final RadioButtonGroup<String> choosePeriod = new RadioButtonGroup<>();

    private final DatePicker periodStart = new DatePicker("Отчётный период с");
    private final DatePicker periodEnd = new DatePicker("Отчётный период по");

    private final ComboBox<String> months = new ComboBox<>("Месяц");
    private final ComboBox<Integer> years = new ComboBox<>("Год");

    private final Button cancel = new Button("Отмена");
    private final Button loadButton = new Button("Сформировать отчёт");

    private final List<SampleContractor> contractors;
    private final List<SampleReport> reports;
    private SampleContractor searchedContractor;
    private SampleReport searchedReport;

    private List<String> monthsList;
    private int monthPicked;
    private int yearPicked;
    private LocalDate searchedPeriodStart;
    private LocalDate searchedPeriodEnd;

    private final FormLayout formLayout = new FormLayout();
    private final HorizontalLayout choosePeriodLayout = new HorizontalLayout();

    private final Locale russianLocale = new Locale("ru", "RU");

    public ReportGenerationView() {
        addClassName("report-generation-view");

        contractors = DBManager.getRowsFromContractorsTable();
        reports = DBManager.getRowsFromReportsTable();

        add(createTitle());
        add(createFormLayout());
        add(createHorizontalLayout());
        add(createButtonLayout());

        setLocaleToDatePickers();
        addValueChangeListeners();
    }

    private void addValueChangeListeners() {
        name.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                List<String> fullName = Arrays.asList(e.getValue().split(" "));

                for (SampleContractor contractor : contractors)
                    if (Objects.equals(contractor.getLastName(), fullName.get(0)) &&
                            Objects.equals(contractor.getFirstName(), fullName.get(1)) &&
                            Objects.equals(contractor.getSecondName(), fullName.get(2)))
                        searchedContractor = contractor;

                courseName.setItems(coursesForContractor(e.getValue()));
            }
        });

        courseName.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                List<String> courseDescription = Arrays.asList(e.getValue().split(" "));
                String courseCode = courseDescription.get(0).substring(1, courseDescription.get(0).length() - 1);
                for (SampleReport report : reports)
                    if (Objects.equals(report.getCourseCode(), courseCode))
                        searchedReport = report;
                periodStart.setMin(searchedReport.getTransferDateOfRIA());
                periodEnd.setMin(searchedReport.getTransferDateOfRIA());
            }
        });

        choosePeriod.addValueChangeListener(e -> {
            if (e.getValue().equals("Календарь")) {
                choosePeriodLayout.remove(months, years);
                choosePeriodLayout.add(periodStart, periodEnd);
            } else {
                choosePeriodLayout.remove(periodStart, periodEnd);
                choosePeriodLayout.add(months, years);
            }
        });

        periodStart.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                searchedPeriodStart = e.getValue();
                periodEnd.setMin(searchedPeriodStart);
                periodEnd.setMax(searchedPeriodStart.plusMonths(1).minusDays(searchedPeriodStart.getDayOfMonth()));
            }
        });

        periodEnd.addValueChangeListener(e -> {
            if (e.getValue() != null)
                searchedPeriodEnd = e.getValue();
        });

        months.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                for (int i = 0; i < monthsList.size(); i++)
                    if (e.getValue().equals(monthsList.get(i)))
                        monthPicked = i + 1;
                searchedPeriodStart = LocalDate.of(yearPicked, monthPicked, 1);
                searchedPeriodEnd = searchedPeriodStart.withDayOfMonth(searchedPeriodStart.lengthOfMonth());
            }
        });

        years.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                yearPicked = e.getValue();
                searchedPeriodStart = LocalDate.of(yearPicked, monthPicked, 1);
                searchedPeriodEnd = searchedPeriodStart.withDayOfMonth(searchedPeriodStart.lengthOfMonth());
            }
        });

        cancel.addClickListener(e -> clearForm());
        loadButton.addClickListener(e -> reportLoading());
    }

    private void setLocaleToDatePickers() {
        periodStart.setLocale(russianLocale);
        periodEnd.setLocale(russianLocale);
    }

    private void reportLoading() {
        GenerateReport generateReport = new GenerateReport(searchedContractor,
                searchedReport, searchedPeriodStart, searchedPeriodEnd);
        int state = 0;
        try {
            state = generateReport.createTemplateForContractor();
        } catch (InvalidFormatException | IOException | XmlException e) {
            e.printStackTrace();
        }

        if (state == 1) {
            showNotification();
        }
    }

    private void showNotification() {
        Notification notification = new Notification();
        notification.setPosition(Notification.Position.MIDDLE);

        Icon icon = VaadinIcon.CHECK_CIRCLE.create();
        icon.setColor("var(--lumo-success-color)");

        Div uploadSuccessful = new Div(new Text("Отчёт успешно сформирован"));

        FileProvider provider = new FileProvider();
        Button showReportButton = new Button("Посмотреть отчёт");
        showReportButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        showReportButton.getElement().getStyle().set("margin-left", "var(--lumo-space-xl)");
        showReportButton.addClickListener(event -> {
            UI.getCurrent().getPage().open(provider
                    .createSharedLink("/Сформированные отчёты/" + searchedContractor.getLastName() + " "
                            + WordsConverter.convertNumericMonthToString(searchedPeriodStart.getMonthValue()) + " "
                            + searchedReport.getCourseCode() + ".docx"), "_blank");
            notification.close();
        });

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> notification.close());

        HorizontalLayout layout = new HorizontalLayout(icon,
                uploadSuccessful, showReportButton, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.open();
    }

    public static void showErrorNotification() {
        Notification notification = new Notification();
        notification.setPosition(Notification.Position.MIDDLE);

        Icon icon = VaadinIcon.WARNING.create();
        icon.setColor("var(--lumo-error-color)");

        Div uploadSuccessful = new Div(new Text("По выбранному месяцу не может быть сформирован отчёт"));

        FileProvider provider = new FileProvider();

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> notification.close());

        HorizontalLayout layout = new HorizontalLayout(icon,
                uploadSuccessful, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.open();
    }

    private void clearForm() {
        periodStart.clear();
        periodEnd.clear();
        name.clear();
        courseName.clear();
        months.clear();
        years.clear();
        name.setItems(createListOfContractors());
        courseName.setItems(coursesForContractor(""));
    }

    private Component createTitle() {
        return new H3("Поиск");
    }

    private Component createFormLayout() {
        HorizontalLayout text = new HorizontalLayout();
        Paragraph paragraph = new Paragraph("* Коллеги, сообщаем вам, что для партнеров ООО Логомашина, АНО Горький Медиа, ООО Бета, " +
                "СЗ Зайцев Дмитрий Геннадьевич, МГУ, СЗ Марчук Анна Петровна, СЗ Орлов Пётр Сергеевич, ПАО Московская биржа, " +
                "ООО Креатив Пипл и ИП Глазырин Дмитрий Александрович документы заполняются вручную!");
        paragraph.getStyle().set("color", "red").set("font-size", "12px");
        text.add(paragraph);
        text.setWidth(formLayout.getWidth());

        name.setItems(createListOfContractors());
        courseName.setItems(coursesForContractor(""));

        choosePeriod.setLabel("Выбрать отчётный период");
        choosePeriod.setItems("Календарь", "Месяц/Год");

        formLayout.add(text, name, courseName, choosePeriod);
        formLayout.setColspan(text, 2);
        return formLayout;
    }

    private List<String> createListOfContractors() {
        List<String> names = new ArrayList<>();
        for (SampleContractor c : contractors)
            names.add(c.getLastName() + " " + c.getFirstName() + " " + c.getSecondName());
        return names;
    }

    private List<String> coursesForContractor(String fullName) {
        List<String> courses = new ArrayList<>();
        if (!fullName.equals("")) {
            for (SampleReport r : reports)
                if (r.getContractor().contains(fullName))
                    courses.add("(" + r.getCourseCode() + ") " + r.getCourseName());
        } else
            for (SampleReport r : reports)
                courses.add("(" + r.getCourseCode() + ") " + r.getCourseName());
        return courses;
    }

    private Component createHorizontalLayout() {
        choosePeriodLayout.addClassName("choose-period-layout");
        periodStart.setMaxWidth(200, Unit.PIXELS);
        periodEnd.setMaxWidth(200, Unit.PIXELS);
        periodStart.setPlaceholder("ДД.ММ.ГГГГ");
        periodEnd.setPlaceholder("ДД.ММ.ГГГГ");
        periodStart.setMin(LocalDate.of(2020, 1, 1));
        periodEnd.setMin(LocalDate.of(2020, 1, 1));

        monthsList = Arrays.asList("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь");
        months.setItems(monthsList);

        LocalDate now = LocalDate.now(ZoneId.systemDefault());
        List<Integer> yearsList = IntStream.range(2020, now.getYear() + 9)
                .boxed().collect(Collectors.toList());
        years.setItems(yearsList);

        months.setMaxWidth(200, Unit.PIXELS);
        years.setMaxWidth(200, Unit.PIXELS);

        choosePeriodLayout.add(periodStart, periodEnd);
        return choosePeriodLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        loadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(loadButton);
        buttonLayout.add(cancel);
        return buttonLayout;
    }
}
