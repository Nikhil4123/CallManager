# Call Manager App

## Overview
Call Manager is an Android application that provides call management functionality with two main features:
1. Call History - View detailed call logs with contact information
2. Dialpad - Make calls directly from the app

## Features

### Call History
- View complete call history
- Display caller name (if saved in contacts)
- Show call duration
- Indicate call type (Incoming/Outgoing/Missed/Rejected)
- Display date and time of calls
- Sort calls by most recent first

### Dialpad
- Numeric keypad for entering phone numbers
- Direct calling functionality
- Contact integration
- Simple and intuitive interface


## Required Permissions
```xml
<uses-permission android:name="android.permission.READ_CALL_LOG" />
<uses-permission android:name="android.permission.READ_CONTACTS" />
<uses-permission android:name="android.permission.CALL_PHONE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
```

## Setup Instructions

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Run the app on an emulator or physical device

## Components Description

### MainActivity
- Main entry point of the application
- Handles permission requests
- Displays call history
- Provides navigation to dialpad

### DialpadActivity
- Implements phone dialer interface
- Handles number input
- Manages call initiation
- Provides basic phone functionality

### ListAdapter
- Custom adapter for displaying call history
- Formats call details for display
- Handles call history item views

### ContactAdapter
- Manages contact list display
- Handles contact selection
- Provides click functionality for calling contacts

### CallDetails
Data class containing:
- Caller name
- Phone number
- Call duration
- Call type
- Call date and time

### CallService & CallReceiver
- Monitor phone state changes
- Track incoming and outgoing calls
- Update call history

## Implementation Details

### Permission Handling
The app implements runtime permission requests for:
- Call log access
- Contact access
- Phone state monitoring
- Making phone calls

### Data Management
- Call history is retrieved from system CallLog
- Contact information is fetched from ContactsContract
- All data operations are performed asynchronously

### User Interface
- Material Design components
- Responsive layouts
- Simple navigation between screens
- Clear call history display

## Dependencies
```gradle
dependencies {
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
}
```

## Common Issues and Solutions

1. Permission Denied
   - Ensure all required permissions are granted in app settings
   - Implement proper permission handling in settings

2. Contact Display Issues
   - Verify contact permissions
   - Check contact sync settings on device

3. Call History Not Updating
   - Check call log permissions
   - Verify CallReceiver registration

## Future Enhancements

1. Call Recording Feature
2. Call Blocking Functionality
3. Contact Group Management
4. Call Statistics and Analytics
5. Dark Mode Support
6. Call Reminder Settings

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

This project is licensed under the MIT License - see the LICENSE file for details.
