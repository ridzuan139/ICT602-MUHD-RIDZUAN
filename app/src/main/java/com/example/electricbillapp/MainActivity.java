package com.example.electricbillapp;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    TextView txtOutput;
    EditText editKWh;
    EditText editRebate;
    Button btnCalc;
    Button btnClear;
    Button btnSave;

    // Firebase database reference
    //FirebaseDatabase database = FirebaseDatabase.getInstance("https://electricbillcalculator-3e3a4-default-rtdb.firebaseio.com/");
    //DatabaseReference myRef = database.getReference("message");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        txtOutput = findViewById(R.id.txtOutput);
        editKWh = findViewById(R.id.editKWh);
        editRebate = findViewById(R.id.editRebate);
        btnCalc = findViewById(R.id.btnCalc);
        btnClear = findViewById(R.id.btnClear);
        btnSave = findViewById(R.id.btnSave);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Optionally set a custom title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Electricity Bill Calculator");
        }


        // Calculate button functionality
        btnCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Taking values from EditText
                String inputKWh = editKWh.getText().toString();
                String inputRebate = editRebate.getText().toString();

                // Check if inputs are empty
                if (inputKWh.isEmpty() || inputRebate.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter both KWh and Rebate values.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convert to double
                double KWh = Double.parseDouble(inputKWh);
                double Rebate = Double.parseDouble(inputRebate);

                // Calculate total charges
                double totalCharges = 0.0;
                double afterRebate = 0.0;
                if (KWh <= 200) {
                    totalCharges = KWh * 0.218;
                } else if (KWh > 200 && KWh <= 300) {
                    totalCharges = ((KWh - 200) * 0.334) + (200 * 0.218);
                } else if (KWh > 300 && KWh <= 600) {
                    totalCharges = ((KWh - 300) * 0.516) + (200 * 0.218) + (100 * 0.334);
                } else if (KWh > 600) {
                    totalCharges = (KWh - 600) * 0.546 + (200 * 0.218) + (100 * 0.334) + (300 * 0.516);
                }
                afterRebate = totalCharges - (totalCharges * (Rebate/100));

                // Display the result
                txtOutput.setText("RM"+Double.toString(afterRebate));
            }
        });

        // Clear button functionality
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtOutput.setText("0.0");
                editKWh.setText("");
                editRebate.setText("");
            }
        });

        // Inside your onClickListener:
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Capture user input
                String inputKWh = editKWh.getText().toString();
                String inputRebate = editRebate.getText().toString();
                String totalCharges = txtOutput.getText().toString();

                if (inputKWh.isEmpty() || inputRebate.isEmpty() || totalCharges.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please calculate first!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get the current month
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy"); // Example: "November 2024"
                String currentMonth = dateFormat.format(calendar.getTime());

                // Create a data object
                CalculationData data = new CalculationData(inputKWh, inputRebate, totalCharges, currentMonth);

                // Save to Firebase with logging and error handling
                /*myRef.push().setValue(data)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                                Log.d("Firebase", "Data saved successfully!");
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to save data!", Toast.LENGTH_SHORT).show();
                                Log.e("Firebase", "Failed to save data: " + task.getException());
                            }
                        });*/
            }
        });

        // Handle system bars insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selected = item.getItemId();

        if (selected == R.id.menuHome) {
            Intent homeIntent = new Intent(this, MainActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            Toast.makeText(this, "Home Clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (selected == R.id.menuAbout) {
            Intent aboutIntent = new Intent(this, MenuAbout.class);
            startActivity(aboutIntent);
            Toast.makeText(this, "About Clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Inner class for data model
    public class CalculationData {
        private String kwh;
        private String rebate;
        private String totalCharges;
        private String month;

        // Default constructor for Firebase
        public CalculationData() {}

        // Constructor with all fields
        public CalculationData(String kwh, String rebate, String totalCharges, String month) {
            this.kwh = kwh;
            this.rebate = rebate;
            this.totalCharges = totalCharges;
            this.month = month;
        }

        // Getters and setters (if needed)
        public String getKwh() {
            return kwh;
        }

        public void setKwh(String kwh) {
            this.kwh = kwh;
        }

        public String getRebate() {
            return rebate;
        }

        public void setRebate(String rebate) {
            this.rebate = rebate;
        }

        public String getTotalCharges() {
            return totalCharges;
        }

        public void setTotalCharges(String totalCharges) {
            this.totalCharges = totalCharges;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }
    }

}