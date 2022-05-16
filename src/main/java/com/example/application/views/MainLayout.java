package com.example.application.views;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.application.data.report.FileProvider;
import com.example.application.data.report.GenerateReport;
import com.example.application.database.DBManager;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.router.PageTitle;
import com.example.application.views.contractorsform.ContractorsFormView;
import com.example.application.views.listofcontractors.ListofContractorsView;
import com.example.application.views.reportform.ReportFormView;
import com.example.application.views.listofreports.ListofReportsView;
import com.example.application.views.reportgeneration.ReportGenerationView;
import com.vaadin.flow.component.avatar.Avatar;
import com.example.application.data.entity.User;
import com.example.application.security.AuthenticatedUser;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.component.contextmenu.ContextMenu;

@PageTitle("Main")
public class MainLayout extends AppLayout {

    public static class MenuItemInfo {

        private String text;
        private String iconClass;
        private Class<? extends Component> view;

        public MenuItemInfo(String text, String iconClass, Class<? extends Component> view) {
            this.text = text;
            this.iconClass = iconClass;
            this.view = view;
        }

        public String getText() {
            return text;
        }

        public String getIconClass() {
            return iconClass;
        }

        public Class<? extends Component> getView() {
            return view;
        }

    }

    private H1 viewTitle;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        if (accessChecker.hasAccess(ContractorsFormView.class)) {
            addToNavbar(createDropboxButton());
            addToNavbar(createLogButton());
        }
        addToDrawer(createDrawerContent());
    }

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassName("text-secondary");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames("m-0", "text-l");

        Header header = new Header(toggle, viewTitle);
        header.addClassNames("bg-base", "border-b", "border-contrast-10", "box-border", "flex", "h-xl", "items-center",
                "w-full");
        return header;
    }

    private Component createDrawerContent() {
        H2 appName = new H2("Skillbox");
        appName.addClassNames("flex", "items-center", "h-xl", "m-0", "px-m", "text-m");

        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName,
                createNavigation(), createFooter());
        section.addClassNames("flex", "flex-col", "items-stretch", "max-h-full", "min-h-full");
        return section;
    }

    private Nav createNavigation() {
        Nav nav = new Nav();
        nav.addClassNames("border-b", "border-contrast-10", "flex-grow", "overflow-auto");
        nav.getElement().setAttribute("aria-labelledby", "views");

        H3 views = new H3("Views");
        views.addClassNames("flex", "h-m", "items-center", "mx-m", "my-0", "text-s", "text-tertiary");
        views.setId("views");

        UnorderedList list = new UnorderedList();
        list.addClassNames("list-none", "m-0", "p-0");
        nav.add(list);

        for (RouterLink link : createLinks()) {
            ListItem item = new ListItem(link);
            list.add(item);
        }
        return nav;
    }

    private List<RouterLink> createLinks() {
        MenuItemInfo[] menuItems = new MenuItemInfo[]{ //
                new MenuItemInfo("Регистрация контрагента", "la la-user", ContractorsFormView.class), //

                new MenuItemInfo("Список контрагентов", "la la-columns", ListofContractorsView.class), //

                new MenuItemInfo("Регистрация отчёта", "la la-file", ReportFormView.class), //

                new MenuItemInfo("Список отчётов", "la la-columns", ListofReportsView.class), //

                new MenuItemInfo("Формирование отчёта", "la la-download", ReportGenerationView.class), //

        };

        List<RouterLink> links = new ArrayList<>();
        for (MenuItemInfo menuItemInfo : menuItems) {
            if (accessChecker.hasAccess(menuItemInfo.getView())) {
                links.add(createLink(menuItemInfo));
            }
        }
        return links;
    }

    private static RouterLink createLink(MenuItemInfo menuItemInfo) {
        RouterLink link = new RouterLink();
        link.addClassNames("flex", "mx-s", "p-s", "relative", "text-secondary");
        link.setRoute(menuItemInfo.getView());

        Span icon = new Span();
        icon.addClassNames("me-s", "text-l");
        if (!menuItemInfo.getIconClass().isEmpty()) {
            icon.addClassNames(menuItemInfo.getIconClass());
        }

        Span text = new Span(menuItemInfo.getText());
        text.addClassNames("font-medium", "text-s");

        link.add(icon, text);
        return link;
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("flex", "items-center", "my-s", "px-m", "py-xs");

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            GenerateReport.setAuthenticatedUser(user);

            Avatar avatar = new Avatar(user.getName(), user.getProfilePictureUrl());
            avatar.addClassNames("me-xs");

            ContextMenu userMenu = new ContextMenu(avatar);
            userMenu.setOpenOnClick(true);
            userMenu.addItem("Logout", e -> {
                authenticatedUser.logout();
            });

            Span name = new Span(user.getName());
            name.addClassNames("font-medium", "text-s", "text-secondary");

            layout.add(avatar, name);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

    private Component createDropboxButton() {
        Button uploadButton = new Button();
        uploadButton.setIcon(VaadinIcon.FILE_PROCESS.create());
        uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        uploadButton.getElement().getStyle().set("margin-right", "10px");

        uploadButton.addClickListener(e -> createUploadDialog().open());
        return uploadButton;
    }

    private Dialog createUploadDialog() {
        Dialog dialog = new Dialog();
        dialog.getElement().setAttribute("aria-label", "Загрузить файл .xlsx");
        dialog.add(createUploadLayout());
        dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        dialog.setMaxWidth("400px");

        return dialog;
    }

    private Component createUploadLayout() {
        VerticalLayout uploadLayout = new VerticalLayout();

        H1 dialogTitle = new H1("Dropbox");
        dialogTitle.getStyle().set("margin", "var(--lumo-space-m) 0").set("font-size", "2em")
                .set("font-weight", "bold");
        Header header = new Header(dialogTitle);

        Paragraph description = new Paragraph("Выберите файл .xlsx размера не более 1 MB " +
                "или перетащите его в область");

        FileProvider provider = new FileProvider();
        String filePath = "/Отчёты по продажам и пакетам.xlsx";

        Upload upload = createUploadArea(filePath, provider);
        Button showTableButton = createButtonWithLink(
                "Перейти в отчёт по продажам и пакетам", filePath, provider);
        Button showReports = createButtonWithLink(
                "Перейти к итоговым отчётам", "/Сформированные отчёты", provider);

        uploadLayout.add(header, new H3("Загрузка .xlsx"), description,
                upload, new H3("Просмотр файлов"), showTableButton, showReports);
        uploadLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        return uploadLayout;
    }

    private Upload createUploadArea(String path, FileProvider provider) {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setDropAllowed(true);
        upload.setMaxFiles(1);
        upload.setAutoUpload(true);
        upload.setAcceptedFileTypes(".xlsx", ".xls");

        Button uploadButton = new Button("Загрузить файл...");
        upload.setUploadButton(uploadButton);
        upload.addSucceededListener(event -> {
            provider.deleteFileFromDropbox(path);
            provider.uploadExcelToDropbox(buffer, path);
        });

        return upload;
    }

    private Button createButtonWithLink(String text, String path, FileProvider provider) {
        Button button = new Button(text);
        button.addClickListener(event -> {
            UI.getCurrent().getPage().open(provider
                    .createSharedLink(path), "_blank");
        });
        return button;
    }

    private Button createLogButton() {
        Button logButton = new Button();
        logButton.setIcon(VaadinIcon.RECORDS.create());
        logButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        logButton.getElement().getStyle().set("margin-right", "10px");

        logButton.addClickListener(e -> createLogDialog().open());
        return logButton;
    }

    private Dialog createLogDialog() {
        Dialog dialog = new Dialog();
        dialog.getElement().setAttribute("aria-label", "Logs");
        dialog.add(createLogLayout());
        dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        dialog.setMaxWidth("800px");
        dialog.setMaxHeight("500px");

        return dialog;
    }

    private Component createLogLayout() {
        VerticalLayout logLayout = new VerticalLayout();

        H1 dialogTitle = new H1("Logs");
        dialogTitle.getStyle().set("margin", "var(--lumo-space-m) 0").set("font-size", "2em")
                .set("font-weight", "bold");
        Header header = new Header(dialogTitle);

        DBManager.deleteRowFromLogs(LocalDate.now());
        List<String> logs = DBManager.getRowsFromLogsTable();

        logLayout.add(header);
        for (String l : logs)
        {
            Paragraph logText = new Paragraph();
            logText.setText(l);
            logLayout.add(logText);
        }

        logLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        return logLayout;
    }
}
