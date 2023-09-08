package ch.so.arp.planregister.views;

import java.util.Comparator;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility;

import ch.so.arp.planregister.Dokument;
import ch.so.arp.planregister.DokumentService;

@PageTitle("Planregister - Amt für Raumplanung - Kanton Solothurn")
@Route(value = "hello")
@RouteAlias(value = "")
public class PlanregisterView extends VerticalLayout {
    private Grid<Dokument> grid = new Grid<>(Dokument.class, false); 
    private TextField filterText = new TextField();
    private Filters filters;

    private DokumentService dokumentService;
//    private TextField name;
//    private Button sayHello;

    public PlanregisterView(DokumentService dokumentService) {
        this.dokumentService = dokumentService;
//        name = new TextField("Your name");
//        sayHello = new Button("Say hello");
//        sayHello.addClickListener(e -> {
//            Notification.show("Hello " + name.getValue());
//        });
//        sayHello.addClickShortcut(Key.ENTER);
//
//        setMargin(true);
//        setVerticalComponentAlignment(Alignment.END, name, sayHello);
//
//        add(name, sayHello);
        
        //addClassName("list-view-fooooXXXX"); 
        addClassNames("hello-world-view");

        setSizeFull();
//        configureGrid(); 

//        Div container = new Div();
//        container.setId("container-div");
        
        // TODO siehe adress form -> FormLayout
        filters = new Filters(() -> refreshGrid());
        VerticalLayout layout = new VerticalLayout(filters);
        //layout.setAlignItems(FlexComponent.Alignment.BASELINE);
        //VerticalLayout layout = new VerticalLayout(createMobileFilters(), filters, createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);

        add(layout);
//        container.add(layout);
//        add(container);

        
        //add(getToolbar(), grid);
//        add(getToolbar());
//
//        updateList();
    }
    
    public static class Filters extends HorizontalLayout /*implements Specification<SamplePerson>*/ {

        private final TextField fts = new TextField("Bezeichnung / Stichwort / RRB-Nr.");
        private final DatePicker startDate = new DatePicker("RRB-Datum");
        private final DatePicker endDate = new DatePicker();
        private final ComboBox<String> municipality = new ComboBox<String>("Gemeinde");
        private final ComboBox<String> planningInstrument = new ComboBox<String>("Planungsinstrument");
        private final ComboBox<String> legalStatus = new ComboBox<String>("Rechtsstatus");
        private final ComboBox<String> planningAuthority = new ComboBox<String>("Planungsbehörde");
        private final Checkbox isPartOfLandUsePlanning = new Checkbox();

        public Filters(Runnable onSearch) {
            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);
            //fts.setPlaceholder("First or last name");
            
            fts.setAutocomplete(Autocomplete.OFF);

            isPartOfLandUsePlanning.setLabel("Bestandteil der Ortsplanung");
            
//            occupations.setItems("Insurance Clerk", "Mortarman", "Beer Coil Cleaner", "Scale Attendant");
//
//            roles.setItems("Worker", "Supervisor", "Manager", "External");
//            roles.addClassName("double-width");

            // Action on enter
            // TODO:
            // - Soll das überhaupt angeboten werden?
            // - Wird bissle mühsam, weil das Reseten das Ereignis
            // auch wieder auslöst.
            fts.addKeyDownListener(Key.ENTER, event -> {
                System.out.println("run...");
            });

            startDate.addValueChangeListener(event -> {
                // TODO: 
                // - Verhindern, dass nach dem Drücken des Reset-Buttons auch nochmals die Tabelle
                // gefreshed wird.
                // - Anpassen wahrscheinlich an den ersten Werte in der ComboBox (bei normalen ComboBoxen).
                if (event.getValue() != null) {
                    System.out.println("startDate run");                    
                }
            });
                        
            
            // Action buttons
            Button resetBtn = new Button("Reset");
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(e -> {
                fts.clear();
                startDate.clear();
                endDate.clear();
                municipality.clear();
                planningInstrument.clear();
                legalStatus.clear();
                planningAuthority.clear();
                isPartOfLandUsePlanning.clear();
                onSearch.run();
            });

            Button searchBtn = new Button("Search");
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            this.setAlignItems(FlexComponent.Alignment.BASELINE);
            
            add(fts, createDateRangeFilter(), municipality, planningInstrument, legalStatus, planningAuthority, isPartOfLandUsePlanning, actions);

        }
        
        private Component createDateRangeFilter() {
            startDate.setPlaceholder("von");
            endDate.setPlaceholder("bis");
        
            // For screen readers
            startDate.setAriaLabel("Datum von");
            endDate.setAriaLabel("Datum bis");
        
            FlexLayout dateRangeComponent = new FlexLayout(startDate, new Text(" – "), endDate);
            dateRangeComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
            dateRangeComponent.addClassName(LumoUtility.Gap.XSMALL);
        
            return dateRangeComponent;
        }
    }
    
    
    private void configureGrid() {
        grid.addClassNames("document-grid");
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        //grid.getStyle().set("line-height", "120px");
        //grid.getStyle().set("--lumo-space-l", "0");

        
        grid.setSizeFull();
        
        grid.addColumn(new LocalDateRenderer<>(Dokument::rrbDatum, "dd.MM.YYYY"))
        .setSortable(true)
        .setComparator(new Comparator<Dokument>() {
            @Override
            public int compare(Dokument d1, Dokument d2) {
                if (d1.rrbDatum() == null && d2.rrbDatum() == null) return 0;
                if (d1.rrbDatum() == null) return -1;
                if (d2.rrbDatum() == null) return 1;

                return d1.rrbDatum().compareTo(d2.rrbDatum());
            }
        })
        .setHeader("RRB-Datum").setWidth("5%");        
        grid.addColumn(Dokument::rrbNr).setHeader("RRB-Nr.").setSortable(true).setWidth("5%");
        grid.addColumn(Dokument::planungsinstrument).setHeader("Planungsinstrument").setSortable(true).setWidth("10%");
        grid.addColumn(Dokument::bezeichnung).setHeader("Bezeichnung").setSortable(true).setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(Dokument::gemeinde).setHeader("Gemeinde").setSortable(true).setWidth("10%");
        grid.addColumn(Dokument::rechtsstatus).setHeader("Rechtsstatus").setSortable(true).setWidth("5%");
        grid.addColumn(Dokument::planungsbehoerde).setHeader("Planungsbehörde").setSortable(true).setWidth("5%");
        
        //grid.getColumns().forEach(col -> col.setAutoWidth(true)); 
        
        
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY); 

        Button addContactButton = new Button("Add contact");

        var toolbar = new HorizontalLayout(filterText, addContactButton); 
        toolbar.addClassName("toolbar"); 
        return toolbar;

//        filterText.setPlaceholder("Filter by name...");
//        filterText.setClearButtonVisible(true);
//        filterText.setValueChangeMode(ValueChangeMode.LAZY); 
//        filterText.addValueChangeListener(e -> {
//            System.out.println(e.getValue());
////            organisationFilter.setSearchTerm(e.getValue());
////            filterDataProvider.setFilter(organisationFilter);
//        });
//        
//        Button addContactButton = new Button("Add contact");
//
//        var toolbar = new HorizontalLayout(filterText, addContactButton); 
//        toolbar.addClassName("toolbar"); 
//        return toolbar;
    }
    
    private void updateList() { 
        grid.setItems(dokumentService.findAllDocuments(filterText.getValue()));
//        dokumentService.findAllDocuments(filterText.getValue());
    }
    
    private void refreshGrid() {
        System.out.println("refreshGrid");
        //grid.getDataProvider().refreshAll();
    }

}
