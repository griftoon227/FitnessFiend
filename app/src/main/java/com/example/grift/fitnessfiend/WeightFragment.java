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
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class WeightFragment extends Fragment {
    private EditText dateEditText;
    private EditText timeEditText;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Calendar calendar = Calendar.getInstance();
    private int currentHour;
    private int currentMinute;
    private String amPM;

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_weight, container, false);
        createNotificationChannel();
        dateEditText = view.findViewById(R.id.dateEditText);
        dateEditText.setOnClickListener(view12 -> {
            datePickerDialog = new DatePickerDialog(requireContext(), (
                    datePicker, year, month, day) -> dateEditText.setText(String.format("%s/%s/%s", month + 1, day,
                    year)), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            datePickerDialog.show();
        });

        timeEditText = view.findViewById(R.id.timeEditText);
        timeEditText.setOnClickListener(view1 -> {
            currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            currentMinute = calendar.get(Calendar.MINUTE);

            timePickerDialog = new TimePickerDialog(getActivity(), (timePicker, hourOfDay, minutes) -> {
                if (hourOfDay >= 12){
                    amPM = "PM";
                }
                else {
                    amPM = "AM";
                }
                timeEditText.setText(String.format("%02d:%02d %s", hourOfDay <= 12 ? (hourOfDay == 0 ? 12 : hourOfDay) : hourOfDay-12, minutes, amPM));
            }, currentHour, currentMinute, false);
            timePickerDialog.show();
        });

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(
                    FirebaseAuth.getInstance().getCurrentUser().getUid());
            reference.child("weight_reminder_screen").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dateEditText.setText(dataSnapshot.child("date").getValue(String.class));
                    timeEditText.setText(dataSnapshot.child("time").getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }

        Button setReminderButton = view.findViewById(R.id.setRemindButton);
        setReminderButton.setOnClickListener(view13 -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(
                        FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.child("weight_reminder_screen").child("date").setValue(dateEditText.getText().toString());
                reference.child("weight_reminder_screen").child("time").setValue(timeEditText.getText().toString());
            }
            Toast.makeText(getContext(), "Reminder set", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), WeightReminderBroadcast.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        });

        return view;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "WeightReminderChannel";
            String description = "Channel for Weight Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyTime", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
