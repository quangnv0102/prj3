package com.example.prj3.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prj3.R;
import com.example.prj3.adapters.TicketAdapter;
import com.example.prj3.models.Ticket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookingsFragment extends Fragment {

    private RecyclerView rvTickets;
    private LinearLayout layoutEmpty;
    private ProgressBar progressBar;
    private TicketAdapter ticketAdapter;
    private List<Ticket> tickets = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvTickets = view.findViewById(R.id.rvTickets);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        progressBar = view.findViewById(R.id.progressBar);

        ticketAdapter = new TicketAdapter(tickets);
        rvTickets.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTickets.setAdapter(ticketAdapter);

        view.findViewById(R.id.btnExploreMovies).setOnClickListener(v -> {
            // Navigate to movies tab
            if (getActivity() != null) {
                com.google.android.material.bottomnavigation.BottomNavigationView nav =
                    getActivity().findViewById(R.id.bottomNavigation);
                if (nav != null) nav.setSelectedItemId(R.id.nav_movies);
            }
        });

        loadTickets();
    }

    private void loadTickets() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        progressBar.setVisibility(View.VISIBLE);

        FirebaseDatabase.getInstance().getReference("tickets").child(userId)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!isAdded()) return;
                    tickets.clear();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Ticket ticket = child.getValue(Ticket.class);
                        if (ticket != null) {
                            ticket.setId(child.getKey());
                            tickets.add(0, ticket); // newest first
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                    ticketAdapter.notifyDataSetChanged();

                    if (tickets.isEmpty()) {
                        layoutEmpty.setVisibility(View.VISIBLE);
                        rvTickets.setVisibility(View.GONE);
                    } else {
                        layoutEmpty.setVisibility(View.GONE);
                        rvTickets.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (isAdded()) progressBar.setVisibility(View.GONE);
                }
            });
    }
}
