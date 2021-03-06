package nottheory.donationtracker.Controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nottheory.donationtracker.Model.Donation;
import nottheory.donationtracker.Model.DonationCollection;
import nottheory.donationtracker.Model.Location;
import nottheory.donationtracker.Model.LocationCollection;
import nottheory.donationtracker.Model.LoginManager;
import nottheory.donationtracker.R;

/**
 * The activity where you can search all donations from all locations
 */
public class DonationSearchActivity extends AppCompatActivity {
    private Switch searchByCat;
    private TextView searchBox;
    private RecyclerView donationSearchList;
    private Spinner catSpinner;
    private Spinner locSpinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Button searchButton;
        Button backButton;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_search);
        searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSearch();
            }
        });
        backButton = findViewById(R.id.search_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        searchBox = findViewById(R.id.search_entry);
        searchBox.setVisibility(View.VISIBLE);
        locSpinner = findViewById(R.id.search_location_spinner);
        ArrayList<String> locationList = new ArrayList<>();
        locationList.add("All");
        LocationCollection allLocations = LoginManager.getLocations();
        locationList.addAll(allLocations.getLocationNames());
        locSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                locationList));
        catSpinner = findViewById(R.id.search_cat_spinner);
        ArrayList<String> categoryList = new ArrayList<>();
        addCategories(categoryList);

        catSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                categoryList));
        catSpinner.setVisibility(View.GONE);
        searchByCat = findViewById(R.id.search_switch);
        searchByCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchByCat.isChecked()) {
                    catSpinner.setVisibility(View.VISIBLE);
                    searchBox.setVisibility(View.GONE);
                } else {
                    catSpinner.setVisibility(View.GONE);
                    searchBox.setVisibility(View.VISIBLE);
                }
            }
        });
        donationSearchList = findViewById(R.id.donationSearchList);
        List<Donation> donationArray = allLocations.getAllDonationsAL();
        donationSearchList.setAdapter(new DonationSearchActivity.DonationAdapter(this,
                donationArray.toArray()));
        donationSearchList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void addCategories(Collection<String> categoryList) {
        categoryList.add("Clothing");
        categoryList.add("Hat");
        categoryList.add("Kitchen");
        categoryList.add("Electronics");
        categoryList.add("Household");
        categoryList.add("Other");
    }

    private class DonationAdapter extends RecyclerView.Adapter
            <DonationSearchActivity.DonationAdapter.DonationViewHolder> {
        private final Object[] donations;
        private final Context context;
        public class DonationViewHolder extends RecyclerView.ViewHolder {
            final Button donation;

            DonationViewHolder(View view) {
                super(view);
                donation = view.findViewById(R.id.location_row_button);
            }
        }

        DonationAdapter(Context context, Object[] donations) {
            this.donations = donations;
            this.context = context;
        }

        @NonNull
        @Override
        public DonationSearchActivity.DonationAdapter.DonationViewHolder onCreateViewHolder(
                @NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.location_recyclerview_row,
                    parent, false);
            return new DonationSearchActivity.DonationAdapter.DonationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final DonationSearchActivity.DonationAdapter.
                                             DonationViewHolder viewHolder, int position) {
            viewHolder.donation.setText(donations[position].toString());
            viewHolder.donation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DonationSearchActivity.this,
                            DonationInfoActivity.class);
                    intent.putExtra("donation", (
                            (Donation) donations[viewHolder.getAdapterPosition()]).getName());
                    //feature envy is necessary to tell next activity which donation was clicked

//                    LocationCollection allLocations = LoginManager.getLocations();
//                    Location l = allLocations.getLocationWithDonation(
//                            (Donation) donations[viewHolder.getAdapterPosition()]);
                    intent.putExtra("location", LoginManager.getNameOfLocationWithDonation(
                            (Donation) donations[viewHolder.getAdapterPosition()]
                    ));
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return donations.length;
        }
    }

    private void doSearch() {
        LocationCollection allLocations = LoginManager.getLocations();
        ArrayList<Location> locationList  = allLocations.getLocations();
        ArrayList<Donation> donationList = new ArrayList<>();
        Object locSelected = locSpinner.getSelectedItem();
        if (("All".equals(locSelected.toString())) ||
                ("".equals(locSelected.toString()))) {
            for (Location l : locationList) {
                donationList.addAll(l.getDonations());
            }
        } else {
            String locationString = locSelected.toString();
            LocationCollection locationCol = new LocationCollection(locationList);
            Location location = locationCol.getLocationByName(locationString);
            if (location != null) {
                List<Donation> donations = location.getDonations();
                donationList.addAll(donations);
            }
        }
        DonationCollection donations = new DonationCollection(donationList);
        Object catSelected = catSpinner.getSelectedItem();
        if (searchByCat.isChecked()) {
            donationList = donations.getDonationsByCategory(
                    catSelected.toString());
        } else {
            CharSequence searchText = searchBox.getText();
            donationList = donations.getDonationsBySimilarName(searchText.toString());
        }
        donationSearchList.setAdapter(new DonationSearchActivity.DonationAdapter(this,
                donationList.toArray()));
    }
}
