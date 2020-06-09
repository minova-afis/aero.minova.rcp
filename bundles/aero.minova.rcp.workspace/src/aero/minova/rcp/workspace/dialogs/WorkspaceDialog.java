package aero.minova.rcp.workspace.dialogs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
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

import aero.minova.rcp.workspace.WorkspaceException;
import aero.minova.rcp.workspace.handler.WorkspaceHandler;

@SuppressWarnings("restriction")
public class WorkspaceDialog extends Dialog {

	private Text username;
	private Text password;
	private Text text;
	private Button btnOK;
	private Button btnConnect;
	private Text message;
	private Text connectionString;
	private Text remoteUsername;
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
		GridLayout layout = new GridLayout(3, false);
		layout.marginRight = 5;
		layout.marginLeft = 10;
		container.setLayout(layout);

		Label lblProfile = new Label(container, SWT.NONE);
		lblProfile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblProfile.setText("Profile");

		profile = new Combo(container, SWT.NONE);
		profile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		profile.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int i = profile.getSelectionIndex();
				logger.info("Item " + i + " selected");
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		new Label(container, SWT.NONE);

		Label lblUser = new Label(container, SWT.NONE);
		lblUser.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUser.setText("Username");

		username = new Text(container, SWT.BORDER);
		username.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		// username.setText(workspaceData.getUsername());
		username.addModifyListener(e -> {
			Text textWidget = (Text) e.getSource();
			String userText = textWidget.getText();
			// workspaceData.setUsername(userText);
		});

		Label lblPassword = new Label(container, SWT.NONE);
		lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPassword.setText("Password");

		password = new Text(container, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		// TODO
		// password.setText(workspaceData.getPassword());
		password.addModifyListener(e -> {
			Text textWidget = (Text) e.getSource();
			String passwordText = textWidget.getText();
			// TODO
			// workspaceData.setPassword(passwordText);
		});

		Label lblApplicationArea = new Label(container, SWT.NONE);
		lblApplicationArea.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblApplicationArea.setText("Application Area");

		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnNewButton = new Button(container, SWT.ARROW | SWT.DOWN);
		btnNewButton.setText("List Applications");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Label lblMessage = new Label(container, SWT.NONE);
		lblMessage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMessage.setText("Message");

		message = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
//		message.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		message.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 2));
		new Label(container, SWT.NONE);

		Label lblConnectionString = new Label(container, SWT.NONE);
		lblConnectionString.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblConnectionString.setText("Connection String");

		connectionString = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		connectionString.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);

		Label lblRemoteUsername = new Label(container, SWT.NONE);
		lblRemoteUsername.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblRemoteUsername.setText("Remote Username");

		remoteUsername = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		remoteUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		progressBar = new ProgressBar(container, SWT.SMOOTH);
		progressBar.setBounds(100, 10, 200, 20);
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(container, SWT.NONE);

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
				subMonitor = SubMonitor.convert(monitor, 20);
				for (int i = 0; i < 21; i++) {
					try {
						TimeUnit.SECONDS.sleep(0);
						subMonitor.split(1);
						sync.asyncExec(() -> {
							workspaceHandler = null;
							try {
								message.setText("");
								workspaceHandler = WorkspaceHandler.newInstance(new URL(text.getText()), logger);
								btnOK.setEnabled(
										workspaceHandler.checkConnection(username.getText(), password.getText()));
							} catch (MalformedURLException | WorkspaceException e1) {
								logger.error(e1);
								message.setText(e1.getMessage());
								btnOK.setEnabled(false);
							}
							if (workspaceHandler != null) {
								connectionString.setText(workspaceHandler.getConnectionString());
								remoteUsername.setText(workspaceHandler.getRemoteUsername());
								profile.setText(workspaceHandler.getDisplayName());
							}
						});
					} catch (InterruptedException e) {
						return Status.CANCEL_STATUS;
					}
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		text.setText("file:/Users/saak/Documents/Entwicklung/MINOVA");
		btnOK = createButton(parent, IDialogConstants.OPEN_ID, IDialogConstants.OPEN_LABEL, true);
		btnConnect = createButton(parent, IDialogConstants.RETRY_ID, "Check", false);
		btnOK.setEnabled(false);
		btnConnect.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				checkWorkspace();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);

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
					progressBar.setSelection(progressBar.getSelection() + work);
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
								if (runningTasks > 0) {
									// --- some tasks are still running ---
									progressBar.setToolTipText("Currently running: " + runningTasks);

								} else {
									// --- all tasks are done (a reset of selection could also be done) ---
									progressBar.setToolTipText("No background progress running.");
									progressBar.setSelection(0);
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
		// TODO
		// return workspaceData.getUsername();
		return "";
	}

	public void setUsername(String username) {
		// TODO
		// workspaceData.setUsername(username);
	}

	public String getPassword() {
		// TODO
		// return workspaceData.getPassword();
		return "";
	}

	public void setPassword(String password) {
		// TODO
		// workspaceData.setPassword(password);
	}

}