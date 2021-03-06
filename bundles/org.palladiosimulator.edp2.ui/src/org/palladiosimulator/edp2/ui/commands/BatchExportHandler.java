package org.palladiosimulator.edp2.ui.commands;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.ui.EDP2UIPlugin;
import org.palladiosimulator.edp2.ui.wizards.BatchFileExportWizard;

public class BatchExportHandler extends AbstractHandler implements IHandler {
    
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        final ISelection selection = HandlerUtil.getCurrentSelection(event);

        if (selection instanceof TreeSelection) {
            final TreeSelection treeSelection = (TreeSelection) selection;
            if (treeSelection.getFirstElement() instanceof Repository) {
                final Repository repo = (Repository) treeSelection.getFirstElement();
                final WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), new BatchFileExportWizard(repo));
                dialog.open();
            } else {
                openErrorMesage();
            }
        } else {
            openErrorMesage();
        }
        return null;
    }

    private void openErrorMesage() {
        final Shell shell = EDP2UIPlugin.INSTANCE.getWorkbench().getActiveWorkbenchWindow().getShell();
        ErrorDialog.openError(
                shell,
                "Select Repository",
                "Batch Export Failed.",
                new Status(IStatus.ERROR, EDP2UIPlugin.PLUGIN_ID, "You need to select a EDP2 Repository to execute the Batch Export Command"));
    }
}
