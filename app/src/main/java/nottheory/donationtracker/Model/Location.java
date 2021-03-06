package nottheory.donationtracker.Model;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * A class to store all information about a location
 */
public class Location {
    private final String name;
    private final String address;
    private final String city;
    private final String state;
    private final String type;
    private final String phone;
    private final String website;
    private final String zipcode;
    private final String latitude;
    private final String longitude;
    private final DonationCollection inventory = new DonationCollection();
    private int row;
    /**
     * A constructor for a location with all required information
     * @param name the location's name
     * @param latitude the lattitude of a location
     * @param longitude the longitude of a location
     * @param address the address of a location
     * @param city the city a location is in
     * @param state the state a location is in
     * @param zipcode the zipcode of a location
     * @param type the type of location this is
     * @param phone the phone number of the location
     * @param website the website for the location
     */
    public Location(String name, String latitude, String longitude, String address, String city,
                    String state, String zipcode, String type, String phone, String website) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.type = type;
        this.phone = phone;
        this.website = website;
        this.zipcode = zipcode;
        this.latitude = latitude;
        this.longitude = longitude;
        try {
            DatabaseConnection.sendRawSQL("CREATE TABLE IF NOT EXISTS `" + name + " INV` " +
                    "(donation_id INT AUTO_INCREMENT, timestamp TEXT, shortDescript TEXT, " +
                    "longDescript TEXT, value TEXT, category TEXT, comments TEXT, PRIMARY KEY " +
                    "(donation_id))  ENGINE=INNODB;");
            String locationinventory = DatabaseConnection.sendRawSQL("SELECT timestamp, " +
                    "shortDescript, longDescript, value, category, " +
                    "comments FROM `" + name + " INV`");
            if (!"".equals(locationinventory)) {
                String delimiter = "\\),\\(";
                String noEndsLocation = locationinventory.substring(1,
                        locationinventory.length() - 1);
                String[] donation_list = noEndsLocation.split(delimiter);
                for (String donation_x: donation_list) {
                    String noEndDonation = donation_x.substring(1,
                            donation_x.length() - 1);
                    String[] donation_parts = noEndDonation.split("', '");
                    if (donation_parts.length == 5) {
                        Donation this_donation = new Donation(donation_parts[0],
                                donation_parts[1], donation_parts[2], donation_parts[3],
                                donation_parts[4]);
                        inventory.addDonation(this_donation);
                    } else {
                        Donation this_donation = new Donation(donation_parts[0],
                                donation_parts[1], donation_parts[2], donation_parts[3],
                                donation_parts[4], donation_parts[5]);
                        inventory.addDonation(this_donation);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a donation to the location's collection
     * @param d the donation we want to add
     */
    public void addDonation(Donation d) {
        try {
            DatabaseConnection.sendRawSQL("INSERT INTO `" + name + " INV` (timestamp," +
                    " shortDescript, longDescript, value, category, comments) " +
            "VALUES(" + d.dataBaseString() + ");");
        } catch (Exception e) {
            e.printStackTrace();
        }
        inventory.addDonation(d);
    }

    /**
     * Gets the location's name
     * @return the location's name
     */
    public String getName(){
        return name;
    }

    /**
     * Sets the row aspect of a location (an artifact from the CSV file)
     * @param row the row we apply to the location
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Gets the row from a location (an artifact from the CSV file
     * @return the row of a location
     */
    public int getRow(){
        return row;
    }

    /**
     * getter for the donations that belong
     * to the location
     * @return a list of the donations that belong to
     * the location
     */
    public List<Donation> getDonations() {
        return inventory.getDonations();
    }

    /**
     * getter method for the donations belonging to the locations
     * @return a DonationCollection containing the donations
     * belonging to the location
     */
    public DonationCollection getDonationCollection() {
        return inventory;
    }

    private double getLat() {
        return Double.valueOf(this.latitude);
    }

    private double getLong() {
        return Double.valueOf(this.longitude);
    }

    private String getSnippet() {
        String text = "";
        text += "Phone: " + phone + "\n";
        text += "Website: " + website;
        return text;
    }

    @NonNull
    public String toString() {
        String text = "";
        text += "Name: " + name + "\n";
        text += "Latitude: " + latitude + "\n";
        text += "Longitude: " + longitude + "\n";
        text += "Street address: " + address + "\n";
        text += "City: " + city + "\n";
        text += "State: " + state + "\n";
        text += "Zip Code: " + zipcode + "\n";
        text += "Location type: " + type + "\n";
        text += "Phone: " + phone + "\n";
        text += "Website: " + website;
        return text;
    }

    /**
     * generates text representation of the location
     * for logging and lookup purposes
     * @return the log text in the format:
     * Location Name: name LocationNumber: rowNumber
     */
    public String logText() {
        return "Location Name: " + name + " LocationNumber: " + row;
    }

    /**
     * makes marker options for the map marker
     * to be displayed for the location
     * @return the MarkerOptions object for the
     * marker that represents the location
     */
    public MarkerOptions makeMarkerOption() {
        MarkerOptions markerOption = new MarkerOptions();
        MarkerOptions markerPosition = markerOption.position(new LatLng(this.getLat(),
                this.getLong()));
        MarkerOptions markerTitle = markerPosition.title(name);
        return markerTitle.snippet(this.getSnippet());
    }
}

