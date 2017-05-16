package com.NewApp ;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import zephyr.android.BioHarnessBT.BTClient;
import zephyr.android.BioHarnessBT.ConnectedEvent;
import zephyr.android.BioHarnessBT.ConnectedListener;
import zephyr.android.BioHarnessBT.PacketTypeRequest;
import zephyr.android.BioHarnessBT.ZephyrPacketArgs;
import zephyr.android.BioHarnessBT.ZephyrPacketEvent;
import zephyr.android.BioHarnessBT.ZephyrPacketListener;
import zephyr.android.BioHarnessBT.ZephyrProtocol;

/**
 * Created by Caterina on 6/17/2015.
 */
public class ConnectListenerImplExtra implements ConnectedListener<BTClient> {
        private Handler _handler;
        private int GEN_PACKET = 1200;
        private int ECG_PACKET = 1202;
        private int BREATH_PACKET = 1204;
        private int R_to_R_PACKET = 1206;
        private int ACCELEROMETER_PACKET = 1208;
        private int SERIAL_NUM_PACKET = 1210;
        private int SUMMARY_DATA_PACKET = 1212;
        private int EVENT_DATA_PACKET = 1214;
        public int BREATHING_PACKET_ID = 33;
        public int R_to_R_PACKET_ID = 36;
        public int ACCELEROMETER_PACKET_ID = 42;
        public int SUMMARY_DATA_PACKET_ID = 43;
        public int EVENT_DATA_PACKET_ID = 44;
        public int SERIAL_NUMBER = 11;
        public int LOGGING_ENABLE_PACKET_ID = 75;
        public String SerialNumber;
        private int TotalNumGPBytes;
        private int TotalNumECGBytes;
        private int TotalNumBreathBytes;
        private int TotalNumRtoRBytes;
        private int TotalNumAccelerometerBytes;
        private int TotalNumSummaryBytes;
        private int TotalNumEventBytes;
        private int TotalMissedPacketsGP;
        private int TotalMissedPacketsECG;
        private int TotalMissedPacketsBreathing;
        private int TotalMissedPacketsRtoR;
        private int TotalMissedAccelerometer;
        private int TotalMissedSummaryPackets;
        private int TotalMissedEventPackets;
        private byte[] Payload;
        private ConnectListenerImplExtra.GeneralPacketInfo GPInfoPacket = new ConnectListenerImplExtra.GeneralPacketInfo();
        private ConnectListenerImplExtra.ECGPacketInfo ECGInfoPacket = new ConnectListenerImplExtra.ECGPacketInfo();
        private ConnectListenerImplExtra.BreathingPacketInfo BreathingInfoPacket = new ConnectListenerImplExtra.BreathingPacketInfo();
        private ConnectListenerImplExtra.RtoRPacketInfo RtoRInfoPacket = new ConnectListenerImplExtra.RtoRPacketInfo();
        private ConnectListenerImplExtra.AccelerometerPacketInfo AccInfoPacket = new ConnectListenerImplExtra.AccelerometerPacketInfo();
        private ConnectListenerImplExtra.SummaryPacketInfo SummaryInfoPacket = new ConnectListenerImplExtra.SummaryPacketInfo();
        public PacketTypeRequest RequestedPacketTypes = new PacketTypeRequest();

        public ConnectListenerImplExtra(Handler handler, byte[] dataBytes) {
            this._handler = handler;
            this.TotalNumGPBytes = 0;
            this.TotalNumECGBytes = 0;
            this.TotalNumBreathBytes = 0;
            this.TotalNumRtoRBytes = 0;
            this.TotalNumAccelerometerBytes = 0;
            this.TotalMissedPacketsGP = 0;
            this.TotalMissedPacketsECG = 0;
            this.TotalMissedPacketsBreathing = 0;
            this.TotalMissedPacketsRtoR = 0;
            this.TotalMissedAccelerometer = 0;
            this.TotalNumSummaryBytes = 0;
            this.TotalMissedSummaryPackets = 0;
            this.TotalNumEventBytes = 0;
            this.TotalMissedEventPackets = 0;
            this.Payload = dataBytes;
        }

