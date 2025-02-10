package org.palladiosimulator.edp2.visualization.properties.sections;

import java.awt.Color;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.measure.Measure;
import jakarta.measure.MeasureFormat;
import jakarta.measure.unit.UnitFormat;

import org.apache.commons.lang.ClassUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.palladiosimulator.edp2.datastream.configurable.IPropertyConfigurable;
import org.palladiosimulator.edp2.datastream.configurable.reflective.ConfigurationProperty;

import de.uka.ipd.sdq.dialogs.selection.FilteredItemsAdapterFactory;
import de.uka.ipd.sdq.dialogs.selection.SelectEObjectDialog;

public class EDP2PropertiesTable {

    /**
     * Index of the column in the {@link #visualPropertiesTable} containing the properties' editable
     * values.
     */
    private static int editColumn = 1;

    /**
     * Index of the column in the {@link #visualPropertiesTable} containing the properties' labels.
     */
    private static int labelColumn = 0;

    /** Define the filter list. */
    private static final ArrayList<Object> FILTER_LIST = new ArrayList<Object>();

    /** Define the additional references. */
    private static final ArrayList<EReference> ADDITIONAL_REFERENCES = new ArrayList<EReference>();

    static {
        // FIXME hardcoded so that any EClass object is displayed and no references are filter.
        // Extend EObject properties so that these information can be read from the property
        // and then remove this static initializer.
        FILTER_LIST.add(EObject.class);
    }

    private final Table myTable;

    private static final String MEASURE_PARSE_ERROR_MESSAGE = "The given input could not be recognized as a valid measurement.\nRequired is a value follwed by a unit!\n\nDetailed error message:\n %s";
    // assume that we use '.' as decimal point rather than ',' -> English locale
    private static final Locale NUMBER_FORMAT_LOCALE = Locale.ENGLISH;
    private final MeasureFormat measureFormat;

    /**
     * The last selected input, the properties of which are displayed in the table.
     */
    private IPropertyConfigurable lastSelectedInput;

    public EDP2PropertiesTable(final Composite parent) {
        myTable = new Table(parent, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);

        initializeTable();
        this.measureFormat = initializeMeasureFormat();

    }

    private MeasureFormat initializeMeasureFormat() {
        final NumberFormat numberFormat = NumberFormat.getInstance(NUMBER_FORMAT_LOCALE);
        numberFormat.setMinimumFractionDigits(1);

        return MeasureFormat.getInstance(numberFormat, UnitFormat.getInstance());

    }

