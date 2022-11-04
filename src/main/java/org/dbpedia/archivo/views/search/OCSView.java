package org.dbpedia.archivo.views.search;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import org.dbpedia.archivo.lookup.LookupFrontendData;
import org.dbpedia.archivo.lookup.LookupObject;
import org.dbpedia.archivo.lookup.LookupRequester;
import org.dbpedia.archivo.utils.MossUtilityFunctions;
import org.dbpedia.archivo.views.main.MainView;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Route(value = "search")
@RouteAlias(value = "")
@PageTitle("Search")
//@CssImport("./views/about/about-view.css")
public class OCSView extends Div implements BeforeEnterObserver {

    List<LookupFrontendData> suggestions = new ArrayList<>();
    Grid<LookupFrontendData> suggestion_grid = new Grid<>();

    Div versionDiv = new Div();


    public OCSView() {
        addClassName("about-view");

        //Headline
        add(new H1("Search Ontologies"));

        suggestion_grid.setItems(suggestions);
        suggestion_grid.setWidth("50%");

        TextField search_field = new TextField();
        search_field.setWidth("50%");
        search_field.addValueChangeListener(event -> {
            suggestions.clear();
            updateSuggestions(event.getValue());
            suggestion_grid.getDataProvider().refreshAll();
        });

        search_field.setValueChangeMode(ValueChangeMode.LAZY);
        search_field.setPlaceholder("Search for annotation terms (Classes and Properties)");
        search_field.setMinWidth("50%");
        search_field.setClearButtonVisible(true);

        suggestion_grid.addColumn(new ComponentRenderer<>(frontend_data -> {
            HorizontalLayout cell = new HorizontalLayout();
            cell.add(frontend_data.generate_html_repr());
            return cell;
        })).setHeader("Suggestions");

        Label inputLabel = new Label("annotation url");

        TextField inputTF = new TextField();
        inputTF.setPlaceholder("http://www.w3.org/2002/07/owl#Thing");
        inputTF.setValue("http://www.w3.org/2002/07/owl#Thing");
        inputTF.setWidth("100%");
        // TODO [ENTER] key


        HorizontalLayout grids = new HorizontalLayout(suggestion_grid);
        grids.setWidth("100%");

        HorizontalLayout inputs = new HorizontalLayout(search_field);
        inputs.setWidth("100%");


        VerticalLayout vl = new VerticalLayout(
                new Label("Paste Databus file identifier of the file you like to annotate"),
                inputs,
                grids,
                versionDiv
        );

        add(vl);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        Location location = beforeEnterEvent.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        Map<String, List<String>> parametersMap = queryParameters.getParameters();
        List<String> defaultDatabusFiles = Collections.singletonList("https://databus.dbpedia.org/jj-author/mastr/bnetza-mastr/01.04.01/bnetza-mastr_rli_type=wind.nt.gz");
        String databusFile = parametersMap.getOrDefault("dfid", defaultDatabusFiles).get(0);
    }

    private void updateSuggestions(String query) {
        suggestions.clear();
        List<LookupObject> search_result;
        try {
            search_result = LookupRequester.getResult(query);
        } catch (Exception e) {
            System.out.println("Exception:" + e);
            search_result = new ArrayList<>();
        }


        for (LookupObject lo : search_result) {
            try {
                String label = MossUtilityFunctions.getValFromArray(lo.getLabel());
                String definition = MossUtilityFunctions.getValFromArray(lo.getDefinition());
                String comment = MossUtilityFunctions.getValFromArray(lo.getComment());
                suggestions.add(new LookupFrontendData(lo.getResource()[0], label, definition, comment));
            } catch (Exception e) {
                System.out.println("Exception:" + e);
            }
        }
    }
}