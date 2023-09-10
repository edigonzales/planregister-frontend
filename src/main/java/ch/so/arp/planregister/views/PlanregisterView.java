package ch.so.arp.planregister.views;

import java.sql.Types;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

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
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility;

import ch.so.arp.planregister.Dokument;
import ch.so.arp.planregister.DokumentService;
import ch.so.arp.planregister.Gemeinde;
import ch.so.arp.planregister.GemeindeService;
import ch.so.arp.planregister.Planungsbehoerde;
import ch.so.arp.planregister.PlanungsbehoerdeService;
import ch.so.arp.planregister.Planungsinstrument;
import ch.so.arp.planregister.PlanungsinstrumentService;
import ch.so.arp.planregister.Predicate;
import ch.so.arp.planregister.Rechtsstatus;
import ch.so.arp.planregister.RechtsstatusService;

@PageTitle("Planregister - Amt für Raumplanung - Kanton Solothurn")
@Route(value = "hello")
@RouteAlias(value = "")
public class PlanregisterView extends VerticalLayout {
    private Grid<Dokument> grid = new Grid<>(Dokument.class, false); 
    private Filters filters;

    private DokumentService dokumentService;
    private GemeindeService gemeindeService;
    private PlanungsinstrumentService planungsinstrumentService;
    private RechtsstatusService rechtsstatusService;
    private PlanungsbehoerdeService planungsbehoerdeService;

    public PlanregisterView(DokumentService dokumentService, GemeindeService gemeindeService, 
            PlanungsinstrumentService planungsinstrumentService, RechtsstatusService rechtsstatusService,
            PlanungsbehoerdeService planungsbehoerdeService) {
        this.dokumentService = dokumentService;
        this.gemeindeService = gemeindeService;
        this.planungsinstrumentService = planungsinstrumentService;
        this.rechtsstatusService = rechtsstatusService;
        this.planungsbehoerdeService = planungsbehoerdeService;
                
        //addClassName("list-view-fooooXXXX"); 
        addClassNames("hello-world-view");

        setSizeFull();
//        configureGrid(); 

//        Div container = new Div();
//        container.setId("container-div");
        
        // TODO siehe adress form -> FormLayout
        filters = new Filters(() -> refreshGrid());
        VerticalLayout layout = new VerticalLayout(filters, configureGrid());
        //layout.setAlignItems(FlexComponent.Alignment.BASELINE);
        //VerticalLayout layout = new VerticalLayout(createMobileFilters(), filters, createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);

        add(createTitle(), layout);
        refreshGrid();
//        container.add(layout);
//        add(container);

        
        //add(getToolbar(), grid);
//        add(getToolbar());
//
//        updateList();
    }
    
    public class Filters extends HorizontalLayout /*implements Specification<SamplePerson>*/ {

        private final TextField fts = new TextField("Bezeichnung / Stichwort / RRB-Nr.");
        private final DatePicker startDate = new DatePicker("RRB-Datum");
        private final DatePicker endDate = new DatePicker();
        private final ComboBox<Gemeinde> municipality = new ComboBox<>("Gemeinde");
        private final ComboBox<Planungsinstrument> planningInstrument = new ComboBox<>("Planungsinstrument");
        private final ComboBox<Rechtsstatus> legalStatus = new ComboBox<>("Rechtsstatus");
        private final ComboBox<Planungsbehoerde> planningAuthority = new ComboBox<>("Planungsbehörde");
        private final Checkbox isPartOfLandUsePlanning = new Checkbox();

        public Filters(Runnable onSearch) {
            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM, LumoUtility.BoxSizing.BORDER);
           
            fts.setAutocomplete(Autocomplete.OFF);
            
            municipality.setItems(gemeindeService.findAllMunicipalities());
            municipality.setItemLabelGenerator(Gemeinde::name);
            
            planningInstrument.setItems(planungsinstrumentService.findAllPlanningInstruments()); 
            planningInstrument.setItemLabelGenerator(Planungsinstrument::name);
            
            legalStatus.setItems(rechtsstatusService.findAllLegalStatus());
            legalStatus.setItemLabelGenerator(Rechtsstatus::name);
            
