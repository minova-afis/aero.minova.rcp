package aero.minova.rcp.workspace.dialogs;

import static org.eclipse.jface.widgets.WidgetFactory.label;

import java.util.Objects;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.core.runtime.jobs.ProgressProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.core.ui.Util;
import aero.minova.rcp.workspace.WorkspaceException;
import aero.minova.rcp.workspace.handler.WorkspaceAccessPreferences;
import aero.minova.rcp.workspace.handler.WorkspaceHandler;

@SuppressWarnings("restriction")
public class WorkspaceDialog extends Dialog {

	private Text username;
	private Text password;
	private Text applicationArea;
	private Button btnOK;
	private Text message;
	private Text connectionString;
	private Combo profile;

	private WorkspaceHandler workspaceHandler;
	private Logger logger;
	private ProgressBar progressBar;
	private GlobalProgressMonitor monitor;
	private final UISynchronize sync;
	private SubMonitor subMonitor;
	private boolean loadedProfile = false;

	public WorkspaceDialog(Shell parentShell, Logger logger, UISynchronize sync) {
		super(parentShell);
		this.sync = Objects.requireNonNull(sync);
		this.logger = logger;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(6, false));

		// Layout data für die Labels
		GridDataFactory labelGridData = GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.CENTER);

		label(SWT.NONE).text("Profile").supplyLayoutData(labelGridData::create).create(container);
		profile = new Combo(container, SWT.NONE);
		profile.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false, 4, 1));
		fillProfiles();
		profile.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int i = profile.getSelectionIndex();
				logger.info("Item " + i + " selected (" + profile.getText() + ")");
				username.setText("");
				loadedProfile = true;
				password.setText("xxxxxxxxxxxxxxxxxxxx");
				connectionString.setText("");
				applicationArea.setText("");
				checkWorkspace();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		profile.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				int i = profile.getSelectionIndex();
				logger.info("Item " + i + " selected (" + profile.getText() + ")");
				try {
					username.setText("");
					if (profile.getText().isEmpty() || !loadedProfile || i == -1) {
						password.setText("");
					} else {
						password.setText("xxxxxxxxxxxxxxxxxxxx");
					}
					System.out.println("Profil ist gesetzt: " + loadedProfile);
					connectionString.setText("");
					applicationArea.setText("");
					checkWorkspace();
				} catch (NullPointerException ex) {

				}
			}
		});

		CLabel deleteProfile = new CLabel(container, SWT.CENTER | SWT.VERTICAL);
		deleteProfile.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.FILL).grab(false, true)
				.hint(25, SWT.DEFAULT).create());
		deleteProfile.setText("🗑️");
		deleteProfile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (!profile.getText().isEmpty()) {
					WorkspaceAccessPreferences.deleteSavedWorkspace(profile.getText());
					profile.clearSelection();
					profile.removeAll();
					loadedProfile = false;
					fillProfiles();
					deleteDialogEntries();
				}
			}
		});

		/**
		 * USERNAME
		 */
		label(SWT.NONE).text("Username").supplyLayoutData(labelGridData::create).create(container);
		username = new Text(container, SWT.BORDER);
		username.addFocusListener(new FocusAdapterExtension());
		username.setLayoutData(GridDataFactory.fillDefaults().hint(130, SWT.DEFAULT).span(2, 1).create());

		/**
		 * PASSWORD
		 */
		label(SWT.NONE).text("Password").layoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1))
				.create(container);
		password = new Text(container, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(GridDataFactory.fillDefaults().hint(130, SWT.DEFAULT).span(2, 1).create());
		password.addFocusListener(new FocusAdapterExtension());

		/**
		 * CONNECTION STRING
		 */
		label(SWT.NONE).text("Connection String").layoutData(new GridData(SWT.FILL, SWT.RIGHT, false, false, 1, 1))
				.create(container);
		connectionString = new Text(container, SWT.BORDER);
		connectionString.addFocusListener(new FocusAdapterExtension());
		connectionString.setLayoutData(GridDataFactory.fillDefaults().hint(400, SWT.DEFAULT).span(5, 1).create());

		/**
		 * APPLICATION AREA
		 */
		label(SWT.NONE).text("Application Area").layoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1))
				.create(container);
		applicationArea = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		applicationArea.setLayoutData(GridDataFactory.fillDefaults().hint(365, SWT.DEFAULT).span(5, 1).create());
		applicationArea.setEnabled(false);

		/**
		 * MESSAGE
		 */
		label(SWT.NONE).text("Message").supplyLayoutData(labelGridData::create).create(container);
		message = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
		message.setLayoutData(
				GridDataFactory.fillDefaults().grab(true, true).hint(365, SWT.DEFAULT).span(5, 2).create());
		message.setEnabled(false);
		// Wird benötigt, damit 2 Zeilen für das Message-Feld angezeigt werden.
		new Label(container, SWT.NONE);

		/**
		 * PROGRESSBAR
		 */
		progressBar = new ProgressBar(container, SWT.BORDER | SWT.SMOOTH);
		progressBar.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		progressBar.setLayoutData(GridDataFactory.fillDefaults().hint(SWT.DEFAULT, 20).span(6, 1).create());
		progressBar.setBounds(100, 10, 200, SWT.NONE);

		monitor = new GlobalProgressMonitor();
		Job.getJobManager().setProgressProvider(new ProgressProvider() {
			@Override
			public IProgressMonitor createMonitor(Job job) {
				return monitor.addJob(job);
			}
		});

		return container;
	}

	/**
	 * Leert die Textfelder aus dem Dialog
	 */
	private void deleteDialogEntries() {
		username.setText("");
		password.setText("");
		connectionString.setText("");
		applicationArea.setText("");
		checkWorkspace();
	}

	/**
	 * Füllt die ComboBox mit den vorhandenen Profilen
	 */
	private void fillProfiles() {
		for (ISecurePreferences prefs : WorkspaceAccessPreferences.getSavedWorkspaceAccessData(logger)) {
			try {
				profile.add(prefs.get(WorkspaceAccessPreferences.PROFILE, ""));
			} catch (StorageException e1) {
				logger.error(e1, e1.getMessage());
			}
		}
	}

	/**
	 * Prüft die eingetragenen Daten. Wenn das Profil vorhanden ist, werden die Dialog-Felder entsprechend der gespeicherten Werte gesetzt.
	 * Passwort wird nicht gesetzt!
	 */
	private void checkWorkspace() {
		Job job = new Job("Check Connection") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				subMonitor = SubMonitor.convert(monitor, 2);
				subMonitor.split(1);
				sync.asyncExec(() -> {
					workspaceHandler = null;
					try {
						if (Util.isAvailable(message)) {
							message.setText("");
							workspaceHandler = WorkspaceHandler.newInstance(profile.getText(),
									connectionString.getText(), logger);
							if (workspaceHandler != null) {
								btnOK.setEnabled(workspaceHandler.checkConnection(username.getText(),
										password.getText(), applicationArea.getText()));
								profile.setText(workspaceHandler.getProfile());
								username.setText(workspaceHandler.getUsername());
								password.setText(workspaceHandler.getPassword());
								applicationArea.setText(workspaceHandler.getApplicationArea());
								connectionString.setText(workspaceHandler.getConnectionString());
							}
						}
					} catch (WorkspaceException e1) {
						logger.error(e1);
						message.setText(e1.getMessage());
						btnOK.setEnabled(false);
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.schedule();

	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		btnOK = createButton(parent, IDialogConstants.OPEN_ID, IDialogConstants.OPEN_LABEL, true);
		btnOK.setEnabled(false);
		btnOK.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setWorkspaceData2WorkspaceHandler();
				okPressed();
			}
		});
	}

	private final class FocusAdapterExtension extends FocusAdapter {
		@Override
		public void focusLost(FocusEvent e) {
			checkWorkspace();
		}
	}

	private final class GlobalProgressMonitor extends NullProgressMonitor {
		// thread-Safe via thread confinement of the UI-Thread
		// (means access only via UI-Thread)
		private long runningTasks = 0L;

		@Override
		public void beginTask(final String name, final int totalWork) {
			sync.syncExec(new Runnable() {

				@Override
				public void run() {
					if (runningTasks <= 0) {
						// --- no task is running at the moment ---
						progressBar.setSelection(0);
						progressBar.setMaximum(totalWork);

					} else {
						// --- other tasks are running ---
						progressBar.setMaximum(progressBar.getMaximum() + totalWork);
					}

					runningTasks++;
					progressBar.setToolTipText("Currently running: " + runningTasks + "\nLast task: " + name);
				}
			});
		}

		@Override
		public void worked(final int work) {
			sync.syncExec(new Runnable() {

				@Override
				public void run() {
					if (progressBar != null && !progressBar.isDisposed()) {
						progressBar.setSelection(progressBar.getSelection() + work);
					}
				}
			});
		}

		public IProgressMonitor addJob(Job job) {
			if (job != null) {
				job.addJobChangeListener(new JobChangeAdapter() {
					@Override
					public void done(IJobChangeEvent event) {
						sync.syncExec(new Runnable() {

							@Override
							public void run() {
								runningTasks--;
								if (!progressBar.isDisposed()) {
									if (runningTasks > 0) {
										// --- some tasks are still running ---
										progressBar.setToolTipText("Currently running: " + runningTasks);

									} else {
										// --- all tasks are done (a reset of selection could also be done) ---
										progressBar.setToolTipText("No background progress running.");
										progressBar.setSelection(0);
									}
								}
							}
						});

						// clean-up
						event.getJob().removeJobChangeListener(this);
					}
				});
			}
			return this;
		}
	}

	@Override
	protected void okPressed() {
		try {
			workspaceHandler.open();
		} catch (WorkspaceException e) {
			e.printStackTrace();
		}
		super.okPressed();
	}

	public void setWorkspaceData2WorkspaceHandler() {
		try {
			workspaceHandler.checkConnection(username.getText(), password.getText(), applicationArea.getText());
		} catch (WorkspaceException e) {
			e.printStackTrace();
		}
	}

	public String getUsername() {
		return workspaceHandler.getUsername();
	}

	public String getPassword() {
		return workspaceHandler.getPassword();
	}

	public String getConnection() {
		return workspaceHandler.getConnectionString();
	}

}