        public void Connected(ConnectedEvent<BTClient> eventArgs) {
            System.out.println(String.format("Connected to BioHarness %s.", new Object[]{((BTClient)eventArgs.getSource()).getDevice().getName()}));
            String SerialNum = ((BTClient)eventArgs.getSource()).getDevice().getName();
            this.SerialNumber = SerialNum;
            this.RequestedPacketTypes.ACCELEROMETER_ENABLE = true;
            ZephyrProtocol _protocol = new ZephyrProtocol(((BTClient)eventArgs.getSource()).getComms(), this.RequestedPacketTypes);
            _protocol.addZephyrPacketEventListener(new ZephyrPacketListener() {
                private int seqIDGenPacket = -1;
                private int missedGenPacket;
                private int seqIDECGPacket = -1;
                private int missedECGPacket;
                private int seqIDBreathPacket = -1;
                private int missedBreathPacket;
                private int seqIDRtoRPacket = -1;
                private int missedRtoRPacket;
                private int seqIDAccelPacket = -1;
                private int missedAccelPacket;
                private int seqIDSummaryPacket = -1;
                private int missedSummaryPacket;
                private int seqIDEventPacket = -1;
                private int missedEventPacket;

                public void ReceivedPacket(ZephyrPacketEvent eventArgs) {
                    ZephyrPacketArgs msg = eventArgs.getPacket();
                    byte CRCFailStatus = msg.getCRCStatus();
                    byte RcvdBytes = msg.getNumRvcdBytes();
                    int NumBytesbeforeEventSpData;
                    if(msg.getMsgID() == 32) {
                        NumBytesbeforeEventSpData = msg.getBytes()[0] & 127 + ((msg.getBytes()[0] & 128) > 1?128:0);
                        ConnectListenerImplExtra.this.TotalNumGPBytes = ConnectListenerImplExtra.this.TotalNumGPBytes + RcvdBytes;
                        byte[] EventSpDataSize = msg.getBytes();
                        int EventInfoPacket = EventSpDataSize[9] & 255;
                        int text1 = EventSpDataSize[10] & 255;
                        int b1 = NumBytesbeforeEventSpData - this.seqIDGenPacket;
                        if(b1 > 1) {
                            this.missedGenPacket += b1 - 1;
                        }

                        this.seqIDGenPacket = NumBytesbeforeEventSpData;
                        if(this.seqIDGenPacket == 255) {
                            this.seqIDGenPacket = -1;
                        }

                        ConnectListenerImplExtra.this.TotalMissedPacketsGP = ConnectListenerImplExtra.this.TotalMissedPacketsGP + this.missedGenPacket;
                        String RtoRSamples = String.format("Received GP Packet#%d,Bytes Rcvd #%d, Dropped Pckts #%d, CRC Fail #%d ", new Object[]{Integer.valueOf(NumBytesbeforeEventSpData), Integer.valueOf(ConnectListenerImplExtra.this.TotalNumGPBytes), Integer.valueOf(ConnectListenerImplExtra.this.TotalMissedPacketsGP), Byte.valueOf(CRCFailStatus)});
                        Message text11 = ConnectListenerImplExtra.this._handler.obtainMessage(ConnectListenerImplExtra.this.GEN_PACKET);
                        Bundle b11 = new Bundle();
                        b11.putString("genText", RtoRSamples);
                        //Log.d("Zephyr General Packet Parsed", RtoRSamples);
                        text11.setData(b11);
                        ConnectListenerImplExtra.this._handler.sendMessage(text11);
                    }

                    int EventSpDataSize1;
                    String EventInfoPacket1;
                    Message text12;
                    Bundle b12;
                    short[] RtoRSamples1;
                    if(msg.getMsgID() == 34) {
                        NumBytesbeforeEventSpData = msg.getBytes()[0] & 127 + ((msg.getBytes()[0] & 128) > 1?128:0);
                        ConnectListenerImplExtra.this.TotalNumECGBytes = ConnectListenerImplExtra.this.TotalNumECGBytes + RcvdBytes;
                        EventSpDataSize1 = NumBytesbeforeEventSpData - this.seqIDECGPacket;
                        if(EventSpDataSize1 > 1) {
                            this.missedECGPacket += EventSpDataSize1 - 1;
                        }

                        this.seqIDECGPacket = NumBytesbeforeEventSpData;
                        if(this.seqIDECGPacket == 255) {
                            this.seqIDECGPacket = -1;
                        }

                        ConnectListenerImplExtra.this.TotalMissedPacketsECG = ConnectListenerImplExtra.this.TotalMissedPacketsECG + this.missedECGPacket;
                        EventInfoPacket1 = String.format("Received ECG Packet#%d, Bytes Rcvd #%d, Dropped Pckts #%d, CRC Fail #%d ", new Object[]{Integer.valueOf(NumBytesbeforeEventSpData), Integer.valueOf(ConnectListenerImplExtra.this.TotalNumECGBytes), Integer.valueOf(ConnectListenerImplExtra.this.TotalMissedPacketsECG), Byte.valueOf(CRCFailStatus)});
                        text12 = ConnectListenerImplExtra.this._handler.obtainMessage(ConnectListenerImplExtra.this.ECG_PACKET);
                        b12 = new Bundle();
                        b12.putString("ecgText", EventInfoPacket1);
                        Log.d("ZephyrPacketParsed", EventInfoPacket1);
                        text12.setData(b12);
                        ConnectListenerImplExtra.this._handler.sendMessage(text12);
                        RtoRSamples1 = new short[63];
                        RtoRSamples1 = ConnectListenerImplExtra.this.ECGInfoPacket.GetECGSamples(msg.getBytes());
                    }

                    if(ConnectListenerImplExtra.this.BREATHING_PACKET_ID == msg.getMsgID()) {
                        NumBytesbeforeEventSpData = msg.getBytes()[0] & 127 + ((msg.getBytes()[0] & 128) > 1?128:0);
                        ConnectListenerImplExtra.this.TotalNumBreathBytes = ConnectListenerImplExtra.this.TotalNumBreathBytes + RcvdBytes;
                        EventSpDataSize1 = NumBytesbeforeEventSpData - this.seqIDBreathPacket;
                        if(EventSpDataSize1 > 1) {
                            this.missedBreathPacket += EventSpDataSize1 - 1;
                        }

                        this.seqIDBreathPacket = NumBytesbeforeEventSpData;
                        if(this.seqIDBreathPacket == 255) {
                            this.seqIDBreathPacket = -1;
                        }

                        ConnectListenerImplExtra.this.TotalMissedPacketsBreathing = ConnectListenerImplExtra.this.TotalMissedPacketsBreathing + this.missedBreathPacket;
                        EventInfoPacket1 = String.format("Received Breathing Packet#%d,Bytes Rcvd #%d, Dropped Pckts #%d, CRC Fail #%d", new Object[]{Integer.valueOf(NumBytesbeforeEventSpData), Integer.valueOf(ConnectListenerImplExtra.this.TotalNumBreathBytes), Integer.valueOf(ConnectListenerImplExtra.this.TotalMissedPacketsBreathing), Byte.valueOf(CRCFailStatus)});
                        text12 = ConnectListenerImplExtra.this._handler.obtainMessage(ConnectListenerImplExtra.this.BREATH_PACKET);
                        b12 = new Bundle();
                        b12.putString("breathText", EventInfoPacket1);
                        Log.d("ZephyrPacketParsed", EventInfoPacket1);
                        text12.setData(b12);
                        ConnectListenerImplExtra.this._handler.sendMessage(text12);
                        System.out.println("Breathing Year is is " + ConnectListenerImplExtra.this.BreathingInfoPacket.GetTSYear(msg.getBytes()));
                        RtoRSamples1 = new short[18];
                        RtoRSamples1 = ConnectListenerImplExtra.this.BreathingInfoPacket.GetBreathingSamples(msg.getBytes());
                    }

                    if(ConnectListenerImplExtra.this.R_to_R_PACKET_ID == msg.getMsgID()) {
                        NumBytesbeforeEventSpData = msg.getBytes()[0] & 127 + ((msg.getBytes()[0] & 128) > 1?128:0);
                        EventSpDataSize1 = NumBytesbeforeEventSpData - this.seqIDRtoRPacket;
                        if(EventSpDataSize1 > 1) {
                            this.missedRtoRPacket += EventSpDataSize1 - 1;
                        }

                        this.seqIDRtoRPacket = NumBytesbeforeEventSpData;
                        if(this.seqIDRtoRPacket == 255) {
                            this.seqIDRtoRPacket = -1;
                        }

                        ConnectListenerImplExtra.this.TotalMissedPacketsRtoR = ConnectListenerImplExtra.this.TotalMissedPacketsRtoR + this.missedRtoRPacket;
                        EventInfoPacket1 = String.format("Received R to R Packet#%d,Bytes Rcvd #%d, Dropped Pckts #%d, CRC Fail #%d", new Object[]{Integer.valueOf(NumBytesbeforeEventSpData), Integer.valueOf(ConnectListenerImplExtra.this.TotalNumRtoRBytes), Integer.valueOf(ConnectListenerImplExtra.this.TotalMissedPacketsRtoR), Byte.valueOf(CRCFailStatus)});
                        text12 = ConnectListenerImplExtra.this._handler.obtainMessage(ConnectListenerImplExtra.this.R_to_R_PACKET);
                        b12 = new Bundle();
                        b12.putString("RtoRText", EventInfoPacket1);
                        //Log.d("ZephyrR to R PacketParsed", EventInfoPacket1);
                        text12.setData(b12);
                        ConnectListenerImplExtra.this._handler.sendMessage(text12);
                        System.out.println("R to R Year is is " + ConnectListenerImplExtra.this.RtoRInfoPacket.GetTSYear(msg.getBytes()));
                        int[] RtoRSamples2 = new int[18];
                        RtoRSamples2 = ConnectListenerImplExtra.this.RtoRInfoPacket.GetRtoRSamples(msg.getBytes());
                    }

                    if(ConnectListenerImplExtra.this.ACCELEROMETER_PACKET_ID == msg.getMsgID()) {
                        NumBytesbeforeEventSpData = msg.getBytes()[0] & 127 + ((msg.getBytes()[0] & 128) > 1?128:0);
                        EventSpDataSize1 = NumBytesbeforeEventSpData - this.seqIDAccelPacket;
                        if(EventSpDataSize1 > 1) {
                            this.missedAccelPacket += EventSpDataSize1 - 1;
                        }

                        this.seqIDAccelPacket = NumBytesbeforeEventSpData;
                        if(this.seqIDAccelPacket == 255) {
                            this.seqIDAccelPacket = -1;
                        }

                        ConnectListenerImplExtra.this.TotalMissedAccelerometer = ConnectListenerImplExtra.this.TotalMissedAccelerometer + this.missedAccelPacket;
                        EventInfoPacket1 = String.format("Received Accelerometer Packet#%d,Bytes Rcvd #%d, Dropped Pckts #%d, CRC Fail #%d", new Object[]{Integer.valueOf(NumBytesbeforeEventSpData), Integer.valueOf(ConnectListenerImplExtra.this.TotalNumAccelerometerBytes), Integer.valueOf(ConnectListenerImplExtra.this.TotalMissedAccelerometer), Byte.valueOf(CRCFailStatus)});
                        text12 = ConnectListenerImplExtra.this._handler.obtainMessage(ConnectListenerImplExtra.this.ACCELEROMETER_PACKET);
                        b12 = new Bundle();
                        b12.putString("Accelerometertext", EventInfoPacket1);
                        Log.d("ZephyrPacketParsed", EventInfoPacket1);
                        text12.setData(b12);
                        ConnectListenerImplExtra.this._handler.sendMessage(text12);
                        System.out.println("Accleration Year is is " + ConnectListenerImplExtra.this.AccInfoPacket.GetTSYear(msg.getBytes()));
                        ConnectListenerImplExtra.this.AccInfoPacket.UnpackAccelerationData(msg.getBytes());
                    }

                    if(ConnectListenerImplExtra.this.SERIAL_NUMBER == msg.getMsgID()) {
                        System.out.println("Received Serial Number");
                        String NumBytesbeforeEventSpData1 = ConnectListenerImplExtra.this.SerialNumber;
                        Message EventSpDataSize2 = ConnectListenerImplExtra.this._handler.obtainMessage(1210);
                        Bundle EventInfoPacket2 = new Bundle();
                        EventInfoPacket2.putString("SerialNumtxt", NumBytesbeforeEventSpData1);
                       // Log.d("Zephyr Serial Number PacketParsed", NumBytesbeforeEventSpData1);
                        EventSpDataSize2.setData(EventInfoPacket2);
                        ConnectListenerImplExtra.this._handler.sendMessage(EventSpDataSize2);
                    }

                    if(ConnectListenerImplExtra.this.SUMMARY_DATA_PACKET_ID == msg.getMsgID()) {
                        NumBytesbeforeEventSpData = msg.getBytes()[0] & 127 + ((msg.getBytes()[0] & 128) > 1?128:0);
                        ConnectListenerImplExtra.this.TotalNumSummaryBytes = ConnectListenerImplExtra.this.TotalNumSummaryBytes + RcvdBytes;
                        EventSpDataSize1 = NumBytesbeforeEventSpData - this.seqIDSummaryPacket;
                        if(EventSpDataSize1 > 1) {
                            this.missedSummaryPacket += EventSpDataSize1 - 1;
                        }

                        this.seqIDSummaryPacket = NumBytesbeforeEventSpData;
                        if(this.seqIDSummaryPacket == 255) {
                            this.seqIDSummaryPacket = -1;
                        }

                        ConnectListenerImplExtra.this.TotalMissedSummaryPackets = ConnectListenerImplExtra.this.TotalMissedSummaryPackets + this.missedSummaryPacket;
                        EventInfoPacket1 = String.format("Received Summary Packet#%d,Bytes Rcvd #%d, Dropped Pckts #%d, CRC Fail #%d", new Object[]{Integer.valueOf(NumBytesbeforeEventSpData), Integer.valueOf(ConnectListenerImplExtra.this.TotalNumSummaryBytes), Integer.valueOf(ConnectListenerImplExtra.this.TotalMissedSummaryPackets), Byte.valueOf(CRCFailStatus)});
                        text12 = ConnectListenerImplExtra.this._handler.obtainMessage(ConnectListenerImplExtra.this.SUMMARY_DATA_PACKET);
                        b12 = new Bundle();
                        b12.putString("SummaryDataText", EventInfoPacket1);
                        //Log.d("Zephyr Summary PacketParsed", EventInfoPacket1);
                        text12.setData(b12);
                        ConnectListenerImplExtra.this._handler.sendMessage(text12);
                        System.out.println("Battery Voltage is  " + ConnectListenerImplExtra.this.SummaryInfoPacket.GetBatteryVoltage(msg.getBytes()));
                        System.out.println("Posture is  " + ConnectListenerImplExtra.this.SummaryInfoPacket.GetPosture(msg.getBytes()));
                        System.out.println("RSSI is  " + ConnectListenerImplExtra.this.SummaryInfoPacket.GetRSSI(msg.getBytes()));
                        System.out.println("Link Quality is  " + ConnectListenerImplExtra.this.SummaryInfoPacket.GetLinkQuality(msg.getBytes()));
                        System.out.println("TxPower is  " + ConnectListenerImplExtra.this.SummaryInfoPacket.GetTxPower(msg.getBytes()));
                        System.out.println("Sagittal Accn Min  is  " + ConnectListenerImplExtra.this.SummaryInfoPacket.GetSagittal_AxisAccnMin(msg.getBytes()));
                        System.out.println("Device Internal Temperature is  " + ConnectListenerImplExtra.this.SummaryInfoPacket.GetDevice_Internal_Temperature(msg.getBytes()));
                    }

                    if(ConnectListenerImplExtra.this.EVENT_DATA_PACKET_ID == msg.getMsgID()) {
                        byte NumBytesbeforeEventSpData2 = 11;
                        EventSpDataSize1 = RcvdBytes - NumBytesbeforeEventSpData2;
                        if(EventSpDataSize1 > 0) {
                            System.out.println("Received Event Packet with Received Byte length " + EventSpDataSize1);
                            ConnectListenerImplExtra.EventPacketInfo EventInfoPacket3 = ConnectListenerImplExtra.this.new EventPacketInfo((byte)EventSpDataSize1);
                            System.out.println("Event SeqNumber is " + EventInfoPacket3.GetSeqNum(msg.getBytes()));
                        }
                    }

                }
            });
        }

