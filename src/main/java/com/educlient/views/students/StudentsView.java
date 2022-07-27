package com.educlient.views.students;

import com.educlient.data.entity.Student;
import com.educlient.data.service.StudentService;
import com.educlient.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
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

@PageTitle("Students")
@Route(value = "student_list/:studentID?/:action?(edit)", layout = MainLayout.class)
public class StudentsView extends Div implements BeforeEnterObserver {

    private final String STUDENT_ID = "studentID";
    private final String STUDENT_EDIT_ROUTE_TEMPLATE = "student_list/%s/edit";

    private Grid<Student> grid = new Grid<>(Student.class, false);

    private TextField lastName;
    private TextField firstName;
    private TextField email;
    private DatePicker dateOfBirth;
    private TextField status;
    private TextField studyYear;
    private TextField mentorId;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Student> binder;

    private Student student;

    private final StudentService studentService;

    @Autowired
    public StudentsView(StudentService studentService) {
        this.studentService = studentService;
        addClassNames("students-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("lastName").setAutoWidth(true);
        grid.addColumn("firstName").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("dateOfBirth").setAutoWidth(true);
        grid.addColumn("status").setAutoWidth(true);
        grid.addColumn("studyYear").setAutoWidth(true);
        grid.addColumn("mentorId").setAutoWidth(true);
        grid.setItems(query -> studentService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(STUDENT_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(StudentsView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Student.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(studyYear).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("studyYear");
        binder.forField(mentorId).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("mentorId");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.student == null) {
                    this.student = new Student();
                }
                binder.writeBean(this.student);

                studentService.update(this.student);
                clearForm();
                refreshGrid();
                Notification.show("Student details stored.");
                UI.getCurrent().navigate(StudentsView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the student details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> studentId = event.getRouteParameters().get(STUDENT_ID).map(UUID::fromString);
        if (studentId.isPresent()) {
            Optional<Student> studentFromBackend = studentService.get(studentId.get());
            if (studentFromBackend.isPresent()) {
                populateForm(studentFromBackend.get());
            } else {
                Notification.show(String.format("The requested student was not found, ID = %s", studentId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(StudentsView.class);
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
        dateOfBirth = new DatePicker("Date Of Birth");
        status = new TextField("Status");
        studyYear = new TextField("Study Year");
        mentorId = new TextField("Mentor Id");
        Component[] fields = new Component[]{lastName, firstName, email, dateOfBirth, status, studyYear, mentorId};

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

    private void populateForm(Student value) {
        this.student = value;
        binder.readBean(this.student);

    }
}
