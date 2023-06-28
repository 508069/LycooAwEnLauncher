// Generated by view binder compiler. Do not edit!
package com.example.mysettings.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import com.example.mysettings.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class StorageVolumeBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final ProgressBar progressBar;

  @NonNull
  public final TextView summary;

  @NonNull
  public final TextView title;

  private StorageVolumeBinding(@NonNull LinearLayout rootView, @NonNull ProgressBar progressBar,
      @NonNull TextView summary, @NonNull TextView title) {
    this.rootView = rootView;
    this.progressBar = progressBar;
    this.summary = summary;
    this.title = title;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static StorageVolumeBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static StorageVolumeBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.storage_volume, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static StorageVolumeBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.progress_bar;
      ProgressBar progressBar = rootView.findViewById(id);
      if (progressBar == null) {
        break missingId;
      }

      id = android.R.id.summary;
      TextView summary = rootView.findViewById(id);
      if (summary == null) {
        break missingId;
      }

      id = android.R.id.title;
      TextView title = rootView.findViewById(id);
      if (title == null) {
        break missingId;
      }

      return new StorageVolumeBinding((LinearLayout) rootView, progressBar, summary, title);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