        public class AccelerometerPacketInfo {
            private byte _SequenceNum;
            private int _TSYear;
            private byte _TSMonth;
            private byte _TSDay;
            private long _MsOfDay;
            public short NUM_ACCN_SAMPLES = 20;
            public ConnectListenerImplExtra.AccelerometerPacketInfo.XYZ_AccelerationData XYZ_AccnDataSamples = new ConnectListenerImplExtra.AccelerometerPacketInfo.XYZ_AccelerationData();

            public AccelerometerPacketInfo() {
            }

            public byte GetSeqNum(byte[] Payload) {
                this._SequenceNum = (byte)(Payload[0] & 255);
                return this._SequenceNum;
            }

            public int GetTSYear(byte[] Payload) {
                this._TSYear = Payload[1] & 255;
                this._TSYear |= (Payload[2] & 255) << 8;
                return this._TSYear;
            }

            public byte GetTSMonth(byte[] Payload) {
                this._TSMonth = Payload[3];
                return this._TSMonth;
            }

            public byte GetTSDay(byte[] Payload) {
                this._TSDay = Payload[4];
                return this._TSDay;
            }

            public long GetMsofDay(byte[] Payload) {
                this._MsOfDay = 0L;
                this._MsOfDay = (long)(Payload[5] & 255);
                this._MsOfDay |= (long)((Payload[6] & 255) << 8);
                this._MsOfDay |= (long)((Payload[7] & 255) << 16);
                this._MsOfDay |= (long)((Payload[8] & 255) << 24);
                return this._MsOfDay;
            }

            public double[] GetX_axisAccnData() {
                return this.XYZ_AccnDataSamples.X_axisAccnData;
            }

            public double[] GetY_axisAccnData() {
                return this.XYZ_AccnDataSamples.Y_axisAccnData;
            }

            public double[] GetZ_axisAccnData() {
                return this.XYZ_AccnDataSamples.Z_axisAccnData;
            }

