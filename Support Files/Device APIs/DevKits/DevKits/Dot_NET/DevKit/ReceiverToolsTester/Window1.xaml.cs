using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Linq;
using System.Diagnostics;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Threading;
using System.Xml;

using DexCom.Common;
using DexCom.ReceiverTools;

namespace ReceiverToolsTester
{
	/// <summary>
	/// Interaction logic for Window1.xaml
	/// </summary>
	public partial class Window1 : Window
	{
		private const string TraceTitle = "Trace:MainWindow";
		private event Action<string, object> PostMessage;
		private bool m_isLoaded = false;

		private bool m_displayDatabaseSynchronizationMessage = false;
		private ReceiverScanner m_receiverScanner = new ReceiverScanner();
		private ObservableCollection<string> m_messageCollection = new ObservableCollection<string>();

        private List<DexCom.ReceiverApi.DeviceRegistryInfo> m_attachedReceivers = null;
        private List<ReceiverContext> m_contexts = null;

		#region Window Lifetime
		public Window1()
		{
			// Wire up some lifetime events.
			Initialized += Window_Initialized;
			Loaded += Window_Loaded;
			Closing += Window_Closing;

			InitializeComponent();
		}

		private void Window_Initialized(object sender, EventArgs e)
		{
			// This and our children have been initialized.  Parents not yet finished initializing.
			DoAddMessage("Main Window Initialized");

			// Wire up our own custom message handler.
			PostMessage += OnPostedMessage;
		}

		private void Window_Loaded(object sender, RoutedEventArgs e)
		{
			// After initialization, after layout, before rendered: This, our children, and parents are loaded and ready.
			try
			{
                m_contexts = new List<ReceiverContext>(m_ctrlWindowTabs.Items.Count - 1);

				m_receiverScanner.StateChangedEvent += ReceiverScanner_StateChangedEvent;
				m_receiverScanner.ReceiverContextCreatedEvent += ReceiverScanner_ReceiverContextCreatedEvent;
				m_receiverScanner.ReceiverContextRemovedEvent += ReceiverScanner_ReceiverContextRemovedEvent;
                m_receiverScanner.UnhandledExceptionDuringScanning += ReceiverScanner_UnhandledExceptionDuringScanning;

                // Change frequency that scanner checks for receivers.
				m_receiverScanner.Interval = TimeSpan.FromSeconds(1.0);

                DisplayableReceiverScanner drs = new DisplayableReceiverScanner()
                {
                    ReceiverScanner = m_receiverScanner,
                    ScannerState = m_receiverScanner.CurrentState.ToString(),
                    ScanInterval = m_receiverScanner.Interval.TotalSeconds
                };

                drs.PropertyChanged += new PropertyChangedEventHandler(drs_PropertyChanged);
                m_receiverScanner.Tag = drs;
                m_ctrlTestingTab.DataContext = drs;

				// Initialize our controls.
				m_ctrlMessages.ItemsSource = m_messageCollection;

				m_isLoaded = true;
				DoUpdateWindow();

				DoAddMessage("Using Dexcom Receiver Tools DevKit assembly version: " + Utils.GetAssemblyVersion());
				DoAddMessage("Main Window Loaded");
			}
			catch (Exception exception)
			{
				MessageBox.Show(this, "Unhandled Exception: " + exception.Message, Title);
				FireMessage("DoSafeClose");
			}
		}

		private void Window_Closing(object sender, System.ComponentModel.CancelEventArgs e)
		{
			m_isLoaded = false;

			// Unwire all the subscribed events.
			Initialized -= Window_Initialized;
			Loaded -= Window_Loaded;
			Closing -= Window_Closing;
			PostMessage -= OnPostedMessage;

			// Explicit "cleanup" just so that we don't get events while our window is closing.
			m_receiverScanner.ClearAllEventsBeforeExit();
		}
		#endregion

		#region Window Lifetime Utilities and Messaging
		// Fire a command asynchronously to ourselves.
		private void FireMessage(string command)
		{
			if (m_isLoaded)
			{
				FireMessage(command, null);
			}
		}

		// Fire a command and message argument asynchronously to ourselves.
		private void FireMessage(string command, object message)
		{
			if (m_isLoaded)
			{
				FireMessage(DispatcherPriority.Normal, command, message);
			}
		}

		private void FireMessage(DispatcherPriority priority, string command, object message)
		{
			if (m_isLoaded)
			{
				Dispatcher.BeginInvoke(priority, PostMessage, command, message);
			}
		}

		private void OnPostedMessage(string command, object message)
		{
			try
			{
				switch (command)
				{
					case "DoUpdateWindow":
					{
						DoUpdateWindow();
						break;
					}
					case "DoShowMessage":
					{
						MessageBox.Show(this, message as string);
						break;
					}
					case "DoAddMessage":
					{
						DoAddMessage(message as string);
						break;
					}
					case "DoShowUnhandledException":
					{
						MessageBox.Show(this, "Unhandled Exception: " + message as string, null, MessageBoxButton.OK, MessageBoxImage.Error, MessageBoxResult.None);
						break;
					}
					case "DoSafeClose":
					{
						DoSafeClose((bool?)message);
						break;
					}
					case "DoAddReceiverContext":
					{
						DoAddReceiverContext(message as ReceiverContext);
						break;
					}
					case "DoRemoveReceiverContext":
					{
						DoRemoveReceiverContext(message as ReceiverContext);
						break;
					}
					case "DoUpdateDisplayableReceiverContextGlucoseRecords":
					{
						DoUpdateDisplayableReceiverContextGlucoseRecords(message as ReceiverContext);
						break;
					}
					case "DoUpdateDisplayableReceiverContextMeterRecords":
					{
						DoUpdateDisplayableReceiverContextMeterRecords(message as ReceiverContext);
						break;
					}
					case "DoUpdateDisplayableReceiverContextSettingsRecords":
					{
						DoUpdateDisplayableReceiverContextSettingsRecords(message as ReceiverContext);
						break;
					}
                    case "DoUpdateDisplayableReceiverContextInsertionTimeRecords":
                    {
                        DoUpdateDisplayableReceiverContextInsertionTimeRecords(message as ReceiverContext);
                        break;
                    }
                    case "DoUpdateReceiverState":
                    {
                        DoUpdateReceiverState(message as ReceiverContext);
                        break;
                    }
                    case "DoUpdateScannerState":
                    {
                        DoUpdateScannerState();
                        break;
                    }
                    default:
					{
						throw new DexComException("Unknown command string in OnPostedMessage: " + command);
					}
				}
			}
			catch (Exception exception)
			{
				MessageBox.Show(this, "Unhandled Exception: " + exception.Message, Title);
			}
		}

