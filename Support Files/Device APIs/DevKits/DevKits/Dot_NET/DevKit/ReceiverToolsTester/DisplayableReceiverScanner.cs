using System;
using System.Collections.ObjectModel;
using System.ComponentModel;
using DexCom.ReceiverTools;

namespace ReceiverToolsTester
{
    /// <summary>
    /// Just a class wrapper that helps implement observable WPF data.
    /// </summary>
    internal class DisplayableReceiverScanner : INotifyPropertyChanged
    {
        private string m_currentScannerState = String.Empty;
        private double m_scanIntervalInSeconds = 0;

        public ReceiverScanner ReceiverScanner { get; set; }

        public double ScanInterval
        {
            get { return m_scanIntervalInSeconds; }
            set
            {
                if (value != m_scanIntervalInSeconds)
                {
                    ReceiverScanner.Interval = TimeSpan.FromSeconds(value);

                    //do it like this because ReceiverScanner may impose a lower limit
                    m_scanIntervalInSeconds = ReceiverScanner.Interval.TotalSeconds;

                    if (PropertyChanged != null) //raise event synchronously
                    {
                        PropertyChanged(this, new PropertyChangedEventArgs("ScanInterval"));
                    }
                }
            }
        }

        public string ScannerState
        {
            get { return m_currentScannerState; }
            set
            {
                m_currentScannerState = value;

                if (PropertyChanged != null) //raise event synchronously
                {
                    PropertyChanged(this, new PropertyChangedEventArgs("ScannerState"));
                }
            }
        }

        #region INotifyPropertyChanged Members

        /// <summary>Raised when operating state changes</summary>
        public event PropertyChangedEventHandler PropertyChanged;

        #endregion
    }
}