            public void UnpackAccelerationData(byte[] Payload) {
                short PayloadIndex = 0;
                byte NumBytesinPackingPattern = 15;
                byte SizeAccelerometerDataPayload = 75;
                byte NumBitsPerAccelerationSample = 10;
                short NumIterationsPer15Bytes = (short)(SizeAccelerometerDataPayload / NumBytesinPackingPattern);
                byte NumAccelerationSamplesPer5bytes = 4;
                short X_axis_SampleIndex = 0;
                short Y_axis_SampleIndex = 0;
                short Z_axis_SampleIndex = 0;
                boolean SIGN_CONV_FACTR = true;
                short SIGN_CONV_FACTR_SIGNED = 1024;

                for(short i = 0; i < NumIterationsPer15Bytes; ++i) {
                    long PackedData = 0L;
                    PackedData = (long)(Payload[9 + PayloadIndex] & 255);
                    ++PayloadIndex;
                    PackedData |= (long)((Payload[9 + PayloadIndex] & 255) << 8);
                    ++PayloadIndex;
                    PackedData |= (long)((Payload[9 + PayloadIndex] & 255) << 16);
                    ++PayloadIndex;
                    PackedData |= (long)((Payload[9 + PayloadIndex] & 255) << 24);
                    ++PayloadIndex;
                    PackedData &= 4294967295L;
                    long temp = (long)((Payload[9 + PayloadIndex] & 255) << 24);
                    temp <<= 8;
                    PackedData |= temp;
                    ++PayloadIndex;

                    short j;
                    int SignBit;
                    short tempAccn;
                    for(j = 0; j < NumAccelerationSamplesPer5bytes; ++j) {
                        switch(j) {
                            case 0:
                            case 3:
                                tempAccn = (short)((int)(PackedData & 1023L));
                                SignBit = tempAccn >> 9;
                                if(1 == SignBit) {
                                    tempAccn -= SIGN_CONV_FACTR_SIGNED;
                                }

                                this.XYZ_AccnDataSamples.X_axisAccnData[X_axis_SampleIndex] = (double)tempAccn / 10.0D;
                                ++X_axis_SampleIndex;
                                PackedData >>= NumBitsPerAccelerationSample;
                                break;
                            case 1:
                                tempAccn = (short)((int)(PackedData & 1023L));
                                SignBit = tempAccn >> 9;
                                if(1 == SignBit) {
                                    tempAccn -= SIGN_CONV_FACTR_SIGNED;
                                }

                                this.XYZ_AccnDataSamples.Y_axisAccnData[Y_axis_SampleIndex] = (double)tempAccn / 10.0D;
                                ++Y_axis_SampleIndex;
                                PackedData >>= NumBitsPerAccelerationSample;
                                break;
                            case 2:
                                tempAccn = (short)((int)(PackedData & 1023L));
                                SignBit = tempAccn >> 9;
                                if(1 == SignBit) {
                                    tempAccn -= SIGN_CONV_FACTR_SIGNED;
                                }

                                this.XYZ_AccnDataSamples.Z_axisAccnData[Z_axis_SampleIndex] = (double)tempAccn / 10.0D;
                                ++Z_axis_SampleIndex;
                                PackedData >>= NumBitsPerAccelerationSample;
                        }
                    }

                    PackedData = 0L;
                    PackedData = (long)(Payload[9 + PayloadIndex] & 255);
                    ++PayloadIndex;
                    PackedData |= (long)((Payload[9 + PayloadIndex] & 255) << 8);
                    ++PayloadIndex;
                    PackedData |= (long)((Payload[9 + PayloadIndex] & 255) << 16);
                    ++PayloadIndex;
                    PackedData |= (long)((Payload[9 + PayloadIndex] & 255) << 24);
                    ++PayloadIndex;
                    PackedData &= 4294967295L;
                    temp = (long)((Payload[9 + PayloadIndex] & 255) << 24);
                    temp <<= 8;
                    PackedData |= temp;
                    ++PayloadIndex;

                    for(j = 0; j < NumAccelerationSamplesPer5bytes; ++j) {
                        switch(j) {
                            case 0:
                            case 3:
                                tempAccn = (short)((int)(PackedData & 1023L));
                                SignBit = tempAccn >> 9;
                                if(1 == SignBit) {
                                    tempAccn -= SIGN_CONV_FACTR_SIGNED;
                                }

                                this.XYZ_AccnDataSamples.Y_axisAccnData[Y_axis_SampleIndex] = (double)tempAccn / 10.0D;
                                ++Y_axis_SampleIndex;
                                PackedData >>= NumBitsPerAccelerationSample;
                                break;
                            case 1:
                                tempAccn = (short)((int)(PackedData & 1023L));
                                SignBit = tempAccn >> 9;
                                if(1 == SignBit) {
                                    tempAccn -= SIGN_CONV_FACTR_SIGNED;
                                }

                                this.XYZ_AccnDataSamples.Z_axisAccnData[Z_axis_SampleIndex] = (double)tempAccn / 10.0D;
                                ++Z_axis_SampleIndex;
                                PackedData >>= NumBitsPerAccelerationSample;
                                break;
                            case 2:
                                tempAccn = (short)((int)(PackedData & 1023L));
                                SignBit = tempAccn >> 9;
                                if(1 == SignBit) {
                                    tempAccn -= SIGN_CONV_FACTR_SIGNED;
                                }

                                this.XYZ_AccnDataSamples.X_axisAccnData[X_axis_SampleIndex] = (double)tempAccn / 10.0D;
                                ++X_axis_SampleIndex;
                                PackedData >>= NumBitsPerAccelerationSample;
                        }
                    }

                    PackedData = 0L;
                    PackedData = (long)(Payload[9 + PayloadIndex] & 255);
                    ++PayloadIndex;
                    PackedData |= (long)((Payload[9 + PayloadIndex] & 255) << 8);
                    ++PayloadIndex;
                    PackedData |= (long)((Payload[9 + PayloadIndex] & 255) << 16);
                    ++PayloadIndex;
                    PackedData |= (long)((Payload[9 + PayloadIndex] & 255) << 24);
                    ++PayloadIndex;
                    PackedData &= 4294967295L;
                    temp = (long)((Payload[9 + PayloadIndex] & 255) << 24);
                    temp <<= 8;
                    PackedData |= temp;
                    ++PayloadIndex;

                    for(j = 0; j < NumAccelerationSamplesPer5bytes; ++j) {
                        switch(j) {
                            case 0:
                            case 3:
                                tempAccn = (short)((int)(PackedData & 1023L));
                                SignBit = tempAccn >> 9;
                                if(1 == SignBit) {
                                    tempAccn -= SIGN_CONV_FACTR_SIGNED;
                                }

                                this.XYZ_AccnDataSamples.Z_axisAccnData[Z_axis_SampleIndex] = (double)tempAccn / 10.0D;
                                ++Z_axis_SampleIndex;
                                PackedData >>= NumBitsPerAccelerationSample;
                                break;
                            case 1:
                                tempAccn = (short)((int)(PackedData & 1023L));
                                SignBit = tempAccn >> 9;
                                if(1 == SignBit) {
                                    tempAccn -= SIGN_CONV_FACTR_SIGNED;
                                }

                                this.XYZ_AccnDataSamples.X_axisAccnData[X_axis_SampleIndex] = (double)tempAccn / 10.0D;
                                ++X_axis_SampleIndex;
                                PackedData >>= NumBitsPerAccelerationSample;
                                break;
                            case 2:
                                tempAccn = (short)((int)(PackedData & 1023L));
                                SignBit = tempAccn >> 9;
                                if(1 == SignBit) {
                                    tempAccn -= SIGN_CONV_FACTR_SIGNED;
                                }

                                this.XYZ_AccnDataSamples.Y_axisAccnData[Y_axis_SampleIndex] = (double)tempAccn / 10.0D;
                                ++Y_axis_SampleIndex;
                                PackedData >>= NumBitsPerAccelerationSample;
                        }
                    }
                }

                boolean var21 = true;
            }

            public class XYZ_AccelerationData {
                double[] X_axisAccnData;
                double[] Y_axisAccnData;
                double[] Z_axisAccnData;

                public XYZ_AccelerationData() {
                    this.X_axisAccnData = new double[AccelerometerPacketInfo.this.NUM_ACCN_SAMPLES];
                    this.Y_axisAccnData = new double[AccelerometerPacketInfo.this.NUM_ACCN_SAMPLES];
                    this.Z_axisAccnData = new double[AccelerometerPacketInfo.this.NUM_ACCN_SAMPLES];
                }
            }
        }

        public class BreathingPacketInfo {
            private byte _SequenceNum;
            private int _TSYear;
            private byte _TSMonth;
            private byte _TSDay;
            private long _MsOfDay;
            public final short NUM_BREATHING_SAMPLES_PER_PACKET = 18;
            private short[] _BreathingSamples = new short[18];

            public BreathingPacketInfo() {
            }

            public byte GetSeqNum(byte[] Payload) {
                this._SequenceNum = (byte)(Payload[0] & 255);
                return this._SequenceNum;
            }

            public int GetTSYear(byte[] Payload) {
                this._TSYear = Payload[1] & 255;
                this._TSYear |= (Payload[2] & 255) << 8;
                return this._TSYear;
            }

            public byte GetTSMonth(byte[] Payload) {
                this._TSMonth = Payload[3];
                return this._TSMonth;
            }

            public byte GetTSDay(byte[] Payload) {
                this._TSDay = Payload[4];
                return this._TSDay;
            }

            public long GetMsofDay(byte[] Payload) {
                this._MsOfDay = 0L;
                this._MsOfDay = (long)(Payload[5] & 255);
                this._MsOfDay |= (long)((Payload[6] & 255) << 8);
                this._MsOfDay |= (long)((Payload[7] & 255) << 16);
                this._MsOfDay |= (long)((Payload[8] & 255) << 24);
                return this._MsOfDay;
            }

            public short[] GetBreathingSamples(byte[] Payload) {
                byte NumBreathingSamplesPer5bytes = 4;
                short PayloadIndex = 0;
                byte NumBitsPerBreathingSample = 10;
                short NumIterations4Breathingdata = (short)(18 / NumBreathingSamplesPer5bytes);
                short NumBreathingSamplesLeftover = (short)(18 % NumBreathingSamplesPer5bytes);
                short BreathingSampleindex = 0;

                long PackedData;
                short j;
                for(short i = 0; i < NumIterations4Breathingdata; ++i) {
                    PackedData = 0L;
                    PackedData = (long)(Payload[9 + PayloadIndex] & 255);
                    ++PayloadIndex;
                    PackedData |= (long)((Payload[9 + PayloadIndex] & 255) << 8);
                    ++PayloadIndex;
                    PackedData |= (long)((Payload[9 + PayloadIndex] & 255) << 16);
                    ++PayloadIndex;
                    PackedData |= (long)((Payload[9 + PayloadIndex] & 255) << 24);
                    ++PayloadIndex;
                    PackedData &= 4294967295L;
                    long temp = (long)((Payload[9 + PayloadIndex] & 255) << 24);
                    temp <<= 8;
                    PackedData |= temp;
                    ++PayloadIndex;

                    for(j = 0; j < NumBreathingSamplesPer5bytes; ++j) {
                        this._BreathingSamples[BreathingSampleindex] = (short)((int)(PackedData & 1023L));
                        ++BreathingSampleindex;
                        PackedData >>= NumBitsPerBreathingSample;
                    }
                }

                PackedData = 0L;
                PackedData = (long)(Payload[9 + PayloadIndex] & 255);
                ++PayloadIndex;
                PackedData |= (long)((Payload[9 + PayloadIndex] & 255) << 8);
                ++PayloadIndex;
                PackedData |= (long)((Payload[9 + PayloadIndex] & 255) << 16);

                for(j = 0; j < NumBreathingSamplesLeftover; ++j) {
                    this._BreathingSamples[BreathingSampleindex] = (short)((int)(PackedData & 1023L));
                    ++BreathingSampleindex;
                    PackedData >>= NumBitsPerBreathingSample;
                }

                return this._BreathingSamples;
            }
        }

