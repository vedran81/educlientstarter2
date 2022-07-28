package com.educlient.views.mentors;

import com.educlient.data.entity.Mentor;
import com.educlient.data.service.MentorService;
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
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

@PageTitle("Mentors")
@Route(value = "mentor_list/:mentorID?/:action?(edit)", layout = MainLayout.class)
public class MentorsView extends Div implements BeforeEnterObserver {

    private final String MENTOR_ID = "mentorID";
    private final String MENTOR_EDIT_ROUTE_TEMPLATE = "mentor_list/%s/edit";

    private Grid<Mentor> grid = new Grid<>(Mentor.class, false);

    private TextField lastName;
    private TextField firstName;
    private TextField email;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Mentor> binder;

    private Mentor mentor;

    private final MentorService mentorService;

    @Autowired
    public MentorsView(MentorService mentorService) {
        this.mentorService = mentorService;
        addClassNames("mentors-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("lastName").setAutoWidth(true);
        grid.addColumn("firstName").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.setItems(query -> mentorService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(MENTOR_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(MentorsView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Mentor.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.mentor == null) {
                    this.mentor = new Mentor();
                }
                binder.writeBean(this.mentor);

                mentorService.update(this.mentor);
                clearForm();
                refreshGrid();
                Notification.show("Mentor details stored.");
                UI.getCurrent().navigate(MentorsView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the mentor details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> mentorId = event.getRouteParameters().get(MENTOR_ID).map(Long::parseLong);
        if (mentorId.isPresent()) {
            Optional<Mentor> mentorFromBackend = mentorService.get(mentorId.get());
            if (mentorFromBackend.isPresent()) {
                populateForm(mentorFromBackend.get());
            } else {
                Notification.show(String.format("The requested mentor was not found, ID = %s", mentorId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(MentorsView.class);
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
        lastName = new TextField("Last Name");
        firstName = new TextField("First Name");
        email = new TextField("Email");
        Component[] fields = new Component[]{lastName, firstName, email};

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

    private void populateForm(Mentor value) {
        this.mentor = value;
        binder.readBean(this.mentor);

    }
}