    private void initializeTable() {
        myTable.setLinesVisible(true);
        myTable.setHeaderVisible(true);

        // set the table layout
        final GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
        gridData.heightHint = 250;
        gridData.widthHint = 250;
        myTable.setLayoutData(gridData);

        final TableLayout tableLayout = new TableLayout();
        tableLayout.addColumnData(new ColumnWeightData(1));
        tableLayout.addColumnData(new ColumnWeightData(2));
        myTable.setLayout(tableLayout);

        final TableViewer commonPropertiesTableViewer = new TableViewer(myTable);
        final TableViewerColumn keyColumn = new TableViewerColumn(commonPropertiesTableViewer, SWT.NONE);
        keyColumn.getColumn().setText("Property");

        final TableViewerColumn valueColumn = new TableViewerColumn(commonPropertiesTableViewer, SWT.NONE);
        valueColumn.getColumn().setText("Value");

        // the editor for the cells
        myTable.addListener(SWT.MouseDown, new Listener() {
            @Override
            public void handleEvent(final Event event) {
                final Rectangle clientArea = myTable.getClientArea();
                final Point pt = new Point(event.x, event.y);
                int index = myTable.getTopIndex();
                while (index < myTable.getItemCount()) {
                    boolean visible = false;
                    final TableItem item = myTable.getItem(index);
                    // look if the mouse event is in the editable column
                    final Rectangle rect = item.getBounds(editColumn);
                    if (rect.contains(pt)) {
                        final Class<?> propertyType = lastSelectedInput.getPropertyType(item.getText(labelColumn));
                        if (ClassUtils.isAssignable(propertyType, Boolean.class, true)) {
                            openBooleanDialog(index, myTable);
                        } else if (ClassUtils.isAssignable(propertyType, String.class, true)) {
                            openTextDialog(index, myTable, String.class);
                        } else if (ClassUtils.isAssignable(propertyType, Integer.class, true)) {
                            openTextDialog(index, myTable, Integer.class);
                        } else if (ClassUtils.isAssignable(propertyType, Float.class, true)) {
                            openTextDialog(index, myTable, Float.class);
                        } else if (ClassUtils.isAssignable(propertyType, Double.class, true)) {
                            openTextDialog(index, myTable, Double.class);
                        } else if (ClassUtils.isAssignable(propertyType, Color.class, true)) {
                            openColorAndTransparencyDialog(item, myTable);
                        } else if (ClassUtils.isAssignable(propertyType, EObject.class, true)) {
                            openEObjectDialog(item, myTable, propertyType, FILTER_LIST, ADDITIONAL_REFERENCES);
                        } else if (ClassUtils.isAssignable(Measure.class, propertyType, true)) {
                            openMeasureDialog(index, myTable);
                        } else {
                            //  throw new RuntimeException("Unsupported property type found!");
                            return;
                        }
                        return;
                    }
                    if (!visible && rect.intersects(clientArea)) {
                        visible = true;
                    }
                    if (!visible) {
                        return;
                    }
                    index++;
                }
            }
        });
    }

    /**
     * Opens a {@link ColorDialog} to change the color of the last selected
     * {@link JFreeChartVisualizationSingleDatastreamInput}.
     *
     * @param item
     *            index the row-index of the cell to be edited
     * @param shell
     *            the Shell in which the dialog is displayed.
     */
    protected void openColorAndTransparencyDialog(final TableItem item, final Table table) {
        final ColorDialog colorPicker = new ColorDialog(table.getShell());
        colorPicker.setRGB(item.getBackground().getRGB());
        final RGB rgbColor = colorPicker.open();
        if (rgbColor != null) {
            final Color newColor = new Color(rgbColor.red, rgbColor.green, rgbColor.blue);
            updateColorCell(item, newColor);
            updateProperties(item.getText(labelColumn), newColor, table);
        }

    }

    /**
     * Opens an {@link EObjectDialog} to change the EObject of the last selected
     * {@link JFreeChartVisualizationSingleDatastreamInput}.
     *
     * @param item
     *            index the row-index of the cell to be edited
     * @param propertyType
     * @param shell
     *            the Shell in which the dialog is displayed.
     */
    protected void openEObjectDialog(final TableItem item, final Table table, final Class<?> propertyType,
            final Collection<?> filterList, final Collection<EReference> additionalChildReferences) {
        final SelectEObjectDialog selectEObjectDialog = new SelectEObjectDialog(table.getShell(),
                propertyType.getSimpleName(), new ResourceSetImpl(), new AdapterFactoryContentProvider(
                        new FilteredItemsAdapterFactory(EMFAdapterFactoryHelper.ADAPTER_FACTORY, filterList,
                                additionalChildReferences)), EMFAdapterFactoryHelper.ADAPTER_FACTORY_LABEL_PROVIDER);

        selectEObjectDialog.setProvidedService(EObject.class);
        selectEObjectDialog.open();
        if (selectEObjectDialog.getResult() != null) {
            final EObject eObject = selectEObjectDialog.getResult();
            updateProperties(item.getText(labelColumn), eObject, table);
        }
    }

    /**
     * @param item
     * @param col
     */
    private void updateColorCell(final TableItem item, final Color col) {
        if (col.getAlpha() == 0) {
            item.setText(1, "(default Color)");
        } else {
            item.setText(1, "");
            item.setBackground(1, new org.eclipse.swt.graphics.Color(myTable.getDisplay(), col.getRed(),
                    col.getGreen(), col.getBlue()));
        }
    }

