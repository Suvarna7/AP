using System;
using System.Collections.ObjectModel;
using System.ComponentModel;
using DexCom.ReceiverTools;

namespace ReceiverToolsTester
{
	/// <summary>
	/// Just a class wrapper that helps implement observable WPF data.
	/// </summary>
	internal class DisplayableReceiverContext : INotifyPropertyChanged
	{
		private string m_currentReceiverState = String.Empty;
		private double m_syncIntervalInSeconds = 0;
		private string m_tag = null;

		public ReceiverContext ReceiverContext { get; set; }

		public int LastEstimatedGlucoseRecordIndex { get; set; }
		public ObservableCollection<EstimatedGlucoseRecord> EstimatedGlucoseRecords { get; private set; }

		public int LastMeterRecordIndex { get; set; }
		public ObservableCollection<MeterRecord> MeterRecords { get; private set; }

		public int LastSettingsRecordIndex { get; set; }
		public ObservableCollection<SettingsRecord> SettingsRecords { get; private set; }

		public int LastInsertionTimeRecordIndex { get; set; }
		public ObservableCollection<InsertionTimeRecord> InsertionTimeRecords { get; private set; }

		public double SyncInterval
		{
			get { return m_syncIntervalInSeconds; }
			set
			{
                if (value != m_syncIntervalInSeconds)
                {
                    ReceiverContext.Interval = TimeSpan.FromSeconds(value);

                    //do it like this because ReceiverContext may impose a lower limit
                    m_syncIntervalInSeconds = ReceiverContext.Interval.TotalSeconds;

                    if (PropertyChanged != null) //raise event synchronously
                    {
                        PropertyChanged(this, new PropertyChangedEventArgs("SyncInterval"));
                    }
                }
			}
		}

		public string ReceiverState
		{
			get { return m_currentReceiverState; }
			set
			{
				m_currentReceiverState = value;

				if (PropertyChanged != null) //raise event synchronously
				{
					PropertyChanged(this, new PropertyChangedEventArgs("ReceiverState"));
				}
			}
		}

		public string Tag
		{
			get { return m_tag; }
			set
			{
				m_tag = value;

				if (PropertyChanged != null) //raise event synchronously
				{
					PropertyChanged(this, new PropertyChangedEventArgs("Tag"));
				}
			}
		}

		public DisplayableReceiverContext()
		{
			EstimatedGlucoseRecords = new ObservableCollection<EstimatedGlucoseRecord>();
			MeterRecords = new ObservableCollection<MeterRecord>();
			SettingsRecords = new ObservableCollection<SettingsRecord>();
			InsertionTimeRecords = new ObservableCollection<InsertionTimeRecord>();

			LastEstimatedGlucoseRecordIndex = -1;
			LastMeterRecordIndex = -1;
			LastSettingsRecordIndex = -1;
			LastInsertionTimeRecordIndex = -1;
		}

		public void AddEstimatedGlucoseRecord(EstimatedGlucoseRecord record)
		{
			if (record != null)
			{
				EstimatedGlucoseRecords.Insert(0, record);
			}
		}

		public void AddMeterRecord(MeterRecord record)
		{
			if (record != null)
			{
				MeterRecords.Insert(0, record);
			}
		}

		public void AddSettingsRecord(SettingsRecord record)
		{
			if (record != null)
			{
				SettingsRecords.Insert(0, record);
			}
		}

		public void AddInsertionTimeRecord(InsertionTimeRecord record)
		{
			if (record != null)
			{
				InsertionTimeRecords.Insert(0, record);
			}
		}

		#region INotifyPropertyChanged Members

		/// <summary>Raised when operating state changes</summary>
		public event PropertyChangedEventHandler PropertyChanged;

		#endregion
	}
}