		private void DoSafeClose(bool? result)
		{
			foreach (Window window in OwnedWindows)
			{
				window.Close();
			}

			Close();
		}

		private void DoUpdateWindow()
		{
			if (m_isLoaded)
			{
				bool did_tabs_change = false;

				foreach (TabItem tab in m_ctrlWindowTabs.Items)
				{
					if (tab != null && tab != m_ctrlTestingTab)
					{
						DisplayableReceiverContext context = tab.DataContext as DisplayableReceiverContext;

						Visibility prev_visibility = tab.Visibility;
						Visibility new_visibility = context == null ? Visibility.Collapsed : System.Windows.Visibility.Visible;

						if (new_visibility != prev_visibility)
						{
							tab.Visibility = new_visibility;
							did_tabs_change = true;
						}
					}
				}

				if (did_tabs_change)
				{
					m_ctrlWindowTabs.SelectedItem = m_ctrlTestingTab;
				}
			}
		}
		#endregion

		#region Subscriptions and Callbacks
		void ReceiverScanner_ReceiverContextRemovedEvent(ReceiverScanner sender, ReceiverContext receiverContext)
		{
			FireMessage("DoAddMessage", string.Format("Receiver Detached: Serial#={0}, Port={1}, Database ID={2}", receiverContext.SerialNumber, receiverContext.RegistryInfo.PortName, receiverContext.ReceiverId));

			receiverContext.ClearAllEventsBeforeExit();
			receiverContext.RequestExit();

			FireMessage("DoRemoveReceiverContext", receiverContext);
			FireMessage("DoUpdateWindow");
		}

		void ReceiverScanner_ReceiverContextCreatedEvent(ReceiverScanner sender, ReceiverContext receiverContext)
		{
            m_contexts.Add(receiverContext);

			// Scanner just found a newly attached receiver.
            string message = string.Format("Receiver Attached: Serial#={0}, Port={1}, Database ID={2}\n{3}", 
                receiverContext.SerialNumber, 
                receiverContext.RegistryInfo.PortName, 
                receiverContext.ReceiverId, 
                DoDumpObjectProperties(receiverContext.RegistryInfo));

			FireMessage("DoAddMessage", message);

			// Subscribe to the "stuff" the receiver context will publish to us.
            receiverContext.StateChangedEvent += ReceiverContext_StateChangedEvent;
			receiverContext.NewEstimatedGlucoseRecordEvent += ReceiverContext_NewEstimatedGlucoseRecordEvent;
			receiverContext.NewMeterRecordEvent += ReceiverContext_NewMeterRecordEvent;
			receiverContext.NewSettingsRecordEvent += ReceiverContext_NewSettingsRecordEvent;
			receiverContext.NewInsertionTimeRecordEvent += ReceiverContext_NewInsertionTimeRecordEvent;
			receiverContext.DatabaseRecordsSynchronized += ReceiverContext_DatabaseRecordsSynchronized;
			receiverContext.InitialBackgroundSynchronizationCompleted += receiverContext_InitialBackgroundSynchronizationCompleted;
			receiverContext.UnhandledExceptionDuringSynchronization += ReceiverContext_UnhandledExceptionDuringSynchronization;

			// Change frequency that receiver context will check for new records.
			receiverContext.Interval = TimeSpan.FromSeconds(5);

			// Start the receiver context listening and publishing information to us.
			receiverContext.RunInBackground();

			FireMessage("DoAddReceiverContext", receiverContext);
			FireMessage("DoUpdateWindow");
		}

        void ReceiverScanner_UnhandledExceptionDuringScanning(ReceiverScanner sender, Exception exception)
        {
            FireMessage("DoAddMessage", string.Format("Receiver Scanner : UNHANDLED EXCEPTION = {0}", exception.ToString()));

            // Good place to decide if you want to halt the Receiver Scanner or just keep going.

            //receiverScanner.RequestExit(); // Just let the scanner know we want to exit but don't wait.
            //receiverScanner.Exit(); // Let the scanner know we want to exit and wait for exit to completely finish.
            //receiverScanner.Abort(); // Let the scanner know we want to abort but don't wait.  Similar to exit but skips any cleanup code and set the Aborted state.
        }

		void ReceiverScanner_StateChangedEvent(object sender, OperationStateChangedEventArgs e)
		{
            FireMessage("DoUpdateScannerState");
			FireMessage("DoAddMessage", string.Format("Receiver Detection Tool has OperationStateChange {0} => {1}", e.PriorState.ToString(), e.CurrentState.ToString()));

			ReceiverScanner scanner = sender as ReceiverScanner;
			if (e.CurrentState == LongRunningOperationState.Aborted && scanner != null && scanner.AbortedException != null)
			{
				// Dump any reason for why the operation (background scanner) aborted.
                FireMessage("DoAddMessage", "Receiver Scanner ABORTED: " + scanner.AbortedException.ToString());
			}
		}

		void ReceiverContext_UnhandledExceptionDuringSynchronization(ReceiverContext receiverContext, Exception exception)
		{
			FireMessage("DoAddMessage", string.Format("Receiver Context {0} : UNHANDLED EXCEPTION = {1}", receiverContext.SerialNumber, exception.ToString()));

			// Good place to decide if you want to halt the Receiver Context or just keep going.
			
			//receiverContext.RequestExit(); // Just let the context know we want to exit but don't wait.
			//receiverContext.Exit(); // Let the context know we want to exit and wait for exit to completely finish.
			//receiverContext.Abort(); // Let the context know we want to abort but don't wait.  Similar to exit but skips any cleanup code and set the Aborted state.
		}

		void ReceiverContext_NewEstimatedGlucoseRecordEvent(ReceiverContext receiverContext, EstimatedGlucoseRecord record)
		{
			FireMessage("DoAddMessage", string.Format("Receiver {0} has new EGV REC#{1}: {2} @ {3}", receiverContext.SerialNumber, record.RecordNumber, record.Value, record.DisplayTime));
			FireMessage("DoUpdateDisplayableReceiverContextGlucoseRecords", receiverContext);
		}

		void ReceiverContext_NewMeterRecordEvent(ReceiverContext receiverContext, MeterRecord record)
		{
			FireMessage("DoAddMessage", string.Format("Receiver {0} has new METER REC#{1}: {2} @ {3}", receiverContext.SerialNumber, record.RecordNumber, record.Value, record.MeterDisplayTime));
			FireMessage("DoUpdateDisplayableReceiverContextMeterRecords", receiverContext);
		}

