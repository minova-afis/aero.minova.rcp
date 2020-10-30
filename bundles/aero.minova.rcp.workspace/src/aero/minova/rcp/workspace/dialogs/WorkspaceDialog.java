package aero.minova.rcp.workspace.dialogs;

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
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.component.annotations.Component;
import aero.minova.rcp.core.ui.Util;
import aero.minova.rcp.workspace.WorkspaceException;
import aero.minova.rcp.workspace.handler.WorkspaceAccessPreferences;
import aero.minova.rcp.workspace.handler.WorkspaceHandler;

@SuppressWarnings("restriction")
@Component
public class WorkspaceDialog extends Dialog{

	private Text username;
	private Text password;
	private Text applicationArea;
	private Button btnOK;
	private Button btnConnect;
	private Text message;
	private Text connectionString;
	private Combo profile;

	private WorkspaceHandler workspaceHandler;
	private Logger logger;
	private ProgressBar progressBar;
	private GlobalProgressMonitor monitor;
	private final UISynchronize sync;
	private SubMonitor subMonitor;

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

		Label lblProfile = new Label(container, SWT.NONE);
		labelGridData.applyTo(lblProfile);
		lblProfile.setText("Profile");

		profile = new Combo(container, SWT.NONE);
		profile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 5, 1));
		for (ISecurePreferences prefs : WorkspaceAccessPreferences.getSavedWorkspaceAccessData(logger)) {
			try {
				profile.add(prefs.get(WorkspaceAccessPreferences.PROFILE, ""));
			} catch (StorageException e1) {
				logger.error(e1, e1.getMessage());
			}
		}
		profile.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int i = profile.getSelectionIndex();
				logger.info("Item " + i + " selected (" + profile.getText() + ")");
				username.setText("");
				password.setText("");
				connectionString.setText("");
				applicationArea.setText("");
				checkWorkspace();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		profile.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				int i = profile.getSelectionIndex();
				logger.info("Item " + i + " selected (" + profile.getText() + ")");
				try {
					username.setText("");
					password.setText("");
					connectionString.setText("");
					applicationArea.setText("");
					checkWorkspace();
				} catch (NullPointerException ex) {

				}
			}
		});
		
//		Button delete = new Button(container, SWT.PUSH);
//		GridData gd_delete = new GridData(GridData.VERTICAL_ALIGN_END);
//		gd_delete.verticalAlignment = SWT.FILL;
//		delete.setLayoutData(gd_delete);
//		delete.setText("List Applications");
		

		Label lblUsername = new Label(container, SWT.NONE);
		lblUsername.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		labelGridData.applyTo(lblUsername);
		lblUsername.setText("Username");

		username = new Text(container, SWT.BORDER);
		GridData gd_username = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_username.widthHint = 130;
		username.setLayoutData(gd_username);
		GridData gd3 = new GridData(GridData.FILL);
		gd3.verticalSpan = 1;
		gd3.horizontalSpan = 2;
		gd3.grabExcessHorizontalSpace = true;
		gd3.grabExcessVerticalSpace = false;
		gd3.horizontalAlignment = SWT.FILL;
		gd3.widthHint = 60;
		// username.setText(workspaceData.getUsername());
		username.addModifyListener(e -> {
			Text textWidget = (Text) e.getSource();
			String userText = textWidget.getText();
			// workspaceData.setUsername(userText);
		});

		Label lblPassword = new Label(container, SWT.NONE);
		lblPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		labelGridData.applyTo(lblPassword);
		lblPassword.setText("Password");

		password = new Text(container, SWT.BORDER | SWT.PASSWORD);
		GridData gd6 = new GridData(GridData.FILL);
		gd6.verticalSpan = 1;
		gd6.horizontalSpan = 2;
		gd6.grabExcessVerticalSpace = false;
		gd6.horizontalAlignment = SWT.FILL;
		gd6.widthHint = 130;
		password.setLayoutData(gd6);
		// TODO
		// password.setText(workspaceData.getPassword());
		password.addModifyListener(e -> {
			Text textWidget = (Text) e.getSource();
			String passwordText = textWidget.getText();
			// TODO
			// workspaceData.setPassword(passwordText);
		});
		// new Label(container, SWT.NONE);

		Label lblConnectionString = new Label(container, SWT.NONE);
		GridData gd5 = new GridData();
		gd5.horizontalAlignment = SWT.BEGINNING;
		gd5.widthHint = 120;
		gd5.horizontalSpan = 1;
		gd5.verticalSpan = 1;
		gd5.horizontalAlignment = SWT.FILL;
		lblConnectionString.setLayoutData(gd5);
		labelGridData.applyTo(lblConnectionString);
		lblConnectionString.setText("Connection String");

		connectionString = new Text(container, SWT.BORDER);
		GridData gd_connectionString = new GridData(SWT.FILL, SWT.CENTER, false, false, 5, 1);
		gd_connectionString.widthHint = 400;
		connectionString.setLayoutData(gd_connectionString);

		Label lblApplicationArea = new Label(container, SWT.NONE);
		GridData gd_lblApplicationArea = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd_lblApplicationArea.verticalAlignment = SWT.FILL;
		gd_lblApplicationArea.horizontalAlignment = SWT.RIGHT;
		lblApplicationArea.setLayoutData(gd_lblApplicationArea);

		lblApplicationArea.setText("Application Area");

		applicationArea = new Text(container, SWT.BORDER);
		GridData gd = new GridData(GridData.FILL);
		gd.widthHint = 365;
		gd.verticalSpan = 1;
		gd.horizontalSpan = 4;
		gd.grabExcessVerticalSpace = false;
		gd.horizontalAlignment = SWT.FILL;
		applicationArea.setLayoutData(gd);

		Button btnNewButton = new Button(container, SWT.ARROW | SWT.DOWN);
		GridData gd_btnNewButton = new GridData(GridData.VERTICAL_ALIGN_END);
		gd_btnNewButton.verticalAlignment = SWT.FILL;
		btnNewButton.setLayoutData(gd_btnNewButton);
		btnNewButton.setText("List Applications");
		
		btnNewButton.addSelectionListener(new SelectionAdapter() {@Override
		public void widgetSelected(SelectionEvent e) {
			DirectoryDialog dialog = new DirectoryDialog(parent.getShell());
			dialog.setText("Select Directory");
			String text = dialog.open();
			applicationArea.setText(text);
		}});

		Label lblMessage = new Label(container, SWT.NONE);
		labelGridData.applyTo(lblMessage);
		lblMessage.setText("Message");

		message = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
		GridData gd2 = new GridData(GridData.FILL);
		gd2.verticalSpan = 2;
		gd2.horizontalSpan = 5;
		gd2.grabExcessVerticalSpace = false;
		gd2.verticalAlignment = SWT.FILL;
		gd2.horizontalAlignment = SWT.FILL;
		message.setLayoutData(gd2);
		new Label(container, SWT.NONE);

		progressBar = new ProgressBar(container, SWT.BORDER | SWT.SMOOTH);
		progressBar.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		GridData gd_progressBar = new GridData(SWT.FILL, SWT.FILL, false, false, 6, 1);
		gd_progressBar.heightHint = 20;
		progressBar.setLayoutData(gd_progressBar);
