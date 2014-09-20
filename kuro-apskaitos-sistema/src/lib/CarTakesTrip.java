package lib;

public class CarTakesTrip {

    private String car_registration_number;
    private int trip_id;
    private long start_counter;
    private long end_counter;

    public CarTakesTrip(String car_registration_number, int trip_id, long start_counter, long end_counter) {
        this.car_registration_number = car_registration_number;
        this.trip_id = trip_id;
        this.start_counter = start_counter;
        this.end_counter = end_counter;
    }

    public String getCarRegistrationNumber() {
        return car_registration_number;
    }

    public void setCarRegistrationNumber(String car_registration_number) {
        this.car_registration_number = car_registration_number;
    }

    public int getTripID() {
        return trip_id;
    }

    public void setTripID(int trip_id) {
        this.trip_id = trip_id;
    }


    public long getStartCounter() {
        return start_counter;
    }


    public void setStartCounter(long start_counter) {
        this.start_counter = start_counter;
    }

    public long getEndCounter() {
        return end_counter;
    }

    public void setEndCounter(long end_counter) {
        this.end_counter = end_counter;
    }

    public long getDistance() {
        return this.end_counter - this.start_counter;
    }

}