		void ReceiverContext_NewSettingsRecordEvent(ReceiverContext receiverContext, SettingsRecord record)
		{
			FireMessage("DoAddMessage", string.Format("Receiver {0} has new SETTINGS REC#{1} @ {2}", receiverContext.SerialNumber, record.RecordNumber, record.DisplayTime));
			FireMessage("DoUpdateDisplayableReceiverContextSettingsRecords", receiverContext);
		}

		void ReceiverContext_NewInsertionTimeRecordEvent(ReceiverContext receiverContext, InsertionTimeRecord record)
		{
			FireMessage("DoAddMessage", string.Format("Receiver {0} has new INSERTION REC#{1} @ {2}: IsInserted={3}, InsertionDisplayTime={4}, State={5}", receiverContext.SerialNumber, record.RecordNumber, record.DisplayTime, record.IsInserted, record.InsertionDisplayTime, record.SessionState.ToString()));
            FireMessage("DoUpdateDisplayableReceiverContextInsertionTimeRecords", receiverContext);
		}

		void ReceiverContext_DatabaseRecordsSynchronized(object sender, EventArgs e)
		{
			// Each and every time the receiver context checks for new records.
			ReceiverContext receiver_context = sender as ReceiverContext;
			if (receiver_context != null)
			{
				// A bit noisy ... so we let the user decide.
				if (m_displayDatabaseSynchronizationMessage)
				{
					FireMessage("DoAddMessage", string.Format("Receiver {0} database records synchronized.", receiver_context.SerialNumber));
				}
			}
		}

		void receiverContext_InitialBackgroundSynchronizationCompleted(object sender, EventArgs e)
		{
			// ReceiverContext is running (just started running) in the background and has completed initial database sync 
			// ... good opportunity to update the displayble values to the screen since initial database records don't fire events.
			ReceiverContext receiver_context = sender as ReceiverContext;
			if (receiver_context != null)
			{
				FireMessage("DoAddMessage", string.Format("Receiver {0} database records synchronized for the first time ... updating displayable records.", receiver_context.SerialNumber));

				FireMessage("DoUpdateDisplayableReceiverContextGlucoseRecords", receiver_context);
				FireMessage("DoUpdateDisplayableReceiverContextMeterRecords", receiver_context);
				FireMessage("DoUpdateDisplayableReceiverContextSettingsRecords", receiver_context);
                FireMessage("DoUpdateDisplayableReceiverContextInsertionTimeRecords", receiver_context);
			}
		}

		void ReceiverContext_StateChangedEvent(object sender, OperationStateChangedEventArgs e)
		{
			ReceiverContext receiver_context = sender as ReceiverContext;
			if (receiver_context != null)
			{
                FireMessage("DoUpdateReceiverState", receiver_context);
                FireMessage("DoAddMessage", string.Format("Receiver {0} has OperationStateChange {1} => {2}", receiver_context.SerialNumber, e.PriorState.ToString(), e.CurrentState.ToString()));

				if (e.CurrentState == LongRunningOperationState.Aborted && receiver_context.AbortedException != null)
				{
					// Dump any reason for why the operation (receiver database synching) aborted.
					FireMessage("DoAddMessage", "Receiver Context ABORTED: " + receiver_context.AbortedException.ToString());
				}
			}
		}

        void drs_PropertyChanged(object sender, PropertyChangedEventArgs e)
        {
            string prop = e.PropertyName;

            if (String.Compare("ScanInterval", prop, false) == 0)
            {
                FireMessage("DoAddMessage", String.Format("Scan interval changed to {0} seconds", (m_receiverScanner.Tag as DisplayableReceiverScanner).ScanInterval));
            }
        }

        void drc_PropertyChanged(object sender, PropertyChangedEventArgs e)
        {
            string prop = e.PropertyName;

            if (String.Compare("SyncInterval", prop, false) == 0)
            {
                ReceiverContext context = (sender as DisplayableReceiverContext).ReceiverContext;

                if (context != null)
                {
                    FireMessage("DoAddMessage", String.Format("Receiver {0} sync interval changed to {1} seconds", context.SerialNumber, context.Interval.TotalSeconds));
                }
            }
        }
		#endregion

		#region Private Implementation
		/// <summary>
		/// Sample method for checking, prompting, and installing Dexcom's VCP Driver if needed.  Note that the actual driver installation executable is "embedded" in the Dexcom provided library/assembly.
		/// </summary>
		private void DoCheckForVirtualComPortDriver()
		{
			try
			{
				DoAddMessage("VCP Driver Check ... START.");

				bool is_vcp_1002_driver_installed = Utils.IsDriverInstalled();
				bool is_administrator = Utils.IsAdministrator();
				bool is_really_admin = Utils.IsReallyAdministrator();
				bool is_evidence_of_receiver_ever_attached = Utils.IsEvidenceOfReceiverEverAttached();

				DoAddMessage(string.Format("G4 VCP Driver Check ... IsInstalled={0}, IsCurrentlyAdmin={1}, IsReallyAdmin={2}, IsEvidenceOfPriorAttached={3}", is_vcp_1002_driver_installed, is_administrator, is_really_admin, is_evidence_of_receiver_ever_attached));

				if (!is_vcp_1002_driver_installed)
				{
					if (is_administrator || is_really_admin)
					{
						// NOTE ... WinXP seems to install with less pop-ups when the device is attached.  While Vista/Win7 seem to install better when detached.
						// string os_kind = DexCom.Common.Data.Tools.GetOperatingSystemKind();

						MessageBoxResult result = MessageBox.Show(
							this,
							"This application requires a virtual USB/COM driver to communicate with the DexCom Receiver.  Please remove any DexCom Receivers attached to the computer and then press OK to begin the driver installation application.",
							"DexCom Driver required for communication with receiver.",
							MessageBoxButton.OKCancel,
							MessageBoxImage.Question,
							MessageBoxResult.OK);

						if (result == MessageBoxResult.OK)
						{
							DoAddMessage("VCP Driver Check ... Prompt to install VCP Driver OK'ed.");

							bool run_as_admin_requested = false;

							if (!is_administrator && is_really_admin)
							{
								run_as_admin_requested = true;
								DoAddMessage("G4 VCP Driver Check ... Launching driver package installer with 'RUNAS' verb.");
							}

							Utils.RunDriverSetup(run_as_admin_requested);

							// NOTE:  We don't get any indication if the user canceled deeper into the installation (i.e. Hardware Wizard)
							// FORNOW ... perhaps we should check again for driver before giving prompt/succeeded message!

							MessageBox.Show(this, "Please attach (or unplug and reattach) the receiver to complete the installation.", "Pre-Installation of the DexCom Driver succeeded.");
						}
						else
						{
							DoAddMessage("VCP Driver Check ... Prompt to install VCP Driver cancelled.");
						}
					}
					else
					{
						MessageBox.Show(this, "Administrative rights are required to install the DexCom Driver.  Please run this application with administrative rights.", "DexCom Driver installation requires administrative rights.");
					}
				}
				else if (!is_evidence_of_receiver_ever_attached)
				{
					MessageBox.Show(this, "Please attach (or unplug and reattach) the receiver to complete the installation of the DexCom Driver.", "DexCom Driver already pre-installed.");
				}
				else
				{
					// Installed and we should be good to go!
					DoAddMessage("VCP Driver Check ... Already installed and ready to go!");
				}

				DoAddMessage("VCP Driver Check ... DONE.");
			}
			catch (Exception exception)
			{
				DoAddMessage("VCP Driver Check ... FAILED: " + exception.ToString());
				MessageBox.Show("Unhandled Exception: ", exception.Message);
			}
		}