//		progressBar.setFont(new Font("American Typewriter", 20, SWT.NORMAL));
		progressBar.setBounds(100, 10, 200, SWT.NONE);

		monitor = new GlobalProgressMonitor();

		Job.getJobManager().setProgressProvider(new ProgressProvider() {
			@Override
			public IProgressMonitor createMonitor(Job job) {
				return monitor.addJob(job);
			}
		});

		updateProfiles();

		return container;
	}

	private void updateProfiles() {
		// TODO
		// workspaces = WorkspaceData.getWorkspaceData();
//		String profileNames[] = new String[workspaces.length];
//		for (int i = 0; i < workspaces.length; i++) {
//			profileNames[i] = workspaces[i].getDisplayName();
//		}
	}

	private void checkWorkspace() {
		Job job = new Job("Check Connection") {
			protected IStatus run(IProgressMonitor monitor) {
				subMonitor = SubMonitor.convert(monitor, 2);
				subMonitor.split(1);
				sync.asyncExec(() -> {
					workspaceHandler = null;
					try {
						if (Util.isAvailable(message)) {
							message.setText("");
							workspaceHandler = WorkspaceHandler.newInstance(profile.getText(), connectionString.getText(), logger);
							btnOK.setEnabled(workspaceHandler.checkConnection(username.getText(), password.getText(), applicationArea.getText()));
							profile.setText(workspaceHandler.getProfile());
							username.setText(workspaceHandler.getUsername());
							password.setText(workspaceHandler.getPassword());
							applicationArea.setText(workspaceHandler.getApplicationArea());
							connectionString.setText(workspaceHandler.getConnectionString());
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
//		try {
//			Optional<ISecurePreferences> primaryWorkspaceHandler = getSavedPrimaryWorkspaceAccessData(logger);
//			if (primaryWorkspaceHandler.isPresent()) {
//				applicationArea.setText(//
//						primaryWorkspaceHandler.get().get(URL, null));
//				password.setText(//
//						primaryWorkspaceHandler.get().get(PASSWORD, "Minova+0"));
//				username.setText(//
//						primaryWorkspaceHandler.get().get(USER, "sa"));
//			} else {
//				text.setText("file:/Users/bauer/Documents/Entwicklung/MINOVA");
//				password.setText("Minova+0");
//				username.setText("sa");
//			}
//		} catch (StorageException e) {
//			throw new RuntimeException(e);
//		}
		btnOK = createButton(parent, IDialogConstants.OPEN_ID, IDialogConstants.OPEN_LABEL, true);
		btnConnect = createButton(parent, IDialogConstants.RETRY_ID, "Check", false);
		btnOK.setEnabled(false);
		btnConnect.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				checkWorkspace();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		btnOK.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				okPressed();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
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
		// TODO
		// WorkspaceData wd = new WorkspaceData();
//		try {
//			wd.setConnection(new URL(text.getText()));
//		} catch (MalformedURLException e) {
//			// kann nicht kommen
//		}
//		wd.setProfile(workspaceHandler.getProfile());
//		wd.setUsername(username.getText());
//		wd.setPassword(password.getText());
//		workspaceHandler.open();
		super.okPressed();
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