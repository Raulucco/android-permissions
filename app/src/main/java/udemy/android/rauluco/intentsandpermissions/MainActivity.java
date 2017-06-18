package udemy.android.rauluco.intentsandpermissions;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final int PHONE_CALL_CODE = 50;
    private final int PHOTO_CODE = 25;

    private Intent callIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText phoneNum = (EditText) findViewById(R.id.telephoneNum);
        EditText webDomain = (EditText) findViewById(R.id.webDomain);
        EditText emailAddress = (EditText) findViewById(R.id.emailAddress);

        ImageButton callBtn = (ImageButton) findViewById(R.id.callBtn);
        ImageButton browserBtn = (ImageButton) findViewById(R.id.callBtn);
        ImageButton emailBtn = (ImageButton) findViewById(R.id.callBtn);
        ImageButton photoBtn = (ImageButton) findViewById(R.id.photoBtn);

        setCallBtnListener(callBtn, phoneNum);
        setBrowserBtnListener(browserBtn, webDomain);
        setEmailBtnListener(emailBtn, emailAddress);
        setPhotoBtnListener(photoBtn);
    }

    private boolean isValidValue (String str) {
        return str != null && !str.isEmpty();
    }

    private void setEmailBtnListener(ImageButton emailBtn, final EditText email) {
        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = email.getText().toString();
                if (isValidValue(address)) {
                    Intent i = new Intent(Intent.ACTION_SEND, Uri.parse(address));
                    i.setType("plain/text");
                    startActivity(Intent.createChooser(i, "Select the application"));
                } else {
                    Toast.makeText(MainActivity.this, "Please insert a valid email address", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setBrowserBtnListener(ImageButton browserBtn, final EditText domain) {
        browserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String domainName = domain.getText().toString();
                if (isValidValue(domainName)) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("http://" + domainName));
                    startActivity(i);
                } else {
                    Toast.makeText(MainActivity.this, "Please insert a valid domain name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setCallBtnListener(ImageButton callBtn, final EditText phone) {
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = phone.getText().toString();
                if (isValidValue(number)) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                    if (AndroidVersionIsActual()) {
                        if (CallsAreAllow() &&
                                ActivityCompat.checkSelfPermission(
                                        MainActivity.this, Manifest.permission.CALL_PHONE
                                ) == PackageManager.PERMISSION_GRANTED)
                        {
                            startActivity(intent);
                        } else if (!shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                            setCallIntent(intent);
                            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PHONE_CALL_CODE);
                        } else {
                            Toast.makeText(MainActivity.this, "Please allow me to call", Toast.LENGTH_LONG).show();
                            Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            settingsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                            settingsIntent.setData(Uri.parse("package:" + getPackageName()));
                            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            startActivity(settingsIntent);
                        }
                    } else {
                        if (CallsAreAllow()) {
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Please assign call permission", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please insert a valid phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean CallsAreAllow() {
       return PermissionIsAllow(Manifest.permission.CALL_PHONE);
    }

    private boolean AndroidVersionIsActual() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private boolean PermissionIsAllow(String permission) {
        int resultCode = this.checkCallingOrSelfPermission(permission);
        return resultCode == PackageManager.PERMISSION_GRANTED;
    }

    public void setPhotoBtnListener(ImageButton photoBtn) {
        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(i, PHOTO_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String photoUri = data.toUri(0);
                Toast.makeText(MainActivity.this, "" + photoUri, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Something whent wrong", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PHONE_CALL_CODE) {
            String permission = permissions[0];

            if (permission.equals(Manifest.permission.CALL_PHONE) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    callIntent != null)
            {
                startActivity(callIntent);
            }

            setCallIntent(null);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void setCallIntent(Intent callIntent) {
        this.callIntent = callIntent;
    }
}