            planningAuthority.setItems(planungsbehoerdeService.findAllPlanningAuthorities());
            planningAuthority.setItemLabelGenerator(Planungsbehoerde::name);
            
            isPartOfLandUsePlanning.setLabel("Bestandteil der Ortsplanung");
                        
            // Action on components value change / key presses
            fts.addKeyDownListener(Key.ENTER, event -> {
                if (!fts.isEmpty()) {
                    onSearch.run();                
                }
            });

            startDate.addValueChangeListener(event -> {
                if (event.getValue() != null) {
                    onSearch.run();                
                }
            });

            endDate.addValueChangeListener(event -> {
                if (event.getValue() != null) {
                    onSearch.run();                
                }
            });
            
            municipality.addValueChangeListener(event -> {
                if (event.getValue() != null) {
                    onSearch.run();
                }
            });

            planningInstrument.addValueChangeListener(event -> {
                if (event.getValue() != null) {
                    onSearch.run();
                }
            });
            
            legalStatus.addValueChangeListener(event -> {
                if (event.getValue() != null) {
                    onSearch.run();
                }
            });
            
            planningAuthority.addValueChangeListener(event -> {
                if (event.getValue() != null) {
                    onSearch.run();
                }
            });     
            
            isPartOfLandUsePlanning.addValueChangeListener(event -> {
                if (event.getValue() != null) {
                    onSearch.run();
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
        
        public List<Predicate> getPredicates() {
            List<Predicate> predicateList = new ArrayList<>();
            
            if (!fts.isEmpty()) {
                String[] tokens = fts.getValue().split(" ");
                for (String token : tokens) {
                    predicateList.add(new Predicate("bezeichnung", "%"+token+"%", Types.VARCHAR, "ILIKE"));
                    
                    // TODO
                    // Das geht noch nicht. Es resultieren AND, es müssen aber OR sein
                    // Sowas wie ein NestedPredicate?
//                    predicateList.add(new Predicate("rrb_nr", "%"+token+"%", Types.VARCHAR, "ILIKE"));
//                    predicateList.add(new Predicate("gemeinde", "%"+token+"%", Types.VARCHAR, "ILIKE"));
//                    predicateList.add(new Predicate("planungsinstrument", "%"+token+"%", Types.VARCHAR, "ILIKE"));
//                    predicateList.add(new Predicate("rechtsstatus", "%"+token+"%", Types.VARCHAR, "ILIKE"));
//                    predicateList.add(new Predicate("planungsbehoerde", "%"+token+"%", Types.VARCHAR, "ILIKE"));
                }
            }
            
            if (!startDate.isEmpty()) {
                predicateList.add(new Predicate("rrb_datum", startDate.getValue().toString(), Types.DATE, ">"));
            }

            if (!endDate.isEmpty()) {
                predicateList.add(new Predicate("rrb_datum", endDate.getValue().toString(), Types.DATE, "<"));
            }
            
            if (!municipality.isEmpty()) {
                predicateList.add(new Predicate("gemeinde", municipality.getValue().name(), Types.VARCHAR, "="));
            }
            
            if (!planningInstrument.isEmpty()) {
                predicateList.add(new Predicate("planungsinstrument", "%"+planningInstrument.getValue().name()+"%", Types.VARCHAR, "ILIKE"));
            }

            if (!legalStatus.isEmpty()) {
                predicateList.add(new Predicate("rechtsstatus", legalStatus.getValue().name(), Types.VARCHAR, "="));
            }
            
            if (!planningAuthority.isEmpty()) {
                predicateList.add(new Predicate("planungsbehoerde", planningAuthority.getValue().name(), Types.VARCHAR, "="));
            }
            
            if (isPartOfLandUsePlanning.getValue()) {
                // TODO: Scheint zu funktionieren. Sieht auf den ersten Blick abenteuerlich aus.
                predicateList.add(new Predicate("aktuelle_ortsplanung", "TRUE", Types.BOOLEAN, "="));
            }
            
            return predicateList;
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
    
    private Component createTitle() {
        H1 title = new H1("Planregister");
        title.addClassName("title-h1");
        return title;
    }
    
    private Grid<Dokument> configureGrid() {
        grid.addClassNames("document-grid");
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_NO_BORDER);

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
        grid.addColumn(Dokument::bezeichnung).setHeader("Bezeichnung").setSortable(true).setAutoWidth(true).setFlexGrow(2);
        grid.addColumn(Dokument::gemeinde).setHeader("Gemeinde").setSortable(true).setWidth("10%");
        //grid.addColumn(Dokument::rechtsstatus).setHeader("Rechtsstatus").setSortable(true).setWidth("5%");
        grid.addColumn(Dokument::rechtsstatus).setHeader("Rechtsstatus").setSortable(true).setWidth("5%");
        grid.addColumn(Dokument::planungsbehoerde).setHeader("Planungsbehörde").setSortable(true).setWidth("5%");
        
        grid.setItemDetailsRenderer(createDocumentDetailsRenderer());

        return grid;
        //grid.getColumns().forEach(col -> col.setAutoWidth(true)); 
    }
        
    private void refreshGrid() {
        //System.out.println(filters.getPredicates());
        grid.setItems(dokumentService.findDocuments(filters.getPredicates()));
        //grid.getDataProvider().refreshAll();
    }
    
    private Renderer<Dokument> createDocumentDetailsRenderer() {
        return new ComponentRenderer<>(DocumentDetailLayout::new,    
                DocumentDetailLayout::setDocument);
    }

    public class DocumentDetailLayout extends FormLayout {
        private final TextField municipalityField = new TextField("Gemeinde");
        private final TextField planningInstrumentField = new TextField("Planungsinstrument");
        private final TextField descriptionField = new TextField("Bezeichnung");
        private final TextField rrbDateField = new TextField("RRB-Datum");
        private final TextField rrbNumberField = new TextField("RRB-Nr.");
        private final TextField inForceSinceField = new TextField("Rechtskräftig ab");
        private final TextField legalStatusField = new TextField("Rechtsstatus");
        private final TextField planningAuthorityField = new TextField("Planungsbehörde");
        private final TextField responsibleOfficeField = new TextField("Zuständiges Amt");
        private final TextField documentLinkField = new TextField("Dokumente");
        private final TextField mapLinkField = new TextField("Karte");

        private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        
        public DocumentDetailLayout() {   
            setResponsiveSteps(new ResponsiveStep("0", 1));                
                Stream.of(municipalityField, planningInstrumentField, descriptionField, rrbDateField, 
                        rrbNumberField, inForceSinceField, legalStatusField, planningAuthorityField, 
                        responsibleOfficeField, documentLinkField, mapLinkField).forEach(field -> {
                            field.setReadOnly(true);
                            add(field);
                        });            
                setResponsiveSteps(new ResponsiveStep("0", 2));

                documentLinkField.setPrefixComponent(VaadinIcon.FILE.create());
                documentLinkField.getElement().addEventListener("click", event -> {
                    getUI().ifPresent(ui -> ui.getPage().open(documentLinkField.getValue()));
                });

                mapLinkField.setPrefixComponent(VaadinIcon.MAP_MARKER.create());
                mapLinkField.getElement().addEventListener("click", event -> {
                    getUI().ifPresent(ui -> ui.getPage().open(mapLinkField.getValue()));
                });

        }

        public void setDocument(Dokument document) {
            municipalityField.setValue(document.gemeinde());
            planningInstrumentField.setValue(document.planungsinstrument());
            descriptionField.setValue(document.bezeichnung());
            rrbDateField.setValue(document.rrbDatum().format(dtf));
            rrbNumberField.setValue(document.rrbNr());
            inForceSinceField.setValue(document.rechtskraftAb().format(dtf));
            legalStatusField.setValue(document.rechtsstatus());
            planningAuthorityField.setValue(document.planungsbehoerde());
            responsibleOfficeField.setValue(document.zustaendigesAmt());
            documentLinkField.setValue(document.dokumentUrl().toString());
            mapLinkField.setValue(document.karteUrl().toString());

        }
    }


}
