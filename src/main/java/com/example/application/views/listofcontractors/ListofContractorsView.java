package com.example.application.views.listofcontractors;

import java.util.List;

import com.example.application.database.DBManager;
import com.example.application.data.entity.SampleContractor;

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

@PageTitle("Список контрагентов")
@Route(value = "list-of-contactors/:sampleContractorID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("admin")
@Uses(Icon.class)
public class ListofContractorsView extends Div {

    private final Grid<SampleContractor> grid = new Grid<>(SampleContractor.class, false);

    private List<SampleContractor> contractors = DBManager.getRowsFromContractorsTable();

    public ListofContractorsView() {
        addClassNames("listof-contractors-view", "flex", "flex-col", "h-full");

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);

        add(splitLayout);
        configureGrid();
    }

    private void configureGrid() {
        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, contractor) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_TERTIARY);
                    button.addClickListener(e -> this.removeContractor(contractor));
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                }));
        grid.addColumn("lastName").setHeader("Фамилия").setAutoWidth(true);
        grid.addColumn("firstName").setHeader("Имя").setAutoWidth(true);
        grid.addColumn("secondName").setHeader("Отчество").setAutoWidth(true);
        grid.addColumn("contractorType").setHeader("Тип контрагента").setAutoWidth(true);
        grid.addColumn("OOOForm").setHeader("Форма юридического лица").setAutoWidth(true);
        grid.addColumn("OOOName").setHeader("Наименование юридического лица").setAutoWidth(true);
        grid.addColumn("taxPercentage").setHeader("Ставка по налогам").setAutoWidth(true);
        grid.addColumn("signatoryPosition").setHeader("Должность подписанта").setAutoWidth(true);
        grid.addColumn("selfemployedDate").setHeader("Дата постановки самозанятым").setAutoWidth(true);
        grid.addColumn("registrationCertificateNumber").setHeader("Номер справки постановки на учет").setAutoWidth(true);
        grid.addColumn("registrationCertificateDate").setHeader("Дата справки постановки на учет").setAutoWidth(true);
        grid.addColumn("registrationNumber").setHeader("ОГРНИП").setAutoWidth(true);
        grid.addColumn("ITN").setHeader("ИНН").setAutoWidth(true);
        grid.addColumn("proxyNumber").setHeader("Номер доверенности").setAutoWidth(true);
        grid.addColumn("proxyDate").setHeader("Дата получения доверенности").setAutoWidth(true);

        grid.setItems(contractors);

        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();
    }

    private void removeContractor(SampleContractor contractor) {
        if (contractor == null)
            return;
        DBManager.deleteRowFromContractors(contractor.getID());
        contractors = DBManager.getRowsFromContractorsTable();
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
        grid.setItems(contractors);
        grid.getDataProvider().refreshAll();
    }
}
