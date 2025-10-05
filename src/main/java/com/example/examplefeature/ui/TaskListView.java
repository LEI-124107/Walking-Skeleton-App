package com.example.examplefeature.ui;

import com.example.base.ui.component.ViewToolbar;
import com.example.examplefeature.Task;
import com.example.examplefeature.TaskService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.dialog.Dialog;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

import static com.vaadin.flow.spring.data.VaadinSpringDataHelpers.toSpringPageRequest;

@Route("")
@PageTitle("Task List")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Task List")
class TaskListView extends Main {

    private final TaskService taskService;

    final TextField description;
    final DatePicker dueDate;
    final ComboBox<Task.Priority> priorityBox; // <-- Add this line
    final Button createBtn;
    final Grid<Task> taskGrid;

    TaskListView(TaskService taskService) {
        this.taskService = taskService;

        description = new TextField();
        description.setPlaceholder("What do you want to do?");
        description.setAriaLabel("Task description");
        description.setMaxLength(Task.DESCRIPTION_MAX_LENGTH);
        description.setMinWidth("20em");

        dueDate = new DatePicker();
        dueDate.setPlaceholder("Due date");
        dueDate.setAriaLabel("Due date");

        // Add priority dropdown
        priorityBox = new ComboBox<>("Priority");
        priorityBox.setItems(Task.Priority.values());
        priorityBox.setValue(Task.Priority.MEDIUM); // Default value

        createBtn = new Button("Create", event -> createTask());
        createBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(getLocale())
                .withZone(ZoneId.systemDefault());
        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(getLocale());

        taskGrid = new Grid<>();
        taskGrid.setItems(query -> taskService.list(toSpringPageRequest(query)).stream());

        // Priority column with colored icon and label
        taskGrid.addComponentColumn(task -> {
            Icon icon;
            String color;
            String label;
            switch (task.getPriority()) {
                case HIGH:
                    icon = VaadinIcon.ARROW_UP.create();
                    color = "red";
                    label = "High";
                    break;
                case MEDIUM:
                    icon = VaadinIcon.ARROW_RIGHT.create();
                    color = "orange";
                    label = "Medium";
                    break;
                case LOW:
                    icon = VaadinIcon.ARROW_DOWN.create();
                    color = "green";
                    label = "Low";
                    break;
                default:
                    icon = VaadinIcon.QUESTION.create();
                    color = "gray";
                    label = "Unknown";
            }
            icon.getStyle().set("color", color);
            Span priorityLabel = new Span(label);
            priorityLabel.getStyle().set("color", color).set("font-weight", "bold");
            Span container = new Span(icon, priorityLabel);
            container.getElement().getStyle().set("display", "flex").set("align-items", "center").set("gap", "0.3em");
            return container;
        }).setHeader("Priority");

        taskGrid.addColumn(Task::getDescription).setHeader("Description");
        taskGrid.addColumn(task -> Optional.ofNullable(task.getDueDate()).map(dateFormatter::format).orElse("Never"))
                .setHeader("Due Date");
        taskGrid.addColumn(task -> dateTimeFormatter.format(task.getCreationDate())).setHeader("Creation Date");
        taskGrid.setSizeFull();

        setSizeFull();
        addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL);

        // Add priorityBox to the toolbar group
        add(new ViewToolbar("Task List", ViewToolbar.group(description, dueDate, priorityBox, createBtn)));
        add(taskGrid);

        // Add an "Edit Priority" button column to the grid
        taskGrid.addComponentColumn(task -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create(), event -> openEditPriorityDialog(task));
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            return editBtn;
        }).setHeader("Edit Priority");
    }

    private void createTask() {
        // Pass priority to service
        taskService.createTask(description.getValue(), dueDate.getValue(), priorityBox.getValue());
        taskGrid.getDataProvider().refreshAll();
        description.clear();
        dueDate.clear();
        priorityBox.setValue(Task.Priority.MEDIUM);
        Notification.show("Task added", 3000, Notification.Position.BOTTOM_END)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void openEditPriorityDialog(Task task) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit Priority");

        ComboBox<Task.Priority> priorityField = new ComboBox<>("Priority");
        priorityField.setItems(Task.Priority.values());
        priorityField.setValue(task.getPriority());

        Button saveBtn = new Button("Save", event -> {
            task.setPriority(priorityField.getValue());
            taskService.updateTaskPriority(task.getId(), priorityField.getValue());
            taskGrid.getDataProvider().refreshAll();
            dialog.close();
            Notification.show("Priority updated", 2000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        Button cancelBtn = new Button("Cancel", event -> dialog.close());

        dialog.getFooter().add(cancelBtn, saveBtn);
        dialog.add(priorityField);
        dialog.open();
    }

}
