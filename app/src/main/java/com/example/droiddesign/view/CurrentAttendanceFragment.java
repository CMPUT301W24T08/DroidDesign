package com.example.droiddesign.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.droiddesign.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CurrentAttendanceFragment extends Fragment {
	private ListView attendanceListView;
	private ArrayAdapter<String> attendanceListAdapter;
	private List<String> attendanceList;

	private FirebaseFirestore firestore;
	private CollectionReference attendeeListRef;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		firestore = FirebaseFirestore.getInstance();
		attendeeListRef = firestore.collection("EventsDB").document("eventid").collection("attendeeList");
		attendanceList = new ArrayList<>();
		attendanceListAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, attendanceList);
		retrieveAttendanceList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_current_attendance, container, false);
		attendanceListView = view.findViewById(R.id.attendance_list_view);
		attendanceListView.setAdapter(attendanceListAdapter);
		return view;
	}

	private void retrieveAttendanceList() {
		attendeeListRef.get().addOnCompleteListener(task -> {
			if (task.isSuccessful() && task.getResult() != null) {
				for (DocumentSnapshot document : task.getResult().getDocuments()) {
					String userName = document.getString("userName");
					if (userName != null) {
						attendanceList.add(userName);
					}
				}
				attendanceListAdapter.notifyDataSetChanged();
			}
		});
	}
}