        public class ECGPacketInfo {
            private byte _SequenceNum;
            private int _TSYear;
            private byte _TSMonth;
            private byte _TSDay;
            private long _MsOfDay;
            public final short NUM_ECG_SAMPLES_PER_PACKET = 63;
            private short[] _ECGSamples = new short[63];

            public ECGPacketInfo() {
            }

            public byte GetSeqNum(byte[] Payload) {
                this._SequenceNum = (byte)(Payload[0] & 255);
                return this._SequenceNum;
            }

            public int GetTSYear(byte[] Payload) {
                this._TSYear = Payload[1] & 255;
                this._TSYear |= (Payload[2] & 255) << 8;
                return this._TSYear;
            }

            public byte GetTSMonth(byte[] Payload) {
                this._TSMonth = Payload[3];
                return this._TSMonth;
            }

            public byte GetTSDay(byte[] Payload) {
                this._TSDay = Payload[4];
                return this._TSDay;
            }

            public long GetMsofDay(byte[] Payload) {
                this._MsOfDay = 0L;
                this._MsOfDay = (long)(Payload[5] & 255);
                this._MsOfDay |= (long)((Payload[6] & 255) << 8);
                this._MsOfDay |= (long)((Payload[7] & 255) << 16);
                this._MsOfDay |= (long)((Payload[8] & 255) << 24);
                return this._MsOfDay;
            }

            public short[] GetECGSamples(byte[] Payload) {
                byte NumECGSamplesPer5bytes = 4;
                short PayloadIndex = 0;
                byte NumBitsPerECGSample = 10;
                short NumIterations4ECGdata = (short)(63 / NumECGSamplesPer5bytes);
                short NumECGSamplesLeftover = (short)(63 % NumECGSamplesPer5bytes);
                short EcgSampleindex = 0;

                long PackedData;
                short j;
                for(short i = 0; i < NumIterations4ECGdata; ++i) {
                    PackedData = 0L;
                    PackedData = (long)Payload[9 + PayloadIndex] & 255L;
                    ++PayloadIndex;
                    PackedData |= (long)((Payload[9 + PayloadIndex] & 255) << 8);
                    ++PayloadIndex;
                    PackedData |= (long)((Payload[9 + PayloadIndex] & 255) << 16);
                    ++PayloadIndex;
                    PackedData |= (long)((Payload[9 + PayloadIndex] & 255) << 24);
                    ++PayloadIndex;
                    PackedData &= 4294967295L;
                    long temp = (long)((Payload[9 + PayloadIndex] & 255) << 24);
                    temp <<= 8;
                    PackedData |= temp;
                    ++PayloadIndex;

                    for(j = 0; j < NumECGSamplesPer5bytes; ++j) {
                        this._ECGSamples[EcgSampleindex] = (short)((int)(PackedData & 1023L));
                        ++EcgSampleindex;
                        PackedData >>= NumBitsPerECGSample;
                    }
                }

                PackedData = 0L;
                PackedData = (long)(Payload[9 + PayloadIndex] & 255);
                ++PayloadIndex;
                PackedData |= (long)((Payload[9 + PayloadIndex] & 255) << 8);
                ++PayloadIndex;
                PackedData |= (long)((Payload[9 + PayloadIndex] & 255) << 16);
                ++PayloadIndex;
                PackedData |= (long)((Payload[9 + PayloadIndex] & 255) << 24);

                for(j = 0; j < NumECGSamplesLeftover; ++j) {
                    this._ECGSamples[EcgSampleindex] = (short)((int)(PackedData & 1023L));
                    ++EcgSampleindex;
                    PackedData >>= NumBitsPerECGSample;
                }

                return this._ECGSamples;
            }
        }

        public class EventPacketInfo {
            private byte _SequenceNum;
            private int _TSYear;
            private byte _TSMonth;
            private byte _TSDay;
            private long _MsOfDay;
            private short _EventCode;
            private byte[] _EventSpecificData;

            public EventPacketInfo(byte NumBytesEventSpData) {
                this._EventSpecificData = new byte[NumBytesEventSpData];
            }

            public byte GetSeqNum(byte[] Payload) {
                this._SequenceNum = (byte)(Payload[0] & 255);
                return this._SequenceNum;
            }

            public int GetTSYear(byte[] Payload) {
                this._TSYear = Payload[1] & 255;
                this._TSYear |= (Payload[2] & 255) << 8;
                return this._TSYear;
            }

            public byte GetTSMonth(byte[] Payload) {
                this._TSMonth = Payload[3];
                return this._TSMonth;
            }

            public byte GetTSDay(byte[] Payload) {
                this._TSDay = Payload[4];
                return this._TSDay;
            }

            public long GetMsofDay(byte[] Payload) {
                this._MsOfDay = 0L;
                this._MsOfDay = (long)(Payload[5] & 255);
                this._MsOfDay |= (long)((Payload[6] & 255) << 8);
                this._MsOfDay |= (long)((Payload[7] & 255) << 16);
                this._MsOfDay |= (long)((Payload[8] & 255) << 24);
                return this._MsOfDay;
            }

            public short GetEventCode(byte[] Payload) {
                boolean EventCodeTemp = false;
                short EventCodeTemp1 = (short)(Payload[9] & 255);
                EventCodeTemp1 = (short)(EventCodeTemp1 | (Payload[10] & 255) << 8);
                this._EventCode = EventCodeTemp1;
                return this._EventCode;
            }

            public byte[] GetEventSpecificData(byte[] Payload) {
                System.arraycopy(Payload, 11, this._EventSpecificData, 0, this._EventSpecificData.length);
                return this._EventSpecificData;
            }
        }

        public class GeneralPacketInfo {
            private byte _SequenceNum;
            private int _TSYear;
            private byte _TSMonth;
            private byte _TSDay;
            private long _MsOfDay;
            private int _HeartRate;
            private double _RespirationRate;
            private double _SkinTemperature;
            private int _Posture;
            private double _VMU;
            private double _PeakAcceleration;
            private double _BatteryVoltage;
            private double _BreathingWaveAmpl;
            private double _ECGAmplitude;
            private double _ECGNoise;
            private double _XAxis_Accn_Min;
            private double _XAxis_Accn_Peak;
            private double _YAxis_Accn_Min;
            private double _YAxis_Accn_Peak;
            private double _ZAxis_Accn_Min;
            private double _ZAxis_Accn_Peak;
            private int _ZephyrSysChan;
            private int _GSR;
            private byte _ROGStatus;
            private byte _AlarmSts;
            private byte _WornStatus;
            private byte _UserIntfBtnStatus;
            private byte _BHSigLowStatus;
            private byte _BHSensConnStatus;
            private byte _BatteryStatus;

            public GeneralPacketInfo() {
            }

            public byte GetSeqNum(byte[] Payload) {
                this._SequenceNum = (byte)(Payload[0] & 255);
                return this._SequenceNum;
            }

            public int GetTSYear(byte[] Payload) {
                this._TSYear = Payload[1] & 255;
                this._TSYear |= (Payload[2] & 255) << 8;
                return this._TSYear;
            }

            public byte GetTSMonth(byte[] Payload) {
                this._TSMonth = Payload[3];
                return this._TSMonth;
            }

            public byte GetTSDay(byte[] Payload) {
                this._TSDay = Payload[4];
                return this._TSDay;
            }

