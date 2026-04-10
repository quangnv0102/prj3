package com.example.prj3.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.prj3.R;
import com.example.prj3.activities.LoginActivity;
import com.example.prj3.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private CircleImageView civAvatar;
    private TextView tvProfileName, tvProfileEmail, tvTotalBookings, tvMemberSince;
    private TextInputLayout tilName, tilPhone;
    private TextInputEditText etName, etPhone;

    private FirebaseAuth mAuth;
    private String userId;

    private final ActivityResultLauncher<String> imagePickerLauncher =
        registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) uploadAvatar(uri);
        });

    private final ActivityResultLauncher<String> permissionLauncher =
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted) imagePickerLauncher.launch("image/*");
            else Toast.makeText(requireContext(), "Cần quyền truy cập ảnh!", Toast.LENGTH_SHORT).show();
        });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;
        userId = user.getUid();

        bindViews(view);
        loadUserData(user);
        setupClickListeners();
    }

    private void bindViews(View view) {
        civAvatar = view.findViewById(R.id.civAvatar);
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        tvTotalBookings = view.findViewById(R.id.tvTotalBookings);
        tvMemberSince = view.findViewById(R.id.tvMemberSince);
        tilName = view.findViewById(R.id.tilName);
        tilPhone = view.findViewById(R.id.tilPhone);
        etName = view.findViewById(R.id.etName);
        etPhone = view.findViewById(R.id.etPhone);
    }

    private void loadUserData(FirebaseUser firebaseUser) {
        tvProfileEmail.setText(firebaseUser.getEmail());

        if (firebaseUser.getPhotoUrl() != null && isAdded()) {
            Glide.with(requireContext())
                .load(firebaseUser.getPhotoUrl())
                .circleCrop()
                .placeholder(R.drawable.ic_nav_profile)
                .into(civAvatar);
        }

        FirebaseDatabase.getInstance().getReference("users").child(userId)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!isAdded()) return;
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        tvProfileName.setText(user.getName() != null ? user.getName() : "Người dùng");
                        tvTotalBookings.setText(String.valueOf(user.getTotalBookings()));
                        etName.setText(user.getName());
                        etPhone.setText(user.getPhone());

                        // Format member since date
                        if (user.getCreatedAt() > 0) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
                            tvMemberSince.setText(sdf.format(new Date(user.getCreatedAt())));
                        }

                        // Load avatar from Cloud Storage URL
                        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty() && isAdded()) {
                            Glide.with(requireContext())
                                .load(user.getAvatarUrl())
                                .circleCrop()
                                .placeholder(R.drawable.ic_nav_profile)
                                .into(civAvatar);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
    }

    private void setupClickListeners() {
        requireView().findViewById(R.id.ivEditAvatar).setOnClickListener(v -> pickImage());
        requireView().findViewById(R.id.civAvatar).setOnClickListener(v -> pickImage());

        requireView().findViewById(R.id.btnSave).setOnClickListener(v -> saveProfile());

        requireView().findViewById(R.id.btnLogout).setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void pickImage() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            ? Manifest.permission.READ_MEDIA_IMAGES
            : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(requireContext(), permission)
                == PackageManager.PERMISSION_GRANTED) {
            imagePickerLauncher.launch("image/*");
        } else {
            permissionLauncher.launch(permission);
        }
    }

    private void uploadAvatar(Uri uri) {
        Toast.makeText(requireContext(), "Đang tải ảnh lên...", Toast.LENGTH_SHORT).show();

        StorageReference storageRef = FirebaseStorage.getInstance()
            .getReference("avatars/" + userId + ".jpg");

        storageRef.putFile(uri)
            .addOnSuccessListener(taskSnapshot ->
                storageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                    String avatarUrl = downloadUrl.toString();

                    // Update avatar in Database
                    FirebaseDatabase.getInstance().getReference("users")
                        .child(userId).child("avatarUrl").setValue(avatarUrl);

                    // Display immediately
                    if (isAdded()) {
                        Glide.with(requireContext())
                            .load(avatarUrl)
                            .circleCrop()
                            .into(civAvatar);
                        Toast.makeText(requireContext(), "Cập nhật ảnh thành công!", Toast.LENGTH_SHORT).show();
                    }
                })
            )
            .addOnFailureListener(e ->
                Toast.makeText(requireContext(), "Lỗi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
    }

    private void saveProfile() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";

        if (name.isEmpty()) {
            tilName.setError(getString(R.string.error_empty_name));
            return;
        }
        tilName.setError(null);

        FirebaseDatabase.getInstance().getReference("users").child(userId)
            .child("name").setValue(name);
        FirebaseDatabase.getInstance().getReference("users").child(userId)
            .child("phone").setValue(phone)
            .addOnSuccessListener(unused -> {
                tvProfileName.setText(name);
                Toast.makeText(requireContext(), "Lưu thành công!", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e ->
                Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
    }
}
