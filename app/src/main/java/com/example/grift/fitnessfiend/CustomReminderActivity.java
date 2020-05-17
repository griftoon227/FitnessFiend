package com.example.grift.fitnessfiend;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CustomReminderActivity extends AppCompatActivity {
    private EditText medicineNameEditText;
    private EditText mTimeEditText;
    private EditText mDateEditText;
    private EditText repeatTypeEditText;
    private EditText repeatTypeNo;
    private TextView frequencyTextView;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private int currentHour;
    private int currentMinute;
    private String amPM;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mMinute;
    private int mHour;
    private String mRepeat;
    private String mRepeatNo;
    private String mRepeatType;
    private Switch mRepeatSwitch;
    private DatabaseReference mReference;
    private Calendar mCalendar;
    private long mRepeatTime;
    private Uri mCurrentReminderUri;
    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_reminder);

        Intent intent = getIntent();
        mCurrentReminderUri = intent.getData();

        medicineNameEditText = findViewById(R.id.medicationNameEditText);
        repeatTypeEditText = findViewById(R.id.repeatType);
        mDateEditText = findViewById(R.id.mDateEditText);
        mRepeatSwitch = findViewById(R.id.repeat_switch);
        repeatTypeNo = findViewById(R.id.setRepeatNo);
        frequencyTextView = findViewById(R.id.frequencyTextView);

        //initializers
        mRepeat = "false";
        frequencyTextView.setText(R.string.off_text);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mReference = FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid());
        }

        mDateEditText.setOnClickListener(view -> {
            mCalendar = Calendar.getInstance();
            mYear = mCalendar.get(Calendar.YEAR);
            mMonth = mCalendar.get(Calendar.MONTH);
            mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
            datePickerDialog = new DatePickerDialog(CustomReminderActivity.this, (datePicker, year, month, day) -> mDateEditText.setText((month + 1) + "/" + (day) + "/" + year), mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        mTimeEditText = findViewById(R.id.mTimeEditText);
        mTimeEditText.setOnClickListener(view -> {
            mCalendar = Calendar.getInstance();
            currentHour = mCalendar.get(Calendar.HOUR_OF_DAY);
            currentMinute = mCalendar.get(Calendar.MINUTE);

            timePickerDialog = new TimePickerDialog(CustomReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @SuppressLint({"DefaultLocale", "SetTextI18n"})
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                    if (hourOfDay >= 12){
                        amPM = "PM";
                    }
                    else {
                        amPM = "AM";
                    }
                    mTimeEditText.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPM);
                }
            }, currentHour, currentMinute, false);
            timePickerDialog.show();
        });

        mRepeatSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                mRepeat = "true";
                frequencyTextView.setText(String.format("Every %s %s(s)", mRepeatNo == null ? "" : mRepeatNo,
                        mRepeatType == null ? "" : mRepeatType));
            } else {
                mRepeat = "false";
                frequencyTextView.setText(R.string.off_text);
            }
        });

        repeatTypeEditText.setOnClickListener(view -> {
            final String[] items = new String[5];

            items[0] = "Minute";
            items[1] = "Hour";
            items[2] = "Day";
            items[3] = "Week";
            items[4] = "Month";

            // Create List Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(CustomReminderActivity.this);
            builder.setTitle("Select Type");
            builder.setItems(items, (dialog, item) -> {
                mRepeatType = items[item];
                repeatTypeEditText.setText(mRepeatType);
                frequencyTextView.setText(String.format("Every %s %s(s)", mRepeatNo == null ? "" : mRepeatNo,
                        mRepeatType == null ? "" : mRepeatType));
            });
            AlertDialog alert = builder.create();
            alert.show();
        });

        repeatTypeNo.setOnClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(CustomReminderActivity.this);
            alert.setTitle("Enter Number");

            // Create EditText box to input repeat number
            final EditText input = new EditText(CustomReminderActivity.this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            alert.setView(input);
            alert.setPositiveButton("Ok",
                    (dialog, whichButton) -> {
                        if (input.getText().toString().length() == 0) {
                            mRepeatNo = Integer.toString(1);
                            repeatTypeNo.setText(mRepeatNo);
                            frequencyTextView.setText(String.format("Every %s %s(s)", mRepeatNo == null ? "" : mRepeatNo,
                                    mRepeatType == null ? "" : mRepeatType));
                        } else {
                            mRepeatNo = input.getText().toString().trim();
                            repeatTypeNo.setText(mRepeatNo);
                            frequencyTextView.setText(String.format("Every %s %s(s)", mRepeatNo == null ? "" : mRepeatNo,
                                    mRepeatType == null ? "" : mRepeatType));
                        }
                    });
            alert.setNegativeButton("Cancel", (dialog, whichButton) -> { });
            alert.show();
        });

        Button saveButton = findViewById(R.id.setRemindButton);
        saveButton.setOnClickListener(view -> {
            String medicineName = medicineNameEditText.getText().toString();
            String frequencyTime = repeatTypeNo.getText().toString() + " " + repeatTypeEditText.getText().toString() + "(s)";

            mCalendar.set(Calendar.MONTH, mMonth);
            mCalendar.set(Calendar.YEAR, mYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
            mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
            mCalendar.set(Calendar.MINUTE, mMinute);
            mCalendar.set(Calendar.SECOND, 0);

            long selectedTimestamp =  mCalendar.getTimeInMillis();

            Intent intent1 = new Intent(CustomReminderActivity.this, MedicineBroadcast.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(CustomReminderActivity.this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            System.out.println(mRepeat);

            if (mRepeat.equals("true")) {
                switch (mRepeatType) {
                    case "Minute":
                        mRepeatTime = Integer.parseInt(mRepeatNo) * milMinute;
                        break;
                    case "Hour":
                        mRepeatTime = Integer.parseInt(mRepeatNo) * milHour;
                        break;
                    case "Day":
                        mRepeatTime = Integer.parseInt(mRepeatNo) * milDay;
                        break;
                    case "Week":
                        mRepeatTime = Integer.parseInt(mRepeatNo) * milWeek;
                        break;
                    case "Month":
                        mRepeatTime = Integer.parseInt(mRepeatNo) * milMonth;
                        break;
                }
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, selectedTimestamp, mRepeatTime, pendingIntent);
                mReference.child("custom_reminder_screen").child(medicineName);
                mReference.child("custom_reminder_screen").child(medicineName).child("date").setValue(mDateEditText.getText().toString());
                mReference.child("custom_reminder_screen").child(medicineName).child("time").setValue(mTimeEditText.getText().toString());
                mReference.child("custom_reminder_screen").child(medicineName).child("frequency").setValue(mRepeatSwitch.getShowText());
                mReference.child("custom_reminder_screen").child(medicineName).child("frequencyTime").setValue(frequencyTime);

            } else if (mRepeat.equals("false")) {
                mReference.child("custom_reminder_screen").child(medicineName);
                mReference.child("custom_reminder_screen").child(medicineName).child("date").setValue(mDateEditText.getText().toString());
                mReference.child("custom_reminder_screen").child(medicineName).child("time").setValue(mTimeEditText.getText().toString());
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, selectedTimestamp, AlarmManager.INTERVAL_DAY, pendingIntent);
            }
            Toast.makeText(getApplicationContext(), "Reminder set", Toast.LENGTH_LONG).show();
            finish();
        });

    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MedicineReminderChannel";
            String description = "Channel for medicine Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyTime", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}