            public long GetMsofDay(byte[] Payload) {
                this._MsOfDay = 0L;
                this._MsOfDay = (long)(Payload[5] & 255);
                this._MsOfDay |= (long)((Payload[6] & 255) << 8);
                this._MsOfDay |= (long)((Payload[7] & 255) << 16);
                this._MsOfDay |= (long)((Payload[8] & 255) << 24);
                return this._MsOfDay;
            }

            public int GetHeartRate(byte[] Payload) {
                this._HeartRate = Payload[9] & 255;
                return this._HeartRate;
            }

            public double GetRespirationRate(byte[] Payload) {
                boolean RespRate = false;
                short RespRate1x = (short)((Payload[12] & 255) << 8);
                boolean RespRate1 = false;
                short RespRate11 = (short)(Payload[11] & 255);
                this._RespirationRate = (double)(RespRate1x | RespRate11) / 10.0D;
                return this._RespirationRate;
            }

            public double GetSkinTemperature(byte[] Payload) {
                boolean SkinTemp = false;
                short SkinTemp1 = (short)((Payload[14] & 255) << 8);
                SkinTemp1 = (short)(SkinTemp1 | Payload[13] & 255);
                this._SkinTemperature = (double)SkinTemp1 / 10.0D;
                return this._SkinTemperature;
            }

            public int GetPosture(byte[] Payload) {
                boolean Posture = false;
                short Posture1 = (short)(Payload[15] & 255);
                Posture1 = (short)(Posture1 | (Payload[16] & 255) << 8);
                this._Posture = Posture1;
                return this._Posture;
            }

            public double GetVMU(byte[] Payload) {
                boolean VMUTemp = false;
                short VMUTemp1 = (short)(Payload[17] & 255);
                VMUTemp1 = (short)(VMUTemp1 | (Payload[18] & 255) << 8);
                this._VMU = (double)VMUTemp1 / 100.0D;
                return this._VMU;
            }

            public double GetPeakAcceleration(byte[] Payload) {
                boolean PeakAcc = false;
                short PeakAcc1 = (short)(Payload[19] & 255);
                PeakAcc1 = (short)(PeakAcc1 | (Payload[20] & 255) << 8);
                this._PeakAcceleration = (double)PeakAcc1 / 100.0D;
                return this._PeakAcceleration;
            }

            public double GetBatteryVoltage(byte[] Payload) {
                boolean BatteryVoltageTemp = false;
                short BatteryVoltageTemp1 = (short)(Payload[21] & 255);
                BatteryVoltageTemp1 = (short)(BatteryVoltageTemp1 | (Payload[22] & 255) << 8);
                this._BatteryVoltage = (double)BatteryVoltageTemp1 / 1000.0D;
                return this._BatteryVoltage;
            }

            public double GetBreathingWaveAmplitude(byte[] Payload) {
                boolean BreathingWaveAmplTemp = false;
                short BreathingWaveAmplTemp1 = (short)(Payload[23] & 255);
                BreathingWaveAmplTemp1 = (short)(BreathingWaveAmplTemp1 | (Payload[24] & 255) << 8);
                this._BreathingWaveAmpl = (double)BreathingWaveAmplTemp1 / 1000.0D;
                return this._BreathingWaveAmpl;
            }

            public double GetECGAmplitude(byte[] Payload) {
                boolean ECGAmplitudeTemp = false;
                short ECGAmplitudeTemp1 = (short)(Payload[25] & 255);
                ECGAmplitudeTemp1 = (short)(ECGAmplitudeTemp1 | (Payload[26] & 255) << 8);
                this._ECGAmplitude = (double)ECGAmplitudeTemp1 / 1000000.0D;
                return this._ECGAmplitude;
            }

            public double GetECGNoise(byte[] Payload) {
                boolean ECGNoiseTemp = false;
                short ECGNoiseTemp1 = (short)(Payload[27] & 255);
                ECGNoiseTemp1 = (short)(ECGNoiseTemp1 | (Payload[28] & 255) << 8);
                this._ECGNoise = (double)ECGNoiseTemp1 / 1000000.0D;
                return this._ECGNoise;
            }

            public double GetX_AxisAccnMin(byte[] Payload) {
                boolean XAxis_Accn_MinTemp = false;
                short XAxis_Accn_MinTemp1 = (short)(Payload[29] & 255);
                XAxis_Accn_MinTemp1 = (short)(XAxis_Accn_MinTemp1 | (Payload[30] & 255) << 8);
                this._XAxis_Accn_Min = (double)XAxis_Accn_MinTemp1 / 100.0D;
                return this._XAxis_Accn_Min;
            }

            public double GetX_AxisAccnPeak(byte[] Payload) {
                boolean XAxis_Accn_PeakTemp = false;
                short XAxis_Accn_PeakTemp1 = (short)(Payload[31] & 255);
                XAxis_Accn_PeakTemp1 = (short)(XAxis_Accn_PeakTemp1 | (Payload[32] & 255) << 8);
                this._XAxis_Accn_Peak = (double)XAxis_Accn_PeakTemp1 / 100.0D;
                return this._XAxis_Accn_Peak;
            }

            public double GetY_AxisAccnMin(byte[] Payload) {
                boolean YAxis_Accn_MinTemp = false;
                short YAxis_Accn_MinTemp1 = (short)(Payload[33] & 255);
                YAxis_Accn_MinTemp1 = (short)(YAxis_Accn_MinTemp1 | (Payload[34] & 255) << 8);
                this._YAxis_Accn_Min = (double)YAxis_Accn_MinTemp1 / 100.0D;
                return this._YAxis_Accn_Min;
            }

            public double GetY_AxisAccnPeak(byte[] Payload) {
                boolean YAxis_Accn_PeakTemp = false;
                short YAxis_Accn_PeakTemp1 = (short)(Payload[35] & 255);
                YAxis_Accn_PeakTemp1 = (short)(YAxis_Accn_PeakTemp1 | (Payload[36] & 255) << 8);
                this._YAxis_Accn_Peak = (double)YAxis_Accn_PeakTemp1 / 100.0D;
                return this._YAxis_Accn_Peak;
            }

            public double GetZ_AxisAccnMin(byte[] Payload) {
                boolean ZAxis_Accn_MinTemp = false;
                short ZAxis_Accn_MinTemp1 = (short)(Payload[37] & 255);
                ZAxis_Accn_MinTemp1 = (short)(ZAxis_Accn_MinTemp1 | (Payload[38] & 255) << 8);
                this._ZAxis_Accn_Min = (double)ZAxis_Accn_MinTemp1 / 100.0D;
                return this._ZAxis_Accn_Min;
            }

            public double GetZ_AxisAccnPeak(byte[] Payload) {
                boolean ZAxis_Accn_PeakTemp = false;
                short ZAxis_Accn_PeakTemp1 = (short)(Payload[39] & 255);
                ZAxis_Accn_PeakTemp1 = (short)(ZAxis_Accn_PeakTemp1 | (Payload[40] & 255) << 8);
                this._ZAxis_Accn_Peak = (double)ZAxis_Accn_PeakTemp1 / 100.0D;
                return this._ZAxis_Accn_Peak;
            }

            public int GetZephyrSysChan(byte[] Payload) {
                boolean ZephyrSysChanTemp = false;
                short ZephyrSysChanTemp1 = (short)(Payload[41] & 255);
                ZephyrSysChanTemp1 = (short)(ZephyrSysChanTemp1 | (Payload[42] & 255) << 8);
                this._ZephyrSysChan = ZephyrSysChanTemp1;
                return this._ZephyrSysChan;
            }

            public int GetGSR(byte[] Payload) {
                boolean GSRTemp = false;
                short GSRTemp1 = (short)(Payload[43] & 255);
                GSRTemp1 = (short)(GSRTemp1 | (Payload[44] & 255) << 8);
                this._GSR = GSRTemp1;
                return this._GSR;
            }

            public byte GetROGStatus(byte[] Payload) {
                this._ROGStatus = (byte)(Payload[49] & 255);
                return this._ROGStatus;
            }

            public byte GetAlarmStatus(byte[] Payload) {
                this._AlarmSts = (byte)(Payload[50] & 255);
                return this._AlarmSts;
            }

            public byte GetBatteryStatus(byte[] Payload) {
                this._BatteryStatus = (byte)(Payload[51] & 127);
                return this._BatteryStatus;
            }

            public byte GetBHSensConnStatus(byte[] Payload) {
                this._BHSensConnStatus = (byte)((Payload[52] & 16) >> 4);
                return this._BHSensConnStatus;
            }