		private void DoAddMessage(string message)
		{
			m_messageCollection.Insert(0, string.Format("{0} => {1}", DateTime.Now.ToString("ddd' 'HH':'mm':'ss'.'fff"), message));
		}

		private void DoAddReceiverContext(ReceiverContext receiverContext)
		{
			if (receiverContext != null)
			{
				foreach (TabItem tab in m_ctrlWindowTabs.Items)
				{
					if (tab != null && tab != m_ctrlTestingTab)
					{
						DisplayableReceiverContext context = tab.DataContext as DisplayableReceiverContext;

						if (context == null)
						{
							DisplayableReceiverContext drc = new DisplayableReceiverContext() 
                            {
                                ReceiverContext = receiverContext, 
                                ReceiverState = receiverContext.CurrentState.ToString(),
                                SyncInterval = receiverContext.Interval.TotalSeconds
                            };

                            drc.PropertyChanged += new PropertyChangedEventHandler(drc_PropertyChanged);
							receiverContext.Tag = drc;
							tab.DataContext = drc;
							break;
						}
					}
				}
			}

			DoUpdateWindow();
		}

		private void DoRemoveReceiverContext(ReceiverContext receiverContext)
		{
			if (receiverContext != null)
			{
				foreach (TabItem tab in m_ctrlWindowTabs.Items)
				{
					if (tab != null && tab != m_ctrlTestingTab)
					{
						DisplayableReceiverContext context = tab.DataContext as DisplayableReceiverContext;

						if (context != null && context.ReceiverContext == receiverContext)
						{
							tab.DataContext = null;
							break;
						}
					}
				}
			}

			DoUpdateWindow();
		}

        private void DoUpdateReceiverState(ReceiverContext receiverContext)
        {
            if (receiverContext != null)
            {
                foreach (TabItem tab in m_ctrlWindowTabs.Items)
                {
                    if (tab != null && tab != m_ctrlTestingTab)
                    {
                        DisplayableReceiverContext display_context = tab.DataContext as DisplayableReceiverContext;

                        if (display_context != null && display_context.ReceiverContext == receiverContext)
                        {
                            display_context.ReceiverState = display_context.ReceiverContext.CurrentState.ToString();
                            
                            break;
                        }
                    }
                }
            }

            DoUpdateWindow();
        }

        private void DoUpdateScannerState()
        {
            (m_receiverScanner.Tag as DisplayableReceiverScanner).ScannerState = m_receiverScanner.CurrentState.ToString();

            DoUpdateWindow();
        }

		private void DoUpdateDisplayableReceiverContextGlucoseRecords(ReceiverContext receiverContext)
		{
			if (receiverContext != null)
			{
				foreach (TabItem tab in m_ctrlWindowTabs.Items)
				{
					if (tab != null && tab != m_ctrlTestingTab)
					{
						DisplayableReceiverContext display_context = tab.DataContext as DisplayableReceiverContext;

						if (display_context != null && display_context.ReceiverContext == receiverContext)
						{
							int last_index = display_context.ReceiverContext.EstimatedGlucoseRecords.LogicalLastIndex;

							if (last_index > 0 && last_index > display_context.LastEstimatedGlucoseRecordIndex)
							{
								for (int i = display_context.LastEstimatedGlucoseRecordIndex + 1; i < last_index; i++)
								{
									display_context.AddEstimatedGlucoseRecord(display_context.ReceiverContext.EstimatedGlucoseRecords.GetAtLogicalIndex(i));
									display_context.LastEstimatedGlucoseRecordIndex = i;
								}
							}
							break;
						}
					}
				}
			}

			DoUpdateWindow();
		}

		private void DoUpdateDisplayableReceiverContextMeterRecords(ReceiverContext receiverContext)
		{
			if (receiverContext != null)
			{
				foreach (TabItem tab in m_ctrlWindowTabs.Items)
				{
					if (tab != null && tab != m_ctrlTestingTab)
					{
						DisplayableReceiverContext display_context = tab.DataContext as DisplayableReceiverContext;

						if (display_context != null && display_context.ReceiverContext == receiverContext)
						{
							int last_index = display_context.ReceiverContext.MeterRecords.LogicalLastIndex;

							if (last_index > 0 && last_index > display_context.LastMeterRecordIndex)
							{
								for (int i = display_context.LastMeterRecordIndex + 1; i < last_index; i++)
								{
									display_context.AddMeterRecord(display_context.ReceiverContext.MeterRecords.GetAtLogicalIndex(i));
									display_context.LastMeterRecordIndex = i;
								}
							}
							break;
						}
					}
				}
			}

			DoUpdateWindow();
		}

		private void DoUpdateDisplayableReceiverContextSettingsRecords(ReceiverContext receiverContext)
		{
			if (receiverContext != null)
			{
				foreach (TabItem tab in m_ctrlWindowTabs.Items)
				{
					if (tab != null && tab != m_ctrlTestingTab)
					{
						DisplayableReceiverContext display_context = tab.DataContext as DisplayableReceiverContext;

						if (display_context != null && display_context.ReceiverContext == receiverContext)
						{
							int last_index = display_context.ReceiverContext.SettingsRecords.LogicalLastIndex;

							if (last_index > 0 && last_index > display_context.LastSettingsRecordIndex)
							{
								for (int i = display_context.LastSettingsRecordIndex + 1; i < last_index; i++)
								{
									display_context.AddSettingsRecord(display_context.ReceiverContext.SettingsRecords.GetAtLogicalIndex(i));
									display_context.LastSettingsRecordIndex = i;
								}
							}
							break;
						}
					}
				}
			}

			DoUpdateWindow();
		}

