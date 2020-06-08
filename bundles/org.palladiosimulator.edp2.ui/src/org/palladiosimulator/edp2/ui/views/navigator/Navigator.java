package org.palladiosimulator.edp2.ui.views.navigator;

import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.databinding.IEMFListProperty;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.palladiosimulator.edp2.EDP2Plugin;
import org.palladiosimulator.edp2.dao.exception.DataNotAccessibleException;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.models.Repository.RepositoryPackage;
import org.palladiosimulator.edp2.util.MeasurementsUtility;

/**
 * ViewPart for the Navigator.
 *
 * @author Sebastian Lehrig
 */
public class Navigator extends ViewPart implements ITabbedPropertySheetPageContributor {

    /** The shown tree. */
    private TreeViewer treeViewer;

    /**
     * The constructor.
     */
    public Navigator() {
        super();
    }

    @Override
    public void createPartControl(final Composite parent) {
        parent.setLayout(new FillLayout());
        this.treeViewer = new TreeViewer(parent, SWT.FULL_SELECTION);
        this.treeViewer.setAutoExpandLevel(8);
        ColumnViewerToolTipSupport.enableFor(this.treeViewer, ToolTip.NO_RECREATE);

        final ObservableListTreeContentProvider contentProvider = new ObservableListTreeContentProvider(
                new NavigatorTreeFactoryImpl(), new NavigatorTreeStructureAdvisorImpl());
        this.treeViewer.setContentProvider(contentProvider);

        // Label Provider; Observe model for change wrt to labels
        final IObservableSet set = contentProvider.getKnownElements();
        final IObservableMap[] map = new IObservableMap[1];
        //        map[0] = EMFProperties.value(RepositoryPackage.Literals.LOCAL_DIRECTORY_REPOSITORY__URI).observeDetail(set);
        map[0] = EMFProperties.value(RepositoryPackage.Literals.LOCAL_MEMORY_REPOSITORY__DOMAIN).observeDetail(set);
        // TODO: Observe other labels
        this.treeViewer.setLabelProvider(new NavigatorTreeLabelProviderImpl(map));

        final IEMFListProperty nodes = EMFProperties
                .list(RepositoryPackage.Literals.REPOSITORIES__AVAILABLE_REPOSITORIES);
        this.treeViewer.setInput(nodes.observe(EDP2Plugin.INSTANCE.getRepositories()));

        // Add key listener
        this.treeViewer.getControl().addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(final KeyEvent e) {
                super.keyReleased(e);

                if (e.keyCode == SWT.DEL) {
                    final IStructuredSelection selection = (IStructuredSelection) Navigator.this.treeViewer
                            .getSelection();

                    if (selection.isEmpty()) {
                        return;
                    }

                    handleDelete(selection);
                }
            }

            private void handleDelete(final IStructuredSelection selection) {
                if (selection instanceof TreeSelection) {
                    final TreeSelection treeSelection = (TreeSelection) selection;
                    final Repository repo = (Repository) treeSelection.getFirstElement();
                    try {
                        MeasurementsUtility.ensureClosedRepository(repo);
                        EDP2Plugin.INSTANCE.getRepositories().getAvailableRepositories().remove(repo);
                    } catch (final DataNotAccessibleException e) {
                        throw new RuntimeException("Could not close EDP data source");
                    }
                }
            }

        });

        // Add double click listener
        this.treeViewer.addDoubleClickListener(new NavigatorDoubleClickListener());
        this.treeViewer.addDragSupport(DND.DROP_LINK, new Transfer[] { LocalSelectionTransfer.getTransfer() },
                new DragSourceListener() {

            @Override
            public void dragStart(final DragSourceEvent event) {
                event.doit = true;
            }

            @Override
            public void dragSetData(final DragSourceEvent event) {
                final IStructuredSelection selection = (IStructuredSelection) Navigator.this.treeViewer
                        .getSelection();
                LocalSelectionTransfer.getTransfer().setSelection(selection);
            }

            @Override
            public void dragFinished(final DragSourceEvent event) {
            }
        });

        getSite().setSelectionProvider(this.treeViewer);
    }

    // it is important to implement setFocus()!
    @Override
    public void setFocus() {
        this.treeViewer.getTree().setFocus();
    }

    @Override
    public String getContributorId() {
        return "org.palladiosimulator.edp2.ui.propertyContributor";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(final Class adapter) {
        if (adapter == IPropertySheetPage.class) {
            return new TabbedPropertySheetPage(this);
        }
        return super.getAdapter(adapter);
    }

    public void collapseTree() {
        this.treeViewer.collapseAll();
    }

    public void expandTree() {
        this.treeViewer.expandAll();
    }
}