    /**
     * Opens a {@link TableEditor} to change a {@link Measure} {@link ConfigurationProperty}.
     *
     * @param index
     *            An integer denoting the row-index of the cell containing
     *            {@link ConfigurationProperty} to be edited.
     * @param table
     *            The {@link Table} containing the {@link ConfigurationProperty} to be edited.
     */
    protected void openMeasureDialog(final int index, final Table table) {
        final TableEditor editor = new TableEditor(table);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;

        final Text text = new Text(table, SWT.NONE);
        final Listener textListener = new Listener() {
            @Override
            public void handleEvent(final Event e) {
                switch (e.type) {
                case SWT.FocusOut:
                    text.dispose();
                    e.doit = false;
                    break;
                case SWT.Traverse:
                    switch (e.detail) {
                    case SWT.TRAVERSE_RETURN:
                        handleTraverseReturn();
                    case SWT.TRAVERSE_ESCAPE:
                        text.dispose();
                        e.doit = false;
                    }
                    break;
                }
            }

            private void handleTraverseReturn() {
                final String input = text.getText();
                Measure<?, ?> result = null;
                try {
                    result = (Measure<?, ?>) measureFormat.parseObject(input);
                } catch (final ParseException e) {
                    showErrorDialog(e);
                }
                if (result != null) {
                    updateProperties(table.getItem(index).getText(labelColumn), result, table);
                    refreshTable();
                }
            }

            private void showErrorDialog(final Exception e) {
                final MessageBox errorDialog = new MessageBox(table.getShell(), SWT.OK | SWT.ICON_ERROR);
                errorDialog.setText("Error");
                errorDialog.setMessage(String.format(MEASURE_PARSE_ERROR_MESSAGE, e.getMessage()));
                errorDialog.open();
            }
        };
        text.addListener(SWT.FocusOut, textListener);
        text.addListener(SWT.Traverse, textListener);
        editor.setEditor(text, table.getItem(index), editColumn);
        text.setText(table.getItem(index).getText(editColumn));
        text.selectAll();
        text.setFocus();
    }

    /**
     * Opens a text dialog for editing cells in the {@link #visualPropertiesTable} by entering text.
     *
     * @param index
     *            the row-index of the cell to be edited
     * @param clazz
     */
    protected void openTextDialog(final int index, final Table table, final Class<?> clazz) {
        final TableEditor editor = new TableEditor(table);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;

        final Text text = new Text(table, SWT.NONE);
        final Listener textListener = new Listener() {
            @Override
            public void handleEvent(final Event e) {
                switch (e.type) {
                case SWT.FocusOut:
                    text.dispose();
                    e.doit = false;
                    break;
                case SWT.Traverse:
                    switch (e.detail) {
                    case SWT.TRAVERSE_RETURN:
                        table.getItem(index).setText(editColumn, text.getText());

                        // FIXME In case, e.g., Integer is expected but a String is entered,
                        // Exceptions are thrown.
                        Object resultValue = null;
                        if (clazz == Float.class) {
                            resultValue = Float.parseFloat(text.getText());
                        } else if (clazz == Double.class) {
                            resultValue = Double.parseDouble(text.getText());
                        } else if (clazz == Long.class) {
                            resultValue = Long.parseLong(text.getText());
                        } else if (clazz == Integer.class) {
                            resultValue = Integer.parseInt(text.getText());
                        } else if (clazz == String.class) {
                            resultValue = text.getText();
                        }

                        updateProperties(table.getItem(index).getText(labelColumn), resultValue, table);
                        refreshTable();
                    case SWT.TRAVERSE_ESCAPE:
                        text.dispose();
                        e.doit = false;
                    }
                    break;
                }
            }
        };
        text.addListener(SWT.FocusOut, textListener);
        text.addListener(SWT.Traverse, textListener);
        editor.setEditor(text, table.getItem(index), editColumn);
        text.setText(table.getItem(index).getText(editColumn));
        text.selectAll();
        text.setFocus();
    }

