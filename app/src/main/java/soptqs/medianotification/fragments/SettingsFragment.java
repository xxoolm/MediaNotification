package soptqs.medianotification.fragments;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import james.colorpickerdialog.dialogs.ColorPickerDialog;
import james.colorpickerdialog.dialogs.PreferenceDialog;
import soptqs.medianotification.R;
import soptqs.medianotification.activities.MainActivity;
import soptqs.medianotification.services.NotificationService;
import soptqs.medianotification.utils.PreferenceUtils;
import soptqs.medianotification.utils.ShellUtils;
import soptqs.medianotification.views.ColorImageView;

public class SettingsFragment extends BaseFragment {

    private static final int REQUEST_NOTIFICATION = 1034;

    private View tutorial;
    private Button tutorialLearnMore;
    private Button tutorialOk;
    private SwitchCompat mediaNotificationSwitch;
    private AppCompatSpinner colorMethodSpinner;
    private View customColorView;
    private ColorImageView customColor;
    private SwitchCompat inverseTextSwitch;
    private SwitchCompat highContrastSwitch;
    private SwitchCompat forceMdIcons;
    private SwitchCompat alwaysDismissibleSwitch;
    private SwitchCompat killProcessSwitch;
    private SwitchCompat cancelOriginalNotificationSwitch;
    private View mediaControls;
    private View storagePermission;
    private Button storagePermissionButton;
    private SwitchCompat albumArtSwitch;
    private SwitchCompat lastFmSwitch;
    private View rootPermission;
    private Button rootPermissionButton;
    private SwitchCompat receiverSwitch;
    private Button copyButton;
    private Button magiskButton;


    private SharedPreferences prefs;

    String[] commandlist = new String[]{
            "mount -o rw,remount /system",
            "mkdir -p -m 755 /system/app/Medianotification",
            "cp /data/app/soptqs.medianotification-1/* /system/app/Medianotification"
    };



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(soptqs.medianotification.R.layout.fragment_settings, container, false);
        tutorial = view.findViewById(soptqs.medianotification.R.id.tutorial);
        tutorialLearnMore = view.findViewById(soptqs.medianotification.R.id.tutorialLearnMore);
        tutorialOk = view.findViewById(soptqs.medianotification.R.id.tutorialOk);
        mediaNotificationSwitch = view.findViewById(soptqs.medianotification.R.id.mediaNotificationSwitch);
        colorMethodSpinner = view.findViewById(soptqs.medianotification.R.id.colorMethodSpinner);
        customColorView = view.findViewById(soptqs.medianotification.R.id.customColorView);
        customColor = view.findViewById(soptqs.medianotification.R.id.customColor);
        inverseTextSwitch = view.findViewById(soptqs.medianotification.R.id.inverseTextSwitch);
        highContrastSwitch = view.findViewById(soptqs.medianotification.R.id.highContrastSwitch);
        forceMdIcons = view.findViewById(soptqs.medianotification.R.id.forceMdIcons);
        alwaysDismissibleSwitch = view.findViewById(soptqs.medianotification.R.id.alwaysDismissibleSwitch);
        killProcessSwitch = view.findViewById(soptqs.medianotification.R.id.killProcessSwitch);
        cancelOriginalNotificationSwitch = view.findViewById(soptqs.medianotification.R.id.cancelOriginalNotificationSwitch);
        mediaControls = view.findViewById(soptqs.medianotification.R.id.mediaControls);
        storagePermission = view.findViewById(soptqs.medianotification.R.id.storagePermission);
        storagePermissionButton = view.findViewById(soptqs.medianotification.R.id.storagePermissionButton);
        albumArtSwitch = view.findViewById(soptqs.medianotification.R.id.albumArtSwitch);
        lastFmSwitch = view.findViewById(soptqs.medianotification.R.id.lastFmSwitch);
        rootPermission = view.findViewById(soptqs.medianotification.R.id.rootPermission);
        rootPermissionButton = view.findViewById(soptqs.medianotification.R.id.rootPermissionButton);
        receiverSwitch = view.findViewById(soptqs.medianotification.R.id.receiverSwitch);
        copyButton = view.findViewById(R.id.CopyapkButton);
        magiskButton = view.findViewById(R.id.magiskapkButton);

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        boolean isTutorial = prefs.getBoolean(PreferenceUtils.PREF_TUTORIAL, true);
        tutorial.setVisibility(isTutorial ? View.VISIBLE : View.GONE);

        tutorialLearnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://theandroidmaster.github.io/about/#TheAndroidMaster/MediaNotification")));
                tutorial.setVisibility(View.GONE);
            }
        });

        tutorialOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefs.edit().putBoolean(PreferenceUtils.PREF_TUTORIAL, false).apply();
                tutorial.setVisibility(View.GONE);
            }
        });

        mediaNotificationSwitch.setChecked(NotificationService.isRunning(getContext()));
        mediaNotificationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"), REQUEST_NOTIFICATION);
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), soptqs.medianotification.R.array.array_color_methods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorMethodSpinner.setAdapter(adapter);
        colorMethodSpinner.setSelection(prefs.getInt(PreferenceUtils.PREF_COLOR_METHOD, 0));
        colorMethodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                prefs.edit().putInt(PreferenceUtils.PREF_COLOR_METHOD, i).apply();
                updateNotification();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        customColor.setColor(prefs.getInt(PreferenceUtils.PREF_CUSTOM_COLOR, Color.WHITE));
        customColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ColorPickerDialog(getContext())
                        .setDefaultPreference(Color.WHITE)
                        .setPreference(prefs.getInt(PreferenceUtils.PREF_CUSTOM_COLOR, Color.WHITE))
                        .setListener(new PreferenceDialog.OnPreferenceListener<Integer>() {
                            @Override
                            public void onPreference(PreferenceDialog dialog, Integer preference) {
                                prefs.edit().putInt(PreferenceUtils.PREF_CUSTOM_COLOR, preference).apply();
                                customColor.setColor(preference);
                                updateNotification();
                            }

                            @Override
                            public void onCancel(PreferenceDialog dialog) {
                            }
                        })
                        .show();
            }
        });

        inverseTextSwitch.setChecked(prefs.getBoolean(PreferenceUtils.PREF_INVERSE_TEXT_COLORS, true));
        inverseTextSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(PreferenceUtils.PREF_INVERSE_TEXT_COLORS, b).apply();
                if (b && highContrastSwitch.isChecked())
                    highContrastSwitch.setChecked(false);
                else updateNotification();
            }
        });

        highContrastSwitch.setChecked(prefs.getBoolean(PreferenceUtils.PREF_HIGH_CONTRAST_TEXT, false));
        highContrastSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(PreferenceUtils.PREF_HIGH_CONTRAST_TEXT, b).apply();
                if (b && inverseTextSwitch.isChecked())
                    inverseTextSwitch.setChecked(false);
                else updateNotification();
            }
        });

        forceMdIcons.setChecked(prefs.getBoolean(PreferenceUtils.PREF_FORCE_MD_ICONS, false));
        forceMdIcons.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(PreferenceUtils.PREF_FORCE_MD_ICONS, b).apply();
            }
        });

        alwaysDismissibleSwitch.setChecked(prefs.getBoolean(PreferenceUtils.PREF_ALWAYS_DISMISSIBLE, false));
        alwaysDismissibleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(PreferenceUtils.PREF_ALWAYS_DISMISSIBLE, b).apply();
                updateNotification();
            }
        });

        killProcessSwitch.setChecked(prefs.getBoolean(PreferenceUtils.PREF_FC_ON_DISMISS, false));
        killProcessSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(PreferenceUtils.PREF_FC_ON_DISMISS, b).apply();
            }
        });

        cancelOriginalNotificationSwitch.setChecked(prefs.getBoolean(PreferenceUtils.PREF_CANCEL_ORIGINAL_NOTIFICATION, false));
        cancelOriginalNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(PreferenceUtils.PREF_CANCEL_ORIGINAL_NOTIFICATION, b).apply();
            }
        });

        mediaControls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle(soptqs.medianotification.R.string.title_media_controls)
                        .setSingleChoiceItems(soptqs.medianotification.R.array.array_control_methods, prefs.getInt(PreferenceUtils.PREF_MEDIA_CONTROLS_METHOD, PreferenceUtils.CONTROLS_METHOD_NONE), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                prefs.edit().putInt(PreferenceUtils.PREF_MEDIA_CONTROLS_METHOD, i).apply();
                            }
                        })
                        .create()
                        .show();
            }
        });

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            storagePermission.setVisibility(View.GONE);
        storagePermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        });

        albumArtSwitch.setChecked(prefs.getBoolean(PreferenceUtils.PREF_SHOW_ALBUM_ART, true));
        albumArtSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(PreferenceUtils.PREF_SHOW_ALBUM_ART, b).apply();
                if (!b && lastFmSwitch.isChecked())
                    lastFmSwitch.setChecked(false);
                updateNotification();
            }
        });

        lastFmSwitch.setChecked(prefs.getBoolean(PreferenceUtils.PREF_USE_LASTFM, true));
        lastFmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(PreferenceUtils.PREF_USE_LASTFM, b).apply();
                if (b && !albumArtSwitch.isChecked())
                    albumArtSwitch.setChecked(true);
            }
        });

        if (ContextCompat.checkSelfPermission(getContext(), "android.permission.UPDATE_APP_OPS_STATS") == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            rootPermission.setVisibility(View.GONE);
        rootPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getContext(), "android.permission.UPDATE_APP_OPS_STATS") == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), soptqs.medianotification.R.string.msg_permission_granted, Toast.LENGTH_SHORT).show();
                    rootPermission.setVisibility(View.GONE);
                } else
                    Toast.makeText(getContext(), soptqs.medianotification.R.string.msg_app_ops_denied, Toast.LENGTH_SHORT).show();
            }
        });

        boolean isReceiver = prefs.getBoolean(PreferenceUtils.PREF_USE_RECEIVER, false);
        receiverSwitch.setChecked(isReceiver);
        mediaControls.setVisibility(isReceiver ? View.VISIBLE : View.GONE);
        receiverSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean(PreferenceUtils.PREF_USE_RECEIVER, b).apply();
                mediaControls.setVisibility(b ? View.VISIBLE : View.GONE);
            }
        });


        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               copyapk();
            }
        });

        magiskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadmodule();
            }
        });

        return view;
    }

    private void updateNotification() {
        if (NotificationService.isRunning(getContext())) {
            Intent intent = new Intent(getContext(), NotificationService.class);
            intent.setAction(NotificationService.ACTION_UPDATE);
            getContext().startService(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_NOTIFICATION && mediaNotificationSwitch != null)
            mediaNotificationSwitch.setChecked(NotificationService.isRunning(getContext()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            storagePermission.setVisibility(View.GONE);
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(soptqs.medianotification.R.string.title_settings);
    }

    @Override
    public void onTutorial() {
        if (tutorial != null)
            tutorial.setVisibility(View.VISIBLE);
    }


    public void downloadmodule(){
        String downURL = "https://raw.githubusercontent.com/Soptq/MediaNotification/Coolapk/MediaNotification4Magisk.zip";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downURL));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(getActivity(), Environment.DIRECTORY_DOWNLOADS, "MediaNotification4Magisk.zip");
        DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = manager.enqueue(request);
        Toast.makeText(getContext(),R.string.download,Toast.LENGTH_LONG).show();
    }

    public void copyapk(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setTitle(R.string.title_alert_copy);
        builder.setMessage(R.string.title_alert_copy_decp);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivity(intent);
                Toast.makeText(getContext(),R.string.reboot,Toast.LENGTH_LONG).show();
            }
        })
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.create().show();
    }
}
