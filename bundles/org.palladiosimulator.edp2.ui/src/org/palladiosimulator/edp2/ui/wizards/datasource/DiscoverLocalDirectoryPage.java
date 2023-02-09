package org.palladiosimulator.edp2.ui.wizards.datasource;

import java.util.HashMap;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.jface.databinding.swt.ISWTObservable;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.palladiosimulator.edp2.dao.exception.DataNotAccessibleException;
import org.palladiosimulator.edp2.local.LocalDirectoryRepository;
import org.palladiosimulator.edp2.local.localFactory;
import org.palladiosimulator.edp2.local.localPackage;
import org.palladiosimulator.edp2.models.Repository.Repository;

/**
 * Wizard page to discover and select a local directory based repository.
 *
 * @author groenda, Sebastian Lehrig
 */
public class DiscoverLocalDirectoryPage extends WizardPage {
    private static final HashMap<Control, ControlDecoration> DECORATOR_MAP = new HashMap<Control, ControlDecoration>();
    private final LocalDirectoryRepository ldRepo;

    public DiscoverLocalDirectoryPage() {
        super("wizardPage");
        this.ldRepo = localFactory.eINSTANCE.createLocalDirectoryRepository();

        setTitle("Discover Local File Data Source");
        setDescription("Please select the data source you want to open.");
        setPageComplete(false);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl(final Composite parent) {
        final Composite container = new Composite(parent, SWT.NULL);
        setControl(container);

        final Text locationText = new Text(container, SWT.BORDER);
        createDirectoryInputSection(container, "Location: ", locationText);
        createControlDecoration(locationText);

        // The additional spacing (default is 5,5) is for the decorations
        GridLayoutFactory.swtDefaults().numColumns(2).spacing(10, 5).generateLayout(container);
        final DataBindingContext dbc = new DataBindingContext();

        // create this before binding the observables to enable displaying an
        // error when the wizard page is shown:
        WizardPageSupport.create(this, dbc);
		
		final IObservableValue observeLocationText = WidgetProperties.text(SWT.Modify).observe(locationText);
		
        dbc.bindValue(observeLocationText, EMFObservables.observeValue(ldRepo, localPackage.Literals.LOCAL_DIRECTORY_REPOSITORY__URI), new UpdateValueStrategy().setAfterConvertValidator(new UriPointsToLocalDirectoryValidator("location", ldRepo)), null);

        final AggregateValidationStatus aggregateValidationStatus = new AggregateValidationStatus(
                dbc.getValidationStatusProviders(), AggregateValidationStatus.MAX_SEVERITY);

        aggregateValidationStatus.addValueChangeListener(new IValueChangeListener() {
            @Override
            public void handleValueChange(final ValueChangeEvent event) {
                // the invocation of the getValue method is necessary
                // the further changes will be fired
                aggregateValidationStatus.getValue();
                for (final Object o : dbc.getBindings()) {
                    final Binding binding = (Binding) o;
                    final IStatus status = (IStatus) binding.getValidationStatus().getValue();
                    Control control = null;
                    if (binding.getTarget() instanceof ISWTObservable) {
                        final ISWTObservable swtObservable = (ISWTObservable) binding.getTarget();
                        control = (Control) swtObservable.getWidget();
                    }
                    final ControlDecoration decoration = DECORATOR_MAP.get(control);
                    if (decoration != null) {
                        if (status.isOK()) {
                            decoration.hide();
                            setPageComplete(true);
                        } else {
                            decoration.setDescriptionText(status.getMessage());
                            decoration.show();
                        }
                    }
                }
            }
        });
    }

    /**
     * Creates a decoration for a control to show validation errors.
     *
     * @param control
     *            The control which should be decorated.
     */
    private void createControlDecoration(final Control control) {
        final ControlDecoration controlDecoration = new ControlDecoration(control, SWT.LEFT | SWT.TOP);
        final FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
                FieldDecorationRegistry.DEC_ERROR);
        controlDecoration.setImage(fieldDecoration.getImage());
        controlDecoration.hide();
        DECORATOR_MAP.put(control, controlDecoration);
    }

    /**
     * Validator which checks that a provided value is not empty.
     *
     * @author groenda
     */
    public static class NotEmptyValidator implements IValidator {

        private final String label;

        public NotEmptyValidator(final String label) {
            this.label = label;
        }

        @Override
        public IStatus validate(final Object value) {
            final String string = (String) value;
            if (string == null || string.trim().length() == 0) {
                return ValidationStatus.error("Please enter a value for " + label + ".");
            }
            return ValidationStatus.ok();
        }
    }

    /**
     * Validator which checks if that the content of a field points to a directory within the local
     * file system.
     *
     * @author groenda
     */
    public static class UriPointsToLocalDirectoryValidator implements IValidator {
        /**
         * Name/Identifier of the field containing the validated data. Used to refer to the field in
         * the messages.
         */
        private final String label;
        /** Repository which is used for validating the provided input. */
        private final LocalDirectoryRepository ldRepo;

        public UriPointsToLocalDirectoryValidator(final String label, final LocalDirectoryRepository ldRepo) {
            this.label = label;
            this.ldRepo = ldRepo;
        }

        @Override
        public IStatus validate(final Object value) {
            final String input = (String) value;
            // there must be any input
            if (input == null || input.trim().length() == 0) {
                return ValidationStatus.error("Please enter a value for " + label + ".");
            }
            // validate input
            try {
                ldRepo.convertUriStringToFile(input);
            } catch (final DataNotAccessibleException e1) {
                return ValidationStatus.error("The value entered in " + label + " is not valid: " + e1.getMessage());
            }
            return ValidationStatus.ok();
        }

    }

    /**
     * Creates a section in the parent container for selection files. Creates a {@link Group} with a
     * label. Inside the group, a text field for the file with the given extension, a button to load
     * from the workspace and a button to load from the file system are displayed.
     *
     * @param container
     *            The parent container
     * @param modelFileLabel
     *            The label of the section around the file input text field
     * @param textFileNameToLoad
     *            The text field that contains the filename. Its parent will be reset to the
     *            appropriate group within this method.
     *
     * @author Palladio.Workflow::FileNamesInputTab
     */
    public void createDirectoryInputSection(final Composite container, final String modelFileLabel,
            final Text textFileNameToLoad) {

        final Group fileInputGroup = new Group(container, SWT.NONE);
        final GridLayout glFileInputGroup = new GridLayout();
        glFileInputGroup.numColumns = 3;
        fileInputGroup.setLayout(glFileInputGroup);
        fileInputGroup.setText(modelFileLabel); // The group name
        fileInputGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        textFileNameToLoad.setParent(fileInputGroup);
        final GridData gd_textFileName = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd_textFileName.widthHint = 200;
        textFileNameToLoad.setLayoutData(gd_textFileName);

        final Button workspaceButton = new Button(fileInputGroup, SWT.NONE);
        workspaceButton.setText("Workspace...");
        workspaceButton.addSelectionListener(new WorkspaceButtonSelectionAdapter(getShell(), textFileNameToLoad));

        final Button buttonUsage = new Button(fileInputGroup, SWT.NONE);
        buttonUsage.setText("File System...");
        buttonUsage.addSelectionListener(new FileSystemButtonSelectionAdater(getShell(), textFileNameToLoad));
    }

    public Repository getRepositoryOnFinish() {
        return this.ldRepo;
    }
}