    /**
     * Opens a dropdown ({@link Combo}) for editing cells in the {@link #visualPropertiesTable},
     * which can only have boolean values.
     *
     * @param index
     *            the row-index of the cell to be edited
     */
    protected void openBooleanDialog(final int index, final Table table) {
        final TableEditor editor = new TableEditor(table);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;
        final Combo comboBox = new Combo(table, SWT.DROP_DOWN);
        comboBox.setItems(new String[] { "true", "false" });
        // set the currently selected item to the value stored in the cell
        comboBox.select(table.getItem(index).getText(editColumn).equals("true") ? 0 : 1);

        final String key = table.getItem(index).getText(labelColumn);
        comboBox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                table.getItem(index).setText(editColumn, comboBox.getItem(comboBox.getSelectionIndex()));
                updateProperties(key, "true".equals(comboBox.getItem(comboBox.getSelectionIndex())), table);
                // if the changed field was the frequency, reset the label of
                // the range
                // axis to default
                // FIXME
                // if (table.getItem(index).getText(labelColumn)
                // .equals(HistogramEditorInput.ABSOLUTE_FREQUENCY_KEY)) {
                // final HistogramEditorInput firstInput = ((HistogramEditorInput)
                // ((AbstractVisualizationInput<JFreeChartVisualisationSingleDatastreamInput<T>>)
                // getInput())
                // .getInputs().get(0));
                // firstInput.setRangeAxisLabel(firstInput
                // .getDefaultRangeAxisLabel());
                // }
                comboBox.dispose();
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
                comboBox.dispose();
            }
        });
        comboBox.addListener(SWT.FocusOut, new Listener() {
            @Override
            public void handleEvent(final Event event) {
                comboBox.dispose();
                event.doit = false;
                return;
            }
        });
        editor.setEditor(comboBox, table.getItem(index), editColumn);
    }

    /**
     * Update the properties of the selected input. Uses
     * {@link AbstractTransformation#setProperties(HashMap)} to update the properties.
     *
     * @param key
     *            the key of the updated value as a String.
     * @param value
     *            the value as an Object.
     */
    private void updateProperties(final String key, final Object value, final Table table) {
        final Map<String, Object> newProperties = new HashMap<String, Object>(lastSelectedInput.getProperties());
        newProperties.put(key, value);
        lastSelectedInput.setProperties(newProperties);
        refreshTable();
    }

    /**
     * Update the table containing the properties of the {@link #lastSelectedInput}.
     */
    public void refreshTable() {
        myTable.clearAll();
        myTable.setItemCount(0);

        if (lastSelectedInput != null) {
            final Map<String, Object> properties = lastSelectedInput.getProperties();

            for (final String key : properties.keySet()) {
                final TableItem item = new TableItem(myTable, SWT.NONE);
                item.setText(0, String.valueOf(key));

                if (lastSelectedInput.isPropertyNotSet(key)) {
                    item.setText(1, "<not set>");
                } else {
                    prettyPrintProperty(item, properties.get(key));
                }
            }
        }

    }

    private void prettyPrintProperty(final TableItem item, final Object value) {
        final Class<?> propertyType = value.getClass();
        if (ClassUtils.isAssignable(propertyType, Color.class)) {
            final Color col = (Color) value;
            updateColorCell(item, col);
        } else if (ClassUtils.isAssignable(propertyType, EObject.class)) {
            final String displayString = EMFAdapterFactoryHelper.ADAPTER_FACTORY_LABEL_PROVIDER.getText(value);
            item.setText(1, displayString);
        } else if (ClassUtils.isAssignable(propertyType, Measure.class)) {
            item.setText(1, this.measureFormat.format(value));
        } else {
            item.setText(1, String.valueOf(value));
        }
    }

    public void setLastSelection(final IPropertyConfigurable iPropertyConfigurable) {
        this.lastSelectedInput = iPropertyConfigurable;
        refreshTable();
    }
}
