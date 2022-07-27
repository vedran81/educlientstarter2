package com.educlient.views.subjects;

import com.educlient.data.entity.Subject;
import com.educlient.data.service.SubjectService;
import com.educlient.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Subjects")
@Route(value = "Subject_list/:subjectID?/:action?(edit)", layout = MainLayout.class)
public class SubjectsView extends Div implements BeforeEnterObserver {

    private final String SUBJECT_ID = "subjectID";
    private final String SUBJECT_EDIT_ROUTE_TEMPLATE = "Subject_list/%s/edit";

    private Grid<Subject> grid = new Grid<>(Subject.class, false);

    private TextField name;
    private TextField year;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Subject> binder;

    private Subject subject;

    private final SubjectService subjectService;

    @Autowired
    public SubjectsView(SubjectService subjectService) {
        this.subjectService = subjectService;
        addClassNames("subjects-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("year").setAutoWidth(true);
        grid.setItems(query -> subjectService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SUBJECT_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(SubjectsView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Subject.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(year).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("year");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.subject == null) {
                    this.subject = new Subject();
                }
                binder.writeBean(this.subject);

                subjectService.update(this.subject);
                clearForm();
                refreshGrid();
                Notification.show("Subject details stored.");
                UI.getCurrent().navigate(SubjectsView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the subject details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> subjectId = event.getRouteParameters().get(SUBJECT_ID).map(UUID::fromString);
        if (subjectId.isPresent()) {
            Optional<Subject> subjectFromBackend = subjectService.get(subjectId.get());
            if (subjectFromBackend.isPresent()) {
                populateForm(subjectFromBackend.get());
            } else {
                Notification.show(String.format("The requested subject was not found, ID = %s", subjectId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(SubjectsView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        name = new TextField("Name");
        year = new TextField("Year");
        Component[] fields = new Component[]{name, year};

        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Subject value) {
        this.subject = value;
        binder.readBean(this.subject);

    }
}