        private void DoUpdateDisplayableReceiverContextInsertionTimeRecords(ReceiverContext receiverContext)
        {
            if (receiverContext != null)
            {
                foreach (TabItem tab in m_ctrlWindowTabs.Items)
                {
                    if (tab != null && tab != m_ctrlTestingTab)
                    {
                        DisplayableReceiverContext display_context = tab.DataContext as DisplayableReceiverContext;

                        if (display_context != null && display_context.ReceiverContext == receiverContext)
                        {
                            int last_index = display_context.ReceiverContext.InsertionTimeRecords.LogicalLastIndex;

                            if (last_index > 0 && last_index > display_context.LastInsertionTimeRecordIndex)
                            {
                                for (int i = display_context.LastInsertionTimeRecordIndex + 1; i < last_index; i++)
                                {
                                    display_context.AddInsertionTimeRecord(display_context.ReceiverContext.InsertionTimeRecords.GetAtLogicalIndex(i));
                                    display_context.LastInsertionTimeRecordIndex = i;
                                }
                            }
                            break;
                        }
                    }
                }
            }

            DoUpdateWindow();
        }

        private string DoDumpObjectProperties<T>(T item)
        {
            StringBuilder sb = new StringBuilder();

            sb.Append(Environment.NewLine);

            foreach (PropertyDescriptor descriptor in TypeDescriptor.GetProperties(item))
            {
                sb.Append(String.Format("   {0}={1}" + Environment.NewLine, descriptor.Name, descriptor.GetValue(item)));
            }

            return sb.ToString();
        }
		#endregion

		#region UI Handlers

        #region Scanner
        private void m_ctrlScanForReceivers_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                m_attachedReceivers = Utils.ScanForAttachedReceivers();

				if (m_attachedReceivers.Count > 0)
				{
					m_ctrlStartReceiverDetection.IsEnabled = false; // This app doesn't handle auto and manual at the same time.
				}

                foreach (var device in m_attachedReceivers)
                {
                    ReceiverContext context = new ReceiverContext(device);
                    m_contexts.Add(context);

                    // Found a newly attached receiver.
                    string message = string.Format("Receiver Attached: Serial#={0}, Port={1}, Database ID={2}\n{3}",
                        context.SerialNumber,
                        context.RegistryInfo.PortName,
                        context.ReceiverId,
                        DoDumpObjectProperties(context.RegistryInfo));

                    DoAddMessage(message);

                    // Subscribe to the "stuff" the receiver context will publish to us.
                    context.StateChangedEvent += ReceiverContext_StateChangedEvent;
                    context.NewEstimatedGlucoseRecordEvent += ReceiverContext_NewEstimatedGlucoseRecordEvent;
                    context.NewMeterRecordEvent += ReceiverContext_NewMeterRecordEvent;
                    context.NewSettingsRecordEvent += ReceiverContext_NewSettingsRecordEvent;
                    context.NewInsertionTimeRecordEvent += ReceiverContext_NewInsertionTimeRecordEvent;
                    context.DatabaseRecordsSynchronized += ReceiverContext_DatabaseRecordsSynchronized;
                    context.InitialBackgroundSynchronizationCompleted += receiverContext_InitialBackgroundSynchronizationCompleted;
                    context.UnhandledExceptionDuringSynchronization += ReceiverContext_UnhandledExceptionDuringSynchronization;

                    context.Interval = TimeSpan.FromSeconds(5);

                    DoAddReceiverContext(context);
                    DoUpdateWindow();
                }

