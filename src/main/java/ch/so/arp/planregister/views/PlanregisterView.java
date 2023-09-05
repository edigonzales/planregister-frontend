package ch.so.arp.planregister.views;

import java.util.Comparator;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import ch.so.arp.planregister.Dokument;
import ch.so.arp.planregister.DokumentService;

@PageTitle("Planregister - Amt für Raumplanung - Kanton Solothurn")
@Route(value = "hello")
@RouteAlias(value = "")
public class PlanregisterView extends VerticalLayout {
    private Grid<Dokument> grid = new Grid<>(Dokument.class, false); 
    private TextField filterText = new TextField();

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
        
        addClassName("list-view"); 
        setSizeFull();
        configureGrid(); 

        add(getToolbar(), grid);

        updateList();
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
}