            public byte _GetBHSigLowStatus(byte[] Payload) {
                this._BHSigLowStatus = (byte)((Payload[52] & 32) >> 5);
                return this._BHSigLowStatus;
            }

            public byte GetUserIntfBtnStatus(byte[] Payload) {
                this._UserIntfBtnStatus = (byte)((Payload[52] & 64) >> 6);
                return this._UserIntfBtnStatus;
            }

            public byte GetWornStatus(byte[] Payload) {
                this._WornStatus = (byte)((Payload[52] & 128) >> 7);
                return this._WornStatus;
            }
        }

        public class RtoRPacketInfo {
            private byte _SequenceNum;
            private int _TSYear;
            private byte _TSMonth;
            private byte _TSDay;
            private long _MsOfDay;
            public final short NUM_RtoR_SAMPLES_PER_PACKET = 18;
            private int[] _RtoRSamples = new int[18];

            public RtoRPacketInfo() {
            }

            public byte GetSeqNum(byte[] Payload) {
                this._SequenceNum = (byte)(Payload[0] & 255);
                return this._SequenceNum;
            }

            public int GetTSYear(byte[] Payload) {
                this._TSYear = Payload[1] & 255;
                this._TSYear |= (Payload[2] & 255) << 8;
                return this._TSYear;
            }

            public byte GetTSMonth(byte[] Payload) {
                this._TSMonth = Payload[3];
                return this._TSMonth;
            }

            public byte GetTSDay(byte[] Payload) {
                this._TSDay = Payload[4];
                return this._TSDay;
            }

            public long GetMsofDay(byte[] Payload) {
                this._MsOfDay = 0L;
                this._MsOfDay = (long)(Payload[5] & 255);
                this._MsOfDay |= (long)((Payload[6] & 255) << 8);
                this._MsOfDay |= (long)((Payload[7] & 255) << 16);
                this._MsOfDay |= (long)((Payload[8] & 255) << 24);
                return this._MsOfDay;
            }

            public int[] GetRtoRSamples(byte[] Payload) {
                short index = 0;

                for(short i = 0; i < 18; ++i) {
                    this._RtoRSamples[i] = 0;
                    this._RtoRSamples[i] = Payload[9 + index] & 255;
                    ++index;
                    this._RtoRSamples[i] |= (Payload[9 + index] & 255) << 8;
                    ++index;
                }

                return this._RtoRSamples;
            }
        }

        public class SummaryPacketInfo {
            private byte _SequenceNum;
            private int _TSYear;
            private byte _TSMonth;
            private byte _TSDay;
            private long _MsOfDay;
            private byte _VersionNumber;
            private int _HeartRate;
            private double _RespirationRate;
            private double _SkinTemperature;
            private int _Posture;
            private double _Activity;
            private double _PeakAcceleration;
            private double _BatteryVoltage;
            private byte _BatteryStatus;
            private double _BreathingWaveAmpl;
            private double _BreathingWaveNoise;
            private byte _BreathingRateConfidence;
            private double _ECGAmplitude;
            private double _ECGNoise;
            private byte _HeartRateConfidence;
            private int _HRV;
            private byte _SystemConfidence;
            private int _GSR;
            private byte _ROGStatus;
            private short _ROGTime;
            private double _Vertical_AxisAccnMin;
            private double _Vertical_AxisAccnPeak;
            private double _Lateral_AxisAccnMin;
            private double _Lateral_AxisAccnPeak;
            private double _Sagittal_AxisAccnMin;
            private double _Sagittal_AxisAccnPeak;
            private double _Device_Internal_Temperature;
            private byte _Status_Worn_Det_Level;
            private byte _Status_Button_Press_Det_Flag;
            private byte _Status_Fitted_to_Garment_Flag;
            private byte _Status_Heart_Rate_Unreliable_Flag;
            private short _LinkQuality;
            private byte _RSSI;
            private short _TxPower;
            private short _Reserved;

            public SummaryPacketInfo() {
            }



            public byte GetSeqNum(byte[] Payload) {
                this._SequenceNum = (byte)(Payload[0] & 255);
                return this._SequenceNum;
            }

            public int GetTSYear(byte[] Payload) {
                this._TSYear = Payload[1] & 255;
                this._TSYear |= (Payload[2] & 255) << 8;
                return this._TSYear;
            }

            public byte GetTSMonth(byte[] Payload) {
                this._TSMonth = Payload[3];
                return this._TSMonth;
            }

            public byte GetTSDay(byte[] Payload) {
                this._TSDay = Payload[4];
                return this._TSDay;
            }

            public long GetMsofDay(byte[] Payload) {
                this._MsOfDay = 0L;
                this._MsOfDay = (long)(Payload[5] & 255);
                this._MsOfDay |= (long)((Payload[6] & 255) << 8);
                this._MsOfDay |= (long)((Payload[7] & 255) << 16);
                this._MsOfDay |= (long)((Payload[8] & 255) << 24);
                return this._MsOfDay;
            }

            public byte GetVersionNumber(byte[] Payload) {
                this._VersionNumber = (byte)(Payload[9] & 255);
                return this._VersionNumber;
            }

            public int GetHeartRate(byte[] Payload) {
                this._HeartRate = Payload[10] & 255;
                return this._HeartRate;
            }

            public double GetRespirationRate(byte[] Payload) {
                boolean RespRate = false;
                short RespRate1x = (short)((Payload[13] & 255) << 8);
                boolean RespRate1 = false;
                short RespRate11 = (short)(Payload[12] & 255);
                this._RespirationRate = (double)(RespRate1x | RespRate11) / 10.0D;
                return this._RespirationRate;
            }

            public double GetSkinTemperature(byte[] Payload) {
                boolean SkinTemp = false;
                short SkinTemp1 = (short)((Payload[15] & 255) << 8);
                SkinTemp1 = (short)(SkinTemp1 | Payload[14] & 255);
                this._SkinTemperature = (double)SkinTemp1 / 10.0D;
                return this._SkinTemperature;
            }

            public int GetPosture(byte[] Payload) {
                boolean Posture = false;
                short Posture1 = (short)(Payload[16] & 255);
                Posture1 = (short)(Posture1 | (Payload[17] & 255) << 8);
                this._Posture = Posture1;
                return this._Posture;
            }

            public double GetActivity(byte[] Payload) {
                boolean ActivityTemp = false;
                short ActivityTemp1 = (short)(Payload[18] & 255);
                ActivityTemp1 = (short)(ActivityTemp1 | (Payload[19] & 255) << 8);
                this._Activity = (double)ActivityTemp1 / 100.0D;
                return this._Activity;
            }

            public double GetPeakAcceleration(byte[] Payload) {
                boolean PeakAcc = false;
                short PeakAcc1 = (short)(Payload[20] & 255);
                PeakAcc1 = (short)(PeakAcc1 | (Payload[21] & 255) << 8);
                this._PeakAcceleration = (double)PeakAcc1 / 100.0D;
                return this._PeakAcceleration;
            }

            public double GetBatteryVoltage(byte[] Payload) {
                boolean BatteryVoltageTemp = false;
                short BatteryVoltageTemp1 = (short)(Payload[22] & 255);
                BatteryVoltageTemp1 = (short)(BatteryVoltageTemp1 | (Payload[23] & 255) << 8);
                this._BatteryVoltage = (double)BatteryVoltageTemp1 / 1000.0D;
                return this._BatteryVoltage;
            }

            public byte GetBatteryLevel(byte[] Payload) {
                this._BatteryStatus = (byte)(Payload[24] & 127);
                return this._BatteryStatus;
            }

            public double GetBreathingWaveAmplitude(byte[] Payload) {
                boolean BreathingWaveAmplTemp = false;
                short BreathingWaveAmplTemp1 = (short)(Payload[25] & 255);
                BreathingWaveAmplTemp1 = (short)(BreathingWaveAmplTemp1 | (Payload[26] & 255) << 8);
                this._BreathingWaveAmpl = (double)BreathingWaveAmplTemp1 / 1000.0D;
                return this._BreathingWaveAmpl;
            }

            public double GetBreathingWaveAmpNoise(byte[] Payload) {
                boolean BreathingWaveNoiseTemp = false;
                short BreathingWaveNoiseTemp1 = (short)(Payload[27] & 255);
                BreathingWaveNoiseTemp1 = (short)(BreathingWaveNoiseTemp1 | (Payload[28] & 255) << 8);
                this._BreathingWaveNoise = (double)BreathingWaveNoiseTemp1 / 1000.0D;
                return this._BreathingWaveNoise;
            }

