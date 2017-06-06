
************************** Dexcom G4 Receiver Tools DevKit *****************************

This document describes the contents of the DevKit and provides instructions for its installation and usage.


CONTENTS

The DevKit (folder) consists of the following top-level items:

1. ProductDescription_G4ReceiverToolsDevKit.doc
2. ReceiverTools - folder containing the ReceiverTools DLL (DexCom.ReceiverTools.dll) and API Description (DexCom G4 Receiver Tools DevKit API Description.chm)
3. ReceiverToolsDevKit.sln - .NET demo/test application Visual Studio 2010 solution file
4. ReceiverToolsTester - folder containing .NET demo/test application source code
4. Bridge - folder containing MATLAB 2009B demo/test application


INSTALLATION

1. Verify the contents of the DevKit folder as described above. Copy the contents to a local folder on the host PC (e.g. C:\DexCom\G4 Receiver Tools DevKit). At this point, the DLL is ready for use as a means for development. 
To demonstrate its functionality using either of the provided test applications, complete the following steps:


.NET demo/test application

1. The .NET test/demo application is a C# application developed with Visual Studio 2010 against .NET 3.5 SP1. To build the application, navigate to the local installation folder (e.g. C:\DexCom\G4 Receiver Tools DevKit) and 
open ReceiverToolsDevKit.sln in Visual Studio 2010. Build the solution. If successful, the application (DexCom.ReceiverToolsTester.exe) will be found in either \ReceiverToolsTester\bin\Debug or \ReceiverToolsTester\bin\Release depending on the IDE build configuration (Debug/Release).

2. Launch ReceiverToolsTester.exe (with no receivers attached to the host PC). Click the 'Check for Drivers' button. Drivers will be automatically installed on the host PC if not already installed. Follow the installation wizard to complete the driver installation.

3. Attach one or more receivers to the back of the host PC

4. Click the 'Start Receiver Detection' button.

5. Confirm that the receiver(s) are detected by the creation of additional tabs in the application GUI to the right of the 'Testing' tab. New tab labels should correspond to a receiver's serial number.


MATLAB Bridge

1. The Bridge folder contains the following subfolders: Bin, Source, and Test. Bin contains binary components needed by MATLAB at runtime, including DexCom.ReceiverTools.dll.

2. The Source folder contains a MATLAB package that wraps the API of the .NET DLL in a MATLAB interface. Launch the MATLAB IDE and add the location of the Source folder (e.g. C:\DexCom\G4 ReceiverTools DevKit\Bridge\Source) to the MATLAB path

3. Issue the command ‘what DexCom/Receiver’ at the command prompt in the MATLAB Command Window

4. Confirm that MATLAB identifies 5 classes in the DexCom.Receiver package: Receiver, ReceiverEventArgs, ReceiverManager, ReceiverUtils, and ScannerEventArgs. At this point, the DexCom.Receiver package is ready for use as means for 
MATLAB development. 

5. To test installation, launch the main MATLAB function contained in \Bridge\Test\main.m.

6. Confirm a message written to the MATLAB Command Window that states: "Ready to start scannning for receivers ...". Confirm that receivers that are already or subsequently connected are detected. 
