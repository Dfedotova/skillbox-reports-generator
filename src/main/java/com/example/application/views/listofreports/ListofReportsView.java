package com.example.application.views.listofreports;

import java.util.List;

import com.example.application.database.DBManager;
import com.example.application.data.entity.SampleReport;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.example.application.views.MainLayout;

import javax.annotation.security.RolesAllowed;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.dependency.Uses;

@PageTitle("Список отчётов")
@Route(value = "list-of-reports/:sampleReportID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("admin")
@Uses(Icon.class)
public class ListofReportsView extends Div {

    private final Grid<SampleReport> grid = new Grid<>(SampleReport.class, false);

    private List<SampleReport> reports = DBManager.getRowsFromReportsTable();

    public ListofReportsView() {
        addClassNames("listof-reports-view", "flex", "flex-col", "h-full");

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);

        add(splitLayout);
        configureGrid();
    }

    private void configureGrid() {
        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, report) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_TERTIARY);
                    button.addClickListener(e -> this.removeReport(report));
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                }));
        grid.addColumn("courseCode").setHeader("Номер курса").setAutoWidth(true);
        grid.addColumn("courseName").setHeader("Название курса").setWidth("250px").setResizable(true);
        grid.addColumn("courseDirection").setHeader("Направление курса").setWidth("250px").setResizable(true);
        grid.addColumn("contractor").setHeader("Контрагент").setWidth("250px").setResizable(true);
        grid.addColumn("courseObject").setHeader("Предмет договора").setWidth("250px").setResizable(true);
        grid.addColumn("royaltyPercentage").setHeader("Ставка по роялти").setAutoWidth(true);
        grid.addColumn("contractNumber").setHeader("Номер договора").setAutoWidth(true);
        grid.addColumn("contractDate").setHeader("Дата договора").setAutoWidth(true);
        grid.addColumn("transferDateOfRIA").setHeader("Дата передачи РИД").setAutoWidth(true);
        grid.addColumn("k2").setHeader("К2").setAutoWidth(true);

        grid.setItems(reports);

        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();
    }

    private void removeReport(SampleReport report) {
        if (report == null)
            return;
        DBManager.deleteRowFromReports(report.getID());
        reports = DBManager.getRowsFromReportsTable();
        this.refreshGrid();
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.setItems(reports);
        grid.getDataProvider().refreshAll();
    }
}