            public byte GetBreathingRateConfidence(byte[] Payload) {
                this._BreathingRateConfidence = (byte)(Payload[29] & 255);
                return this._BreathingRateConfidence;
            }

            public double GetECGAmplitude(byte[] Payload) {
                boolean ECGAmplitudeTemp = false;
                short ECGAmplitudeTemp1 = (short)(Payload[30] & 255);
                ECGAmplitudeTemp1 = (short)(ECGAmplitudeTemp1 | (Payload[31] & 255) << 8);
                this._ECGAmplitude = (double)ECGAmplitudeTemp1 / 1000000.0D;
                return this._ECGAmplitude;
            }

            public double GetECGNoise(byte[] Payload) {
                boolean ECGNoiseTemp = false;
                short ECGNoiseTemp1 = (short)(Payload[32] & 255);
                ECGNoiseTemp1 = (short)(ECGNoiseTemp1 | (Payload[33] & 255) << 8);
                this._ECGNoise = (double)ECGNoiseTemp1 / 1000000.0D;
                return this._ECGNoise;
            }

            public byte GetHeartRateRateConfidence(byte[] Payload) {
                this._HeartRateConfidence = (byte)(Payload[34] & 255);
                return this._HeartRateConfidence;
            }

            public int GetHearRateVariability(byte[] Payload) {
                boolean HRVTemp = false;
                int HRVTemp1 = Payload[35] & 255;
                HRVTemp1 |= (Payload[36] & 255) << 8;
                this._HRV = HRVTemp1;
                return this._HRV;
            }

            public byte GetSystemConfidence(byte[] Payload) {
                this._SystemConfidence = (byte)(Payload[37] & 255);
                return this._SystemConfidence;
            }

            public int GetGSR(byte[] Payload) {
                boolean GSRTemp = false;
                short GSRTemp1 = (short)(Payload[38] & 255);
                GSRTemp1 = (short)(GSRTemp1 | (Payload[39] & 255) << 8);
                this._GSR = GSRTemp1;
                return this._GSR;
            }

            public byte GetROGStatus(byte[] Payload) {
                boolean ROGStatusTemp = false;
                short ROGStatusTemp1 = (short)(Payload[40] & 255);
                ROGStatusTemp1 = (short)(ROGStatusTemp1 | (Payload[41] & 255) << 8);
                this._ROGStatus = (byte)(ROGStatusTemp1 & 7);
                return this._ROGStatus;
            }

            public int GetROGTime(byte[] Payload) {
                boolean ROGStatusTemp = false;
                short ROGStatusTemp1 = (short)(Payload[40] & 255);
                ROGStatusTemp1 = (short)(ROGStatusTemp1 | (Payload[41] & 255) << 8);
                this._ROGTime = (short)((ROGStatusTemp1 & '\ufff8') >> 3);
                return this._ROGTime;
            }

            public double GetVertical_AxisAccnMin(byte[] Payload) {
                boolean Vertical_AxisAccnMinTemp = false;
                short Vertical_AxisAccnMinTemp1 = (short)(Payload[42] & 255);
                Vertical_AxisAccnMinTemp1 = (short)(Vertical_AxisAccnMinTemp1 | (Payload[43] & 255) << 8);
                this._Vertical_AxisAccnMin = (double)Vertical_AxisAccnMinTemp1 / 100.0D;
                return this._Vertical_AxisAccnMin;
            }

            public double GetVertical_AxisAccnPeak(byte[] Payload) {
                boolean Vertical_AxisAccnPeakTemp = false;
                short Vertical_AxisAccnPeakTemp1 = (short)(Payload[44] & 255);
                Vertical_AxisAccnPeakTemp1 = (short)(Vertical_AxisAccnPeakTemp1 | (Payload[45] & 255) << 8);
                this._Vertical_AxisAccnPeak = (double)Vertical_AxisAccnPeakTemp1 / 100.0D;
                return this._Vertical_AxisAccnPeak;
            }

            public double GetLateral_AxisAccnMin(byte[] Payload) {
                boolean Lateral_AxisAccnMinTemp = false;
                short Lateral_AxisAccnMinTemp1 = (short)(Payload[46] & 255);
                Lateral_AxisAccnMinTemp1 = (short)(Lateral_AxisAccnMinTemp1 | (Payload[47] & 255) << 8);
                this._Lateral_AxisAccnMin = (double)Lateral_AxisAccnMinTemp1 / 100.0D;
                return this._Lateral_AxisAccnMin;
            }

            public double GetLateral_AxisAccnPeak(byte[] Payload) {
                boolean Lateral_AxisAccnPeakTemp = false;
                short Lateral_AxisAccnPeakTemp1 = (short)(Payload[48] & 255);
                Lateral_AxisAccnPeakTemp1 = (short)(Lateral_AxisAccnPeakTemp1 | (Payload[49] & 255) << 8);
                this._Lateral_AxisAccnPeak = (double)Lateral_AxisAccnPeakTemp1 / 100.0D;
                return this._Lateral_AxisAccnPeak;
            }

            public double GetSagittal_AxisAccnMin(byte[] Payload) {
                boolean Sagittal_AxisAccnMinTemp = false;
                short Sagittal_AxisAccnMinTemp1 = (short)(Payload[50] & 255);
                Sagittal_AxisAccnMinTemp1 = (short)(Sagittal_AxisAccnMinTemp1 | (Payload[51] & 255) << 8);
                this._Sagittal_AxisAccnMin = (double)Sagittal_AxisAccnMinTemp1 / 100.0D;
                return this._Sagittal_AxisAccnMin;
            }

            public double GetSagittal_AxisAccnPeak(byte[] Payload) {
                boolean Sagittal_AxisAccnPeakTemp = false;
                short Sagittal_AxisAccnPeakTemp1 = (short)(Payload[52] & 255);
                Sagittal_AxisAccnPeakTemp1 = (short)(Sagittal_AxisAccnPeakTemp1 | (Payload[53] & 255) << 8);
                this._Sagittal_AxisAccnPeak = (double)Sagittal_AxisAccnPeakTemp1 / 100.0D;
                return this._Sagittal_AxisAccnPeak;
            }

            public double GetDevice_Internal_Temperature(byte[] Payload) {
                boolean Device_Internal_TemperatureTemp = false;
                short Device_Internal_TemperatureTemp1 = (short)(Payload[54] & 255);
                Device_Internal_TemperatureTemp1 = (short)(Device_Internal_TemperatureTemp1 | (Payload[55] & 255) << 8);
                this._Device_Internal_Temperature = (double)Device_Internal_TemperatureTemp1 / 10.0D;
                return this._Device_Internal_Temperature;
            }

            private byte Get_Status_WornDet_Level(byte[] Payload) {
                this._Status_Worn_Det_Level = (byte)(Payload[56] & 3);
                return this._Status_Worn_Det_Level;
            }

            private byte GetStatus_Button_Press_Det_Flag(byte[] Payload) {
                this._Status_Button_Press_Det_Flag = (byte)((Payload[56] & 4) >> 2);
                return this._Status_Button_Press_Det_Flag;
            }

            private byte GetStatus_Fitted_to_Garment_Flag(byte[] Payload) {
                this._Status_Fitted_to_Garment_Flag = (byte)((Payload[56] & 8) >> 3);
                return this._Status_Fitted_to_Garment_Flag;
            }

            private byte GetStatus_Heart_Rate_Unreliable_Flag(byte[] Payload) {
                this._Status_Heart_Rate_Unreliable_Flag = (byte)((Payload[56] & 16) >> 4);
                return this._Status_Heart_Rate_Unreliable_Flag;
            }

            public short GetLinkQuality(byte[] Payload) {
                this._LinkQuality = (short)(Payload[58] & 255);
                return this._LinkQuality;
            }

            public byte GetRSSI(byte[] Payload) {
                this._RSSI = (byte)(Payload[59] & 255);
                return this._RSSI;
            }

            public short GetTxPower(byte[] Payload) {
                this._TxPower = (short)(Payload[60] & 255);
                return this._TxPower;
            }

            private short GetReserved(byte[] Payload) {
                boolean ReservedTemp = false;
                short ReservedTemp1 = (short)(Payload[61] & 255);
                ReservedTemp1 = (short)(ReservedTemp1 | (Payload[62] & 255) << 8);
                this._Reserved = ReservedTemp1;
                return this._Reserved;
            }
        }
    }