                DoUpdateWindow();
            }
            catch (Exception exception)
            {
                DoAddMessage("Unhandled Exception: " + exception.ToString());
                FireMessage("DoShowUnhandledException", exception.Message);
            }
        }

        private void m_ctrlCheckForDrivers_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                DoCheckForVirtualComPortDriver();
            }
            catch (Exception exception)
            {
                DoAddMessage("Unhandled Exception: " + exception.ToString());
                FireMessage("DoShowUnhandledException", exception.Message);
            }
        }

        private void m_ctrlExtractDrivers_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                string filename = string.Empty;
                byte[] executable = null;

                Utils.ExtractDriverSetup(out filename, out executable);

                Trace.Assert(string.IsNullOrEmpty(filename) == false, "Failed to extract driver file name.");
                Trace.Assert(executable != null, "Failed to extract driver executable.");

                DoAddMessage(string.Format("Driver='{0}', Bytes={1}", filename, executable.Length));
            }
            catch (Exception exception)
            {
                DoAddMessage("Unhandled Exception: " + exception.ToString());
                FireMessage("DoShowUnhandledException", exception.Message);
            }
        }

        private void m_ctrlCheckInternetTime_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                // Manually get connection status and time offset to pooled internet time servers.
                bool is_connected = false;
                TimeSpan internet_offset = Utils.InternetTimeOffset(out is_connected);
                DoAddMessage(string.Format("Internet Time Offset: Connected = {0}, Offset = {1}", is_connected, internet_offset));

                if (is_connected)
                {
                    // This is really just the same thing as DateTime.Now + internet_offset.

                    DateTime dt_internet = Utils.InternetTime();
                    DoAddMessage("Local Time (Adjusted to Internet) = " + dt_internet.ToString("yyyy'-'MM'-'dd' 'HH':'mm':'ss'.'fff"));
                }
            }
            catch (Exception exception)
            {
                DoAddMessage("Unhandled Exception: " + exception.ToString());
                FireMessage("DoShowUnhandledException", exception.Message);
            }
        }

        private void m_ctrlClearScannerEvents_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                m_receiverScanner.ClearAllEventsBeforeExit();
                DoAddMessage(string.Format("Cleared scanner events"));
            }
            catch (Exception exception)
            {
                DoAddMessage("Unhandled Exception: " + exception.ToString());
                FireMessage("DoShowUnhandledException", exception.Message);
            }
        }

        private void m_ctrlStartReceiverDetection_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                m_receiverScanner.RunInBackground();

				m_ctrlScanForReceivers.IsEnabled = false; // This app doesn't handle auto and manual at the same time.
				m_ctrlRemoveReceivers.IsEnabled = false; // This app doesn't handle auto and manual at the same time.

                DoAddMessage(string.Format("Receiver Scanner start requested"));
            }
            catch (Exception exception)
            {
                DoAddMessage("Unhandled Exception: " + exception.ToString());
                FireMessage("DoShowUnhandledException", exception.Message);
            }
        }

        private void m_ctrlResetReceiverDetection_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                m_receiverScanner.Reset();
                DoAddMessage(string.Format("Receiver Scanner reset"));
            }
            catch (Exception exception)
            {
                DoAddMessage("Unhandled Exception: " + exception.ToString());
                FireMessage("DoShowUnhandledException", exception.Message);
            }
        }

        private void m_ctrlPauseReceiverDetection_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                bool status = m_receiverScanner.RequestPause();
                DoAddMessage(string.Format("Scanner pause request returned {0}", status));
            }
            catch (Exception exception)
            {
                DoAddMessage("Unhandled Exception: " + exception.ToString());
                FireMessage("DoShowUnhandledException", exception.Message);
            }
        }

        private void m_ctrlResumeReceiverDetection_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                m_receiverScanner.RequestResume();
                DoAddMessage(string.Format("Receiver Scanner resume requested."));
            }
            catch (Exception exception)
            {
                DoAddMessage("Unhandled Exception: " + exception.ToString());
                FireMessage("DoShowUnhandledException", exception.Message);
            }
        }

        private void m_ctrlStopReceiverDetection_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                m_receiverScanner.RequestExit();
                DoAddMessage(string.Format("Receiver Scanner stop called."));
            }
            catch (Exception exception)
            {
                DoAddMessage("Unhandled Exception: " + exception.ToString());
                FireMessage("DoShowUnhandledException", exception.Message);
            }
        }

        private void m_ctrlAbortReceiverDetection_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                m_receiverScanner.Abort();
                DoAddMessage(string.Format("Receiver Scanner abort called."));
            }
            catch (Exception exception)
            {
                DoAddMessage("Unhandled Exception: " + exception.ToString());
                FireMessage("DoShowUnhandledException", exception.Message);
            }
        }

        private void TraceSync_Checked(object sender, RoutedEventArgs e)
        {
            m_displayDatabaseSynchronizationMessage = true;
        }

        private void TraceSync_Unchecked(object sender, RoutedEventArgs e)
        {
            m_displayDatabaseSynchronizationMessage = false;
        }

        private void m_ctrlRemoveReceivers_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                foreach (var context in m_contexts)
                {
                    context.ClearAllEventsBeforeExit();
                    context.Exit();

                    DoRemoveReceiverContext(context);

                    DoAddMessage(string.Format("Removed Receiver {0}", context.SerialNumber));
                }

                m_contexts.Clear();
            }
            catch (Exception exception)
            {
                DoAddMessage("Unhandled Exception: " + exception.ToString());
                FireMessage("DoShowUnhandledException", exception.Message);
            }
        }
        #endregion

        #region Receiver
        private void ReadTransmitterId_Click(object sender, RoutedEventArgs e)
		{
			try
			{
				TabItem current_tab = m_ctrlWindowTabs.SelectedItem as TabItem;

				if (current_tab != null && current_tab != m_ctrlTestingTab)
				{
					DisplayableReceiverContext display_context = current_tab.DataContext as DisplayableReceiverContext;

					if (display_context != null && display_context.ReceiverContext != null)
					{
						string transmitter_id = display_context.ReceiverContext.ReadTransmitterId();

						DoAddMessage(string.Format("Receiver {0} has Transmitter ID {1}", display_context.ReceiverContext.SerialNumber, transmitter_id));
					}
				}
			}
			catch (Exception exception)
			{
				DoAddMessage("Unhandled Exception: " + exception.ToString());
				FireMessage("DoShowUnhandledException", exception.Message);
			}
		}

		private void ReadDisplayTime_Click(object sender, RoutedEventArgs e)
		{
			try
			{
				TabItem current_tab = m_ctrlWindowTabs.SelectedItem as TabItem;

				if (current_tab != null && current_tab != m_ctrlTestingTab)
				{
					DisplayableReceiverContext display_context = current_tab.DataContext as DisplayableReceiverContext;

					if (display_context != null && display_context.ReceiverContext != null)
					{
						DateTime dt = display_context.ReceiverContext.ReadDisplayTime();

						DoAddMessage(string.Format("Receiver {0} has current display time {1}", display_context.ReceiverContext.SerialNumber, dt.ToString("G")));
					}
				}
			}
			catch (Exception exception)
			{
				DoAddMessage("Unhandled Exception: " + exception.ToString());
				FireMessage("DoShowUnhandledException", exception.Message);
			}
		}

		private void ReadSystemTime_Click(object sender, RoutedEventArgs e)
		{
			try
			{
				TabItem current_tab = m_ctrlWindowTabs.SelectedItem as TabItem;

				if (current_tab != null && current_tab != m_ctrlTestingTab)
				{
					DisplayableReceiverContext display_context = current_tab.DataContext as DisplayableReceiverContext;

					if (display_context != null && display_context.ReceiverContext != null)
					{
						DateTime dt = display_context.ReceiverContext.ReadSystemTime();

						DoAddMessage(string.Format("Receiver {0} has current system time {1}", display_context.ReceiverContext.SerialNumber, dt.ToString("G")));
					}
				}
			}
			catch (Exception exception)
			{
				DoAddMessage("Unhandled Exception: " + exception.ToString());
				FireMessage("DoShowUnhandledException", exception.Message);
			}
		}

        private void ReadGlucoseDisplayUnits_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                TabItem current_tab = m_ctrlWindowTabs.SelectedItem as TabItem;

                if (current_tab != null && current_tab != m_ctrlTestingTab)
                {
                    DisplayableReceiverContext display_context = current_tab.DataContext as DisplayableReceiverContext;

                    if (display_context != null && display_context.ReceiverContext != null)
                    {
                        string units = display_context.ReceiverContext.ReadGlucoseDisplayUnits();

                        DoAddMessage(string.Format("Receiver {0} has glucose diplay units of {1}", display_context.ReceiverContext.SerialNumber, units));
                    }
                }
            }
            catch (Exception exception)
            {
                DoAddMessage("Unhandled Exception: " + exception.ToString());
                FireMessage("DoShowUnhandledException", exception.Message);
            }
        }

        private void ReadFirmwareHeader_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                TabItem current_tab = m_ctrlWindowTabs.SelectedItem as TabItem;

                if (current_tab != null && current_tab != m_ctrlTestingTab)
                {
                    DisplayableReceiverContext display_context = current_tab.DataContext as DisplayableReceiverContext;

                    if (display_context != null && display_context.ReceiverContext != null)
                    {
                        string header = display_context.ReceiverContext.FirmwareHeader.XFirmwareHeader.OuterXml;

                        DoAddMessage(string.Format("Receiver {0} firmware header: {1}", display_context.ReceiverContext.SerialNumber, header));
                        DoAddMessage(DoDumpObjectProperties(display_context.ReceiverContext.FirmwareHeader));
                    }
                }
            }
            catch (Exception exception)
            {
                DoAddMessage("Unhandled Exception: " + exception.ToString());
                FireMessage("DoShowUnhandledException", exception.Message);
            }
        }

        private void ReadDatabaseRecords_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                TabItem current_tab = m_ctrlWindowTabs.SelectedItem as TabItem;

                if (current_tab != null && current_tab != m_ctrlTestingTab)
                {
                    DisplayableReceiverContext display_context = current_tab.DataContext as DisplayableReceiverContext;

                    if (display_context != null && display_context.ReceiverContext != null)
                    {
                        display_context.ReceiverContext.ReadDatabaseRecords(true);
                        					
                        DoAddMessage(string.Format("Read database records of Receiver {0}", display_context.ReceiverContext.SerialNumber));

                        DoUpdateDisplayableReceiverContextGlucoseRecords(display_context.ReceiverContext);
                        DoUpdateDisplayableReceiverContextMeterRecords(display_context.ReceiverContext);
                        DoUpdateDisplayableReceiverContextSettingsRecords(display_context.ReceiverContext);
                        DoUpdateDisplayableReceiverContextInsertionTimeRecords(display_context.ReceiverContext);

                        //display_context.ReceiverContext.CurrentEstimatedGlucoseRecord = display_context.ReceiverContext.EstimatedGlucoseRecords.Last();

                        //DoUpdateWindow();
                    }
                }
            }
            catch (Exception exception)
            {
                DoAddMessage("Unhandled Exception: " + exception.ToString());
                FireMessage("DoShowUnhandledException", exception.Message);
            }
        }

        private void ResetReceiverContext_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                TabItem current_tab = m_ctrlWindowTabs.SelectedItem as TabItem;

                if (current_tab != null && current_tab != m_ctrlTestingTab)
                {
                    DisplayableReceiverContext display_context = current_tab.DataContext as DisplayableReceiverContext;

                    if (display_context != null && display_context.ReceiverContext != null)
                    {
                        display_context.ReceiverContext.Reset();
                        DoAddMessage(string.Format("Receiver {0} reset", display_context.ReceiverContext.SerialNumber));
                    }
                }
            }
            catch (Exception exception)
            {
                DoAddMessage("Unhandled Exception: " + exception.ToString());
                FireMessage("DoShowUnhandledException", exception.Message);
            }
        }

        private void StartReceiverContext_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                TabItem current_tab = m_ctrlWindowTabs.SelectedItem as TabItem;

                if (current_tab != null && current_tab != m_ctrlTestingTab)
                {
                    DisplayableReceiverContext display_context = current_tab.DataContext as DisplayableReceiverContext;

                    if (display_context != null && display_context.ReceiverContext != null)
                    {
                        display_context.ReceiverContext.RunInBackground();
                        DoAddMessage(string.Format("Receiver {0} started", display_context.ReceiverContext.SerialNumber));
                    }
                }
            }
            catch (Exception exception)
            {
                DoAddMessage("Unhandled Exception: " + exception.ToString());
                FireMessage("DoShowUnhandledException", exception.Message);
            }
        }

        private void StopReceiverContext_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                TabItem current_tab = m_ctrlWindowTabs.SelectedItem as TabItem;

                if (current_tab != null && current_tab != m_ctrlTestingTab)
                {
                    DisplayableReceiverContext display_context = current_tab.DataContext as DisplayableReceiverContext;

                    if (display_context != null && display_context.ReceiverContext != null)
                    {
                        display_context.ReceiverContext.RequestExit();
                        DoAddMessage(string.Format("Receiver {0} stopped", display_context.ReceiverContext.SerialNumber));
                    }
                }
            }
            catch (Exception exception)
            {
                DoAddMessage("Unhandled Exception: " + exception.ToString());
                FireMessage("DoShowUnhandledException", exception.Message);
            }
        }

        private void AbortReceiverContext_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                TabItem current_tab = m_ctrlWindowTabs.SelectedItem as TabItem;

                if (current_tab != null && current_tab != m_ctrlTestingTab)
                {
                    DisplayableReceiverContext display_context = current_tab.DataContext as DisplayableReceiverContext;

                    if (display_context != null && display_context.ReceiverContext != null)
                    {
                        display_context.ReceiverContext.Abort();
                        DoAddMessage(string.Format("Receiver {0} aborted", display_context.ReceiverContext.SerialNumber));
                    }
                }
            }
            catch (Exception exception)
            {
                DoAddMessage("Unhandled Exception: " + exception.ToString());
                FireMessage("DoShowUnhandledException", exception.Message);
            }
        }

        private void ClearReceiverEvents_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                TabItem current_tab = m_ctrlWindowTabs.SelectedItem as TabItem;

                if (current_tab != null && current_tab != m_ctrlTestingTab)
                {
                    DisplayableReceiverContext display_context = current_tab.DataContext as DisplayableReceiverContext;

                    if (display_context != null && display_context.ReceiverContext != null)
                    {
                        display_context.ReceiverContext.ClearAllEventsBeforeExit();
                        DoAddMessage(string.Format("Cleared receiver {0} events", display_context.ReceiverContext.SerialNumber));
                    }
                }
            }
            catch (Exception exception)
            {
                DoAddMessage("Unhandled Exception: " + exception.ToString());
                FireMessage("DoShowUnhandledException", exception.Message);
            }
        }

		private void PauseReceiverContext_Click(object sender, RoutedEventArgs e)
		{
			try
			{
				TabItem current_tab = m_ctrlWindowTabs.SelectedItem as TabItem;

				if (current_tab != null && current_tab != m_ctrlTestingTab)
				{
					DisplayableReceiverContext display_context = current_tab.DataContext as DisplayableReceiverContext;

					if (display_context != null && display_context.ReceiverContext != null)
					{
						bool status = display_context.ReceiverContext.RequestPause();
						DoAddMessage(string.Format("Receiver {0} pause request returned {1}", display_context.ReceiverContext.SerialNumber, status));
					}
				}
			}
			catch (Exception exception)
			{
				DoAddMessage("Unhandled Exception: " + exception.ToString());
				FireMessage("DoShowUnhandledException", exception.Message);
			}
		}

		private void ResumeReceiverContext_Click(object sender, RoutedEventArgs e)
		{
			try
			{
				TabItem current_tab = m_ctrlWindowTabs.SelectedItem as TabItem;

				if (current_tab != null && current_tab != m_ctrlTestingTab)
				{
					DisplayableReceiverContext display_context = current_tab.DataContext as DisplayableReceiverContext;

					if (display_context != null && display_context.ReceiverContext != null)
					{
						display_context.ReceiverContext.RequestResume();
						DoAddMessage(string.Format("Receiver {0} resume requested.", display_context.ReceiverContext.SerialNumber));
					}
				}
			}
			catch (Exception exception)
			{
				DoAddMessage("Unhandled Exception: " + exception.ToString());
				FireMessage("DoShowUnhandledException", exception.Message);
			}
		}

        private void TestRecordFilters_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                TabItem current_tab = m_ctrlWindowTabs.SelectedItem as TabItem;

                if (current_tab != null && current_tab != m_ctrlTestingTab)
                {
                    DisplayableReceiverContext display_context = current_tab.DataContext as DisplayableReceiverContext;

                    if (display_context != null && display_context.ReceiverContext != null)
                    {
                        DateTime then = DateTime.Now - TimeSpan.FromDays(7.0);

                        #region Tests
                        if (display_context.ReceiverContext.EstimatedGlucoseRecords.Count== 0)
                        {
                            Debug.Assert(false, "No estimated glucose records available");
                        }

                        if (display_context.ReceiverContext.MeterRecords.Count == 0)
                        {
                            Debug.Assert(false, "No meter records available");
                        }

                        if (display_context.ReceiverContext.SettingsRecords.Count == 0)
                        {
                            Debug.Assert(false, "No settings records available");
                        }

                        if (display_context.ReceiverContext.InsertionTimeRecords.Count == 0)
                        {
                            Debug.Assert(false, "No insertion time records available");
                        }

                        {
                            var first = display_context.ReceiverContext.GetEstimatedGlucoseRecordsSince(then);
                            var second = display_context.ReceiverContext.QueryEstimatedGlucoseRecords(r => r.DisplayTime >= then);

                            Debug.Assert(first.Count > 0, "No estimated glucose records found since");
                            Debug.Assert(second.Count > 0, "No estimated glucose records found by query");
                            Debug.Assert(first.Count == second.Count, "Found different numbers of estimated glucose records");
                        }

                        {
                            var first = display_context.ReceiverContext.GetMeterRecordsSince(then);
                            var second = display_context.ReceiverContext.QueryMeterRecords(r => r.DisplayTime >= then);

                            Debug.Assert(first.Count > 0, "No meter records found since");
                            Debug.Assert(second.Count > 0, "No meter records found by query");
                            Debug.Assert(first.Count == second.Count, "Found different numbers of meter records");
                        }

                        {
                            var first = display_context.ReceiverContext.GetSettingsRecordsSince(then);
                            var second = display_context.ReceiverContext.QuerySettingsRecords(r => r.DisplayTime >= then);

                            Debug.Assert(first.Count > 0, "No settings records found since");
                            Debug.Assert(second.Count > 0, "No meter settings found by query");
                            Debug.Assert(first.Count == second.Count, "Found different numbers of settings records");
                        }

                        {
                            var first = display_context.ReceiverContext.GetInsertionTimeRecordsSince(then);
                            var second = display_context.ReceiverContext.QueryInsertionTimeRecords(r => r.DisplayTime >= then);

                            Debug.Assert(first.Count > 0, "No meter records found since");
                            Debug.Assert(second.Count > 0, "No meter records found by query");
                            Debug.Assert(first.Count == second.Count, "Found different numbers of meter records");
                        }
                        #endregion

                        DoAddMessage("Records query test passed");
                    }
                }
            }
            catch (Exception exception)
            {
                DoAddMessage("Unhandled Exception: " + exception.ToString());
                FireMessage("DoShowUnhandledException", exception.Message);
            }
        }

        private void TagRecords_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                TabItem current_tab = m_ctrlWindowTabs.SelectedItem as TabItem;

                if (current_tab != null && current_tab != m_ctrlTestingTab)
                {
                    DisplayableReceiverContext display_context = current_tab.DataContext as DisplayableReceiverContext;

                    if (display_context != null && display_context.ReceiverContext != null)
                    {
                        if (!String.IsNullOrEmpty(display_context.Tag))
                        {
                            if (display_context.ReceiverContext.EstimatedGlucoseRecords.Count == 0)
                            {
                                Debug.Assert(false, "No estimated glucose records available");
                            }

                            if (display_context.ReceiverContext.MeterRecords.Count == 0)
                            {
                                Debug.Assert(false, "No meter records available");
                            }

                            if (display_context.ReceiverContext.SettingsRecords.Count == 0)
                            {
                                Debug.Assert(false, "No settings records available");
                            }

                            if (display_context.ReceiverContext.InsertionTimeRecords.Count == 0)
                            {
                                Debug.Assert(false, "No insertion time records available");
                            }

                            AsynchronousQueue<EstimatedGlucoseRecord> glucose = display_context.ReceiverContext.EstimatedGlucoseRecords;
                            EstimatedGlucoseRecord firstGlucose = glucose.GetFirst();
                            firstGlucose.Tag = display_context.Tag;
                            DoAddMessage(DoDumpObjectProperties(firstGlucose));

                            AsynchronousQueue<MeterRecord> meter = display_context.ReceiverContext.MeterRecords;
                            MeterRecord firstMeter= meter.GetFirst();
                            firstMeter.Tag = display_context.Tag;
                            DoAddMessage(DoDumpObjectProperties(firstMeter));

                            AsynchronousQueue<SettingsRecord> settings = display_context.ReceiverContext.SettingsRecords;
                            SettingsRecord firstSettings= settings.GetFirst();
                            firstSettings.Tag = display_context.Tag;
                            DoAddMessage(DoDumpObjectProperties(firstSettings));

                            AsynchronousQueue<InsertionTimeRecord> insertion = display_context.ReceiverContext.InsertionTimeRecords;
                            InsertionTimeRecord firstInsertion = insertion.GetFirst();
                            firstInsertion.Tag = display_context.Tag;
                            DoAddMessage(DoDumpObjectProperties(firstInsertion));
                        }
                    }
                }
            }
            catch (Exception exception)
            {
                DoAddMessage("Unhandled Exception: " + exception.ToString());
                FireMessage("DoShowUnhandledException", exception.Message);
            }
        }
        #endregion

        #endregion
    }
}
